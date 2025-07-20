import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080';
const TRANSACTION_AMOUNT = 100; // 입출금할 금액
const INITIAL_BALANCE = 1000000; // 초기 잔액

// 1. 테스트 실행 전 모든 VU가 공유할 단일 계정 생성 및 초기 잔액 설정
export function setup() {
  // ... (이전 회원가입 및 로그인 코드는 동일) ...
  const loginRes = http.post(`${BASE_URL}/login`, loginPayload, registerParams);
  const accessToken = loginRes.json('accessToken');
  check(loginRes, { 'setup: login successful': (r) => r.status === 200 && accessToken !== null });

  const authHeaders = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${accessToken}`,
    },
  };

  // 초기 마일리지 설정
  const depositRes = http.post(`${BASE_URL}/k6/deposit`, JSON.stringify(INITIAL_BALANCE), authHeaders);

  // 💥 수정된 부분: 응답 본문을 숫자로 변환
  const initialBalance = Number(depositRes.body);

  check(depositRes, { 'setup: initial deposit successful': (r) => r.status === 200 && !isNaN(initialBalance) });

  // 콘솔에 실제 받은 값을 로그로 남겨 디버깅을 쉽게 함
  console.log(`[Setup] Server Response for initial balance: ${depositRes.body}`);
  console.log(`[Setup] Account for 'concurrent_user' created with initial balance: ${initialBalance}`);

  return { accessToken: accessToken, initialBalance: initialBalance };
}

export const options = {
  scenarios: {
    // 동시에 입금과 출금을 실행하는 시나리오
    deposit_and_withdraw_concurrently: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 50 }, // 10초 동안 VU를 50명까지 늘림
        { duration: '20s', target: 50 }, // 50명으로 20초간 유지
        { duration: '5s', target: 0 },   // 5초 동안 VU를 0으로 줄임
      ],
      gracefulRampDown: '5s',
    },
  },
};

// 2. 모든 가상 사용자가 동시에 동일한 계정에 대해 입출금 실행
export default function (data) {
  const authHeaders = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${data.accessToken}`,
    },
  };

  // VU ID가 홀수이면 입금, 짝수이면 출금하여 총액 변동을 0으로 만듦
  if (__VU % 2 === 0) {
    const withdrawRes = http.post(`${BASE_URL}/k6/withdraw`, JSON.stringify(TRANSACTION_AMOUNT), authHeaders);
    check(withdrawRes, {'withdraw successful': r => r.status === 200});
  } else {
    const depositRes = http.post(`${BASE_URL}/k6/deposit`, JSON.stringify(TRANSACTION_AMOUNT), authHeaders);
    check(depositRes, {'deposit successful': r => r.status === 200});
  }
  sleep(0.5); // 짧은 대기 시간
}

// 3. 테스트 종료 후 최종 상태 검증
export function teardown(data) {
  if (!data.accessToken) {
    console.log('[Teardown] No access token, skipping final balance check.');
    return;
  }

  const authHeaders = {
    headers: {
      'Authorization': `Bearer ${data.accessToken}`,
    },
  };

  // 최종 잔액 조회
  const res = http.get(`${BASE_URL}/k6/mileage`, authHeaders);

  // 💥 수정된 부분: 응답 본문을 숫자로 변환
  const finalBalance = Number(res.body);

  console.log(`
--- Concurrency Test Verification ---`);
  console.log(`Initial Balance: ${data.initialBalance}`);
  console.log(`Final Balance  : ${finalBalance}`);
  console.log(`Difference     : ${finalBalance - data.initialBalance}`);

  // 최종 잔액이 초기 잔액과 반드시 일치해야 함
  check(res, {
    'Final balance must be correct': (r) => finalBalance === data.initialBalance,
  });
}
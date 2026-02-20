import http from 'k6/http';
import { check, sleep, group } from 'k6';

// 테스트를 실행할 API 서버의 기본 URL
const BASE_URL = 'http://localhost:8081';

// 테스트에 사용할 입출금 금액과 초기 잔액 설정
const TRANSACTION_AMOUNT = 100;
const INITIAL_BALANCE = 1000000;

/**
 * 1. Setup 단계 (테스트 실행 전 준비 과정)
 */
export function setup() {
  const uniqueId = `${__VU}_${new Date().getTime()}`;
  const userId = `k6_user_${uniqueId}`;
  const userSabun = `k6_sabun_${uniqueId}`;
  const userPassword = 'password';

  const registerPayload = JSON.stringify({
    sabun: userSabun,
    id: userId,
    pw: userPassword,
    name: 'K6 Concurrent User',
    role: 'ROLE_NORMAL',
  });

  const requestParams = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const registerRes = http.post(`${BASE_URL}/member/register`, registerPayload, requestParams);
  check(registerRes, { 'SETUP: User registration successful': (r) => r.status === 200 });

  const loginPayload = JSON.stringify({
    id: userId,
    pw: userPassword,
  });

  const loginRes = http.post(`${BASE_URL}/login`, loginPayload, requestParams);
  const accessToken = loginRes.json('accessToken');
  check(loginRes, { 'SETUP: Login successful and got access token': (r) => r.status === 200 && accessToken !== null });

  const authHeaders = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${accessToken}`,
    },
  };

  const depositRes = http.post(`${BASE_URL}/k6/deposit`, JSON.stringify(INITIAL_BALANCE), authHeaders);
  const initialBalance = Number(depositRes.body);
  check(depositRes, { 'SETUP: Initial deposit successful': (r) => r.status === 200 && !isNaN(initialBalance) });

  console.log(`[SETUP] Test account for '${userId}' created with initial balance: ${initialBalance}`);

  return { accessToken: accessToken, initialBalance: initialBalance };
}

/**
 * 2. 시나리오 및 옵션 설정
 */
export const options = {
  scenarios: {
    deposit_and_withdraw_concurrently: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 200 }, // 30초 동안 200명으로 증가 (Ramp-up)
        { duration: '3m', target: 200 },  // 3분 동안 200명이 미친듯이 입출금 반복 (Stress)
        { duration: '10s', target: 0 },   // 10초 동안 종료 (Ramp-down)
      ],
      gracefulRampDown: '5s',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.10'], // 에러율 10% 미만 허용
    http_req_duration: ['p(95)<2000'], // 95%가 2초 안에 들어오면 허용
  }
};

/**
 * 3. Main 단계 (수정된 실제 테스트 로직)
 */
export default function (data) {
  const authHeaders = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${data.accessToken}`,
    },
  };

  // 이렇게 하면 모든 반복(iteration)은 잔액에 영향을 주지 않아(net-zero), 최종 잔액을 정확히 예측할 수 있음
  group('Deposit and Withdraw Transaction', function () {
    const depositRes = http.post(`${BASE_URL}/k6/deposit`, JSON.stringify(TRANSACTION_AMOUNT), authHeaders);
    check(depositRes, { 'Deposit successful': r => r.status === 200 });

    const withdrawRes = http.post(`${BASE_URL}/k6/withdraw`, JSON.stringify(TRANSACTION_AMOUNT), authHeaders);
    check(withdrawRes, { 'Withdraw successful': r => r.status === 200 });
  });

  sleep(0.5);
}

/**
 * 4. Teardown 단계 (테스트 종료 후 정리 과정)
 */
export function teardown(data) {
  if (!data.accessToken) {
    console.log('[TEARDOWN] No access token, skipping final balance check.');
    return;
  }

  const authHeaders = {
    headers: {
      'Authorization': `Bearer ${data.accessToken}`,
    },
  };

  const res = http.get(`${BASE_URL}/k6/mileage`, authHeaders);
  const finalBalance = Number(res.body);

  console.log(`--- Concurrency Test Verification ---`);
  console.log(`Initial Balance : ${data.initialBalance}`);
  console.log(`Final Balance   : ${finalBalance}`);
  console.log(`Difference      : ${finalBalance - data.initialBalance}`);

  check(res, {
    'FINAL CHECK: Final balance must be correct': (r) => finalBalance === data.initialBalance,
  });
}
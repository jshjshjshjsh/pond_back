import http from 'k6/http';
import { check, sleep, group } from 'k6';

// 테스트 대상 URL
const BASE_URL = 'http://localhost:8080';

// 테스트 설정
const TRANSACTION_AMOUNT = 100;
const INITIAL_BALANCE = 10000; // 초기 잔액 넉넉히

/**
 * 1. Setup: 테스트 유저 생성 및 초기화
 * - Mileage(Redis)와 Point(JPA Lock) 모두 사용할 수 있는 유저를 만듭니다.
 */
export function setup() {
    const uniqueId = `${__VU}_${new Date().getTime()}`;
    const userId = `k6_tester_${uniqueId}`;
    const userSabun = `k6_sabun_${uniqueId}`;
    const userPassword = 'password';

    // 1. 회원가입 (이때 Mileage, Point 엔티티가 모두 생성되어야 합니다!)
    const registerPayload = JSON.stringify({
        sabun: userSabun,
        id: userId,
        pw: userPassword,
        name: 'Concurrency Tester',
        role: 'ROLE_NORMAL',
    });

    const params = { headers: { 'Content-Type': 'application/json' } };

    const regRes = http.post(`${BASE_URL}/member/register`, registerPayload, params);
    check(regRes, { 'Registered': (r) => r.status === 200 });

    // 2. 로그인 (토큰 발급)
    const loginPayload = JSON.stringify({ id: userId, pw: userPassword });
    const loginRes = http.post(`${BASE_URL}/login`, loginPayload, params);
    const accessToken = loginRes.json('accessToken');

    check(loginRes, { 'Logged in': (r) => r.status === 200 && accessToken !== null });

    // 3. 초기 잔액 설정 (Mileage는 이미 0원 생성, Point도 생성되었다고 가정)

    return { accessToken: accessToken };
}

/**
 * 2. Scenarios: 3가지 락 전략을 비교하기 위한 시나리오 정의
 * - executor: 'ramping-vus' (점진적 부하 증가)
 */
export const options = {
    scenarios: {
        // 시나리오 1: 기존 Redis 분산락 (Mileage)
        /*
        redis_lock_strategy: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '5s', target: 20 }, // 웜업
                { duration: '10s', target: 50 }, // 부하 유지
                { duration: '5s', target: 0 },  // 종료
            ],
            gracefulRampDown: '0s',
            exec: 'testRedisLock', // 실행할 함수 지정
        },
         */
        // 시나리오 2: 낙관적 락 (Point)
        optimistic_lock_strategy: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '5s', target: 20 },
                { duration: '10s', target: 50 },
                { duration: '5s', target: 0 },
            ],
            gracefulRampDown: '0s',
            exec: 'testOptimisticLock', // 실행할 함수 지정
        },
        // 시나리오 3: 비관적 락 (Point)
        /*
        pessimistic_lock_strategy: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '5s', target: 20 },
                { duration: '10s', target: 50 },
                { duration: '5s', target: 0 },
            ],
            gracefulRampDown: '0s',
            exec: 'testPessimisticLock', // 실행할 함수 지정
        },
         */
    },
    thresholds: {
        // 전체 에러율 1% 미만 (낙관적 락 실패 포함 시 조정 필요)
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<500'],
    }
};

/**
 * 3. 각 시나리오별 실행 함수
 */

// [Scenario 1] Redis 분산락 테스트 (기존 Mileage)
export function testRedisLock(data) {
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${data.accessToken}`
    };

    // 입금 -> 출금 (Net Zero)
    http.post(`${BASE_URL}/k6/deposit`, JSON.stringify(TRANSACTION_AMOUNT), { headers: headers });
    http.post(`${BASE_URL}/k6/withdraw`, JSON.stringify(TRANSACTION_AMOUNT), { headers: headers });
}

// [Scenario 2] 낙관적 락 테스트 (New Point)
export function testOptimisticLock(data) {
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${data.accessToken}`
    };

    // 충돌 유발을 위해 동시에 입금 요청
    const res = http.post(`${BASE_URL}/k6/point/deposit/optimistic`, JSON.stringify(TRANSACTION_AMOUNT), { headers: headers });

    // 낙관적 락은 충돌 시 500 또는 409 에러가 발생할 수 있음 -> 이를 Grafana에서 에러율로 확인
    check(res, { 'Optimistic: Success': r => r.status === 200 });
}

// [Scenario 3] 비관적 락 테스트 (New Point)
export function testPessimisticLock(data) {
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${data.accessToken}`
    };

    const res = http.post(`${BASE_URL}/k6/point/deposit/pessimistic`, JSON.stringify(TRANSACTION_AMOUNT), { headers: headers });

    // 비관적 락은 느리더라도 성공해야 함
    check(res, { 'Pessimistic: Success': r => r.status === 200 });
}
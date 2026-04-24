import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080';
const TRANSACTION_AMOUNT = 100;

export function setup() {
    const uniqueId = `${__VU}_${new Date().getTime()}`;
    const userId = `redis_tester_${uniqueId}`;
    const userSabun = `redis_sabun_${uniqueId}`;
    const userPassword = 'password';

    http.post(`${BASE_URL}/member/register`, JSON.stringify({
        sabun: userSabun, id: userId, pw: userPassword, name: 'Redis Tester', role: 'ROLE_NORMAL',
    }), { headers: { 'Content-Type': 'application/json' } });

    const loginRes = http.post(`${BASE_URL}/login`, JSON.stringify({ id: userId, pw: userPassword }), { headers: { 'Content-Type': 'application/json' } });
    return { accessToken: loginRes.json('accessToken') };
}

export const options = {
    // 무한 루프 돌리면서 터미널에서 Redis 끄기 좋게 30초 동안 50명 유지
    scenarios: {
        redis_lock_test: {
            executor: 'constant-vus',
            vus: 50,
            duration: '30s',
        },
    },
};

export default function (data) {
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${data.accessToken}`,
        },
    };

    const res = http.post(`${BASE_URL}/k6/deposit`, JSON.stringify(TRANSACTION_AMOUNT), params);

    // 팩트 체크: Redis가 죽었을 때 무한 대기(타임아웃) 타지 않고, AOP에서 던진 예외가 500 에러와 함께 즉시 반환되어야 함
    check(res, {
        '정상 락 획득 및 입금 성공 (200)': (r) => r.status === 200,
        '서킷 브레이커 작동 (Fail-fast 차단)': (r) => r.status === 409 && r.body.includes('현재 요청이 많아 처리할 수 없습니다'),
        '서킷 열리기 전 통신 에러': (r) => r.status === 500 && r.body.includes('락 획득 중 인터럽트/장애 발생')
    });

    sleep(0.1);
}
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = 'http://localhost:8080';

export function setup() {
    const uniqueId = `${__VU}_${new Date().getTime()}`;
    const userId = `gemini_tester_${uniqueId}`;
    const userSabun = `gemini_sabun_${uniqueId}`;
    const userPassword = 'password';

    const regRes = http.post(`${BASE_URL}/member/register`, JSON.stringify({
        sabun: userSabun, id: userId, pw: userPassword, name: 'Gemini Tester', role: 'ROLE_NORMAL',
    }), { headers: { 'Content-Type': 'application/json' } });

    // 🔥 회원가입이 성공했는지 로그로 확인
    console.log(`[Register] Status: ${regRes.status}, Body: ${regRes.body}`);

    const loginRes = http.post(`${BASE_URL}/login`, JSON.stringify({ id: userId, pw: userPassword }), { headers: { 'Content-Type': 'application/json' } });

    // 🔥 로그인 성공했는지 로그로 확인
    console.log(`[Login] Status: ${loginRes.status}, Body: ${loginRes.body}`);

    return { accessToken: loginRes.json('accessToken') };
}

export const options = {
    // RateLimiter(초당 5회)를 확실하게 터뜨리기 위해 초당 50번의 요청을 10초간 때림
    scenarios: {
        gemini_bomb: {
            executor: 'constant-arrival-rate',
            rate: 50,
            timeUnit: '1s',
            duration: '10s',
            preAllocatedVUs: 50,
            maxVUs: 100,
        },
    },
};

export default function (data) {
    const params = {
        headers: {
            'Content-Type': 'text/plain',
            'Authorization': `Bearer ${data.accessToken}`,
        },
    };

    const res = http.post(`${BASE_URL}/ai/summary`, '테스트 업무 요약 내용입니다. 정리해주세요.', params);

    // 팩트 체크: 정상 응답(200)이거나, 서킷 브레이커가 작동해서 Fallback 텍스트가 바로 튀어나와야 성공
    check(res, {
        '서킷 브레이커 Fallback 작동 확인': (r) => r.body.includes('AI 서버 지연 중입니다. 요약 기능은 잠시 후 다시 시도해주세요.'),
        '정상 응답 확인': (r) => r.status === 200 && !r.body.includes('AI 서버 지연 중'),
    });
}
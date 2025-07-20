import http from 'k6/http';
import { check, sleep } from 'k6';

// 1. 테스트 옵션 설정
export const options = {
    // 가상 사용자 10명이 30초 동안 테스트를 수행
    vus: 50,
    duration: '30s',

    // 성능 목표(Thresholds) 설정
    thresholds: {
        // HTTP 요청 실패율이 1% 미만이어야 함
        http_req_failed: ['rate<0.01'],
        // 95%의 요청이 200ms 안에 처리되어야 함
        http_req_duration: ['p(95)<200'],
    },
};

// 2. 테스트 실행 함수
export default function () {
    // GET 요청을 보내고 응답을 res 변수에 저장
    const res = http.get('http://localhost:8080/hello?name=k6');

    // 3. 응답 검증 (Checks)
    check(res, {
        // 'status is 200': 응답 코드가 200인지 확인
        'status is 200': (r) => r.status === 200,
        // 'response body contains "Hello, k6"': 응답 본문에 특정 문자열이 포함되어 있는지 확인
        'response body contains "Hello, k6"': (r) => r.body.includes('Hello, k6'),
    });

    // 각 요청 사이에 1초 대기
    sleep(1);
}
## 🐸 Pond: 업무 보고서 작성을 위한 캘린더 기반 협업 서비스 (Backend)
Pond는 매월 수행한 업무를 캘린더에 간편하게 기록하고, 팀 단위로 쉽게 공유하여 월간 업무 보고서 작성의 노고를 줄여주기 위해 제작한 웹 서비스의 백엔드입니다.

이 Repository는 Spring Boot로 구현된 Pond의 백엔드 API 서버 코드를 관리합니다.

<img width="2538" height="2238" alt="localhost_5173_" src="https://github.com/user-attachments/assets/597f038f-f2c7-4a00-be97-2e5f966e9c00" />

<img width="2538" height="3576" alt="localhost_calendar" src="https://github.com/user-attachments/assets/8086c5a6-93e4-47d5-be46-6624c5b4efc4" />

<br>

### ✨ 주요 기능
- **👤 사용자 관리:**
  - JWT(JSON Web Token) 기반의 안전한 회원가입 및 로그인/로그아웃 기능을 제공합니다.
  - 사용자는 자신의 개인 정보(역할, 비밀번호 등)를 안전하게 변경할 수 있습니다.
    
- **🤝 팀 관리:**
  - 사용자는 팀을 생성하고 다른 멤버를 사번으로 초대하여 팀을 구성할 수 있습니다.
  - 자신이 속한 팀과 팀원들의 목록을 조회할 수 있습니다.

- **📅 캘린더 기반 업무 일지 (Work History):**
  - 캘린더 UI에 맞춰 업무 내용을 기간별로 등록, 수정, 삭제할 수 있습니다.
  - 업무 내용은 개인적으로만 기록하거나, 팀 전체에 공유할 수 있습니다.

- **🤖 AI 기반 월간 업무 요약:**
  - 지정된 기간의 업무 기록들을 Google Gemini AI를 통해 자동으로 요약하고 정리합니다.
  - 생성된 월간 요약 보고서를 저장하고 팀원들과 공유할 수 있습니다.

- **🔐 역할 기반 접근 제어 (RBAC):**
  - 사용자 역할(ROLE_LEADER, ROLE_NORMAL)에 따라 팀 생성, 멤버 초대 등의 기능 접근 권한을 차등 부여합니다.

<br>


### 🛠️ 기술 스택 및 아키텍처
Pond 백엔드는 클라우드 네이티브 환경에 최적화된 최신 기술 스택으로 구성되어 있습니다.

| 분야 | 기술 |
| -------| ---------|
| Backend	| Java 17, Spring Boot 3.5.3, Spring Security (JWT), Spring Data JPA |
| Database |	MySQL |
| Cache / Lock |	Redis with Redisson (분산 락 구현) |
| AI |	Google Gemini AI API |
| Build Tool |	Gradle |
| CI/CD	| GitHub Actions (Docker Image Build & Push to GHCR, SonarCloud Analysis) |
| Deployment |	Docker, Kubernetes, Helm |
| GitOps |	ArgoCD | 
| Monitoring |	OpenTelemetry (Trace), Jaeger (Visualization) |
| Secret | Management	Sealed Secrets |

<br>

### 📖 API 엔드포인트
| Method |	Path |	설명 |	권한 |
| ---- | - | - | ----- |
| POST |	/member/register |	회원가입	| 아무나 |
| POST |	/login |	로그인 (Access/Refresh Token 발급)	| 아무나 |
| POST |	/login/refresh |	Access Token 재발급	| 아무나 |
| GET |	/logout |	로그아웃	| NORMAL, LEADER |
| GET |	/member/info |	내 정보 조회	| NORMAL, LEADER |
| PATCH |	/member/info |	내 정보 수정	| NORMAL, LEADER |
| POST |	/team/leader/register |	팀 생성	| LEADER |
| POST |	/team/leader/{teamId}/members |	팀에 멤버 추가 |	LEADER |
| GET |	/team/my-teams/members |	내가 속한 팀의 모든 멤버 조회	| NORMAL, LEADER |
| POST |	/calendar/workhistory/save |	업무일지 등록	| NORMAL, LEADER |
| GET |	/calendar/workhistory/list |	내 업무일지 기간별 조회	| NORMAL, LEADER |
| PATCH |	/calendar/workhistory/{id} |	업무일지 수정	| NORMAL, LEADER |
| DELETE |	/calendar/workhistory/{id} |	업무일지 삭제	| NORMAL, LEADER |
| POST |	/ai/summary |	AI 업무 요약 생성 |	| NORMAL, LEADER |
| POST |	/calendar/worksummary |	생성된 AI 요약 저장	| NORMAL, LEADER |
| GET |	/calendar/leader/worksummary/list |	팀원들의 공유된 요약 조회	| LEADER |

<br>

### 🧪 테스트
- **단위/통합 테스트:** JUnit5와 Mockito를 사용하여 서비스, 컨트롤러, 리포지토리의 단위 테스트와 통합 테스트를 작성
- **성능 테스트:** k6를 사용하여 동시성 이슈(마일리지 입출금)에 대한 부하 테스트 스크립트를 작성하여 시스템의 안정성을 검증

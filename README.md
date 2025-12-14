📚 Bookstore API Server

Spring Boot와 MySQL을 사용하여 구현한 웹 서점 REST API 서버입니다.
JWT 기반 인증/인가와 Role 기반 접근 제어(RBAC)를 적용하였으며,
도서·리뷰·댓글·위시리스트 기능을 제공합니다.

본 프로젝트는 DB·API 설계를 실제 서버로 구현하고 JCloud에 배포하는 것을 목표로 합니다.

🛠 기술 스택

Backend: Spring Boot (Kotlin)

Database: MySQL

ORM: Spring Data JPA (Hibernate)

Authentication: JWT (Access / Refresh Token)

Authorization: RBAC (ROLE_USER, ROLE_ADMIN)

Documentation: Swagger (OpenAPI)

Testing: Postman

Deployment: JCloud (Linux VM)

🚀 실행 방법 (Local)
1️⃣ 프로젝트 빌드
./gradlew clean build

2️⃣ 서버 실행
java -jar build/libs/*.jar


실행 전 .env 또는 환경변수 설정이 필요합니다.

🔐 환경변수 설정

GitHub에는 .env.example만 포함되어 있으며,
실제 값이 들어간 .env 파일은 Classroom으로만 제출합니다.

.env.example
# Database
DB_URL=jdbc:mysql://localhost:3306/bookstore
DB_USERNAME=bookstore_user
DB_PASSWORD=bookstore_password

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_ACCESS_EXP_MS=3600000
JWT_REFRESH_EXP_MS=1209600000

# Server
SERVER_PORT=8080

🌐 배포 주소 (JCloud)

Base URL

http://<JCloud_IP>:<PORT>


Swagger UI

http://<JCloud_IP>:<PORT>/swagger-ui/index.html


Health Check

http://<JCloud_IP>:<PORT>/health

🔑 인증(Authentication) 플로우

POST /api/v1/auth/login

이메일 / 비밀번호 로그인

Access Token + Refresh Token 발급

이후 요청 시 Header에 토큰 포함

Authorization: Bearer {accessToken}


Access Token 만료 시

POST /api/v1/auth/refresh로 재발급

👤 역할(Role) 및 권한
Role	권한
USER	도서 조회, 리뷰/댓글 CRUD, 위시리스트
ADMIN	도서 등록/수정/삭제, 관리자 전용 API
🧪 예제 계정 (테스트용)
USER
email: user1@example.com
password: P@ssw0rd!

ADMIN
email: admin@example.com
password: P@ssw0rd!

📌 주요 기능
📖 Book

도서 등록 / 수정 / 삭제 (ADMIN)

도서 목록 조회 (페이지네이션 / 검색 / 정렬)

도서 상세 조회

📝 Review

리뷰 작성 / 수정 / 삭제

도서별 리뷰 조회

리뷰 좋아요

💬 Comment

리뷰 댓글 CRUD

댓글 좋아요

리뷰 하위 Sub-resource 구조

⭐ Wishlist

위시리스트 추가 / 삭제

위시리스트 목록 조회

위시리스트 존재 여부 확인

📚 엔드포인트 요약 (일부)
Method	URL	설명	권한
POST	/api/v1/auth/login	로그인	ALL
GET	/api/v1/books	도서 목록	ALL
POST	/api/v1/books	도서 등록	ADMIN
GET	/api/v1/reviews/{id}	리뷰 조회	ALL
POST	/api/v1/reviews/{id}/comments	댓글 작성	USER
POST	/api/v1/wishlist/{bookId}	위시리스트 추가	USER
GET	/health	헬스체크	ALL

전체 엔드포인트는 Swagger UI에서 확인할 수 있습니다.

📦 Postman

Postman Collection(JSON) 제공

환경 변수:

baseUrl

accessToken

Pre-request Script:

로그인 후 토큰 자동 저장

Test Script:

상태 코드(200/201/403) 검증

📁 위치:

postman/bookstore.postman_collection.json

🔒 보안 고려사항

비밀번호 bcrypt 해시 저장

JWT 기반 무상태(stateless) 인증

민감 정보는 .env로 분리

GitHub Public Repo에 비밀 정보 미포함

⚙️ 한계 및 개선 계획

주문(Order) 도메인 미구현 (추후 확장 예정)

캐싱(Redis) 미적용

관리자 통계 API 고도화 가능

✅ 프로젝트 상태 요약

✔ 4개 이상 리소스

✔ 30개 이상 엔드포인트

✔ JWT 인증 / RBAC

✔ Swagger 문서 자동화

✔ JCloud 배포 및 헬스체크

📌 본 프로젝트는 학습 목적의 과제이며, 실제 서비스 환경에서는 추가적인 보안 및 성능 개선이 필요합니다.


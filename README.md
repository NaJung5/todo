# ToDo API 프로젝트

Spring Boot 기반의 JWT 인증/인가 및 Querydsl 동적 검색 기능이 적용된 ToDo 관리 백엔드 API입니다.  
회원가입, 로그인, 할 일 등록 및 조회 기능을 제공하며, 보안과 확장성을 고려한 구조로 설계하였습니다.

## 🔧 기술 스택

| 구분       | 내용                                         |
|------------|----------------------------------------------|
| Language   | Java 17                                      |
| Framework  | Spring Boot 3.x, Spring Security             |
| Build Tool | Gradle                                       |
| DB         | MariaDB/MySQL (로컬/운영 환경) |
| ORM        | Spring Data JPA + Querydsl                   |
| 인증 방식  | JWT (AccessToken + RefreshToken)            |
| 문서화     | SpringDoc OpenAPI (Swagger) – 적용 완료      |

DB 스키마

```sql
CREATE TABLE todo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,         -- 해당 todo의 고유값
    content VARCHAR(1000) NOT NULL,               -- todo의 내용
    complete VARCHAR(50) NOT NULL,                -- todo의 완료여부
    important VARCHAR(50) NOT NULL,               -- todo의 중요도
    dueDate DATETIME NOT NULL,                    -- 기한 (기본값은 등록일 기준)
    createdAt DATETIME NOT NULL,                  -- 등록일
    modifiedAt DATETIME NOT NULL                  -- 수정일
);

 

CREATE TABLE member (
    sno BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 해당 member의 고유값
    userId VARCHAR(50) NOT NULL,                  -- 유저 ID
    password VARCHAR(100) NOT NULL,               -- 유저 PW
    nickname VARCHAR(100) NOT NULL                -- 유저 별칭
);
```


## 주요 기능

### 회원
- 회원가입 (`POST /api/member/signup`)
- 로그인 (`POST /api/member/login`) – Access / Refresh 토큰 발급
- 토큰 재발급 (`POST /api/member/refresh`)

### 할 일 (Todo)
- 등록 (`POST /api/todos`)
- 단건 조회 (`GET /api/todos/{id}`)
- 목록 조회 (`GET /api/todos`)  
  └ 정렬(`sort=createdAt,desc`), 검색(`title`, `status`) 등 Querydsl 기반 동적 조건 검색 지원  
- 수정 (`PUT /api/todos/{id}`)
- 삭제 (`DELETE /api/todos/{id}`)

> 모든 ToDo API는 JWT 인증 필요 (Authorization 헤더에 Bearer 토큰 포함)


## JWT 인증 구조

- 로그인 시 AccessToken(30분), RefreshToken(7일) 발급
- AccessToken 만료 시 RefreshToken으로 토큰 재발급 가능
- 토큰은 HTTP Header에 `Authorization: Bearer {token}` 형식으로 전달
- RefreshToken은 DB에 저장되며, 탈퇴 또는 재로그인 시 제거

## 📐 프로젝트 구조

com.najung.todo
├── config # 보안 설정 (JWT 필터, Security 설정 등)
├── controller # REST API 컨트롤러
├── domain # JPA 엔티티
├── dto # 요청/응답 DTO
├── repository # JPA, Querydsl Repository
├── service # 비즈니스 로직 처리
├── security # JWT 인증 로직, 필터, 토큰 유틸
└── util # 공통 유틸리티



##  API 문서 (Swagger)

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- 요청/응답 DTO 자동 문서화
- 실행 환경에서 바로 API 테스트 가능


## 실행 방법

```bash
# 프로젝트 클론
git clone https://github.com/NaJung5/todo.git
cd todo

# 의존성 설치 및 빌드
./gradlew clean build

# 애플리케이션 실행
java -jar build/libs/todo-0.0.1-SNAPSHOT.jar
기본 설정은 application.yml 참고


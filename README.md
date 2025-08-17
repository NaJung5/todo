# ToDo API 프로젝트

Spring Boot 기반 ToDo 관리 API 프로젝트입니다.
JWT 기반 인증 구조와 Querydsl 조건 검색을 중심으로 실무 수준의 백엔드 설계를 목표로 했으며
동기·비동기·논블로킹·동시성 제어까지 포함해 다양한 처리 방식을 검증했습니다.

## 기술 스택

| 구분       | 내용                                         |
|------------|----------------------------------------------|
| Language   | Java 17                                      |
| Framework  | Spring Boot 3.x, Spring Security             |
| Build Tool | Gradle                                       |
| DB         | MariaDB/MySQL (로컬/운영 환경) |
| ORM        | Spring Data JPA + Querydsl                   |
| 인증 방식  | JWT (AccessToken + RefreshToken)            |
| 문서화     | SpringDoc OpenAPI (Swagger) – 적용 완료      |

## DB 스키마

```sql

CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL UNIQUE,
    user_password VARCHAR(255) NOT NULL,
    mail VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE todo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    member_id BIGINT,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    token TEXT NOT NULL,
    expiry_date DATETIME NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
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
### 처리 방식 검증
 - 동기 처리: 기본 ToDo 검색 API (searchTodo)
 - 비동기 처리: searchAsync, Timeout 테스트 적용
 - 논블로킹 처리: Reactor Mono 기반 API 제공
 - 레이스 컨디션 제어: Optimistic Lock 기반 테스트로 데이터 정합성 검증
 - 동시성 검증: 멀티스레드 환경 테스트 코드 작성

## JWT 인증 구조
- 로그인 시 AccessToken(30분), RefreshToken(7일) 발급
- AccessToken 만료 시 RefreshToken으로 토큰 재발급 가능
- 토큰은 HTTP Header에 `Authorization: Bearer {token}` 형식으로 전달
- RefreshToken은 DB에 저장되며, 탈퇴 또는 재로그인 시 제거

## 프로젝트 구조

```text
com.najung.todo
├── config         # 보안 설정 (JWT 필터, Security 설정 등)
├── controller     # REST API 컨트롤러
├── domain         # JPA 엔티티
├── dto            # 요청/응답 DTO
├── repository     # JPA, Querydsl Repository
├── service        # 비즈니스 로직 처리
├── security       # JWT 인증 로직, 필터, 토큰 유틸
└── util           # 공통 유틸리티




##  API 문서 (Swagger)

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- 요청/응답 DTO 자동 문서화
- 실행 환경에서 바로 API 테스트 가능


## 실행 방법

```bash
# 프로젝트 클론
git clone https://github.com/NaJung5/todo.git
cd todo

## 테스트 실행
./gradlew test

> 모든 단위/통합 테스트에는 동기, 비동기, 논블로킹, 동시성 검증이 포함되어 있습니다.


# 의존성 설치 및 빌드
./gradlew clean build

# 애플리케이션 실행
java -jar build/libs/todo-0.0.1-SNAPSHOT.jar
기본 설정은 application.yml 참고


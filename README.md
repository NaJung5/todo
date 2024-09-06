할일 (ToDo) API 입니다.

1. 유저가 해야할일(ToDo)를 등록 할 수 있습니다. 회원만 이용이 가능하다 가정 후 제작하였습니다.
2.  JDK17 버전 및 MySql이 필요합니다.
3.  테스트 환경에 맞춰 applicaton.yml에 있는 username과 password 수정이 필요합니다.
4.  Spring Boot: 빠른 개발 및 초기 설정 최소화를 위해 사용하였습니다.
5.  Spring Data JPA: 간결하고 직관적인 데이터베이스 작업을 위해 사용하였습니다.
6.  Swagger: RESTful API 문서화를 진행하였습니다.

DB 스키마
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


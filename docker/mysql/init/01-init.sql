-- Health Challenge 데이터베이스 초기화 스크립트

-- 데이터베이스 생성 (이미 docker-compose에서 생성됨)
-- CREATE DATABASE IF NOT EXISTS health_challenge;
-- USE health_challenge;

-- 문자셋 설정
ALTER DATABASE health_challenge CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- root 사용자 권한 설정 (모든 호스트에서 접근 가능)
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- 사용자 권한 설정
GRANT ALL PRIVILEGES ON health_challenge.* TO 'yong311'@'%';
FLUSH PRIVILEGES;

-- 테이블은 JPA가 자동으로 생성하므로 여기서는 기본 설정만 수행

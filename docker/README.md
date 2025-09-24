# Health Challenge Docker Compose 설정

## 서비스 구성
- **MySQL 8.0**: 메인 데이터베이스
- **Redis 7**: 토큰 블랙리스트 캐시
- **phpMyAdmin**: MySQL 관리 도구 (선택사항)
- **Redis Commander**: Redis 관리 도구 (선택사항)

## 사용 방법

### 1. Docker Compose 실행
```bash
# 백그라운드에서 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 서비스 중지
docker-compose down
```

### 2. 환경변수 설정
애플리케이션 실행 전에 다음 환경변수를 설정하세요:

```bash
# 데이터베이스 설정
export DB_URL="jdbc:mysql://localhost:3306/health_challenge?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DB_USERNAME="yong311"
export DB_PASSWORD="yong311"

# Redis 설정
export REDIS_HOST="localhost"
export REDIS_PORT="6379"
export REDIS_PASSWORD="redisyong"
export REDIS_DATABASE="health"

# JWT 설정
export JWT_SECRET="c2VjcmV0LXNlY3JldC1zZWNyZXQtc2VjcmV0LXNlY3JldC1zZWNyZXQtc2VjcmV0"
```

### 3. 접속 정보

#### 애플리케이션
- **Spring Boot API**: http://localhost:8083
  - 회원가입: `POST /api/auth/signup`
  - 로그인: `POST /api/auth/login`
  - 토큰 갱신: `POST /api/auth/refresh`
  - 로그아웃: `POST /api/auth/signout`
  - 전체 로그아웃: `POST /api/auth/signout-all`

#### 데이터베이스 관리
- **MySQL**: localhost:3306
  - 사용자: yong311
  - 비밀번호: yong311
  - 데이터베이스: health_challenge

- **phpMyAdmin**: http://localhost:8081
  - 사용자: yong311
  - 비밀번호: yong311

#### Redis 관리
- **Redis**: localhost:6379
  - 비밀번호: redisyong
  - 데이터베이스: health

- **Redis Commander**: http://localhost:8082
  - 자동으로 Redis에 연결됨

#### 포트 정보
| 서비스 | 포트 | 설명 |
|--------|------|------|
| Spring Boot | 8083 | REST API 서버 |
| MySQL | 3306 | 데이터베이스 |
| Redis | 6379 | 캐시/세션 저장소 |
| phpMyAdmin | 8081 | MySQL 웹 관리 도구 |
| Redis Commander | 8082 | Redis 웹 관리 도구 |

### 4. 데이터 영속성
- MySQL 데이터: `mysql_data` 볼륨에 저장
- Redis 데이터: `redis_data` 볼륨에 저장

### 5. 개발용 명령어
```bash
# 특정 서비스만 실행
docker-compose up mysql redis

# 서비스 재시작
docker-compose restart mysql

# 볼륨 삭제 (데이터 초기화)
docker-compose down -v
```

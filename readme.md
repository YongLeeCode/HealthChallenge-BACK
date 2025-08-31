# Health Mate
>요즘은 다양한 헬스를 도와주는 앱을 찾을 수 있었다.
기본적으로 나는 하던 것을 꾸준히 하는 편이다.
> 하지만 그러다보니 내가 아는 수준에서 하게 되고, 굳이 찾아보지 않았던 나는 똑같은 것만 반복하게 되었다.
> 분명 다른 앱에서 좋은 정보를 찾을 수 있지만, 여러 조건들 속에서 어떻게 하면 나한테 최적의 운동을 할지 고민하게 되었다.

## 이 웹앱에서 지켜야할 조건
1. 타이머는 꼭 있어야한다.
   1. 단순히 무슨 운동을 해야할지 알려주는 것은 필요없다고 생각한다.
   2. 주로 나는 1분 운동하고 30초 쉬며 반복 운동을 하여 5세트 정도 진행한다.
   3. 매번 타이머에 시간 체크하는 것이 아닌, 진동 혹은 알림으로 알려주는 기능을 원한다.
2. 현재 조건에 맞는 운동 추천
   1. 몸 컨디션, 최근 했던 운동, 운동할 수 있는 시간, 집중적으로 키우고 싶은 운동
   2. 운동하는 그 상황마다 조건이 다를 수 밖에 없다.
   3. 그때마다 최적의 운동 혹은 추천할만한 운동 루틴을 가져와서 보고 싶다.
3. 체크 리스트
   1. 추천했던 운동을 완전히 끝냈을 수도 일부만 성공했을 수 있다.
   2. 얼마나 성공했는지 체크를 할 수 있게 한다.
   3. 완료했을 때 지표를 보여주고, 나의 변화를 통계로 보여주는 것도 좋을 것 같다.
4. 랭킹
   1. 첼린지를 만들어, 내가 어느 정도 수준인지 알 수 있으면 재밌겠다. 
4. 성취도
   1. 이 운동을 하고 보상을 집어 넣어 원하는 보상을 어느정도까지 얻어도 되는지 적으면 좋을 것 같다.
   2. 통계와 지표를 통해 나의 몸의 변화를 체크하는 것도 좋을 것 같다.


# 필요한 기능
1. 타이머
    - 프론트 측에서 만들 예정
    - '+'버튼으로 여러 타이머를 만들 수 있다.
    - 진동 혹은 알림이 가능해야한다.
2. AI
   - input: 
     - 최근 했던 운동 및 시간, 원하는 운동, 운동 가능한 시간, 사용 가능한 기구
   - output:
     - input의 조건에 맞는 운동 추천(단, 서버에 존재하는 운동만 output 가능 - 이유: 사진 혹은 짧은 영상도 같이 넣기 위해)
3. 데이터 저장
4. 운동 루틴 관리
    - 내가 선호하는 운동 추가 및 편집
5. 사용자 관리 및 개인화
    - 로그인/회원가입 (소셜 로그인 포함)
    - 개인 프로필 (나이, 성별, 키, 몸무게 → 칼로리 소모량 계산 등)
    - 개인 운동 기록 히스토리 (캘린더 형태로 내가 언제 운동했는지 한눈에 보기)

# 유저 시나리오

- 웹앱 사이트 진입 
- -> 로그인 
- -> 회원가입 
- -> 로그인 
- -> 메인 페이지 
- -> 나의 프로필 (기본적인 나의 상태 프로필 적기)
- -> 기본 기능 사용(타이머, 운동하고 싶은 몸 부위 선택 후 AI)
- -> 운동 순서 조정 (드래그해서) 
- -> 및 운동 시작 버튼 및 타이머 시작 
- -> 종료 버튼 선택 혹은 운동 루틴 자동 종료
- -> 했던 운동 리스트 체크로 루틴의 퍼센티지 했던 운동 못 했던 운동 저장 버튼 누르기
- -> 요일별 운동 체크


# 기술 스택
## ⚙️ Frontend
- Framework/Library
    - React (Next.js) : 라우팅, 서버사이드 렌더링(SSR), SEO 고려
- 언어
  - TypeScript : 타입 안정성 확보
- 스타일링
  - Tailwind CSS : 반응형 & 빠른 스타일링
    (추가로 UI 컴포넌트 라이브러리 → MUI, shadcn/ui, NextUI 중 선택)
- 상태 관리
  - Redux Toolkit or Zustand : 타이머, 운동 루틴 상태, 사용자 세션 관리
- 타이머/알림
  - 브라우저의 Web Notifications API or Service Worker (푸시 알림/진동)
- 차트/시각화
  - Recharts or Chart.js : 운동 통계, 성취도 그래프
- 드래그 앤 드롭
  - react-beautiful-dnd or dnd-kit : 운동 순서 조정 기능
- Auth 연동
  - NextAuth.js : 로그인/회원가입, 소셜 로그인(Google, GitHub 등)


## ⚙️ 백엔드 기술 스택

- Framework
  - Spring Boot
- 언어
  - Java 17
- Database
  - MySQL
- ORM
  - Spring Data JPA + Hibernate
- 인증/보안
  - 초기: Spring Security + OAuth2 Client (소셜 로그인: 구글, 카카오, 네이버 등)
  - 확장: 소셜 로그인으로 최초 인증 → 이후 JWT 발급 & 세션 관리 (프론트엔드와 API 통신 편리해짐)
- 데이터 통신
  - REST API (JSON 기반)
- 배포/인프라
  - AWS EC2 (서버)
  - AWS RDS (MySQL)
  - S3 + CloudFront (정적 파일, 이미지/영상)
  - Elastic Beanstalk or ECS (컨테이너 기반 배포)
  - Docker (환경 일관성 유지)


# 고도화 작업
### 1. AI 기반 운동 추천 고도화
- 운동 최적화 알고리즘
  - 사용자의 피로도, 최근 운동 기록, 부위별 집중도를 고려한 맞춤형 루틴 자동 생성
- 머신러닝 적용
  - 사용자의 운동 성공률/패턴 데이터를 학습 → 추천 정확도 향상
  - 예: “최근 팔 근육 운동은 성공률 낮음 → 세트 수/휴식 시간 최적화”

### 2. 데이터 저장 & 시각화 고도화
- 개인 통계
  - 월별/주별 운동 성공률, 소모 칼로리, 특정 부위 성장률
  - 시각화: Recharts / D3.js / Chart.js
- 운동 기록 히스토리 강화
  - 캘린더 + 통계 + 개인 목표 대비 달성률 표시
- 알고리즘 기반 목표 설정
  - 목표 달성에 맞춰 루틴 자동 조정


### 3. 인프라/백엔드 고도화
- JWT 기반 세션 + OAuth 통합
- AWS S3 + CDN 최적화 → 이미지/영상 로딩 빠르게
- AI 추천 서비스 분리
  - 별도 마이크로서비스로 구성 → API 호출 방식으로 프론트와 통신
- 배포/자동화
  - CI/CD → AWS CodePipeline, GitHub Actions

## 운동 기록 설계 요약

### 배경과 목표
- 최근 어떤 운동을 했고, 어느 정도 강도와 시간을 들였는지 한눈에 보여주는 지표가 필요함
- 해당 지표를 바탕으로 다음 운동 추천에 활용
- 원시 로그는 상세하게 남기되, 조회/추천은 날짜 단위 요약을 사용해 단순하고 빠르게 제공

### 설계 개요
- 원시 로그 테이블: `ExerciseRecord` (운동별 상세 기록)
- 일일 집계 테이블: `ExerciseDailyRecord` (사용자×날짜 1행)
- 조회/추천은 주로 `ExerciseDailyRecord`를 사용하고, 필요 시 `ExerciseRecord`로 드릴다운

### 엔티티 주요 컬럼
- ExerciseRecord (원시 로그)
  - user, exercise, performedAt(운동 시각)
  - durationSeconds(운동 시간), reps, sets, notes

- ExerciseDailyRecord (일일 집계)
  - user, date(운동한 날짜, 고유키: user+date)
  - totalDurationSeconds(총 운동 시간), totalSets(총 세트)
  - perceivedDifficulty(체감 난이도), satisfaction(만족도)
  - representativeExercise(대표 운동, 선택), notes

### API 엔드포인트
- ExerciseRecord
  - POST `/exercise-records`
  - PUT `/exercise-records/{id}`
  - DELETE `/exercise-records/{id}`
  - GET `/exercise-records/{id}`
  - GET `/exercise-records/users/{userId}`
  - GET `/exercise-records/users/{userId}/date?date=YYYY-MM-DD`

- ExerciseDailyRecord
  - POST `/exercise-daily-records` (upsert, 같은 user+date면 업데이트)
  - PUT `/exercise-daily-records/{id}`
  - DELETE `/exercise-daily-records/{id}`
  - GET `/exercise-daily-records/{id}`
  - GET `/exercise-daily-records/users/{userId}/date?date=YYYY-MM-DD`
  - GET `/exercise-daily-records/users/{userId}`

### 선택한 구조의 이유
- 기록은 많이 쌓이므로 집계 테이블로 대시보드/추천 쿼리를 단순화
- 추천 로직이 필요로 하는 핵심 지표(난이도/만족도/시간)를 날짜 단위로 바로 제공
- 대표 운동을 두어 “무엇을 했는지”의 맥락 유지, 필요 시 원시 로그로 세부 분석 가능

### 확장 아이디어
- 유효성 검증(예: 난이도 1~10, 만족도 1~5 범위)
- 총 볼륨(세트×반복×중량) 등 추가 지표
- `ExerciseRecord` 저장 시 해당 날짜의 `ExerciseDailyRecord` 자동 갱신(도메인 이벤트)

## 도메인 개요

### 현재 도메인
- **Auth**: 로그인/회원가입, JWT 발급/검증
  - 엔티티: 없음(세션 무상태), `User` 참조
  - 주요: `/auth/login`, `/auth/signup`, 토큰 필터

- **User**: 사용자 기본 정보 및 권한
  - 엔티티: `User`, `UserRole`
  - 주요: 사용자 조회/관리(추후 프로필 확장)

- **Exercise**: 운동 사전(메타데이터)
  - 엔티티: `Exercise`, `ExerciseDifficulty`
  - 주요: `/exercises` CRUD, 추천 시 참조용 카탈로그

- **Preference**: 사용자 선호 설정
  - 엔티티: `Preference`, `PreferenceEnum`
  - 주요: `/preferences` CRUD, 추천 가중치 입력

- **ExerciseRecord**: 운동별 원시 기록 로그
  - 엔티티: `ExerciseRecord`
  - 주요: `/exercise-records` CRUD + 사용자/날짜별 조회

- **ExerciseDailyRecord**: 사용자×날짜 집계 기록
  - 엔티티: `ExerciseDailyRecord`
  - 주요: `/exercise-daily-records` upsert/조회, 대시보드·추천용 핵심 지표

### 예정/확장 도메인
- **Routine**: 사용자 루틴(운동 목록, 순서, 세트/휴식 템플릿)
  - 리소스: 루틴, 루틴 아이템, 실행 이력

- **Timer**: 클라이언트 보조용 서버 설정 저장(선택)
  - 리소스: 인터벌/세트 구성 프리셋

- **Recommendation**: 추천/랭킹/챌린지
  - 리소스: 추천 결과 캐시, 챌린지, 랭킹 스냅샷

- **Analytics**: 통계/리포트
  - 리소스: 월간/주간 요약, 목표 대비 달성률

## 권한/인가 정책

### Exercise 생성/수정/삭제
- 생성
  - 로그인 사용자 누구나 생성 가능
  - 생성 시 `creator`(User) 저장
  - 생성한 사용자가 ADMIN이면 `official = true`, 일반 사용자는 `official = false`
- 수정/삭제
  - ADMIN: 모든 `Exercise` 수정/삭제 가능
  - 일반 유저: 자신이 만든(`creator == 본인`) `Exercise`만 수정/삭제 가능
- 응답 필드
  - `official`: 공식 운동 여부
  - `createdByUserId`: 생성자 User ID

추가 확장
- 컨트롤러 레벨 `@PreAuthorize`로 2중 방어 가능
- `official` 변경은 ADMIN만 허용하도록 서비스/컨트롤러에서 검증

## 인증/인가 전략

### 인증 (Authentication)
- **JWT 기반 무상태 인증**
  - `Authorization: Bearer {token}` 헤더로 요청 인증
  - 토큰 유효기간: 1시간 (3600000ms)
  - HS256 알고리즘으로 서명, BASE64 인코딩된 256비트 시크릿 키 사용

- **로그인 플로우**
  - POST `/auth/signup`: 이메일/비밀번호로 회원가입 (기본 `USER` 권한)
  - POST `/auth/signin`: 이메일/비밀번호로 로그인 → JWT 토큰 발급
  - 비밀번호: BCrypt로 해시화하여 저장

- **토큰 구조**
  - `sub`: 사용자 ID (Long)
  - `email`: 사용자 이메일
  - `role`: 사용자 권한 (`USER`, `ADMIN`)
  - `iat`: 발급 시간
  - `exp`: 만료 시간

### 인가 (Authorization)
- **역할 기반 접근 제어 (RBAC)**
  - `USER`: 일반 사용자 (기본 권한)
  - `ADMIN`: 관리자 (모든 권한)

- **보안 필터 체인**
  - `JwtAuthenticationFilter`: JWT 토큰 파싱 및 `SecurityContext` 설정
  - `SecurityConfig`: HTTP 요청별 인증 요구사항 설정
  - `/auth/**`: 인증 없이 접근 가능 (회원가입/로그인)
  - `/actuator/**`: 모니터링 엔드포인트 (인증 없이 접근 가능)
  - 기타 모든 요청: JWT 토큰 필요

- **도메인별 권한 정책**
  - **Exercise**: 생성자 기반 + 역할 기반 혼합 정책
    - 생성: 로그인 사용자 누구나
    - 수정/삭제: ADMIN 또는 본인 소유
  - **User**: 본인 정보만 조회/수정 (ADMIN은 모든 사용자 관리 가능)
  - **Preference**: 본인 설정만 조회/수정
  - **ExerciseRecord/DailyRecord**: 본인 기록만 조회/수정

### 보안 설정
- **Spring Security 구성**
  - CSRF 비활성화 (JWT 기반 API이므로)
  - 세션 무상태 정책 (`STATELESS`)
  - 메서드 레벨 보안 활성화 (`@EnableMethodSecurity`)
  - `@PreAuthorize` 어노테이션으로 컨트롤러 레벨 권한 검증 가능

- **환경 변수**
  - `JWT_SECRET`: BASE64 인코딩된 256비트 시크릿 키
  - `JWT_ACCESS_VALIDITY_MS`: 토큰 유효기간 (기본: 1시간)

### 확장 계획
- **소셜 로그인 통합**
  - OAuth2 Client로 Google, Kakao, Naver 로그인 지원
  - 소셜 인증 후 JWT 토큰 발급으로 통일된 인증 체계

- **토큰 갱신**
  - Refresh Token 발급으로 Access Token 자동 갱신
  - 토큰 블랙리스트 관리 (로그아웃 시)

- **권한 세분화**
  - `MODERATOR`: 운동 검증/승인 권한
  - `PREMIUM_USER`: 고급 기능 접근 권한

- **API 레벨 보안**
  - Rate Limiting (API 호출 제한)
  - Request Logging (보안 감사)
  - IP 화이트리스트/블랙리스트


# ERD v1
[ERD 설계도](https://www.erdcloud.com/d/eAhEK33Aor42jEHGK)
![erd 초기 설계도](../../../../../var/folders/w2/b2zmxb0x39lfzp0_dmpzxfwr0000gn/T/TemporaryItems/NSIRD_screencaptureui_bwwtLl/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202025-08-29%20%EC%98%A4%ED%9B%84%206.40.53.png)

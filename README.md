# LogHub - api

![JVM](https://img.shields.io/badge/jvm-24-orange?style=plastic)
![Gradle](https://img.shields.io/badge/gradle-9-cyan?style=plastic)

![GitHub License](https://img.shields.io/github/license/loghub-me/api?style=plastic&logo=github&color=white)
![GitHub Release](https://img.shields.io/github/release/loghub-me/api?style=plastic&logo=github&color=white)
[![GitHub Actions](https://img.shields.io/github/actions/workflow/status/loghub-me/api/ci.yml?style=plastic&logo=github&label=CI)](https://github.com/loghub-me/api/actions)

#### Repositories

| Repository                                                                                                                                                  | Description                              | Links                                                                                                                                                                                                                                                               |
| ----------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [![GitHub Repo](https://img.shields.io/badge/loghub--me-web-f94949?style=plastic&logo=github)](https://github.com/loghub-me/web)                            | 웹 애플리케이션<br/>Next.js + TypeScript | - [소개](https://github.com/loghub-me/web#소개)<br/>- [구조](https://github.com/loghub-me/web#구조)<br/>- [개발](https://github.com/loghub-me/web#개발)<br/>- [기여](https://github.com/loghub-me/web#기여)                                                         |
| [![GitHub Repo](https://img.shields.io/badge/loghub--me-api-6db240?style=plastic&logo=github)](https://github.com/loghub-me/api)                            | API 서버<br/>Spring Boot + Kotlin        | - [소개](https://github.com/loghub-me/api#소개)<br/>- [구조](https://github.com/loghub-me/api#구조)<br/>- [개발](https://github.com/loghub-me/api#개발)<br/>- [기여](https://github.com/loghub-me/api#기여)                                                         |
| [![GitHub Repo](https://img.shields.io/badge/loghub--me-task--api-aab2ff?style=plastic&logo=github)](https://github.com/loghub-me/task-api)                 | 보조 API 서버<br/>Elysia + TypeScript    | - [소개](https://github.com/loghub-me/task-api#소개)<br/>- [구조](https://github.com/loghub-me/task-api#구조)<br/>- [개발](https://github.com/loghub-me/task-api#개발)<br/>- [기여](https://github.com/loghub-me/task-api#기여)                                     |
| [![GitHub Repo](https://img.shields.io/badge/loghub--me-markdown--render-2d79c7?style=plastic&logo=github)](https://github.com/loghub-me/markdown-renderer) | 마크다운 렌더러                          | - [소개](https://github.com/loghub-me/markdown-renderer#소개)<br/>- [구조](https://github.com/loghub-me/markdown-renderer#구조)<br/>- [개발](https://github.com/loghub-me/markdown-renderer#개발)<br/>- [기여](https://github.com/loghub-me/markdown-renderer#기여) |

## 소개

이 레포지토리는 LogHub API 서버의 소스 코드를 포함합니다. Spring Boot와 Kotlin로 작성되었으며, LogHub의 데이터 관리 및 API 제공을 담당합니다.

## 구조

### `src/main/me.loghub.api`

- `aspect` : AOP 관련 클래스들을 포함합니다. (예: 로킹, trending score 관리)
- `config` : 애플리케이션 설정 관련 클래스들을 포함합니다.
- `constants` : 애플리케이션 전반에서 사용되는 상수들을 포함합니다.
- `controller` : Rest API 컨트롤러들을 포함합니다.
  - `internal` : 내부망 전용 Rest API 컨트롤러들을 포함합니다.
- `dto` : DTO들을 포함합니다.
- `entity` : JPA 엔티티 클래스를 포함합니다.
- `exception` : 커스텀 예외 클래스들을 포함합니다.
- `filter` : 서블릿 필터들을 포함합니다.
- `handler` : 인증 및 예외 처리 핸들러들을 포함합니다.
- `lib` : 외부 라이브러리의 확장을 포함합니다.
- `mapper` : DTO 매퍼들을 포함합니다.
- `proxy` : 외부 API에 접근하기 위한 OpenFeign 클라이언트들을 포함합니다.
- `repository` : JPA 리포지토리들을 포함합니다.
- `service` : 서비스 클래스들을 포함합니다.
- `util` : 다양한 유틸리티 클래스들을 포함합니다.
- `worker` : 백그라운드 작업 관련 클래스를 포함합니다.

### `src/test/me.loghub.api`

> [!NOTE]
> 이 프로젝트의 테스트는 모킹 없는 `@SpringBootTest` 기반의 통합 테스트로 작성되어 있습니다. API 단위로 실제 데이터베이스와 연동하여 테스트합니다. `docker-compose.test.yml` 파일을 활용하여 테스트용 데이터베이스 컨테이너를 실행할 수 있습니다.

- `controller/` : 컨트롤러 테스트 클래스를 포함합니다.
  - `BaseControllerTest.kt` : 공통 설정 및 유틸리티 메서드를 포함하는 베이스 테스트 클래스입니다.

### `src/main/resources`

- `application.yml` : 애플리케이션 설정 파일입니다. (전역)
- `application-dev.yml` : 개발 환경 애플리케이션 설정 파일입니다.
- `application-prod.yml` : 운영 환경 애플리케이션 설정 파일입니다.
- `application-test.yml` : 테스트 환경 애플리케이션 설정 파일입니다.
- `database/` : 데이터베이스 초기화 및 테스트 데이터를 포함합니다.
- `META-INF/`
  - `services/org.hibernate.boot.model.FunctionContributor` : Hibernate 커스텀 함수 등록 파일입니다. (PGroonga 관련)

## 개발

### 개발 환경

- GraalVM v24
- Gradle v9

### 의존성 프로젝트 세팅

> [!IMPORTANT]
> 이 프로젝트는 [loghub-me/postgres](https://github.com/loghub-me/postgres), [loghub-me/task-api](https://github.com/loghub-me/task-api), [redis](https://hub.docker.com/_/redis) 프로젝트에 의존합니다. 해당 프로젝트를 로컬 환경에서 먼저 실행해주세요. 각 프로젝트의 Dockerfile을 활용하여 로컬에서 컨테이너로 실행할 수 있습니다.

```sh
$ git clone git@github.com:loghub-me/postgres.git
# https://github.com/loghub-me/postgres#개발 참고
$ git clone git@github.com:loghub-me/task-api.git
# https://github.com/loghub-me/task-api#개발 참고
```

### 요구 환경 변수

|          변수명           | 설명                           | 예시                                       |
| :-----------------------: | ------------------------------ | ------------------------------------------ |
|        `APP_NAME`         | 애플리케이션 이름              | `loghub-me-api`                            |
|   `ACCESS_TOKEN_SECRET`   | 엑세스 토큰 비밀키             | 비밀키 생성 명령어: `openssl rand -hex 32` |
|       `ASSETS_HOST`       | 에셋 호스트                    | `https://assets.loghub.me`                 |
|      `CLIENT_DOMAIN`      | 클라이언트 도메인              | `localhost`                                |
|       `CLIENT_HOST`       | 클라이언트 호스트              | `http://localhost:3000`                    |
|         `DB_HOST`         | 데이터베이스 호스트            | `localhost`                                |
|         `DB_PORT`         | 데이터베이스 포트              | `5432`                                     |
|         `DB_NAME`         | 데이터베이스 이름              | `your_db`                                  |
|       `DB_USERNAME`       | 데이터베이스 사용자 이름       | `your_username`                            |
|       `DB_PASSWORD`       | 데이터베이스 비밀번호          | `your_password`                            |
|   `DISCORD_WEBHOOK_URL`   | 디스코드 웹훅 URL              | `https://discord.com/api/webhooks/...`     |
|    `GITHUB_CLIENT_ID`     | 깃허브 OAuth 클라이언트 ID     | `your_github_client_id`                    |
|  `GITHUB_CLIENT_SECRET`   | 깃허브 OAuth 클라이언트 시크릿 | `your_github_client_secret`                |
|    `GOOGLE_CLIENT_ID`     | 구글 OAuth 클라이언트 ID       | `your_google_client_id`                    |
|  `GOOGLE_CLIENT_SECRET`   | 구글 OAuth 클라이언트 시크릿   | `your_google_client_secret`                |
|     `OPENAI_API_KEY`      | OpenAI API 키                  | `your_openai_api_key`                      |
|       `REDIS_HOST`        | 레디스 호스트                  | `localhost`                                |
|       `REDIS_PORT`        | 레디스 포트                    | `6379`                                     |
|     `REDIS_PASSWORD`      | 레디스 비밀번호                | `your_redis_password`                      |
|     `RESEND_API_KEY`      | Resend API 키                  | `your_resend_api_key`                      |
|    `RESEND_FROM_EMAIL`    | Resend 발신 이메일             | `noreply@example.org`                      |
|      `TASK_API_HOST`      | task-api 호스트                | `http://localhost:8080`                    |
| `TASK_API_INTERVAL_TOKEN` | task-api 인증 토큰             | `your_task_api_interval_token`             |
|       `SERVER_PORT`       | 서버 포트                      | `8080`                                     |
|     `MANAGEMENT_PORT`     | 관리 포트                      | `8081`                                     |

### 설치 및 실행

> [!NOTE]
> IntelliJ IDEA 개발 환경을 권장합니니다.

경우에 따라 Active Profile을 `dev` 또는 `test`로 설정하고 애플리케이션을 실행해주세요.

### 도커 이미지 빌드 및 실행

#### 빌드

```sh
$ docker build -t loghub-me/api:<version> .
```

#### 실행

> [!WARNING]  
> [loghub-me/task-api](https://github.com/loghub-me/task-api#개발) 프로젝트의 도커 이미지도 필요합니다. 해당 프로젝트에서 도커 이미지를 빌드할 수 있습니다.

> [!WARNING]  
> `.env.postgres`, `.env.redis`, `.env.task-api`, `.env.api` 파일을 작성하여 환경 변수를 설정해야 합니다.

```yml
# docker-compose.yml
services:
  loghub-me-postgres:
    container_name: loghub-me-postgres
    image: ghcr.io/loghub-me/postgres:0.2.0 # change version if needed
    shm_size: 128mb
    ports:
      - "5432:5432/tcp"
    env_file: .env.postgres
    command: ["postgres", "-c", "timezone=Asia/Seoul"]
  loghub-me-redis:
    container_name: loghub-me-redis
    image: redis:latest
    ports:
      - "6379:6379/tcp"
  loghub-me-task-api:
    container_name: loghub-me-task-api
    image: loghub-me/task-api:<version>
    ports:
      - "8082:8080/tcp"
    env_file: .env.task-api
  loghub-me-api:
    container_name: loghub-me-api
    image: ghcr.io/loghub-me/api:<version>
    ports:
      - "8080:8080/tcp"
    env_file: .env.api
    depends_on:
      - loghub-me-postgres
      - loghub-me-redis
      - loghub-me-task-api
```

```sh
$ docker-compose up -d
```

## 기여

기여는 언제나 환영합니다! 버그 리포트, 기능 제안, PR 등 다양한 방법으로 기여할 수 있습니다. 자세한 내용은 [CONTRIBUTING.md](CONTRIBUTING.md)를 참고해주세요.

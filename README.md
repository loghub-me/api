# LogHub - api

![jvm](https://img.shields.io/badge/graalvm-21-orange?style=plastic)
![gradle](https://img.shields.io/badge/gradle-8+-cyan?style=plastic)
![GitHub License](https://img.shields.io/github/license/loghub-me/api?style=plastic)
![GitHub License](https://img.shields.io/github/license/loghub-me/api?style=plastic&logo=github&color=lightgray)
![GitHub Tag](https://img.shields.io/github/tag/loghub-me/api?style=plastic&logo=github&color=lightgray)
[![GitHub Actions](https://img.shields.io/github/actions/workflow/status/loghub-me/api/ci.yml?style=plastic&logo=github&label=CI)](https://github.com/loghub-me/api/actions)

#### Repositories

[![GitHub Repo](https://img.shields.io/badge/GitHub-Web-f94949?style=plastic&logo=github)](https://github.com/loghub-me/web)
[![GitHub Repo](https://img.shields.io/badge/GitHub-API-6db240?style=plastic&logo=github)](https://github.com/loghub-me/api)
[![GitHub Repo](https://img.shields.io/badge/GitHub-Task_API-aab2ff?style=plastic&logo=github)](https://github.com/loghub-me/task-api)
[![GitHub Repo](https://img.shields.io/badge/GitHub-Markdown_Renderer-2d79c7?style=plastic&logo=github)](https://github.com/loghub-me/markdown-renderer)

## 개발 환경 설정

### 개발 환경

- JDK 21 이상
- Gradle 8 이상

### 환경 변수

|            변수명            | 설명                  | 예시                                     |
|:-------------------------:|---------------------|----------------------------------------|
|        `APP_NAME`         | 애플리케이션 이름           | `loghub-me-api`                        |
|   `ACCESS_TOKEN_SECRET`   | 엑세스 토큰 비밀키          | 비밀키 생성 명령어: `openssl rand -hex 3`      |
|       `ASSETS_HOST`       | 에셋 호스트              | `https://assets.loghub.me`             |
|      `CLIENT_DOMAIN`      | 클라이언트 도메인           | `localhost`                            |
|       `CLIENT_HOST`       | 클라이언트 호스트           | `http://localhost:3000`                |
|         `DB_HOST`         | 데이터베이스 호스트          | `localhost`                            |
|         `DB_PORT`         | 데이터베이스 포트           | `5432`                                 |
|         `DB_NAME`         | 데이터베이스 이름           | `your_db`                              |
|       `DB_USERNAME`       | 데이터베이스 사용자 이름       | `your_username`                        |
|       `DB_PASSWORD`       | 데이터베이스 비밀번호         | `your_password`                        |
|   `DISCORD_WEBHOOK_URL`   | 디스코드 웹훅 URL         | `https://discord.com/api/webhooks/...` |
|    `GITHUB_CLIENT_ID`     | 깃허브 OAuth 클라이언트 ID  | `your_github_client_id`                |
|  `GITHUB_CLIENT_SECRET`   | 깃허브 OAuth 클라이언트 시크릿 | `your_github_client_secret`            |
|    `GOOGLE_CLIENT_ID`     | 구글 OAuth 클라이언트 ID   | `your_google_client_id`                |
|  `GOOGLE_CLIENT_SECRET`   | 구글 OAuth 클라이언트 시크릿  | `your_google_client_secret`            |
|     `OPENAI_API_KEY`      | OpenAI API 키        | `your_openai_api_key`                  |
|       `REDIS_HOST`        | 레디스 호스트             | `localhost`                            |
|       `REDIS_PORT`        | 레디스 포트              | `6379`                                 |
|     `REDIS_PASSWORD`      | 레디스 비밀번호            | `your_redis_password`                  |
|     `RESEND_API_KEY`      | Resend API 키        | `your_resend_api_key`                  |
|    `RESEND_FROM_EMAIL`    | Resend 발신 이메일       | `noreply@example.org`                  |
|      `TASK_API_HOST`      | task-api 호스트        | `http://localhost:8080`                |
| `TASK_API_INTERVAL_TOKEN` | task-api 인증 토큰      | `your_task_api_interval_token`         |
|       `SERVER_PORT`       | 서버 포트               | `8080`                                 |
|     `MANAGEMENT_PORT`     | 관리 포트               | `8081`                                 |

### 의존성 프로젝트

> [!IMPORTANT]
> 이 프로젝트는 [task-api](https://github.com/loghub-me/task-api) 프로젝트에 의존합니다. 해당 프로젝트를 Docker 또는 로컬 환경에서 먼저 실행해주세요.

`docker-compose.yml`

```yaml
services:
  loghub-me-task-api:
    container_name: loghub-me-task-api
    image: ghcr.io/loghub-me/task-api:x.x.x # 필요한 버전으로 교체
    ports:
      - "8082:8082"
    env_file: .env.task-api
  loghub-me-postgres:
    container_name: loghub-me-postgres
    image: ghcr.io/loghub-me/postgres:0.1.0 # from groonga/pgroonga:4.0.4-alpine-18
    shm_size: 128mb
    env_file: .env.postgres
    restart: always
  loghub-me-redis:
    container_name: loghub-me-redis
    image: redis:latest
    restart: always
```

### 실행

경우에 따라 Active Profile을 `dev` 또는 `test`로 설정하고 애플리케이션을 실행해주세요.
TRUNCATE TABLE public.articles RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.article_topics RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.series RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.questions RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.topics RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.users RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.search_synonyms RESTART IDENTITY CASCADE;

INSERT INTO public.users(email, username, nickname, role)
VALUES ('admin@loghub.me', 'admin', '관리자', 'ADMIN'),
       ('bot@loghub.me', 'bot', '봇', 'BOT'),
       ('test1@test.com', 'test1', '테스트계정1', 'MEMBER'),
       ('test2@test.com', 'test2', '테스트계정2', 'MEMBER');

INSERT INTO public.topics(slug, name, description)
VALUES ('c', 'C', '저수준 메모리 제어가 가능한 시스템 프로그래밍 언어. 다만 포인터와 친해지지 않으면 금방 멘탈이 나간다.'),
       ('cpp', 'C++', 'C에 객체지향과 다양한 기능을 추가한 언어. 배우는 건 쉽지만 마스터하는 건 평생 프로젝트다.'),
       ('csharp', 'C#', '마이크로소프트의 범용 프로그래밍 언어로, 게임부터 엔터프라이즈까지 커버 가능. 자바와 비슷하지만 좀 더 세련됐다.'),
       ('deno', 'Deno', 'Node.js 창시자가 만든 차세대 런타임. 보안이 강화됐지만, 이름 때문에 도마뱀 생각이 난다.'),
       ('bun', 'bun', '빠른 실행 속도를 목표로 만든 JavaScript/TypeScript 런타임. 이름은 귀여운데 벤치마크는 무섭다.'),
       ('docker', 'Docker', '컨테이너 기술의 표준. “그냥 컨테이너에 넣으세요”가 모든 문제의 해답이 된다.'),
       ('github', 'GitHub', '소스 코드 호스팅과 협업의 대표 플랫폼. 개발자 SNS라고 부를 만하다.'),
       ('gitlab', 'GitLab', '깃허브와 유사하지만, 자체 호스팅과 CI/CD 기능이 강점. 깃허브보다 업무적이다.'),
       ('bitbucket', 'Bitbucket', '아틀라시안의 코드 호스팅 서비스. Jira와 친구 먹는 걸 좋아한다.'),
       ('google', 'Google', '검색, 광고, 클라우드, AI까지 다 하는 글로벌 IT 기업. 심지어 당신보다 당신을 더 잘 안다.'),
       ('java', 'Java', '안정성과 이식성이 뛰어난 범용 언어. 한 번 작성하면 어디서든 실행되지만, 디버깅은 또 다른 이야기다.'),
       ('javascript', 'JavaScript', '웹 브라우저의 언어로 시작해, 이제는 서버와 데스크톱까지 점령. 하지만 타입은 여전히 자유분방하다.'),
       ('typescript', 'TypeScript', '자바스크립트에 정적 타입을 더한 언어. 타입 덕분에 안심이 되지만, 타입스크립트가 타입 때문에 스트레스 줄 때도 있다.'),
       ('python', 'Python', '간결한 문법과 방대한 라이브러리로 사랑받는 언어. 다만 공백이 문법인 건 처음엔 당황스럽다.'),
       ('go', 'Go', '구글이 만든 단순하고 빠른 컴파일 언어. 마스코트인 Gopher가 귀여운 건 반칙.'),
       ('rust', 'Rust', '메모리 안전성과 성능을 모두 잡은 언어. 다만 빌드 성공하기 전까지는 머리가 아플 수 있다.'),
       ('php', 'PHP', '웹 개발의 고전 언어. 비판은 많지만 여전히 수많은 사이트가 이걸로 돌아간다.'),
       ('ruby', 'Ruby', '우아한 문법을 가진 언어. 다만 속도 면에서는 다소 여유롭다.'),
       ('swift', 'Swift', '애플의 공식 앱 개발 언어. Objective-C를 잊게 하려는 애플의 야심작.'),
       ('kotlin', 'Kotlin', '안드로이드 공식 지원 언어로, Java보다 간결하고 현대적. 하지만 러시아어 문서가 가끔 튀어나온다.'),
       ('scala', 'Scala', 'JVM 위에서 동작하는 함수형+객체지향 언어. 배우면 멋있어 보이지만 코드가 시를 닮아간다.'),
       ('lua', 'Lua', '경량 스크립트 언어로 게임과 임베디드에서 인기. 짧고 빠르지만 기능은 꼭 필요한 만큼만 있다.'),
       ('haskell', 'Haskell', '순수 함수형 언어의 대표주자. 불순한 부작용은 컴파일이 거부한다.'),
       ('clojure', 'Clojure', 'Lisp 계열의 함수형 언어로 JVM에서 실행. 괄호를 사랑하게 된다.'),
       ('fsharp', 'F#', '마이크로소프트의 함수형 언어. 닷넷 환경에서 함수형 코딩이 하고 싶을 때 쓴다.'),
       ('dart', 'Dart', '구글이 만든 언어로, Flutter 개발에 최적화. Flutter 없으면 존재감이 희미하다.'),
       ('nodejs', 'Node.js', '자바스크립트를 서버로 확장한 런타임. 덕분에 JS 개발자는 전천후가 됐다.'),
       ('nextjs', 'Next.js', 'React 기반의 SSR/SSG 프레임워크. SEO 때문에 쓰기 시작했지만 기능 때문에 계속 쓴다.'),
       ('nuxt', 'Nuxt.js', 'Vue 기반의 SSR/SSG 프레임워크. Next.js와 이름이 비슷해서 헷갈린다.'),
       ('react', 'React', 'UI를 컴포넌트로 쪼개는 라이브러리. 쪼개다 보면 프로젝트보다 폴더 구조가 먼저 완성된다.'),
       ('vue', 'Vue.js', '학습 곡선이 완만한 프론트엔드 프레임워크. 하지만 프로젝트가 커지면 얘기도 달라진다.'),
       ('angular', 'Angular', '구글이 만든 대규모 프레임워크. 처음엔 복잡하지만, 익숙해지면 믿음직하다.'),
       ('svelte', 'Svelte', '컴파일 타임에 불필요한 코드 제거로 가벼운 프레임워크. 런타임 부담이 없어서 개발자도 가벼워진다.'),
       ('react-router', 'React Router', 'React 앱의 페이지 이동을 담당하는 라이브러리. 길찾기는 쉽지만 상태 관리는 별개다.'),
       ('solidjs', 'SolidJS', '빠른 반응성을 제공하는 프레임워크. React와 닮았지만 훨씬 민첩하다.'),
       ('astro', 'Astro', '정적 사이트 생성에 특화된 프레임워크. HTML과 친해지고 싶다면 최고의 선택.'),
       ('spring', 'Spring', '자바 기반의 엔터프라이즈 프레임워크. 잘 쓰면 생산성이 폭발하지만, 설정은 여전히 복잡하다.'),
       ('spring-boot', 'Spring Boot', '스프링 설정을 자동화한 경량 버전. 실행 버튼 누르면 바로 앱이 뜬다.'),
       ('quarkus', 'Quarkus', '클라우드 환경에 최적화된 자바 프레임워크. 부팅 속도가 눈에 띄게 빠르다.'),
       ('express', 'Express.js', 'Node.js의 대표적인 웹 프레임워크. 단순함 덕분에 입문용으로 인기.'),
       ('fastify', 'Fastify', '고성능 Node.js 웹 프레임워크. 이름처럼 진짜 빠르다.'),
       ('nestjs', 'NestJS', 'Node.js에 타입스크립트와 아키텍처를 더한 프레임워크. 스프링의 감성을 자바스크립트로 옮겼다.'),
       ('elysia', 'ElysiaJS', 'Bun 런타임에 최적화된 초경량 프레임워크. 빠르고 미니멀하다.'),
       ('flask', 'Flask', '파이썬의 경량 웹 프레임워크. 가볍지만 필요한 건 확장으로 다 붙일 수 있다.'),
       ('django', 'Django', '파이썬의 풀스택 웹 프레임워크. 기본 제공 기능이 너무 많아 오히려 선택 장애가 올 수 있다.'),
       ('fastapi', 'FastAPI', '파이썬의 현대적이고 빠른 API 서버 프레임워크. 문서 자동화 기능이 개발자를 행복하게 한다.'),
       ('rails', 'Ruby on Rails', 'Ruby 기반의 풀스택 웹 프레임워크. “컨벤션이 설정보다 낫다”의 교과서.'),
       ('laravel', 'Laravel', 'PHP의 모던 풀스택 프레임워크. ORM부터 인증까지 다 챙겨준다.'),
       ('kubernetes', 'Kubernetes', '컨테이너 오케스트레이션의 표준. 배우는 데 시간이 걸리지만 한번 익히면 못 놓는다.'),
       ('helm', 'Helm', '쿠버네티스 애플리케이션 패키지 관리자. 설치는 쉽지만 YAML 지옥은 여전하다.'),
       ('terraform', 'Terraform', '인프라를 코드로 관리하는 도구. 재현성은 높지만, 실수도 재현된다.'),
       ('ansible', 'Ansible', '에이전트 없이 자동화를 구현하는 도구. YAML만 잘 쓰면 강력하다.'),
       ('aws', 'AWS', '세계 1위 클라우드 서비스. 쓰기 쉽지만 청구서를 볼 땐 심장이 쫄깃해진다.'),
       ('azure', 'Azure', '마이크로소프트의 클라우드 플랫폼. 윈도우 환경과 찰떡궁합.'),
       ('gcp', 'Google Cloud', '구글의 클라우드 서비스. AI와 데이터 분석에 강점이 있다.'),
       ('cloudflare', 'Cloudflare', 'CDN과 보안 서비스 제공업체. 전 세계에서 트래픽을 방패처럼 막아준다.'),
       ('vercel', 'Vercel', '프론트엔드 배포에 특화된 클라우드 서비스. Next.js 개발자가 특히 사랑한다.'),
       ('heroku', 'Heroku', '배포 자동화의 원조격 서비스. 하지만 무료 플랜이 사라진 건 아쉽다.'),
       ('supabase', 'Supabase', '오픈소스 Firebase 대안. Postgres 기반이라 SQL이 가능하다.'),
       ('firebase', 'Firebase', '구글의 서버리스 백엔드 플랫폼. 빠른 MVP 제작에 강력하다.'),
       ('mongodb', 'MongoDB', '스키마 없는 NoSQL DB. 자유로워서 좋지만, 설계는 신중해야 한다.'),
       ('postgresql', 'PostgreSQL', '확장성과 안정성이 뛰어난 오픈소스 RDBMS. 웬만한 기능은 다 들어있다.'),
       ('mysql', 'MySQL', '가장 널리 쓰이는 오픈소스 RDBMS. 단, MyISAM은 잊어라.'),
       ('sqlite', 'SQLite', '파일 하나로 끝나는 경량 DB. 로컬 개발과 임베디드에 최적.'),
       ('redis', 'Redis', '인메모리 데이터 저장소. 속도는 빠르지만 전원 나가면 데이터도 같이 나간다.'),
       ('cassandra', 'Cassandra', '대규모 분산형 NoSQL DB. 확장성은 좋지만 초기 설정이 만만치 않다.'),
       ('elasticsearch', 'Elasticsearch', '고성능 검색 및 분석 엔진. 로그 수집의 단짝 친구다.'),
       ('prettier', 'Prettier', '코드 포맷터. 팀의 스타일 전쟁을 종식시킨다.'),
       ('eslint', 'ESLint', '자바스크립트 코드 품질 검사기. 잔소리는 많지만 틀린 말은 안 한다.'),
       ('babel', 'Babel', '최신 자바스크립트를 구형 브라우저가 이해하게 변환한다. 시간여행자 같은 도구다.'),
       ('webpack', 'Webpack', '모듈 번들러의 대명사. 강력하지만 설정은 복잡하다.'),
       ('vite', 'Vite', '빠른 개발 서버와 번들러. “새로고침”을 기다리는 시간이 줄어든다.'),
       ('rollup', 'Rollup', '경량 번들러. 라이브러리 제작에 최적화됐다.'),
       ('storybook', 'Storybook', 'UI 컴포넌트를 시각적으로 관리하는 도구. 개발자들의 디자인 쇼룸이다.'),
       ('jest', 'Jest', '자바스크립트 테스트 프레임워크. 단위 테스트를 사랑하게 만든다.'),
       ('selenium', 'Selenium', '브라우저 자동화 도구. 테스트하다가 진짜 브라우저가 켜지는 걸 볼 수 있다.'),
       ('postman', 'Postman', 'API 테스트와 디버깅 도구. 버튼 몇 번으로 서버를 괴롭힐 수 있다.'),
       ('insomnia', 'Insomnia', '다크 모드 감성이 강한 API 클라이언트. 이름처럼 새벽까지 개발할 때 잘 어울린다.'),
       ('grafana', 'Grafana', '데이터 시각화 플랫폼. 예쁜 대시보드로 모니터링을 즐겁게 한다.'),
       ('prometheus', 'Prometheus', '모니터링과 알림 시스템. 메트릭 수집의 표준이다.'),
       ('apache', 'Apache', '오랜 역사를 가진 웹 서버. 지금도 조용히 많은 곳에서 돌아간다.'),
       ('nginx', 'NGINX', '고성능 웹 서버와 리버스 프록시. 경량이지만 강력하다.');

INSERT INTO public.articles (slug, title, content, writer_id, writer_username, topics_flat, trending_score, star_count,
                             created_at)
VALUES ('k8s-operators', 'Kubernetes 오퍼레이터 패턴 심층 분석',
        E'# Kubernetes 오퍼레이터 패턴 심층 분석\n\n쿠버네티스 환경에서 **오퍼레이터(Operator)** 는 도메인 지식이 담긴 컨트롤러입니다.  \n본 글에서는 다음과 같은 내용을 다룹니다.\n\n## 📑 목차\n1. 오퍼레이터의 등장 배경\n2. CRD(CustomResourceDefinition) 설계 전략\n3. 컨트롤 루프 구현 예시 – Go *controller-runtime*\n4. 배포 시 고려 사항 및 Helm Chart 작성\n5. 실제 운영 경험 공유\n\n### 1. 오퍼레이터의 등장 배경\n컨테이너 워크로드가 복잡해짐에 따라 수작업 운영은 **한계**에 도달했습니다. 오퍼레이터는 이를 자동화하기 위해 설계되었습니다.  \n> *“쿠버네티스의 선언적 API를 확장하여, 사람이 하던 Day‑2 오퍼레이션을 코드로 옮긴다.”*\n\n### 2. CRD 설계 전략\n- **스키마 명세**: `openAPIV3Schema` 로 유효성 검증\n- **버전 관리**: `v1alpha1 → v1beta1 → v1`\n- **예시**\n```yaml\napiVersion: apiextensions.k8s.io/v1\nkind: CustomResourceDefinition\nmetadata:\n  name: databases.example.com\nspec:\n  group: example.com\n  names:\n    plural: databases\n    singular: database\n    kind: Database\n```\n\n### 3. 컨트롤 루프 구현\n```go\nfunc (r *DatabaseReconciler) Reconcile(ctx context.Context, req ctrl.Request) (ctrl.Result, error) {\n    var db v1.Database\n    if err := r.Get(ctx, req.NamespacedName, &db); err != nil {\n        return ctrl.Result{}, client.IgnoreNotFound(err)\n    }\n    // 상태 동기화 로직 ...\n    return ctrl.Result{RequeueAfter: 30 * time.Second}, nil\n}\n```\n\n### 4. 배포 & Helm Chart\n- `kustomize` 로 환경별 Overlay\n- Helm 값: `resources`, `nodeSelector`, `tolerations` 등\n\n### 5. 운영 경험\n| 항목 | 경험 |\n|------|------|\n| 장애 대응 | CRD 스키마 누락으로 인한 reconcile 실패 발생 |\n| 성능 | 1,000 CR 객체 기준, 컨트롤 루프 지연 < 200 ms |\n| 모니터링 | Prometheus `reconcile_duration_seconds` 노출 |\n\n> 🔗 **Reference**  \n> - [Operator SDK 공식 문서](https://sdk.operatorframework.io/)\n',
        3, 'test1', 'kubernetes:Kubernetes', 8, 12, NOW() - INTERVAL '3 day'),

       ('react-suspense', 'React Suspense와 Concurrent Mode 완전 가이드',
        E'# React Suspense & Concurrent Mode\n\nReact 18부터 **Concurrent Features** 가 Stable 로 진입했습니다.\n\n## 주요 키워드\n- `Suspense`\n- `useTransition`\n- `startTransition`\n- `useDeferredValue`\n\n### 예제 – 이미지 Lazy Loading\n```tsx\n<Suspense fallback={<Spinner />}>\n  <ImageGallery />\n</Suspense>\n```\n\n### 성능 벤치마킹\n| 시나리오 | TTI(ms) | FCP(ms) |\n|----------|---------|---------|\n| 기존 fetch | 1,240 | 620 |\n| Suspense | **820** | **410** |\n\n> **Tip:** HTTP 캐시와 `react-query`를 조합하면 캐싱 효율이 극대화됩니다.\n',
        4, 'test2', 'react:React,javascript:JavaScript', 10, 22, NOW() - INTERVAL '2 day'),

       ('nextjs-edge', 'Next.js Edge Runtime 실제 서비스 적용기',
        E'# Next.js Edge Runtime 적용기\n\nVercel이 발표한 **Edge Runtime** 은 `WebAssembly + V8 Isolates` 위에서 동작합니다.\n\n## 아키텍처 다이어그램\n```mermaid\ngraph TD\n  User --> CDN --> Edge(Route Handler) --> Origin(API)\n```\n\n### Latency 비교\n| 위치 | 기존 Lambda(us‑east‑1) | Edge(Seoul POP) |\n|------|-----------------------|-----------------|\n| TTFB | 320 ms | **45 ms** |\n| TTLB | 430 ms | **60 ms** |\n\nEdge Runtime 사용 시 TTFB가 7배 이상 감소했습니다.\n',
        3, 'test1', 'nextjs:Next.js,nodejs:Node.js', 15, 35, NOW() - INTERVAL '1 day'),

       ('typescript-decorators', 'TypeScript 데코레이터 패턴 실전',
        E'# TypeScript 데코레이터 실전\n\n데코레이터는 **메타‑프로그래밍** 의 핵심입니다.\n\n```ts\nfunction Logger(target: any, key: string) {\n  console.log(`${key} 호출됨`)\n}\nclass Service {\n  @Logger\n  fetch() {}\n}\n```\n\n- Babel 7.22 기준, `@babel/plugin-proposal-decorators` Stage 3\n- React 프로젝트에서는 MobX, Class‑Validator와 함께 자주 사용됩니다.\n',
        4, 'test2', 'typescript:TypeScript,javascript:JavaScript', 6, 5, NOW() - INTERVAL '1 day'),

       ('docker-hardening', 'Docker 이미지 하드닝 베스트 프랙티스',
        E'# Docker 이미지 하드닝 Best Practices\n\n1. **멀티‑스테이지 빌드** 로 불필요한 레이어 제거  \n2. `USER app` 지정으로 Root 권한 최소화  \n3. CVE 스캔 – Trivy / Grype\n\n```dockerfile\nFROM node:20-alpine AS builder\nWORKDIR /app\nCOPY . .\nRUN npm ci && npm run build\n\nFROM nginx:1.27-alpine\nCOPY --from=builder /app/dist /usr/share/nginx/html\nUSER 1001:1001\n```\n\n> 실무에서는 이미지 사이즈를 **250 MB → 57 MB** 로 축소했습니다.\n',
        3, 'test1', 'docker:Docker', 4, 7, NOW() - INTERVAL '12 hour');
INSERT INTO public.article_topics (article_id, topic_id)
SELECT a.id, t.id
FROM public.articles a
         JOIN public.topics t ON ((a.slug = 'k8s-operators' AND t.slug = 'kubernetes') OR
                                  (a.slug = 'react-suspense' AND t.slug IN ('react', 'javascript')) OR
                                  (a.slug = 'nextjs-edge' AND t.slug IN ('nextjs', 'nodejs')) OR
                                  (a.slug = 'typescript-decorators' AND t.slug IN ('typescript', 'javascript')) OR
                                  (a.slug = 'docker-hardening' AND t.slug = 'docker'));

INSERT INTO public.series (slug, title, content, writer_id, writer_username, topics_flat, trending_score, star_count,
                           created_at)
VALUES ('k8s-master', 'Kubernetes Mastery',
        E'# Kubernetes Mastery\n\n쿠버네티스의 모든 것을 다루는 심화 시리즈입니다.\n\n- Pod 스케줄링\n- 컨트롤‑플레인 HA\n- 오퍼레이터 개발\n', 3, 'test1',
        'kubernetes:Kubernetes', 9, 18, NOW() - INTERVAL '4 day'),

       ('react-perf', 'React 성능 최적화',
        E'# React Performance Handbook\n\nFPS를 끌어올리는 실전 기법 모음.\n', 4, 'test2', 'react:React', 7, 14,
        NOW() - INTERVAL '3 day'),

       ('ts-advanced', 'TypeScript 고급 테크닉',
        E'# TypeScript Advanced\n\n제네릭, Conditional Types, Template Literal Types 를 심도 있게 다룹니다.\n', 3, 'test1',
        'typescript:TypeScript', 5, 9, NOW() - INTERVAL '2 day');
INSERT INTO public.series_chapters (title, content, sequence, series_id, writer_id)
VALUES ('컨테이너 오케스트레이션의 역사', E'# 1999–2024 컨테이너 오케스트레이션 연대기\n\n컨테이너 기술의 기원부터 쿠버네티스에 이르기까지의 주요 발전 단계를 살펴봅니다.\n\n## 초기 컨테이너 개념(1999–2005)\n- 1999년 FreeBSD Jail 등장\n- 2001년 Solaris Zones 도입\n\n## 경량화와 격리(2006–2012)\n- Linux 네임스페이스 및 cgroups 개발\n- Docker의 부상 (2013)\n\n```bash\ndocker run --rm hello-world\n```\n
## 쿠버네티스 출현(2014–현재)\n- 2014년 Google Borg 논문 발표\n- 2015년 CNCF로 이전 및 확장', 1,
        ( SELECT id FROM public.series WHERE slug = 'k8s-master' ), 3),

       ('쿠버네티스 핵심 컴포넌트', E'# API‑Server, Scheduler, Controller‑Manager 분석\n\n쿠버네티스 아키텍처의 중추적 역할을 하는 세 가지 컴포넌트를 심층 분석합니다.\n\n## API‑Server\n- 클라이언트 요청 처리\n- Etcd와의 통신 방식\n\n## Scheduler\n- 노드 선택 알고리즘 (Least Requested, Balanced Resource Allocation)\n- Scheduler Extender 활용 예시\n
## Controller‑Manager\n- Deployment, StatefulSet 컨트롤 루프\n- Custom Controller 작성 예제\n
```go\nfunc main() {\n  mgr, _ := ctrl.NewManager(ctrl.GetConfigOrDie(), ctrl.Options{});\n  // Controller 등록\n}\n```',
        2,
        ( SELECT id FROM public.series WHERE slug = 'k8s-master' ), 3),

       ('오퍼레이터 패턴 구현', E'# CRD + Controller 패턴 핸즈온\n\n오퍼레이터 패턴을 통해 커스텀 리소스를 정의하고 컨트롤러를 작성하는 실습 예제를 제공합니다.\n\n## CRD 정의\n```yaml\napiVersion: apiextensions.k8s.io/v1\nkind: CustomResourceDefinition\nmetadata:\n  name: foos.example.com\nspec:\n  group: example.com\n  versions:\n  - name: v1\n    served: true\n    storage: true\n  scope: Namespaced\n  names:\n    plural: foos\n    singular: foo\n    kind: Foo\n```\n
## 컨트롤러 스캐폴딩\n- Kubebuilder 사용법\n- Reconcile 로직 작성\n\n## 핸즈온 데모\n1. CRD 적용: `kubectl apply -f crd.yaml`\n2. 컨트롤러 배포: `make deploy`
', 3,
        ( SELECT id FROM public.series WHERE slug = 'k8s-master' ), 3),

       ('렌더링 병목 분석', E'# React Profiler 사용법\n\nReact 애플리케이션 성능 문제를 Profiler를 통해 진단하는 방법을 설명합니다.\n\n## Profiler 도구 활성화\n- `<Profiler>` 컴포넌트 래핑\n- DevTools Profiler 탭 사용\n\n## 주요 메트릭\n- 렌더링 시간(Time), 자바스크립트 사용량\n- 커밋 횟수(Commits)\n
```jsx\n<Profiler id="App" onRender={callback}>\n  <App />\n</Profiler>\n```\n## 문제 사례\n- 불필요한 리렌더링 탐지\n- 해결: `React.memo`, `useCallback`
', 1,
        ( SELECT id FROM public.series WHERE slug = 'react-perf' ), 4),

       ('메모리 최적화', E'# useMemo vs useCallback\n\n메모리 및 성능 최적화를 위해 React 훅을 적절히 사용하는 방법을 비교 분석합니다.\n\n## useMemo\n- 계산 결과 캐싱\n
```jsx\nconst computed = useMemo(() => expensiveCalc(data), [data]);\n```\n
## useCallback\n- 콜백 함수 참조 유지\n
```jsx\nconst handleClick = useCallback(() => onClick(item), [item]);\n```\n
## 사용 시점 비교\n1. 렌더링 비용이 큰 계산 -> useMemo\n2. 자식 컴포넌트에 함수 전달 -> useCallback
', 2,
        ( SELECT id FROM public.series WHERE slug = 'react-perf' ), 4),

       ('Conditional Types 심화', E'# infer 키워드 활용\n\nTypeScript Conditional Types에서 `infer` 키워드를 사용해 타입을 추론하고 재사용하는 기법을 살펴봅니다.\n\n## 기본 Conditional Types\n```ts\ntype IsString<T> = T extends string ? true : false;\n```\n
## infer 활용 예제\n```ts\ntype ReturnType<T> = T extends (...args: any[]) => infer R ? R : any;\n```\n
## 실전 활용\n- 제네릭 유틸리티 타입 정의\n- 복잡한 타입 매핑 사례\n```ts\ntype ElementType<T> = T extends Array<infer U> ? U : T;\n```', 1,
        ( SELECT id FROM public.series WHERE slug = 'ts-advanced' ), 3);
INSERT INTO public.series_topics (series_id, topic_id)
SELECT s.id, t.id
FROM public.series s
         JOIN public.topics t
              ON ((s.slug = 'k8s-master' AND t.slug = 'kubernetes') OR (s.slug = 'react-perf' AND t.slug = 'react') OR
                  (s.slug = 'ts-advanced' AND t.slug = 'typescript'));

INSERT INTO public.questions (slug, title, content, status, solved_at, closed_at, star_count, answer_count,
                              trending_score, writer_id, writer_username, topics_flat, created_at)
VALUES ('async-await-js', 'JavaScript에서 async/await가 성능에 미치는 영향?',
        E'# async/await 성능 질문\n\n대규모 루프 내에서 `async/await` 를 사용할 때 CPU 사용률이 급증합니다.  \n- V8 12.x  \n- Node.js 20\n\n```js\nfor (const item of list) {\n   await heavyTask(item);\n}\n```\n\n병렬 처리를 위한 최적 패턴이 궁금합니다.\n',
        'SOLVED', NOW() - INTERVAL '20 hour', NULL, 5, 2, 11, 3, 'test1', 'javascript:JavaScript',
        NOW() - INTERVAL '21 hour'),

       ('k8s-ingress', 'Ingress Controller 선택 기준은 무엇인가요?',
        E'# Ingress Controller 선택\n\nNginx, Traefik, Kong 중 어떤 기준으로 선택하시나요?', 'OPEN', NULL, NULL, 2, 0, 4, 3, 'test1',
        'kubernetes:Kubernetes', NOW() - INTERVAL '18 hour'),

       ('docker-compose-scale', 'docker-compose scale 사용 시 주의 사항',
        E'# docker-compose scale 관련 질문\n\ndocker-compose v2 환경에서 `compose up --scale` 시 환경변수 충돌 문제가 발생합니다.', 'OPEN',
        NULL, NULL, 1, 1, 2, 3, 'test1', 'docker:Docker', NOW() - INTERVAL '6 hour');
INSERT INTO public.question_topics (question_id, topic_id)
SELECT q.id, t.id
FROM public.questions q
         JOIN public.topics t ON ((q.slug = 'async-await-js' AND t.slug = 'javascript') OR
                                  (q.slug = 'k8s-ingress' AND t.slug = 'kubernetes') OR
                                  (q.slug = 'docker-compose-scale' AND t.slug = 'docker'));
INSERT INTO public.question_answers (title, content, accepted, accepted_at, question_id, writer_id)
VALUES ('병렬 처리 예시 - Promise.all',
        E'`Promise.all` 로 병렬화하면 이벤트 루프가 블로킹되지 않습니다.\n\n```js\nawait Promise.all(list.map(heavyTask));\n```', TRUE,
        NOW() - INTERVAL '19 hour', ( SELECT id FROM public.questions WHERE slug = 'async-await-js' ), 4),

       ('worker_threads 활용',
        E'CPU 바운드 작업은 `worker_threads` 로 분산하세요.', FALSE, NULL,
        ( SELECT id FROM public.questions WHERE slug = 'async-await-js' ), 2),

       ('환경변수 충돌 해결',
        E'`.env` 파일을 서비스별로 분리하면 충돌을 피할 수 있습니다.', FALSE, NULL,
        ( SELECT id FROM public.questions WHERE slug = 'docker-compose-scale' ), 4);
UPDATE public.questions q
SET answer_count = ( SELECT COUNT(*) FROM public.question_answers qa WHERE qa.question_id = q.id );

INSERT INTO public.search_synonyms
VALUES ('k8s', ARRAY ['kubernetes', 'k8s']),
       ('js', ARRAY ['javascript', 'js']);

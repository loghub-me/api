TRUNCATE TABLE public.articles RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.article_topics RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.article_comments RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.series RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.series_chapters RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.series_topics RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.series_reviews RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.questions RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.question_topics RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.question_answers RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.user_stars RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.user_activities RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.search_synonyms RESTART IDENTITY CASCADE;

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
        E'# Ingress Controller 선택\n\nNginx, Traefik, Kong 중 어떤 기준으로 선택하시나요?', 'OPEN', NULL, NULL, 2, 0, 4, 4, 'test2',
        'kubernetes:Kubernetes', NOW() - INTERVAL '18 hour'),

       ('docker-compose-scale', 'docker-compose scale 사용 시 주의 사항',
        E'# docker-compose scale 관련 질문\n\ndocker-compose v2 환경에서 `compose up --scale` 시 환경변수 충돌 문제가 발생합니다.', 'CLOSED',
        NULL, NOW() - INTERVAL '5 hour', 1, 1, 2, 3, 'test1', 'docker:Docker', NOW() - INTERVAL '6 hour');
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

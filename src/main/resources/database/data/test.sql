INSERT INTO public.topics(slug, name, description)
VALUES ('c', 'C', '저수준 메모리 제어가 가능한 시스템 프로그래밍 언어. 포인터 때문에 머리가 아플 수도 있다.'),
       ('cpp', 'C++', 'C에 객체지향과 다양한 기능을 추가한 언어. 배우는 건 쉽지만 마스터는 평생 숙제.'),
       ('csharp', 'C#', '마이크로소프트의 범용 언어. 자바와 친하지만 좀 더 세련된 느낌.'),
       ('java', 'Java', '전 세계 기업에서 사랑받는 안정적인 언어. 쓰레기 수집기도 열심히 일한다.'),
       ('kotlin', 'Kotlin', '모던 자바 대체제. 안드로이드 개발자에게는 친구이자 신이다.'),
       ('scala', 'Scala', '함수형과 객체지향을 한 몸에. 읽기엔 멋있지만 처음 보면 두려움.'),
       ('groovy', 'Groovy', '자바와 친한 스크립트 언어. 코드를 쓰다 보면 리듬감이 느껴진다.'),
       ('python', 'Python', '문법이 친절하고 배우기 쉽다. “들여쓰기”로 세상을 지배한다.'),
       ('ruby', 'Ruby', '웹 개발의 다이아몬드 언어. 깔끔하지만 때론 느리다.'),
       ('perl', 'Perl', '텍스트 처리의 왕. 읽을 땐 눈이 피곤할 수 있다.'),
       ('php', 'PHP', '웹 서버에 친근한 언어. 배워보면 이상하게 중독된다.'),
       ('javascript', 'JavaScript', '웹 브라우저의 생명줄. 어디서든 달려간다.'),
       ('typescript', 'TypeScript', '자바스크립트의 타입 친화 버전. 안전하지만 약간 장난꾸러기.'),
       ('dart', 'Dart', '플러터와 찰떡궁합. 모바일 개발자의 비밀 병기.'),
       ('go', 'Go', '구글의 효율적인 언어. 간단하지만 강력하다. 고(Gopher) 마스코트가 귀엽다.'),
       ('rust', 'Rust', '메모리 안전을 지키는 언어. 포인터 걱정 없이 마음껏 코딩.'),
       ('swift', 'Swift', '애플 생태계의 별. 배우기 쉽지만 맥 없이는 외로움.'),
       ('objective-c', 'Objective-C', '전통적인 애플 언어. 문법이 낯설어도 역사적 가치가 있다.'),
       ('haskell', 'Haskell', '순수 함수형 언어. 머리 굴리다 보면 시간 가는 줄 모른다.'),
       ('elixir', 'Elixir', '분산 시스템 친화적 언어. 채팅 서버 만들기 좋다.'),
       ('erlang', 'Erlang', '전설의 통신 언어. 서버가 죽어도 메시지는 살아있다.'),
       ('clojure', 'Clojure', '리스트와 친한 함수형 언어. 괄호가 친구가 될 수도 있다.'),
       ('r', 'R', '통계와 데이터 분석에 특화. 그래프를 그리면 기분이 좋다.'),
       ('matlab', 'MATLAB', '수학자와 엔지니어의 사랑. 시뮬레이션이 즐거워진다.'),
       ('julia', 'Julia', '과학 계산 특화 언어. 속도와 편리함을 동시에.'),
       ('fortran', 'Fortran', '역사 깊은 과학 계산 언어. 여전히 일부 슈퍼컴퓨터에서 활약.'),
       ('cobol', 'COBOL', '금융 시스템의 고전. 60년 지나도 은행 서버에서 살아남음.'),
       ('fsharp', 'F#', '마이크로소프트 함수형 언어. 생각보다 쓰임새가 많다.'),
       ('nim', 'Nim', '컴파일 속도가 빠른 언어. 코드가 깔끔하면 마음도 깔끔.'),
       ('zig', 'Zig', '메모리 안전과 속도 모두 챙기는 언어. 새로운 도전의 맛.'),
       ('ocaml', 'OCaml', '함수형과 객체지향의 조화. 코딩하면서 철학적 사고 가능.'),
       ('lua', 'Lua', '게임 스크립팅의 친구. 간단하고 가벼워 어디든 들어간다.'),
       ('powershell', 'PowerShell', '윈도우 관리자 필수 도구. 커맨드라인도 예술이 될 수 있다.'),
       ('bash', 'Bash', '리눅스 쉘의 왕. 스크립트 쓰다 보면 세상을 지배한 기분.');
INSERT INTO public.topics(slug, name, description)
VALUES ('nodejs', 'Node.js', '자바스크립트 런타임으로, 서버 측에서도 JS를 실행할 수 있다. 이벤트 기반이라 동시 처리에 강하다.'),
       ('deno', 'Deno', '보안 강화된 자바스크립트/타입스크립트 런타임. Node.js 창시자가 만들었고, 이름 때문에 도마뱀 생각이 난다.'),
       ('bun', 'Bun', '자바스크립트/타입스크립트 런타임 및 번들러. 속도가 매우 빠르고 빵처럼 굽는 느낌.'),
       ('graalvm', 'GraalVM', '다중 언어 실행 환경. 자바, JS, Python 등 다양한 언어를 하나의 VM에서 실행 가능.'),
       ('jvm', 'JVM', '자바 가상 머신으로, 자바와 그 외 JVM 언어를 실행할 수 있는 환경. 안정적이지만 메모리는 가끔 투정한다.'),
       ('llvm', 'LLVM', '모듈식 컴파일러 인프라. 중간 표현(IR)을 통해 다양한 언어와 플랫폼 지원이 가능하다. 약간 낯설지만 강력하다.');
INSERT INTO public.topics(slug, name, description)
VALUES ('spring-boot', 'Spring Boot', '자바 기반 웹 프레임워크. 설정이 간단하고 빠른 개발에 적합하다. 마치 마법처럼 스타터가 도와준다.'),
       ('nestjs', 'NestJS', '타입스크립트 기반 서버 프레임워크. 구조적이고 확장 가능하며, Angular와 친근한 느낌.'),
       ('express', 'Express', 'Node.js의 대표 웹 프레임워크. 간단하고 가벼워 빠른 서버 구축 가능.'),
       ('ktor', 'Ktor', '코틀린 기반 비동기 서버 프레임워크. Kotlin DSL로 서버를 즐겁게 코딩 가능.'),
       ('fastify', 'Fastify', 'Node.js 웹 프레임워크. 이름처럼 빠른 성능과 플러그인 생태계가 특징.'),
       ('adonisjs', 'AdonisJS', 'Node.js MVC 프레임워크. 안정적인 구조와 친절한 문서가 장점.'),
       ('rails', 'Ruby on Rails', '루비 기반 풀스택 프레임워크. “컨벤션 우선”으로 개발 속도가 빠르다.'),
       ('django', 'Django', '파이썬 기반 웹 프레임워크. 보안과 관리 기능이 강력하고 생산성 높음.'),
       ('flask', 'Flask', '파이썬 경량 웹 프레임워크. 단순함 덕분에 작은 프로젝트에 적합.'),
       ('fastapi', 'FastAPI', '파이썬 기반 최신 웹 프레임워크. 빠른 성능과 자동 문서화 지원.'),
       ('laravel', 'Laravel', 'PHP 기반 웹 프레임워크. 코드를 깔끔하게 유지하고 개발 생산성이 높다.'),
       ('cakephp', 'CakePHP', 'PHP MVC 프레임워크. 컨벤션 기반으로 빠르게 앱을 개발 가능.'),
       ('phoenix', 'Phoenix', '엘릭서 기반 웹 프레임워크. 높은 동시성 처리와 실시간 기능이 장점.'),
       ('elysiajs', 'ElysiaJS', 'Node.js/TypeScript 기반 경량 웹 프레임워크. 성능과 간결함을 추구.');
INSERT INTO public.topics(slug, name, description)
VALUES ('react', 'React', '페이스북이 만든 UI 라이브러리. 컴포넌트 기반으로 재사용이 용이하다.'),
       ('nextjs', 'Next.js', 'React 기반 풀스택 프레임워크. 서버 사이드 렌더링과 정적 사이트 생성을 지원.'),
       ('react-router', 'React Router', 'React에서 라우팅을 담당하는 라이브러리. 페이지 전환이 매끄럽다.'),
       ('vue', 'Vue.js', '진입 장벽이 낮은 UI 프레임워크. 선언적 렌더링과 반응성이 장점.'),
       ('nuxt', 'Nuxt.js', 'Vue 기반 풀스택 프레임워크. 서버 사이드 렌더링과 SEO 최적화 지원.'),
       ('svelte', 'Svelte', '컴파일러 기반 UI 프레임워크. 런타임 없이 빠르게 동작.'),
       ('sveltekit', 'SvelteKit', 'Svelte 기반 풀스택 프레임워크. 서버와 클라이언트를 통합 관리.'),
       ('angular', 'Angular', '구글이 만든 SPA 프레임워크. 타입스크립트 기반으로 대규모 프로젝트에 적합.'),
       ('solidjs', 'SolidJS', '반응성에 최적화된 UI 라이브러리. 빠르고 효율적인 DOM 업데이트 지원.'),
       ('qwik', 'Qwik', '초고속 프레임워크. 필요할 때만 코드를 로드하여 성능 최적화.'),
       ('alpinejs', 'Alpine.js', '경량 JavaScript 프레임워크. HTML 속성만으로 간단한 상호작용 구현 가능.'),
       ('ember', 'Ember.js', '규약과 구조 중심 프레임워크. 대규모 애플리케이션 관리에 유리.'),
       ('backbone', 'Backbone.js', '경량 MVC 프레임워크. 단순하지만 기본 구조 제공으로 빠른 개발 가능.');
INSERT INTO public.topics(slug, name, description)
VALUES ('mysql', 'MySQL', '대표적인 오픈소스 관계형 데이터베이스. 안정적이고 많은 서비스에서 사용된다.'),
       ('postgresql', 'PostgreSQL', '강력한 오픈소스 관계형 DB. 확장성과 SQL 표준 준수에 강점이 있다.'),
       ('sqlite', 'SQLite', '가벼운 임베디드형 관계형 DB. 파일 하나로 데이터베이스가 끝난다.'),
       ('mariadb', 'MariaDB', 'MySQL의 포크 버전. 성능 향상과 기능 확장이 장점.'),
       ('oracle-db', 'Oracle', '기업용 고성능 관계형 DB. 막강하지만 라이선스가 조금 무겁다.'),
       ('sqlserver', 'SQL Server', '마이크로소프트의 관계형 DB. 윈도우 환경과 친화적.'),
       ('mongodb', 'MongoDB', '문서지향 NoSQL 데이터베이스. JSON 같은 BSON 형식으로 데이터를 저장.'),
       ('redis', 'Redis', '인메모리 키-값 저장소. 캐시와 실시간 데이터 처리에 최적화.'),
       ('cassandra', 'Cassandra', '분산형 NoSQL 데이터베이스. 확장성과 내결함성에 강점.'),
       ('couchdb', 'CouchDB', '문서 기반 NoSQL DB. RESTful API와 충돌 처리에 강하다.'),
       ('elasticsearch', 'Elasticsearch', '검색 및 분석 엔진. 로그, 텍스트 검색, 실시간 데이터 분석에 최적화.');
INSERT INTO public.topics(slug, name, description)
VALUES ('docker', 'Docker', '컨테이너 기반 가상화 플랫폼. 환경 차이 없이 애플리케이션을 실행 가능.'),
       ('kubernetes', 'Kubernetes', '컨테이너 오케스트레이션 플랫폼. 대규모 서비스 운영에 최적화.'),
       ('terraform', 'Terraform', '인프라를 코드로 관리하는 IaC 도구. 클라우드 자원을 선언적으로 배포 가능.'),
       ('ansible', 'Ansible', '자동화 구성 관리 도구. SSH 기반으로 서버 설정과 배포를 간단하게.'),
       ('jenkins', 'Jenkins', '오픈소스 CI/CD 서버. 파이프라인 자동화로 반복 작업을 줄인다.'),
       ('github-actions', 'GitHub Actions', 'GitHub 내에서 실행되는 CI/CD 툴. 워크플로우 자동화가 쉽다.'),
       ('gitlab-ci', 'GitLab CI/CD', 'GitLab 통합 CI/CD 시스템. 프로젝트와 파이프라인을 한 곳에서 관리.'),
       ('circle-ci', 'CircleCI', '클라우드 기반 CI/CD 서비스. 빠른 빌드와 배포 지원.'),
       ('travis-ci', 'Travis CI', '오픈소스 프로젝트 친화 CI/CD 서비스. GitHub와 연동 용이.'),
       ('argo-cd', 'Argo CD', '쿠버네티스용 GitOps 배포 도구. 선언적 방식으로 클러스터 관리.');
INSERT INTO public.topics(slug, name, description)
VALUES ('aws', 'AWS', '아마존의 클라우드 플랫폼. 다양한 서비스와 글로벌 인프라를 제공.'),
       ('gcp', 'GCP', '구글 클라우드 서비스. 데이터, AI, 분석에 강점이 있다.'),
       ('azure', 'Azure', '마이크로소프트 클라우드 플랫폼. 윈도우/엔터프라이즈 친화적.'),
       ('cloudflare', 'Cloudflare', '웹 보안 및 CDN 서비스. 빠른 전송과 DDoS 방어 지원.'),
       ('vercel', 'Vercel', '프론트엔드 배포 플랫폼. Next.js와 최적화되어 빠른 배포 가능.'),
       ('heroku', 'Heroku', '클라우드 PaaS. 간단한 앱 배포와 관리에 편리.'),
       ('digitalocean', 'DigitalOcean', '개발자 친화 클라우드. 단순하고 직관적인 서버 관리 제공.');
INSERT INTO public.topics(slug, name, description)
VALUES ('tensorflow', 'TensorFlow', '구글의 오픈소스 딥러닝 라이브러리. 모델 학습과 배포에 강력하다.'),
       ('pytorch', 'PyTorch', '페이스북이 만든 딥러닝 라이브러리. 직관적이고 동적 계산 그래프 지원.'),
       ('keras', 'Keras', '사용하기 쉬운 딥러닝 고수준 API. TensorFlow 위에서 간편하게 모델 구현 가능.'),
       ('scikit-learn', 'scikit-learn', '파이썬 머신러닝 라이브러리. 다양한 알고리즘과 데이터 전처리 지원.');
INSERT INTO public.topics(slug, name, description)
VALUES ('maven', 'Maven', '자바 프로젝트 관리 및 빌드 도구. 의존성 관리와 표준화에 강점.'),
       ('gradle', 'Gradle', '자바/코틀린 기반 빌드 도구. 선언적 DSL과 빠른 빌드 속도가 장점.'),
       ('webpack', 'Webpack', '자바스크립트 모듈 번들러. 코드와 리소스를 하나로 묶어 최적화.'),
       ('rollup', 'Rollup', '모듈 번들러. 주로 라이브러리 개발에 적합하고 트리쉐이킹 지원.'),
       ('vite', 'Vite', '빠른 프론트엔드 빌드 도구. 개발 서버가 매우 빠르고 설정이 간단.'),
       ('cmake', 'CMake', 'C/C++ 빌드 자동화 도구. 플랫폼 독립적인 빌드 설정 가능.');
INSERT INTO public.topics(slug, name, description)
VALUES ('npm', 'npm', 'Node.js 공식 패키지 매니저. 방대한 라이브러리 생태계 제공.'),
       ('yarn', 'Yarn', '빠르고 안정적인 Node.js 패키지 매니저. 의존성 관리를 체계적으로 지원.'),
       ('pnpm', 'pnpm', '효율적인 Node.js 패키지 매니저. 디스크 사용량을 최소화.'),
       ('pip', 'pip', '파이썬 패키지 매니저. PyPI에서 패키지 설치가 간단.'),
       ('poetry', 'Poetry', '파이썬 패키지 관리 및 배포 도구. 의존성 관리가 편리.'),
       ('cargo', 'Cargo', 'Rust 공식 패키지 매니저. 빌드와 의존성 관리를 통합 지원.'),
       ('composer', 'Composer', 'PHP 패키지 매니저. 의존성 설치와 프로젝트 관리에 필수.'),
       ('gem', 'RubyGems', '루비 패키지 매니저. 라이브러리 설치와 관리가 편리.'),
       ('nuget', 'NuGet', '닷넷 패키지 매니저. .NET 라이브러리 설치와 관리에 최적화.');
INSERT INTO public.topics(slug, name, description)
VALUES ('prettier', 'Prettier', '코드 포맷터. 팀의 스타일 전쟁을 종식시킨다.'),
       ('eslint', 'ESLint', '자바스크립트 코드 품질 검사기. 잔소리는 많지만 틀린 말은 안 한다.'),
       ('babel', 'Babel', '최신 자바스크립트를 구형 브라우저가 이해하게 변환한다. 시간여행자 같은 도구다.'),
       ('postman', 'Postman', 'API 테스트와 디버깅 도구. 버튼 몇 번으로 서버를 괴롭힐 수 있다.'),
       ('insomnia', 'Insomnia', '다크 모드 감성이 강한 API 클라이언트. 이름처럼 새벽까지 개발할 때 잘 어울린다.'),
       ('grafana', 'Grafana', '데이터 시각화 플랫폼. 예쁜 대시보드로 모니터링을 즐겁게 한다.'),
       ('prometheus', 'Prometheus', '모니터링과 알림 시스템. 메트릭 수집의 표준이다.');
INSERT INTO public.topics(slug, name, description)
VALUES ('apache', 'Apache', '오랜 역사를 가진 웹 서버. 지금도 조용히 많은 곳에서 돌아간다.'),
       ('nginx', 'NGINX', '고성능 웹 서버와 리버스 프록시. 경량이지만 강력하다.');
INSERT INTO public.topics(slug, name, description)
VALUES ('vscode', 'VSCode', '마이크로소프트의 경량 코드 편집기. 확장성과 커스터마이징이 강점.'),
       ('intellij', 'IntelliJ', 'JetBrains의 강력한 IDE. 자바와 코틀린 개발에 최적화.'),
       ('vim', 'Vim', '강력한 터미널 기반 텍스트 편집기. 초기 학습 곡선이 가파르지만 효율적.'),
       ('neovim', 'Neovim', 'Vim 기반 편집기. 플러그인과 현대적 기능으로 개선됨.'),
       ('emacs', 'Emacs', '역사 깊은 편집기. 거의 모든 것을 확장할 수 있는 완전한 환경.');
INSERT INTO public.topics(slug, name, description)
VALUES ('github', 'GitHub', '소스 코드 호스팅과 협업의 대표 플랫폼. 개발자 SNS라고 부를 만하다.'),
       ('gitlab', 'GitLab', '깃허브와 유사하지만, 자체 호스팅과 CI/CD 기능이 강점. 깃허브보다 업무적이다.');

INSERT INTO public.users(email, username, nickname, role)
VALUES ('admin@gmail.com', 'admin', 'admin', 'ADMIN'),
       ('loghub-bot@loghub.me', 'loghub-bot', 'LogHub 봇', 'BOT'),
       ('member1@loghub.me', 'member1', '멤버1', 'MEMBER'),
       ('member2@loghub.me', 'member2', '멤버2', 'MEMBER');

INSERT INTO public.articles(slug, title, content, writer_id, writer_username)
VALUES ('article-1', 'Article 1', 'Article content 1',
        ( SELECT id FROM public.users WHERE username = 'member1' ), 'member1'), -- for get
       ('article-2', 'Article 2', 'Article content 2',
        ( SELECT id FROM public.users WHERE username = 'member1' ), 'member1'), -- for edit
       ('article-3', 'Article 3', 'Article content 3',
        ( SELECT id FROM public.users WHERE username = 'member1' ), 'member1'), -- for delete
       ('article-4', 'Article 4', 'Article content 4',
        ( SELECT id FROM public.users WHERE username = 'member2' ), 'member2');
INSERT INTO public.article_comments(content, article_id, writer_id)
VALUES ('Article comment 1', 1, ( SELECT id FROM users WHERE username = 'member1' )),
       ('Article comment 2', 1, ( SELECT id FROM users WHERE username = 'member1' ));
INSERT INTO public.article_comments(content, article_id, parent_id, mention_id, writer_id)
VALUES ('Article comment 3', 1, 1,
        ( SELECT id FROM users WHERE username = 'member1' ), ( SELECT id FROM users WHERE username = 'member2' ));
UPDATE public.articles
SET comment_count = c.cnt
FROM ( SELECT article_id, COUNT(*) AS cnt FROM public.article_comments GROUP BY article_id ) c
WHERE id = c.article_id;
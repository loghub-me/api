TRUNCATE TABLE public.users RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.topics RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.articles RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.questions RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.search_synonyms RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.stars RESTART IDENTITY CASCADE;

INSERT INTO public.users(email, username, nickname, provider, role)
VALUES ('admin@admin.com', 'admin', '어드민', 'LOCAL', 'ADMIN'),
       ('bot@bot.com', 'bot', '답변봇', 'LOCAL', 'BOT'),
       ('test1@test.com', 'test1', '테스트1', 'LOCAL', 'MEMBER'),
       ('test2@test.com', 'test2', '테스트2', 'LOCAL', 'MEMBER');
INSERT INTO public.topics(slug, name, description)
VALUES ('c', 'C', 'C 언어에 대한 정보'),
       ('cpp', 'C++', 'C++ 언어에 대한 정보'),
       ('csharp', 'C#', 'C# 언어에 대한 정보'),
       ('deno', 'Deno', 'Deno에 대한 정보'),
       ('docker', 'Docker', 'Docker에 대한 정보'),
       ('github', 'GitHub', 'GitHub에 대한 정보'),
       ('google', 'Google', 'Google에 대한 정보'),
       ('java', 'Java', 'Java 언어에 대한 정보'),
       ('javascript', 'JavaScript', 'JavaScript 언어에 대한 정보'),
       ('kubernetes', 'Kubernetes', 'Kubernetes에 대한 정보'),
       ('nextjs', 'Next.js', 'Next.js에 대한 정보'),
       ('nodejs', 'Node.js', 'Node.js에 대한 정보'),
       ('prettier', 'Prettier', 'Prettier에 대한 정보'),
       ('python', 'Python', 'Python 언어에 대한 정보'),
       ('react', 'React', 'React 라이브러리에 대한 정보'),
       ('spring', 'Spring', 'Spring Framework에 대한 정보'),
       ('typescript', 'TypeScript', 'TypeScript 언어에 대한 정보');
INSERT INTO public.articles(slug, title, content, writer_id, writer_username, topics_flat)
VALUES ('about-c', 'C에 대해서',
        E'# C 언어 소개\nC 언어는 1972년, 벨 연구소(Bell Labs)의 데니스 리치(Dennis Ritchie)가 개발한 절차지향 프로그래밍 언어입니다. 운영 체제 개발을 포함한 시스템 프로그래밍에 적합하도록 설계되었으며, 오늘날 많은 언어의 조상으로 여겨집니다.\n## 🔧 특징\n- **고성능**: 하드웨어에 가까운 저수준 제어 가능\n- **이식성**: 표준에 맞추면 다양한 플랫폼에서 동작\n- **절차지향**: 순차적 명령 흐름 중심 설계\n- **포인터**: 메모리 직접 제어 가능 (하지만 신입 개발자들의 멘탈을 바로 조지는 주범)\n- **작고 빠름**: 런타임 오버헤드가 적음\n## 📦 주요 용도\n- 운영 체제 개발 (ex: UNIX)\n- 임베디드 시스템 (마이크로컨트롤러 등)\n- 컴파일러 및 언어 런타임 구현\n- 성능이 중요한 애플리케이션 (게임, 그래픽 엔진 등)\n## 📘 예시 코드\n```c\n#include <stdio.h>\n\nint main() {\n    printf("Hello, World!\\n");\n    return 0;\n}\n```\n## ⚠️ 주의할 점\n- 메모리 관리 수동 → **malloc/free**의 춤사위는 피할 수 없다\n- 버퍼 오버플로우, 세그멘테이션 폴트 등으로 개발자 눈에서 땀과 눈물이 같이 흐를 수 있음\n- 디버깅 도구 필수! (gdb 없으면 반쯤 장님 상태)\n## 🧬 관련 언어\n- C++: C에 객체지향 개념 추가한 진화형. 하지만 무겁다.\n- Objective-C: C에 Smalltalk 스타일 메시지 추가. Apple 계열에서 주로 사용.\n- Rust, Go: 메모리 안전성과 병행성을 강화한 근래의 대체 언어들\n> 💡 Tip: C는 배우기 쉽지만 **잘 쓰기 어려운 언어**입니다. 마치 마법의 검을 쥔 초보 모험가처럼, 휘두르면 강력하나 자기 자신도 벨 수 있습니다. 신중히 다루세요.\n## 📊 표 예시\n|항목|설명|\n|---|---|\n|예시|이것은 표 예시입니다.\n## 🔗 참고 링크\n- [C 언어 위키피디아](https://ko.wikipedia.org/wiki/C_언어)\n'
           , 3, 'test1', 'c:C'),
       ('about-cpp', 'C++에 대해서',
        E'# C++ 언어 소개\nC++는 Bjarne Stroustrup이 C에 객체지향 기능을 추가하여 개발한 언어입니다. 효율성과 성능을 중시하며, 다양한 프로그래밍 패러다임을 지원합니다.\n## 🔧 특징\n- **객체지향**: 클래스와 상속을 통한 코드 재사용\n- **템플릿**: 제네릭 프로그래밍 지원\n- **STL**: 범용 알고리즘과 컨테이너\n- **메모리 관리**: RAII 기반 자원 관리\n## 📘 예시 코드\n```cpp\n#include <iostream>\n#include <vector>\n\nint main() {\n    std::vector<int> v = {1, 2, 3};\n    for (auto n : v) {\n        std::cout << n << std::endl;\n    }\n    return 0;\n}\n```\n> **Tip**: C++은 강력하지만 복잡하며, 스마트 포인터를 적절히 사용해 메모리 안전을 확보하세요.\n## 📊 표 예시\n|항목|설명|\n|---|---|\n|예시|이것은 표 예시입니다.\n## 🔗 참고 링크\n- [C++ 위키피디아](https://ko.wikipedia.org/wiki/C%2B%2B)\n'
           , 3, 'test1', 'cpp:C++'),
       ('about-java', 'Java에 대해서',
        E'# Java 언어 소개\nJava는 Sun Microsystems의 James Gosling이 개발한 객체지향 언어로, Write Once, Run Anywhere를 지향합니다.\n## 🔧 특징\n- **플랫폼 독립성**: JVM을 통한 실행\n- **강력한 표준 라이브러리**: 컬렉션, IO, 네트워크 등\n- **가비지 컬렉션**: 자동 메모리 관리\n- **멀티스레딩**: 내장 스레드 라이브러리\n## 📘 예시 코드\n```java\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println("Hello, World!");\n    }\n}\n```\n> **Tip**: Java는 안정성과 확장성을 제공하지만, 가비지 컬렉션 특성을 이해하고 튜닝해야 합니다.\n## 📊 표 예시\n|항목|설명|\n|---|---|\n|예시|이것은 표 예시입니다.\n## 🔗 참고 링크\n- [Java 위키피디아](https://ko.wikipedia.org/wiki/Java)\n'
           , 3, 'test1', 'java:Java,spring:Spring'),
       ('about-python', 'Python에 대해서',
        E'# Python 언어 소개\nPython은 Guido van Rossum이 개발한 인터프리티드 고수준 언어로, 가독성과 생산성을 중시합니다.\n## 🔧 특징\n- **동적 타이핑**: 유연한 변수 사용\n- **풍부한 표준 라이브러리**: "batteries included"\n- **간결한 문법**: 들여쓰기로 블록 구조 표현\n- **크로스 플랫폼**: 다양한 OS에서 실행 가능\n## 📘 예시 코드\n```python\ndef greet(name):\n    print(f"Hello, {name}!")\n\ngreet("World")\n```\n> **Tip**: Python은 다재다능하지만, 성능이 중요한 경우 C 확장이나 PyPy를 고려하세요.\n## 📊 표 예시\n|항목|설명|\n|---|---|\n|예시|이것은 표 예시입니다.\n## 🔗 참고 링크\n- [Python 위키피디아](https://ko.wikipedia.org/wiki/Python)\n'
           , 4, 'test2', 'python:Python'),
       ('about-javascript', 'JavaScript에 대해서',
        E'# JavaScript 언어 소개\nJavaScript는 Brendan Eich가 개발한 스크립트 언어로, 웹 브라우저에서 동작하기 위해 만들어졌습니다.\n## 🔧 특징\n- **이벤트 기반**: 비동기 처리 및 콜백\n- **프로토타입 기반**: 클래스 없는 객체지향\n- **동적 타이핑**: 유연하지만 오류 주의\n- **Node.js**: 서버 사이드 실행 환경\n## 📘 예시 코드\n```javascript\nconsole.log("Hello, World!");\n\nfetch("https://api.example.com/data")\n  .then(response => response.json())\n  .then(data => console.log(data));\n```\n> **Tip**: 최신 문법(ES6+)을 사용하고, 비동기 흐름 관리를 위해 async/await를 적극 활용하세요.\n## 📊 표 예시\n|항목|설명|\n|---|---|\n|예시|이것은 표 예시입니다.\n## 🔗 참고 링크\n- [JavaScript 위키피디아](https://ko.wikipedia.org/wiki/JavaScript)\n'
           , 4, 'test2', 'javascript:JavaScript');
INSERT INTO public.article_topics(article_id, topic_id)
VALUES (1, ( SELECT id FROM public.topics WHERE slug = 'c' )),
       (2, ( SELECT id FROM public.topics WHERE slug = 'cpp' )),
       (3, ( SELECT id FROM public.topics WHERE slug = 'java' )),
       (3, ( SELECT id FROM public.topics WHERE slug = 'spring' )),
       (4, ( SELECT id FROM public.topics WHERE slug = 'python' )),
       (5, ( SELECT id FROM public.topics WHERE slug = 'javascript' ));
INSERT INTO public.article_comments(content, article_id, parent_id, mention_id, writer_id, reply_count, created_at,
                                    updated_at)
VALUES ('This is a comment.', 1, NULL, NULL, 3, 1, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
       ('This is a reply.', 1, 1, 3, 4, 0, NOW() - INTERVAL '1 hour', NOW() - INTERVAL '1 hour'),
       ('This is a long long long long long long long long long long mention.', 1, NULL, NULL, 3, 0, NOW(), NOW());

INSERT INTO public.search_synonyms
VALUES ('java', ARRAY ['java', 'javascript']),
       ('javascript', ARRAY ['javascript', 'java']);

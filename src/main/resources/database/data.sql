INSERT INTO public.users(email, username, nickname, provider, role)
VALUES ('admin@admin.com', 'admin', '어드민', 'LOCAL', 'ADMIN'),
       ('bot@bot.com', 'bot', '답변봇', 'LOCAL', 'BOT'),
       ('test1@test.com', 'test1', '테스트1', 'LOCAL', 'MEMBER');
INSERT INTO public.topics(slug, name, description)
VALUES ('c', 'C', 'C는 C언어입니다.'),
       ('cpp', 'C++', 'C++는 C++언어입니다.'),
       ('java', 'Java', 'Java는 Java언어입니다.'),
       ('python', 'Python', 'Python은 Python언어입니다.'),
       ('javascript', 'JavaScript', 'JavaScript는 JavaScript언어입니다.'),
       ('go', 'Go', 'Go는 Go언어입니다.'),
       ('rust', 'Rust', 'Rust는 Rust언어입니다.'),
       ('kotlin', 'Kotlin', 'Kotlin은 Kotlin언어입니다.'),
       ('typescript', 'TypeScript', 'TypeScript는 TypeScript언어입니다.');
INSERT INTO public.articles(slug, title, content, writer_id, writer_username, topics_flat)
VALUES ('about-c', 'C에 대해서', '#C언어란 ...', 3, 'test1', 'c:C,cpp:C++'),
       ('about-cpp', 'C++에 대해서', '#C++언어란 ...', 3, 'test1', 'cpp:C++'),
       ('about-java', 'Java에 대해서', '#Java언어란 ...', 3, 'test1', 'java:Java'),
       ('about-python', 'Python에 대해서', '#Python언어란 ...', 3, 'test1', 'python:Python'),
       ('about-javascript', 'JavaScript에 대해서', '#JavaScript언어란 ...', 3, 'test1', 'javascript:JavaScript'),
       ('about-go', 'Go에 대해서', '#Go언어란 ...', 3, 'test1', 'go:Go'),
       ('about-rust', 'Rust에 대해서', '#Rust언어란 ...', 3, 'test1', 'rust:Rust'),
       ('about-kotlin', 'Kotlin에 대해서', '#Kotlin언어란 ...', 3, 'test1', 'kotlin:Kotlin'),
       ('about-typescript', 'TypeScript에 대해서', '#TypeScript언어란 ...', 3, 'test1', 'typescript:TypeScript');
INSERT INTO public.articles(slug, title, content, writer_id, writer_username, topics_flat, created_at, updated_at)
VALUES ('old-article', 'Old Article', 'This is an old article.', 3, 'test1', 'java:Java', '2020-01-01 00:00:00', '2020-01-01 00:00:00');

INSERT INTO public.article_topics(article_id, topic_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (6, 6),
       (7, 7),
       (8, 8),
       (9, 9);
INSERT INTO search_synonyms
VALUES ('java', ARRAY['java', 'javascript']),
       ('javascript', ARRAY['javascript', 'java']);

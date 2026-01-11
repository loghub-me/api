CREATE TABLE IF NOT EXISTS archive.users (
  LIKE public.users,
  deleted_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS archive.user_meta (
  LIKE public.user_meta,
  deleted_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS archive.articles (
  LIKE public.articles,
  deleted_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS archive.article_comments (
  LIKE public.article_comments,
  deleted_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS archive.series (
  LIKE public.series,
  deleted_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS archive.series_chapters (
  LIKE public.series_chapters,
  deleted_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS archive.series_reviews (
  LIKE public.series_reviews,
  deleted_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS archive.questions (
  LIKE public.questions,
  deleted_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS archive.question_answers (
  LIKE public.question_answers,
  deleted_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

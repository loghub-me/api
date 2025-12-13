CREATE INDEX IF NOT EXISTS topics_trending_idx ON public.topics (trending_score DESC);

CREATE INDEX IF NOT EXISTS articles_writer_id_idx ON public.articles (writer_id);
CREATE INDEX IF NOT EXISTS articles_published_idx ON public.articles (published);
CREATE INDEX IF NOT EXISTS articles_fts_idx ON public.articles USING pgroonga ((ARRAY [title, content, topics_flat])) WITH (tokenizer='TokenMecab', normalizer='NormalizerAuto');
CREATE INDEX IF NOT EXISTS articles_created_at_idx ON public.articles (created_at);
CREATE INDEX IF NOT EXISTS articles_trending_idx ON public.articles (trending_score DESC, star_count DESC);
CREATE INDEX IF NOT EXISTS articles_writer_username_created_at_idx ON public.articles (writer_username, created_at);
CREATE INDEX IF NOT EXISTS articles_writer_username_trending_idx ON public.articles (writer_username, trending_score DESC, star_count DESC);

CREATE INDEX IF NOT EXISTS article_comments_article_id_parent_id_created_at_idx ON public.article_comments (article_id, parent_id, created_at DESC);

CREATE INDEX IF NOT EXISTS series_writer_id_idx ON public.series (writer_id);
CREATE INDEX IF NOT EXISTS series_fts_idx ON public.series USING pgroonga ((ARRAY [title, description, topics_flat])) WITH (tokenizer='TokenMecab', normalizer='NormalizerAuto');
CREATE INDEX IF NOT EXISTS series_created_at_idx ON public.series (created_at);
CREATE INDEX IF NOT EXISTS series_trending_idx ON public.series (trending_score DESC, star_count DESC);
CREATE INDEX IF NOT EXISTS series_writer_username_created_at_idx ON public.series (writer_username, created_at);
CREATE INDEX IF NOT EXISTS series_writer_username_trending_idx ON public.series (writer_username, trending_score DESC, star_count DESC);

CREATE INDEX IF NOT EXISTS series_chapters_series_id_sequence_idx ON public.series_chapters (series_id, sequence);
CREATE INDEX IF NOT EXISTS series_reviews_series_id_created_at_idx ON public.series_reviews (series_id, created_at DESC);

CREATE INDEX IF NOT EXISTS questions_writer_id_idx ON public.questions (writer_id);
CREATE INDEX IF NOT EXISTS questions_fts_idx ON public.questions USING pgroonga ((ARRAY [title, content, topics_flat])) WITH (tokenizer='TokenMecab', normalizer='NormalizerAuto');
CREATE INDEX IF NOT EXISTS questions_status_created_at_idx ON public.questions (status, created_at);
CREATE INDEX IF NOT EXISTS questions_status_trending_idx ON public.questions (status, trending_score DESC, star_count DESC);
CREATE INDEX IF NOT EXISTS questions_writer_username_status_created_at_idx ON public.questions (writer_username, status, created_at);
CREATE INDEX IF NOT EXISTS questions_writer_username_status_trending_idx ON public.questions (writer_username, status, trending_score DESC, star_count DESC);

CREATE INDEX IF NOT EXISTS question_answers_question_id_created_at_idx ON public.question_answers (question_id, created_at);

CREATE INDEX IF NOT EXISTS user_stars_user_id_created_at_idx ON public.user_stars (user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS user_activities_user_id_created_date_idx ON public.user_activities (user_id, created_date);

CREATE OR REPLACE FUNCTION archive.archive_user_on_delete () RETURNS TRIGGER AS '
    BEGIN
        INSERT INTO archive.users (id, email, username, nickname, email_public, provider, role, created_at, updated_at) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.email, OLD.username, OLD.nickname, OLD.email_public, OLD.provider, OLD.role, OLD.created_at, OLD.updated_at);
        RETURN OLD;
    END
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION archive.archive_user_meta_on_delete () RETURNS TRIGGER AS '
    BEGIN
        INSERT INTO archive.users_meta (user_id, readme, github_username, github_verified, total_posted_count, total_added_star_count, total_gazed_star_count) OVERRIDING SYSTEM VALUE
        VALUES (OLD.user_id, OLD.readme, OLD.github_username, OLD.github_verified, OLD.total_posted_count, OLD.total_added_star_count, OLD.total_gazed_star_count);
        RETURN OLD;
    END
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION archive.archive_article_on_delete () RETURNS TRIGGER AS '
    BEGIN
        INSERT INTO archive.articles (id, slug, title, content, normalized_content, thumbnail, published, published_at, star_count, comment_count, trending_score, created_at, updated_at, writer_id, writer_username, topics_flat) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.slug, OLD.title, OLD.content, OLD.normalized_content, OLD.thumbnail, OLD.published, OLD.published_at, OLD.star_count, OLD.comment_count, OLD.trending_score, OLD.created_at, OLD.updated_at, OLD.writer_id, OLD.writer_username, OLD.topics_flat);
        RETURN OLD;
    END
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION archive.archive_article_comment_on_delete () RETURNS TRIGGER AS '
    BEGIN
        INSERT INTO archive.article_comments (id, content, deleted, reply_count, created_at, updated_at, article_id, parent_id, mention_id, writer_id) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.content, OLD.deleted, OLD.reply_count, OLD.created_at, OLD.updated_at, OLD.article_id, OLD.parent_id, OLD.mention_id, OLD.writer_id);
        RETURN OLD;
    END
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION archive.archive_series_on_delete () RETURNS TRIGGER AS '
    BEGIN
        INSERT INTO archive.series (id, slug, title, description, thumbnail, star_count, review_count, trending_score, created_at, updated_at, writer_id, writer_username, topics_flat) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.slug, OLD.title, OLD.description, OLD.thumbnail, OLD.star_count, OLD.review_count, OLD.trending_score, OLD.created_at, OLD.updated_at, OLD.writer_id, OLD.writer_username, OLD.topics_flat);
        RETURN OLD;
    END
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION archive.archive_series_chapter_on_delete () RETURNS TRIGGER AS '
    BEGIN
        INSERT INTO archive.series_chapters (id, title, content, normalized_content, sequence, published, published_at, created_at, updated_at, series_id, writer_id) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.title, OLD.content, OLD.normalized_content, OLD.sequence, OLD.published, OLD.published_at, OLD.created_at, OLD.updated_at, OLD.series_id, OLD.writer_id);
        RETURN OLD;
    END
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION archive.archive_series_review_on_delete () RETURNS TRIGGER AS '
    BEGIN
        INSERT INTO archive.series_reviews (id, content, rating, created_at, updated_at, series_id, writer_id) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.content, OLD.rating, OLD.created_at, OLD.updated_at, OLD.series_id, OLD.writer_id);
        RETURN OLD;
    END
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION archive.archive_question_on_delete () RETURNS TRIGGER AS '
    BEGIN
        INSERT INTO archive.questions (id, slug, title, content, normalized_content, status, solved_at, closed_at, star_count, answer_count, trending_score, created_at, updated_at, writer_id, writer_username, topics_flat) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.slug, OLD.title, OLD.content, OLD.normalized_content, OLD.status, OLD.solved_at, OLD.closed_at, OLD.star_count, OLD.answer_count, OLD.trending_score, OLD.created_at, OLD.updated_at, OLD.writer_id, OLD.writer_username, OLD.topics_flat);
        RETURN OLD;
    END
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION archive.archive_question_answer_on_delete () RETURNS TRIGGER AS '
    BEGIN
        INSERT INTO archive.question_answers (id, title, content, accepted, accepted_at, created_at, updated_at, question_id, writer_id) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.title, OLD.content, OLD.accepted, OLD.accepted_at, OLD.created_at, OLD.updated_at, OLD.question_id, OLD.writer_id);
        RETURN OLD;
    END
' LANGUAGE plpgsql;

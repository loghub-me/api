CREATE OR REPLACE FUNCTION archive.archive_user_on_delete() RETURNS TRIGGER AS
'
    BEGIN
        INSERT INTO archive.users (id, email, username, nickname, readme,
                                   email_visible, star_visible, provider, role,
                                   created_at, updated_at) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.email, OLD.username, OLD.nickname, OLD.readme,
                OLD.email_visible, OLD.star_visible, OLD.provider, OLD.role,
                OLD.created_at, OLD.updated_at);
        RETURN OLD;
    END
' LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION archive.archive_article_on_delete() RETURNS TRIGGER AS
'
    BEGIN
        INSERT INTO archive.articles (id, slug, title, content, thumbnail, star_count, comment_count, trending_score,
                                      created_at, updated_at, writer_id, writer_username,
                                      topics_flat) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.slug, OLD.title, OLD.content, OLD.thumbnail, OLD.star_count, OLD.comment_count,
                OLD.trending_score, OLD.created_at, OLD.updated_at, OLD.writer_id, OLD.writer_username,
                OLD.topics_flat);
        RETURN OLD;
    END
' LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION archive.archive_article_comment_on_delete() RETURNS TRIGGER AS
'
    BEGIN
        INSERT INTO archive.article_comments (id, content, deleted, reply_count, created_at, updated_at, article_id,
                                              parent_id, mention_id, writer_id) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.content, OLD.deleted, OLD.reply_count, OLD.created_at, OLD.updated_at,
                OLD.article_id, OLD.parent_id, OLD.mention_id, OLD.writer_id);
        RETURN OLD;
    END
' LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION archive.archive_series_on_delete() RETURNS TRIGGER AS
'
    BEGIN
        INSERT INTO archive.series (id, slug, title, content, thumbnail, star_count, review_count, trending_score,
                                    created_at, updated_at, writer_id, writer_username,
                                    topics_flat) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.slug, OLD.title, OLD.content, OLD.thumbnail, OLD.star_count, OLD.review_count,
                OLD.trending_score, OLD.created_at, OLD.updated_at, OLD.writer_id, OLD.writer_username,
                OLD.topics_flat);
        RETURN OLD;
    END
' LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION archive.archive_series_chapter_on_delete() RETURNS TRIGGER AS
'
    BEGIN
        INSERT INTO archive.series_chapters (id, title, content, sequence, created_at, updated_at, series_id,
                                             writer_id) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.title, OLD.content, OLD.sequence, OLD.created_at, OLD.updated_at,
                OLD.series_id, OLD.writer_id);
        RETURN OLD;
    END
' LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION archive.archive_series_review_on_delete() RETURNS TRIGGER AS
'
    BEGIN
        INSERT INTO archive.series_reviews (id, content, rating, created_at, updated_at, series_id,
                                            writer_id) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.content, OLD.rating, OLD.created_at, OLD.updated_at,
                OLD.series_id, OLD.writer_id);
        RETURN OLD;
    END
' LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION archive.archive_question_on_delete() RETURNS TRIGGER AS
'
    BEGIN
        INSERT INTO archive.questions (id, slug, title, content, status, solved_at, closed_at, star_count, answer_count,
                                       trending_score, created_at, updated_at, writer_id, writer_username,
                                       topics_flat) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.slug, OLD.title, OLD.content, OLD.status, OLD.solved_at, OLD.closed_at,
                OLD.star_count, OLD.answer_count, OLD.trending_score, OLD.created_at, OLD.updated_at,
                OLD.writer_id, OLD.writer_username, OLD.topics_flat);
        RETURN OLD;
    END
' LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION archive.archive_question_answer_on_delete() RETURNS TRIGGER AS
'
    BEGIN
        INSERT INTO archive.question_answers (id, title, content, accepted, accepted_at, created_at, updated_at,
                                              question_id, writer_id) OVERRIDING SYSTEM VALUE
        VALUES (OLD.id, OLD.title, OLD.content, OLD.accepted, OLD.accepted_at,
                OLD.created_at, OLD.updated_at, OLD.question_id, OLD.writer_id);
        RETURN OLD;
    END
' LANGUAGE plpgsql;
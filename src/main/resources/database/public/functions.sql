CREATE OR REPLACE FUNCTION public.update_topics_trending_score() RETURNS void
    LANGUAGE plpgsql AS
'
    BEGIN
        UPDATE topics SET trending_score = tt.trending_score FROM trending_topics tt WHERE topics.id = tt.topic_id;
        UPDATE topics SET trending_score = 0 WHERE id NOT IN ( SELECT topic_id FROM trending_topics );
    END
';

CREATE OR REPLACE FUNCTION public.insert_user_activity_after_post_article() RETURNS TRIGGER AS
'
    BEGIN
        INSERT INTO public.user_activities(action, article_id, user_id, created_date)
        VALUES (''POST_ARTICLE'', new.id, new.writer_id, new.created_at::date);
        RETURN new;
    END;
' LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION public.insert_user_activity_after_post_series() RETURNS TRIGGER AS
'
    BEGIN
        INSERT INTO public.user_activities(action, series_id, user_id, created_date)
        VALUES (''POST_SERIES'', new.id, new.writer_id, new.created_at::date);
        RETURN new;
    END;
' LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION public.insert_user_activity_after_post_question() RETURNS TRIGGER AS
'
    BEGIN
        INSERT INTO public.user_activities(action, question_id, user_id, created_date)
        VALUES (''POST_QUESTION'', new.id, new.writer_id, new.created_at::date);
        RETURN new;
    END;
' LANGUAGE plpgsql;

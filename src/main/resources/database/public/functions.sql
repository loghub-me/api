CREATE OR REPLACE FUNCTION public.update_topics_trending_score() RETURNS void
    LANGUAGE plpgsql AS
'
    BEGIN
        UPDATE topics SET trending_score = tt.trending_score FROM trending_topics tt WHERE topics.id = tt.topic_id;
        UPDATE topics SET trending_score = 0 WHERE id NOT IN ( SELECT topic_id FROM trending_topics );
    END
';

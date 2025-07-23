DROP TRIGGER IF EXISTS trg_insert_user_activity_after_post_article ON public.articles;
CREATE TRIGGER trg_insert_user_activity_after_post_article
    AFTER INSERT
    ON public.articles
    FOR EACH ROW
EXECUTE FUNCTION public.insert_user_activity_after_post_article();
DROP TRIGGER IF EXISTS trg_insert_user_activity_after_post_series ON public.series;
CREATE TRIGGER trg_insert_user_activity_after_post_series
    AFTER INSERT
    ON public.series
    FOR EACH ROW
EXECUTE FUNCTION public.insert_user_activity_after_post_series();
DROP TRIGGER IF EXISTS trg_insert_user_activity_after_post_question ON public.questions;
CREATE TRIGGER trg_insert_user_activity_after_post_question
    AFTER INSERT
    ON public.questions
    FOR EACH ROW
EXECUTE FUNCTION public.insert_user_activity_after_post_question();

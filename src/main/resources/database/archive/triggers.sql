DROP TRIGGER IF EXISTS trg_archive_user_on_delete ON public.users;
CREATE TRIGGER trg_archive_user_on_delete
    AFTER DELETE ON public.users
    FOR EACH ROW EXECUTE FUNCTION archive.archive_user_on_delete ();

DROP TRIGGER IF EXISTS trg_archive_user_meta_on_delete ON public.user_meta;
CREATE TRIGGER trg_archive_user_meta_on_delete
    AFTER DELETE ON public.user_meta
    FOR EACH ROW EXECUTE FUNCTION archive.archive_user_meta_on_delete ();

DROP TRIGGER IF EXISTS trg_archive_article_on_delete ON public.articles;
CREATE TRIGGER trg_archive_article_on_delete
    AFTER DELETE ON public.articles
    FOR EACH ROW EXECUTE FUNCTION archive.archive_article_on_delete ();

DROP TRIGGER IF EXISTS trg_archive_article_comment_on_delete ON public.article_comments;
CREATE TRIGGER trg_archive_article_comment_on_delete
    AFTER DELETE ON public.article_comments
    FOR EACH ROW EXECUTE FUNCTION archive.archive_article_comment_on_delete ();

DROP TRIGGER IF EXISTS trg_archive_series_on_delete ON public.series;
CREATE TRIGGER trg_archive_series_on_delete
    AFTER DELETE ON public.series
    FOR EACH ROW EXECUTE FUNCTION archive.archive_series_on_delete ();

DROP TRIGGER IF EXISTS trg_archive_series_chapter_on_delete ON public.series_chapters;
CREATE TRIGGER trg_archive_series_chapter_on_delete
    AFTER DELETE ON public.series_chapters
    FOR EACH ROW EXECUTE FUNCTION archive.archive_series_chapter_on_delete ();

DROP TRIGGER IF EXISTS trg_archive_series_review_on_delete ON public.series_reviews;
CREATE TRIGGER trg_archive_series_review_on_delete
    AFTER DELETE ON public.series_reviews
    FOR EACH ROW EXECUTE FUNCTION archive.archive_series_review_on_delete ();

DROP TRIGGER IF EXISTS trg_archive_question_on_delete ON public.questions;
CREATE TRIGGER trg_archive_question_on_delete
    AFTER DELETE ON public.questions
    FOR EACH ROW EXECUTE FUNCTION archive.archive_question_on_delete ();

DROP TRIGGER IF EXISTS trg_archive_question_answer_on_delete ON public.question_answers;
CREATE TRIGGER trg_archive_question_answer_on_delete
    AFTER DELETE ON public.question_answers
    FOR EACH ROW EXECUTE FUNCTION archive.archive_question_answer_on_delete ();

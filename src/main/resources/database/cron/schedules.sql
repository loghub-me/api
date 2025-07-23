SELECT cron.schedule('weekly_topic_trending', '0 1 * * 0', $$CALL public.update_topics_trending_score()$$);

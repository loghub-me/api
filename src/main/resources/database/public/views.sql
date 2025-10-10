DROP VIEW IF EXISTS trending_topics;

CREATE VIEW trending_topics AS
SELECT topic_id, (SUM(trending_score) * 7 + COUNT(*) * 3) AS trending_score
FROM ( SELECT at.topic_id, a.trending_score
       FROM article_topics at
                LEFT JOIN articles a ON at.article_id = a.id
       WHERE a.created_at >= CURRENT_DATE - INTERVAL '7 days'
       UNION ALL
       SELECT st.topic_id, s.trending_score
       FROM series_topics st
                LEFT JOIN series s ON st.series_id = s.id
       WHERE s.created_at >= CURRENT_DATE - INTERVAL '7 days'
       UNION ALL
       SELECT qt.topic_id, q.trending_score
       FROM question_topics qt
                LEFT JOIN questions q ON qt.question_id = q.id
       WHERE q.created_at >= CURRENT_DATE - INTERVAL '7 days' ) combined
GROUP BY topic_id;

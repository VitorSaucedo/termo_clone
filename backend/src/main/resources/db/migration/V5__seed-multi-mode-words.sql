INSERT INTO daily_words (word_id, game_date, language, mode)
SELECT
    w.id,
    CURRENT_DATE + ((ROW_NUMBER() OVER (ORDER BY w.id) - 1) / 2)::INT,
    'pt-BR',
    'DUETO'
FROM (
         SELECT id FROM words
         WHERE is_solution = TRUE AND language = 'pt-BR' AND length = 5
         ORDER BY id
         OFFSET 30 LIMIT 60
     ) w
    ON CONFLICT DO NOTHING;

INSERT INTO daily_words (word_id, game_date, language, mode)
SELECT
    w.id,
    CURRENT_DATE + ((ROW_NUMBER() OVER (ORDER BY w.id) - 1) / 4)::INT,
    'pt-BR',
    'QUARTETO'
FROM (
         SELECT id FROM words
         WHERE is_solution = TRUE AND language = 'pt-BR' AND length = 5
         ORDER BY id
         OFFSET 90 LIMIT 120
     ) w
    ON CONFLICT DO NOTHING;
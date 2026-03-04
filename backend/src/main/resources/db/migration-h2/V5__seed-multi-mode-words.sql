MERGE INTO daily_words (word_id, game_date, language, mode)
    KEY (game_date, language, mode, word_id)
SELECT
    w.id,
    DATEADD('DAY', (ROW_NUMBER() OVER (ORDER BY w.id) - 1) / 2, CURRENT_DATE),
    'pt-BR',
    'DUETO'
FROM (
         SELECT id FROM words
         WHERE is_solution = TRUE AND language = 'pt-BR' AND length = 5
         ORDER BY id DESC
             FETCH FIRST 20 ROWS ONLY
     ) w;

MERGE INTO daily_words (word_id, game_date, language, mode)
    KEY (game_date, language, mode, word_id)
SELECT
    w.id,
    DATEADD('DAY', (ROW_NUMBER() OVER (ORDER BY w.id) - 1) / 4, CURRENT_DATE),
    'pt-BR',
    'QUARTETO'
FROM (
         SELECT word, id FROM words
         WHERE is_solution = TRUE AND language = 'pt-BR' AND length = 5
         ORDER BY word
             FETCH FIRST 28 ROWS ONLY
     ) w;
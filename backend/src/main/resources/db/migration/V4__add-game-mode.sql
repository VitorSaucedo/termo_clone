CREATE TYPE game_mode AS ENUM ('CLASSIC', 'DUETO', 'QUARTETO');

ALTER TABLE daily_words
    ADD COLUMN mode game_mode NOT NULL DEFAULT 'CLASSIC';

ALTER TABLE daily_words
DROP CONSTRAINT uq_daily_word_date_lang;

ALTER TABLE daily_words
    ADD CONSTRAINT uq_daily_word_date_lang_mode
        UNIQUE (game_date, language, mode, word_id);

CREATE UNIQUE INDEX uq_classic_one_word_per_day
    ON daily_words (game_date, language)
    WHERE mode = 'CLASSIC';

CREATE INDEX idx_daily_words_mode
    ON daily_words (game_date, language, mode);

ALTER TABLE game_sessions
    ADD COLUMN mode game_mode NOT NULL DEFAULT 'CLASSIC';

ALTER TABLE game_sessions
DROP CONSTRAINT uq_session_user_daily;

ALTER TABLE game_sessions
    ADD CONSTRAINT uq_session_user_daily_mode
        UNIQUE (user_id, daily_word_id, mode);

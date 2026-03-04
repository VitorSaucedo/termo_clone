ALTER TABLE daily_words
    ADD COLUMN mode VARCHAR(10) NOT NULL DEFAULT 'CLASSIC'
        CHECK (mode IN ('CLASSIC', 'DUETO', 'QUARTETO'));

ALTER TABLE daily_words
DROP CONSTRAINT IF EXISTS uq_daily_word_date_lang;

ALTER TABLE daily_words
    ADD CONSTRAINT uq_daily_word_date_lang_mode
        UNIQUE (game_date, language, mode, word_id);

ALTER TABLE game_sessions
    ADD COLUMN mode VARCHAR(10) NOT NULL DEFAULT 'CLASSIC'
        CHECK (mode IN ('CLASSIC', 'DUETO', 'QUARTETO'));

ALTER TABLE game_sessions
DROP CONSTRAINT IF EXISTS uq_session_user_daily;

ALTER TABLE game_sessions
    ADD CONSTRAINT uq_session_user_daily_mode
        UNIQUE (user_id, daily_word_id);
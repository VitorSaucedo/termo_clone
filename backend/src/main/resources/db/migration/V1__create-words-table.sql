CREATE TABLE words (
                       id          BIGSERIAL       PRIMARY KEY,
                       word        VARCHAR(10)     NOT NULL,
                       length      SMALLINT        NOT NULL,
                       is_solution BOOLEAN         NOT NULL DEFAULT FALSE,
                       language    VARCHAR(10)     NOT NULL DEFAULT 'pt-BR',
                       created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

                       CONSTRAINT uq_word_language UNIQUE (word, language),
                       CONSTRAINT chk_word_length  CHECK (LENGTH(word) = length),
                       CONSTRAINT chk_word_chars   CHECK (word ~ '^[A-ZÁÀÂÃÉÊÍÓÔÕÚÜÇ]+$')
    );

CREATE INDEX idx_words_solution ON words (language, length)
    WHERE is_solution = TRUE;

CREATE INDEX idx_words_lookup ON words (word, language);

CREATE TABLE daily_words (
                             id          BIGSERIAL   PRIMARY KEY,
                             word_id     BIGINT      NOT NULL REFERENCES words (id) ON DELETE RESTRICT,
                             game_date   DATE        NOT NULL,
                             language    VARCHAR(10) NOT NULL DEFAULT 'pt-BR',
                             created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                             CONSTRAINT uq_daily_word_date_lang UNIQUE (game_date, language)
);

CREATE INDEX idx_daily_words_date ON daily_words (game_date DESC, language);

COMMENT ON TABLE words       IS 'Dicionário completo: palavras válidas para tentativa e/ou solução.';
COMMENT ON TABLE daily_words IS 'Agenda editorial da palavra do dia por idioma.';
COMMENT ON COLUMN words.is_solution IS 'TRUE = pode ser a palavra do dia. FALSE = válida apenas como tentativa.';
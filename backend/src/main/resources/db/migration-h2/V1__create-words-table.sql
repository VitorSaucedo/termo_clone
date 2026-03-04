CREATE TABLE words (
                       id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       word        VARCHAR(10)  NOT NULL,
                       length      SMALLINT     NOT NULL,
                       is_solution BOOLEAN      NOT NULL DEFAULT FALSE,
                       language    VARCHAR(10)  NOT NULL DEFAULT 'pt-BR',
                       created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

                       CONSTRAINT uq_word_language UNIQUE (word, language),
                       CONSTRAINT chk_word_length  CHECK (CHAR_LENGTH(word) = length)
);

CREATE INDEX idx_words_solution ON words (language, length);
CREATE INDEX idx_words_lookup   ON words (word, language);


CREATE TABLE daily_words (
                             id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             word_id     BIGINT      NOT NULL REFERENCES words (id) ON DELETE RESTRICT,
                             game_date   DATE        NOT NULL,
                             language    VARCHAR(10) NOT NULL DEFAULT 'pt-BR',
                             created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

                             CONSTRAINT uq_daily_word_date_lang UNIQUE (game_date, language)
);

CREATE INDEX idx_daily_words_date ON daily_words (game_date DESC, language);
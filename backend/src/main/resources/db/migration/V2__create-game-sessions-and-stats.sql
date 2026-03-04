CREATE TYPE game_status AS ENUM ('PLAYING', 'WON', 'LOST');

CREATE TABLE game_sessions (
                               id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id         VARCHAR(128)    NOT NULL,
                               daily_word_id   BIGINT          NOT NULL REFERENCES daily_words (id) ON DELETE RESTRICT,
                               attempts        JSONB           NOT NULL DEFAULT '[]',
                               status          game_status     NOT NULL DEFAULT 'PLAYING',
                               started_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                               finished_at     TIMESTAMPTZ,

                               CONSTRAINT uq_session_user_daily    UNIQUE (user_id, daily_word_id),
                               CONSTRAINT chk_attempts_is_array    CHECK (jsonb_typeof(attempts) = 'array'),
                               CONSTRAINT chk_max_attempts         CHECK (jsonb_array_length(attempts) <= 9),
                               CONSTRAINT chk_finished_at          CHECK (
                                   (status = 'PLAYING' AND finished_at IS NULL) OR
                                   (status <> 'PLAYING' AND finished_at IS NOT NULL)
                                   )
);

CREATE INDEX idx_sessions_user   ON game_sessions (user_id);
CREATE INDEX idx_sessions_daily  ON game_sessions (daily_word_id);
CREATE INDEX idx_sessions_status ON game_sessions (status) WHERE status = 'PLAYING';

CREATE TABLE user_stats (
                            user_id             VARCHAR(128)    PRIMARY KEY,
                            games_played        INT             NOT NULL DEFAULT 0,
                            games_won           INT             NOT NULL DEFAULT 0,
                            current_streak      INT             NOT NULL DEFAULT 0,
                            max_streak          INT             NOT NULL DEFAULT 0,
                            guess_distribution  JSONB           NOT NULL DEFAULT '{"1":0,"2":0,"3":0,"4":0,"5":0,"6":0}',
                            last_played_date    DATE,
                            updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

                            CONSTRAINT chk_games_won_lte_played CHECK (games_won <= games_played),
                            CONSTRAINT chk_streak_positive      CHECK (current_streak >= 0 AND max_streak >= 0)
);

COMMENT ON TABLE  game_sessions                 IS 'Uma sessão por usuário por palavra do dia.';
COMMENT ON COLUMN game_sessions.user_id         IS 'Pode ser UUID anônimo (fingerprint) ou ID autenticado.';
COMMENT ON COLUMN game_sessions.attempts        IS 'Array JSON com as tentativas em ordem de submissão.';
COMMENT ON TABLE  user_stats                    IS 'Estatísticas agregadas — atualizadas ao fim de cada partida.';
COMMENT ON COLUMN user_stats.guess_distribution IS 'Contagem de vitórias por número de tentativas usadas.';
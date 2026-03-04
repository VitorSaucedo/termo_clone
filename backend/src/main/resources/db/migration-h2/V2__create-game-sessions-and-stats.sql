CREATE TABLE game_sessions (
                               id              UUID            PRIMARY KEY,
                               user_id         VARCHAR(128)    NOT NULL,
                               daily_word_id   BIGINT          NOT NULL REFERENCES daily_words (id) ON DELETE RESTRICT,
                               attempts        VARCHAR(2048)   NOT NULL DEFAULT '[]',
                               status          VARCHAR(10)     NOT NULL DEFAULT 'PLAYING'
                                   CHECK (status IN ('PLAYING', 'WON', 'LOST')),
                               started_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                               finished_at     TIMESTAMP WITH TIME ZONE,

                               CONSTRAINT uq_session_user_daily UNIQUE (user_id, daily_word_id),
                               CONSTRAINT chk_finished_at CHECK (
                                   (status = 'PLAYING' AND finished_at IS NULL) OR
                                   (status <> 'PLAYING' AND finished_at IS NOT NULL)
                                   )
);

CREATE INDEX idx_sessions_user  ON game_sessions (user_id);
CREATE INDEX idx_sessions_daily ON game_sessions (daily_word_id);

CREATE TABLE user_stats (
                            user_id             VARCHAR(128)    PRIMARY KEY,
                            games_played        INT             NOT NULL DEFAULT 0,
                            games_won           INT             NOT NULL DEFAULT 0,
                            current_streak      INT             NOT NULL DEFAULT 0,
                            max_streak          INT             NOT NULL DEFAULT 0,
                            guess_distribution  VARCHAR(256)    NOT NULL DEFAULT '{"1":0,"2":0,"3":0,"4":0,"5":0,"6":0}',
                            last_played_date    DATE,
                            updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

                            CONSTRAINT chk_games_won_lte_played CHECK (games_won <= games_played),
                            CONSTRAINT chk_streak_positive      CHECK (current_streak >= 0 AND max_streak >= 0)
);
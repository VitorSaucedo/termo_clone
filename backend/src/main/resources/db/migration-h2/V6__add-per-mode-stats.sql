ALTER TABLE user_stats ADD COLUMN games_played_dueto    INT NOT NULL DEFAULT 0;
ALTER TABLE user_stats ADD COLUMN games_won_dueto       INT NOT NULL DEFAULT 0;
ALTER TABLE user_stats ADD COLUMN guess_dist_dueto      VARCHAR(256) NOT NULL DEFAULT '{}';
ALTER TABLE user_stats ADD COLUMN games_played_quarteto INT NOT NULL DEFAULT 0;
ALTER TABLE user_stats ADD COLUMN games_won_quarteto    INT NOT NULL DEFAULT 0;
ALTER TABLE user_stats ADD COLUMN guess_dist_quarteto   VARCHAR(512) NOT NULL DEFAULT '{}';
ALTER TABLE user_stats
    ADD COLUMN games_played_dueto       INT     NOT NULL DEFAULT 0,
    ADD COLUMN games_won_dueto          INT     NOT NULL DEFAULT 0,
    ADD COLUMN guess_dist_dueto         JSONB   NOT NULL DEFAULT '{"1":0,"2":0,"3":0,"4":0,"5":0,"6":0,"7":0}',
    ADD COLUMN games_played_quarteto    INT     NOT NULL DEFAULT 0,
    ADD COLUMN games_won_quarteto       INT     NOT NULL DEFAULT 0,
    ADD COLUMN guess_dist_quarteto      JSONB   NOT NULL DEFAULT '{"1":0,"2":0,"3":0,"4":0,"5":0,"6":0,"7":0,"8":0,"9":0}';
package com.vitorsaucedo.wordle.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "user_stats")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStats {

    @Id
    @Column(name = "user_id", length = 128)
    private String userId;

    @Column(name = "games_played", nullable = false)
    @Builder.Default
    private int gamesPlayed = 0;

    @Column(name = "games_won", nullable = false)
    @Builder.Default
    private int gamesWon = 0;

    @Column(name = "current_streak", nullable = false)
    @Builder.Default
    private int currentStreak = 0;

    @Column(name = "max_streak", nullable = false)
    @Builder.Default
    private int maxStreak = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "guess_distribution", nullable = false)
    @Builder.Default
    private Map<String, Integer> guessDistribution = new HashMap<>(Map.of(
            "1", 0, "2", 0, "3", 0, "4", 0, "5", 0, "6", 0
    ));

    @Column(name = "last_played_date")
    private LocalDate lastPlayedDate;

    @Column(name = "games_played_dueto", nullable = false)
    @Builder.Default
    private int gamesPlayedDueto = 0;

    @Column(name = "games_won_dueto", nullable = false)
    @Builder.Default
    private int gamesWonDueto = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "guess_dist_dueto", nullable = false)
    @Builder.Default
    private Map<String, Integer> guessDistributionDueto = new HashMap<>(Map.of(
            "1", 0, "2", 0, "3", 0, "4", 0, "5", 0, "6", 0, "7", 0
    ));

    @Column(name = "games_played_quarteto", nullable = false)
    @Builder.Default
    private int gamesPlayedQuarteto = 0;

    @Column(name = "games_won_quarteto", nullable = false)
    @Builder.Default
    private int gamesWonQuarteto = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "guess_dist_quarteto", nullable = false)
    @Builder.Default
    private Map<String, Integer> guessDistributionQuarteto = new HashMap<>(Map.of(
            "1", 0, "2", 0, "3", 0, "4", 0, "5", 0, "6", 0, "7", 0, "8", 0, "9", 0
    ));

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public void recordWin(int attemptsUsed, LocalDate gameDate, GameMode mode) {
        switch (mode) {
            case CLASSIC -> {
                this.gamesPlayed++;
                this.gamesWon++;
                this.guessDistribution.merge(String.valueOf(attemptsUsed), 1, Integer::sum);
                updateStreak(gameDate, true);
            }
            case DUETO -> {
                this.gamesPlayedDueto++;
                this.gamesWonDueto++;
                this.guessDistributionDueto.merge(String.valueOf(attemptsUsed), 1, Integer::sum);
            }
            case QUARTETO -> {
                this.gamesPlayedQuarteto++;
                this.gamesWonQuarteto++;
                this.guessDistributionQuarteto.merge(String.valueOf(attemptsUsed), 1, Integer::sum);
            }
        }
        this.updatedAt = OffsetDateTime.now();
    }

    public void recordLoss(LocalDate gameDate, GameMode mode) {
        switch (mode) {
            case CLASSIC -> {
                this.gamesPlayed++;
                updateStreak(gameDate, false);
            }
            case DUETO    -> this.gamesPlayedDueto++;
            case QUARTETO -> this.gamesPlayedQuarteto++;
        }
        this.updatedAt = OffsetDateTime.now();
    }

    private void updateStreak(LocalDate gameDate, boolean won) {
        boolean isConsecutiveDay = this.lastPlayedDate != null
                && gameDate.minusDays(1).equals(this.lastPlayedDate);

        if (won) {
            this.currentStreak = isConsecutiveDay ? this.currentStreak + 1 : 1;
            this.maxStreak = Math.max(this.maxStreak, this.currentStreak);
        } else {
            this.currentStreak = 0;
        }

        this.lastPlayedDate = gameDate;
    }

    public double getWinRate() {
        return gamesPlayed == 0 ? 0.0 : (double) gamesWon / gamesPlayed * 100;
    }

    public double getWinRateDueto() {
        return gamesPlayedDueto == 0 ? 0.0 : (double) gamesWonDueto / gamesPlayedDueto * 100;
    }

    public double getWinRateQuarteto() {
        return gamesPlayedQuarteto == 0 ? 0.0 : (double) gamesWonQuarteto / gamesPlayedQuarteto * 100;
    }
}
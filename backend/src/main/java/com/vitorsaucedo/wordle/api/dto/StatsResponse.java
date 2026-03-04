package com.vitorsaucedo.wordle.api.dto;

import java.util.Map;

public record StatsResponse(
        int gamesPlayed,
        int gamesWon,
        double winRate,
        int currentStreak,
        int maxStreak,
        Map<String, Integer> guessDistribution,
        int gamesPlayedDueto,
        int gamesWonDueto,
        double winRateDueto,
        Map<String, Integer> guessDistributionDueto,
        int gamesPlayedQuarteto,
        int gamesWonQuarteto,
        double winRateQuarteto,
        Map<String, Integer> guessDistributionQuarteto
) {}
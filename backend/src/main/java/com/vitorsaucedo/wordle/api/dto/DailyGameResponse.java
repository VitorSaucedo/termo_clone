package com.vitorsaucedo.wordle.api.dto;

import com.vitorsaucedo.wordle.domain.GameMode;
import com.vitorsaucedo.wordle.domain.GameStatus;
import com.vitorsaucedo.wordle.service.TileEvaluation;

import java.util.List;
import java.util.UUID;

public record DailyGameResponse(
        UUID sessionId,
        int wordLength,
        int maxAttempts,
        int attemptsMade,
        List<String> pastGuesses,
        List<List<TileEvaluation>> pastEvaluations,
        GameStatus status,
        GameMode mode
) {}
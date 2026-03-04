package com.vitorsaucedo.wordle.api.dto;

import com.vitorsaucedo.wordle.domain.GameStatus;
import com.vitorsaucedo.wordle.service.TileEvaluation;

import java.util.List;
import java.util.UUID;

public record MultiGuessResponse(
        List<GridResult> gridResults,
        int attemptNumber,
        GameStatus globalStatus
) {
    public record GridResult(
            UUID sessionId,
            List<TileEvaluation> evaluations,
            GameStatus status,
            String solution
    ) {}
}
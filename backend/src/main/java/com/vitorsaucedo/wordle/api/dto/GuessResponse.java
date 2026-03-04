package com.vitorsaucedo.wordle.api.dto;

import com.vitorsaucedo.wordle.domain.GameStatus;
import com.vitorsaucedo.wordle.service.TileEvaluation;

import java.util.List;

public record GuessResponse(
        List<TileEvaluation> evaluations,
        int attemptNumber,
        GameStatus status,
        String solution
) {}
package com.vitorsaucedo.wordle.api.dto;

import com.vitorsaucedo.wordle.domain.GameMode;
import com.vitorsaucedo.wordle.domain.GameStatus;

import java.util.List;

public record MultiGameResponse(
        GameMode mode,
        List<DailyGameResponse> grids,
        GameStatus globalStatus,
        int attemptsMade,
        int maxAttempts
) {}
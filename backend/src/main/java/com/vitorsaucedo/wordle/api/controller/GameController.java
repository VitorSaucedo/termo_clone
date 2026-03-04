package com.vitorsaucedo.wordle.api.controller;

import com.vitorsaucedo.wordle.api.dto.*;
import com.vitorsaucedo.wordle.domain.GameMode;
import com.vitorsaucedo.wordle.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/daily")
    public ResponseEntity<DailyGameResponse> getDailyGame(@RequestParam String userId) {
        return ResponseEntity.ok(gameService.getDailyGame(userId));
    }

    @PostMapping("/guess")
    public ResponseEntity<GuessResponse> submitGuess(@Valid @RequestBody GuessRequest request) {
        return ResponseEntity.ok(gameService.submitGuess(request));
    }

    @GetMapping("/multi")
    public ResponseEntity<MultiGameResponse> getMultiGame(
            @RequestParam String userId,
            @RequestParam GameMode mode
    ) {
        return ResponseEntity.ok(gameService.getMultiGame(userId, mode));
    }

    @PostMapping("/multi/guess")
    public ResponseEntity<MultiGuessResponse> submitMultiGuess(
            @Valid @RequestBody MultiGuessRequest request
    ) {
        return ResponseEntity.ok(gameService.submitMultiGuess(request));
    }
}
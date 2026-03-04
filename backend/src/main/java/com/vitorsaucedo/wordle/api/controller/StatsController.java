package com.vitorsaucedo.wordle.api.controller;

import com.vitorsaucedo.wordle.api.dto.StatsResponse;
import com.vitorsaucedo.wordle.service.StatsService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/{userId}")
    public ResponseEntity<StatsResponse> getStats(
            @PathVariable @NotBlank String userId
    ) {
        return ResponseEntity.ok(statsService.getStats(userId));
    }
}
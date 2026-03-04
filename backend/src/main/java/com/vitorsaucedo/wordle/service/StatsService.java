package com.vitorsaucedo.wordle.service;

import com.vitorsaucedo.wordle.api.dto.StatsResponse;
import com.vitorsaucedo.wordle.domain.GameMode;
import com.vitorsaucedo.wordle.domain.GameSession;
import com.vitorsaucedo.wordle.domain.GameStatus;
import com.vitorsaucedo.wordle.domain.UserStats;
import com.vitorsaucedo.wordle.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserStatsRepository userStatsRepository;

    @Transactional
    public void updateStats(GameSession session) {
        if (session.getStatus() == GameStatus.PLAYING) return;

        String userId   = session.getUserId();
        LocalDate date  = session.getDailyWord().getGameDate();
        GameMode mode   = session.getMode();

        UserStats stats = userStatsRepository
                .findById(userId)
                .orElseGet(() -> UserStats.builder().userId(userId).build());

        if (session.getStatus() == GameStatus.WON) {
            stats.recordWin(session.getAttempts().size(), date, mode);
            log.debug("Vitória [{}] registrada para {} em {} tentativas", mode, userId, session.getAttempts().size());
        } else {
            stats.recordLoss(date, mode);
            log.debug("Derrota [{}] registrada para {}", mode, userId);
        }

        userStatsRepository.save(stats);
    }

    @Transactional(readOnly = true)
    public StatsResponse getStats(String userId) {
        return userStatsRepository
                .findById(userId)
                .map(s -> new StatsResponse(
                        // CLASSIC
                        s.getGamesPlayed(),
                        s.getGamesWon(),
                        s.getWinRate(),
                        s.getCurrentStreak(),
                        s.getMaxStreak(),
                        s.getGuessDistribution(),
                        s.getGamesPlayedDueto(),
                        s.getGamesWonDueto(),
                        s.getWinRateDueto(),
                        s.getGuessDistributionDueto(),
                        s.getGamesPlayedQuarteto(),
                        s.getGamesWonQuarteto(),
                        s.getWinRateQuarteto(),
                        s.getGuessDistributionQuarteto()
                ))
                .orElse(new StatsResponse(
                        0, 0, 0.0, 0, 0,
                        Map.of("1",0,"2",0,"3",0,"4",0,"5",0,"6",0),
                        0, 0, 0.0,
                        Map.of("1",0,"2",0,"3",0,"4",0,"5",0,"6",0,"7",0),
                        0, 0, 0.0,
                        Map.of("1",0,"2",0,"3",0,"4",0,"5",0,"6",0,"7",0,"8",0,"9",0)
                ));
    }
}
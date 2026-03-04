package com.vitorsaucedo.wordle.service;

import com.vitorsaucedo.wordle.domain.DailyWord;
import com.vitorsaucedo.wordle.domain.GameSession;
import com.vitorsaucedo.wordle.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionCreatorService {

    private final GameSessionRepository sessionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GameSession createSession(String userId, DailyWord dailyWord) {
        log.debug("Criando nova sessão para usuário {} na palavra {}", userId, dailyWord.getId());
        return sessionRepository.saveAndFlush(
                GameSession.builder()
                        .userId(userId)
                        .dailyWord(dailyWord)
                        .mode(dailyWord.getMode())
                        .build()
        );
    }
}
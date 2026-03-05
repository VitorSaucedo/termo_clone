package com.vitorsaucedo.wordle.service;

import com.vitorsaucedo.wordle.domain.DailyWord;
import com.vitorsaucedo.wordle.domain.GameMode;
import com.vitorsaucedo.wordle.exception.DailyWordNotFoundException;
import com.vitorsaucedo.wordle.repository.DailyWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyWordService {

    private final DailyWordRepository dailyWordRepository;

    @Cacheable(cacheNames = "palavraDoDia", key = "#mode.name() + '::' + #language + '::' + T(java.time.LocalDate).now()")
    public DailyWord getTodaysWord(GameMode mode, String language) {
        log.debug("Buscando palavra do dia no banco para {} [{}] modo {}", LocalDate.now(), language, mode);
        return dailyWordRepository
                .findByGameDateAndLanguageAndMode(LocalDate.now(), language, mode)
                .orElseThrow(() -> new DailyWordNotFoundException(LocalDate.now(), language));
    }

    @Cacheable(cacheNames = "palavrasDoDia", key = "#mode.name() + '::' + #language + '::' + T(java.time.LocalDate).now()")
    public List<DailyWord> getTodaysWords(GameMode mode, String language) {
        log.debug("Buscando palavras do dia no banco para {} [{}] modo {}", LocalDate.now(), language, mode);
        List<DailyWord> words = dailyWordRepository
                .findAllByGameDateAndLanguageAndMode(LocalDate.now(), language, mode);
        if (words.isEmpty()) {
            throw new DailyWordNotFoundException(LocalDate.now(), language);
        }
        return words;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @CacheEvict(cacheNames = {"palavraDoDia", "palavrasDoDia"}, allEntries = true)
    public void evictDailyWordCache() {
        log.info("Cache de palavras do dia invalidado.");
    }
}
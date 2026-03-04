package com.vitorsaucedo.wordle.service;

import com.vitorsaucedo.wordle.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;

    @Cacheable(cacheNames = "validacaoPalavra", key = "#word + '::' + #language")
    @Transactional(readOnly = true)
    public boolean isValidWord(String word, String language) {
        return wordRepository.existsByWordIgnoreCaseAndLanguage(word.toUpperCase(), language);
    }
}
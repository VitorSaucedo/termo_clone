package com.vitorsaucedo.wordle.repository;

import com.vitorsaucedo.wordle.domain.DailyWord;
import com.vitorsaucedo.wordle.domain.GameMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyWordRepository extends JpaRepository<DailyWord, Long> {

    @Query("""
        SELECT dw FROM DailyWord dw
        JOIN FETCH dw.word
        WHERE dw.gameDate = :date
          AND dw.language = :language
          AND dw.mode     = :mode
        """)
    Optional<DailyWord> findByGameDateAndLanguageAndMode(
            @Param("date") LocalDate date,
            @Param("language") String language,
            @Param("mode") GameMode mode
    );

    @Query("""
        SELECT dw FROM DailyWord dw
        JOIN FETCH dw.word
        WHERE dw.gameDate = :date
          AND dw.language = :language
          AND dw.mode     = :mode
        ORDER BY dw.id
        """)
    List<DailyWord> findAllByGameDateAndLanguageAndMode(
            @Param("date") LocalDate date,
            @Param("language") String language,
            @Param("mode") GameMode mode
    );
}
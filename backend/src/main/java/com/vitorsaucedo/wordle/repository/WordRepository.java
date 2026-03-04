package com.vitorsaucedo.wordle.repository;

import com.vitorsaucedo.wordle.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {

    boolean existsByWordIgnoreCaseAndLanguage(String word, String language);

    Optional<Word> findByWordIgnoreCaseAndLanguage(String word, String language);

    @Query("""
        SELECT COUNT(w) FROM Word w
        WHERE w.solution = true
          AND w.language = :language
          AND w.length   = :length
        """)
    long countSolutions(@Param("language") String language,
                        @Param("length") short length);
}
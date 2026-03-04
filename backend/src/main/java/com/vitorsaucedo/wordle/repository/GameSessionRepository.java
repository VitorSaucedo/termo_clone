package com.vitorsaucedo.wordle.repository;

import com.vitorsaucedo.wordle.domain.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {

    @Query("""
        SELECT gs FROM GameSession gs
        JOIN FETCH gs.dailyWord dw
        JOIN FETCH dw.word
        WHERE gs.id = :id
        """)
    Optional<GameSession> findByIdWithWord(@Param("id") UUID id);

    @Query("""
        SELECT gs FROM GameSession gs
        JOIN FETCH gs.dailyWord dw
        JOIN FETCH dw.word
        WHERE gs.userId   = :userId
          AND dw.id       = :dailyWordId
        """)
    Optional<GameSession> findByUserIdAndDailyWordId(
            @Param("userId") String userId,
            @Param("dailyWordId") Long dailyWordId
    );
}
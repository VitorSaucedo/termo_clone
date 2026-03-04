package com.vitorsaucedo.wordle.repository;

import com.vitorsaucedo.wordle.domain.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatsRepository extends JpaRepository<UserStats, String> {
    // String = tipo do @Id (userId)
    // findById(userId) e save() herdados do JpaRepository são suficientes
}
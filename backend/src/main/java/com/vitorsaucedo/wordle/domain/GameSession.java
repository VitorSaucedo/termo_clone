package com.vitorsaucedo.wordle.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "game_sessions")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "daily_word_id", nullable = false)
    private DailyWord dailyWord;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    @Builder.Default
    private List<String> attempts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "game_status")
    @Builder.Default
    private GameStatus status = GameStatus.PLAYING;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "game_mode")
    @Builder.Default
    private GameMode mode = GameMode.CLASSIC;

    @Column(name = "started_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime startedAt = OffsetDateTime.now();

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    public void addAttempt(String guess, String solution) {
        if (this.status != GameStatus.PLAYING) {
            throw new IllegalStateException("Partida já encerrada.");
        }
        this.attempts.add(guess);

        if (guess.equalsIgnoreCase(solution)) {
            finish(GameStatus.WON);
        } else if (this.attempts.size() >= maxAttempts()) {
            finish(GameStatus.LOST);
        }
    }

    public int maxAttempts() {
        return switch (this.mode) {
            case CLASSIC  -> 6;
            case DUETO    -> 7;
            case QUARTETO -> 9;
        };
    }

    private void finish(GameStatus finalStatus) {
        this.status    = finalStatus;
        this.finishedAt = OffsetDateTime.now();
    }

    public boolean isFinished() {
        return this.status != GameStatus.PLAYING;
    }
}
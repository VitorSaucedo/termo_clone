package com.vitorsaucedo.wordle.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "daily_words")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "game_date", nullable = false)
    private java.time.LocalDate gameDate;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String language = "pt-BR";

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "game_mode")
    @Builder.Default
    private GameMode mode = GameMode.CLASSIC;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
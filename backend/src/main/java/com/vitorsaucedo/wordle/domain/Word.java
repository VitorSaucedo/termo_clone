package com.vitorsaucedo.wordle.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "words",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_word_language",
                columnNames = {"word", "language"}
        )
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String word;

    @Column(nullable = false)
    private Short length;

    @Column(name = "is_solution", nullable = false)
    @Builder.Default
    private boolean solution = false;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String language = "pt-BR";

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
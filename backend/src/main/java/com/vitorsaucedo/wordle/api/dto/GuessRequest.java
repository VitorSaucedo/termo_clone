package com.vitorsaucedo.wordle.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record GuessRequest(

        @NotNull(message = "sessionId é obrigatório")
        UUID sessionId,

        @NotBlank(message = "guess é obrigatório")
        @Size(min = 5, max = 5, message = "A palavra deve ter exatamente 5 letras")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ]+$", message = "A palavra deve conter apenas letras")
        String guess

) {}
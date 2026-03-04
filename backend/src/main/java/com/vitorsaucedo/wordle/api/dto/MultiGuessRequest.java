package com.vitorsaucedo.wordle.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record MultiGuessRequest(

        @NotEmpty
        List<UUID> sessionIds,

        @NotBlank
        @Size(min = 5, max = 5, message = "A palavra deve ter exatamente 5 letras")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ]+$", message = "A palavra deve conter apenas letras")
        String guess

) {}
package com.vitorsaucedo.wordle.exception;

import java.util.UUID;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(UUID sessionId) {
        super("Sessão não encontrada: %s".formatted(sessionId));
    }
}
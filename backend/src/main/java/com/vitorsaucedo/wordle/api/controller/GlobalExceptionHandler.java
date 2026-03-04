package com.vitorsaucedo.wordle.api.controller;

import com.vitorsaucedo.wordle.exception.DailyWordNotFoundException;
import com.vitorsaucedo.wordle.exception.GameAlreadyFinishedException;
import com.vitorsaucedo.wordle.exception.InvalidWordException;
import com.vitorsaucedo.wordle.exception.SessionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DailyWordNotFoundException.class)
    public ProblemDetail handleDailyWordNotFound(DailyWordNotFoundException ex) {
        log.warn("Palavra do dia ausente: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ProblemDetail handleSessionNotFound(SessionNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(GameAlreadyFinishedException.class)
    public ProblemDetail handleGameFinished(GameAlreadyFinishedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidWordException.class)
    public ProblemDetail handleInvalidWord(InvalidWordException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> "%s: %s".formatted(e.getField(), e.getDefaultMessage()))
                .collect(Collectors.joining("; "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }

    /** Loga o tipo real da exceção para facilitar o diagnóstico */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Erro inesperado [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno. Tente novamente mais tarde.");
    }
}
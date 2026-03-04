package com.vitorsaucedo.wordle.exception;

import java.time.LocalDate;

public class DailyWordNotFoundException extends RuntimeException {

    public DailyWordNotFoundException(String date, String language) {
        super("Nenhuma palavra do dia cadastrada para %s [%s].".formatted(date, language));
    }

    public DailyWordNotFoundException(LocalDate date, String language) {
        this(date.toString(), language);
    }
}
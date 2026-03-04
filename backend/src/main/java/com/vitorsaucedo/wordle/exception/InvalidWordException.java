package com.vitorsaucedo.wordle.exception;

public class InvalidWordException extends RuntimeException {
  public InvalidWordException(String word) {
    super("'%s' não é uma palavra válida.".formatted(word));
  }
}
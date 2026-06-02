package com.example.habittracker.exception;

public class DuplicateHabitCheckException extends RuntimeException {

    public DuplicateHabitCheckException(String message) {
        super(message);
    }
}

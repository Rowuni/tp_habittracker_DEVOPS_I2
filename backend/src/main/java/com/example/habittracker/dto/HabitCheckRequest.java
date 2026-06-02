package com.example.habittracker.dto;

import java.time.LocalDate;

public record HabitCheckRequest(
        LocalDate checkDate,
        Boolean completed
) {
}

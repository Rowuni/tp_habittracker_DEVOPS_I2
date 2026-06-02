package com.example.habittracker.dto;

public record HabitStreakResponse(
        Long habitId,
        int currentStreak
) {
}

package com.example.habittracker.dto;

public record HabitStatsResponse(
        Long habitId,
        String habitName,
        int currentStreak,
        long completedDaysLast7,
        double progressionPercentage
) {
}

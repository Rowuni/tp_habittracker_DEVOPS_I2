package com.example.habittracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HabitRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must be 120 characters or less")
        String name,

        @Size(max = 1000, message = "Description must be 1000 characters or less")
        String description,

        @Size(max = 80, message = "Category must be 80 characters or less")
        String category,

        @NotBlank(message = "Target frequency is required")
        @Size(max = 80, message = "Target frequency must be 80 characters or less")
        String targetFrequency
) {
}

package com.example.habittracker.service;

import com.example.habittracker.dto.HabitCheckRequest;
import com.example.habittracker.dto.HabitStatsResponse;
import com.example.habittracker.exception.DuplicateHabitCheckException;
import com.example.habittracker.exception.ResourceNotFoundException;
import com.example.habittracker.model.Habit;
import com.example.habittracker.model.HabitCheck;
import com.example.habittracker.repository.HabitCheckRepository;
import com.example.habittracker.repository.HabitRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HabitCheckService {

    private static final int STAT_DAYS = 7;

    private final HabitRepository habitRepository;
    private final HabitCheckRepository habitCheckRepository;
    private final Clock clock;

    public HabitCheckService(
            HabitRepository habitRepository,
            HabitCheckRepository habitCheckRepository,
            Clock clock
    ) {
        this.habitRepository = habitRepository;
        this.habitCheckRepository = habitCheckRepository;
        this.clock = clock;
    }

    public HabitCheck checkHabit(Long habitId, HabitCheckRequest request) {
        ensureHabitExists(habitId);

        LocalDate date = request != null && request.checkDate() != null
                ? request.checkDate()
                : LocalDate.now(clock);
        boolean completed = request == null || request.completed() == null || request.completed();

        habitCheckRepository.findByHabitIdAndCheckDate(habitId, date)
                .ifPresent(existing -> {
                    throw new DuplicateHabitCheckException("Habit already checked for " + date);
                });

        return habitCheckRepository.save(new HabitCheck(habitId, date, completed));
    }

    public void deleteCheck(Long habitId, LocalDate date) {
        ensureHabitExists(habitId);
        HabitCheck check = habitCheckRepository.findByHabitIdAndCheckDate(habitId, date)
                .orElseThrow(() -> new ResourceNotFoundException("Habit check not found for " + date));
        habitCheckRepository.delete(check);
    }

    public List<HabitCheck> getChecks(Long habitId) {
        ensureHabitExists(habitId);
        return habitCheckRepository.findByHabitIdOrderByCheckDateDesc(habitId);
    }

    public int getCurrentStreak(Long habitId) {
        ensureHabitExists(habitId);

        int streak = 0;
        LocalDate date = LocalDate.now(clock);

        while (true) {
            HabitCheck check = habitCheckRepository.findByHabitIdAndCheckDate(habitId, date)
                    .filter(HabitCheck::isCompleted)
                    .orElse(null);

            if (check == null) {
                return streak;
            }

            streak++;
            date = date.minusDays(1);
        }
    }

    public HabitStatsResponse getStats(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ResourceNotFoundException("Habit not found with id " + habitId));

        LocalDate today = LocalDate.now(clock);
        LocalDate startDate = today.minusDays(STAT_DAYS - 1L);
        long completedDays = habitCheckRepository.countByHabitIdAndCompletedTrueAndCheckDateBetween(
                habitId,
                startDate,
                today
        );
        double progression = Math.round((completedDays * 1000.0) / STAT_DAYS) / 10.0;

        return new HabitStatsResponse(
                habit.getId(),
                habit.getName(),
                getCurrentStreak(habitId),
                completedDays,
                progression
        );
    }

    private void ensureHabitExists(Long habitId) {
        if (!habitRepository.existsById(habitId)) {
            throw new ResourceNotFoundException("Habit not found with id " + habitId);
        }
    }
}

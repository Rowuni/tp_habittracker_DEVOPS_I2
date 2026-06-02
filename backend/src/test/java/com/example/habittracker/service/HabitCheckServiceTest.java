package com.example.habittracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.habittracker.dto.HabitCheckRequest;
import com.example.habittracker.dto.HabitStatsResponse;
import com.example.habittracker.exception.DuplicateHabitCheckException;
import com.example.habittracker.model.Habit;
import com.example.habittracker.model.HabitCheck;
import com.example.habittracker.repository.HabitCheckRepository;
import com.example.habittracker.repository.HabitRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HabitCheckServiceTest {

    private static final ZoneId ZONE = ZoneId.of("UTC");
    private static final LocalDate TODAY = LocalDate.of(2026, 6, 2);

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitCheckRepository habitCheckRepository;

    private HabitCheckService habitCheckService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-02T08:00:00Z"), ZONE);
        habitCheckService = new HabitCheckService(habitRepository, habitCheckRepository, clock);
    }

    @Test
    void checkHabitForTodayCreatesCompletedCheck() {
        when(habitRepository.existsById(1L)).thenReturn(true);
        when(habitCheckRepository.findByHabitIdAndCheckDate(1L, TODAY)).thenReturn(Optional.empty());
        when(habitCheckRepository.save(any(HabitCheck.class))).thenAnswer(invocation -> {
            HabitCheck check = invocation.getArgument(0);
            check.setId(10L);
            return check;
        });

        HabitCheck check = habitCheckService.checkHabit(1L, new HabitCheckRequest(null, true));

        assertEquals(10L, check.getId());
        assertEquals(1L, check.getHabitId());
        assertEquals(TODAY, check.getCheckDate());
        assertEquals(true, check.isCompleted());
    }

    @Test
    void checkHabitPreventsDuplicateCheckOnSameDay() {
        when(habitRepository.existsById(1L)).thenReturn(true);
        when(habitCheckRepository.findByHabitIdAndCheckDate(1L, TODAY))
                .thenReturn(Optional.of(new HabitCheck(1L, TODAY, true)));

        assertThrows(
                DuplicateHabitCheckException.class,
                () -> habitCheckService.checkHabit(1L, new HabitCheckRequest(null, true))
        );
        verify(habitCheckRepository, never()).save(any(HabitCheck.class));
    }

    @Test
    void getCurrentStreakCountsCompletedConsecutiveDaysFromToday() {
        when(habitRepository.existsById(1L)).thenReturn(true);
        when(habitCheckRepository.findByHabitIdAndCheckDate(1L, TODAY))
                .thenReturn(Optional.of(new HabitCheck(1L, TODAY, true)));
        when(habitCheckRepository.findByHabitIdAndCheckDate(1L, TODAY.minusDays(1)))
                .thenReturn(Optional.of(new HabitCheck(1L, TODAY.minusDays(1), true)));
        when(habitCheckRepository.findByHabitIdAndCheckDate(1L, TODAY.minusDays(2)))
                .thenReturn(Optional.of(new HabitCheck(1L, TODAY.minusDays(2), true)));
        when(habitCheckRepository.findByHabitIdAndCheckDate(1L, TODAY.minusDays(3)))
                .thenReturn(Optional.empty());

        int streak = habitCheckService.getCurrentStreak(1L);

        assertEquals(3, streak);
    }

    @Test
    void getStatsCalculatesProgressionOverLastSevenDays() {
        Habit habit = new Habit("Read", "Read 20 pages", "Learning", "Daily");
        habit.setId(1L);
        habit.setCreatedAt(LocalDateTime.now());

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepository.existsById(1L)).thenReturn(true);
        when(habitCheckRepository.countByHabitIdAndCompletedTrueAndCheckDateBetween(
                1L,
                TODAY.minusDays(6),
                TODAY
        )).thenReturn(5L);
        when(habitCheckRepository.findByHabitIdAndCheckDate(1L, TODAY)).thenReturn(Optional.empty());

        HabitStatsResponse stats = habitCheckService.getStats(1L);

        assertEquals(1L, stats.habitId());
        assertEquals("Read", stats.habitName());
        assertEquals(0, stats.currentStreak());
        assertEquals(5L, stats.completedDaysLast7());
        assertEquals(71.4, stats.progressionPercentage(), 0.001);
    }
}

package com.example.habittracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.habittracker.dto.HabitRequest;
import com.example.habittracker.model.Habit;
import com.example.habittracker.repository.HabitCheckRepository;
import com.example.habittracker.repository.HabitRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitCheckRepository habitCheckRepository;

    @InjectMocks
    private HabitService habitService;

    @Test
    void createHabitSavesHabit() {
        HabitRequest request = new HabitRequest("Read", "Read 20 pages", "Learning", "Daily");

        when(habitRepository.save(any(Habit.class))).thenAnswer(invocation -> {
            Habit habit = invocation.getArgument(0);
            habit.setId(1L);
            habit.setCreatedAt(LocalDateTime.now());
            return habit;
        });

        Habit created = habitService.createHabit(request);

        assertEquals(1L, created.getId());
        assertEquals("Read", created.getName());
        assertEquals("Read 20 pages", created.getDescription());
        assertEquals("Learning", created.getCategory());
        assertEquals("Daily", created.getTargetFrequency());
        verify(habitRepository).save(any(Habit.class));
    }

    @Test
    void updateHabitChangesExistingHabit() {
        Habit existing = habit(1L, "Read", "Old description", "Learning", "Daily");
        HabitRequest request = new HabitRequest("Workout", "Move for 30 minutes", "Fitness", "Daily");

        when(habitRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(habitRepository.save(any(Habit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Habit updated = habitService.updateHabit(1L, request);

        assertEquals("Workout", updated.getName());
        assertEquals("Move for 30 minutes", updated.getDescription());
        assertEquals("Fitness", updated.getCategory());
        assertEquals("Daily", updated.getTargetFrequency());
        verify(habitRepository).save(existing);
    }

    @Test
    void deleteHabitDeletesHabitAndChecks() {
        Habit existing = habit(1L, "Read", "Read 20 pages", "Learning", "Daily");

        when(habitRepository.findById(1L)).thenReturn(Optional.of(existing));

        habitService.deleteHabit(1L);

        verify(habitCheckRepository).deleteByHabitId(1L);
        verify(habitRepository).delete(existing);
    }

    private Habit habit(Long id, String name, String description, String category, String targetFrequency) {
        Habit habit = new Habit(name, description, category, targetFrequency);
        habit.setId(id);
        habit.setCreatedAt(LocalDateTime.now());
        return habit;
    }
}

package com.example.habittracker.service;

import com.example.habittracker.dto.HabitRequest;
import com.example.habittracker.exception.ResourceNotFoundException;
import com.example.habittracker.model.Habit;
import com.example.habittracker.repository.HabitCheckRepository;
import com.example.habittracker.repository.HabitRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitCheckRepository habitCheckRepository;

    public HabitService(HabitRepository habitRepository, HabitCheckRepository habitCheckRepository) {
        this.habitRepository = habitRepository;
        this.habitCheckRepository = habitCheckRepository;
    }

    public List<Habit> getAllHabits() {
        return habitRepository.findAll();
    }

    public Habit getHabitById(Long id) {
        return habitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habit not found with id " + id));
    }

    public Habit createHabit(HabitRequest request) {
        Habit habit = new Habit(
                request.name(),
                request.description(),
                request.category(),
                request.targetFrequency()
        );
        return habitRepository.save(habit);
    }

    public Habit updateHabit(Long id, HabitRequest request) {
        Habit habit = getHabitById(id);
        habit.setName(request.name());
        habit.setDescription(request.description());
        habit.setCategory(request.category());
        habit.setTargetFrequency(request.targetFrequency());
        return habitRepository.save(habit);
    }

    @Transactional
    public void deleteHabit(Long id) {
        Habit habit = getHabitById(id);
        habitCheckRepository.deleteByHabitId(id);
        habitRepository.delete(habit);
    }
}

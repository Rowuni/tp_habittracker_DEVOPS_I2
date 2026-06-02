package com.example.habittracker.controller;

import com.example.habittracker.dto.HabitRequest;
import com.example.habittracker.model.Habit;
import com.example.habittracker.service.HabitService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/habits")
@CrossOrigin(origins = "http://localhost:4200")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping
    public List<Habit> getAllHabits() {
        return habitService.getAllHabits();
    }

    @GetMapping("/{id}")
    public Habit getHabitById(@PathVariable Long id) {
        return habitService.getHabitById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Habit createHabit(@Valid @RequestBody HabitRequest request) {
        return habitService.createHabit(request);
    }

    @PutMapping("/{id}")
    public Habit updateHabit(@PathVariable Long id, @Valid @RequestBody HabitRequest request) {
        return habitService.updateHabit(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHabit(@PathVariable Long id) {
        habitService.deleteHabit(id);
    }
}

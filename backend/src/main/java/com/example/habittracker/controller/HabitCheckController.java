package com.example.habittracker.controller;

import com.example.habittracker.dto.HabitCheckRequest;
import com.example.habittracker.dto.HabitStatsResponse;
import com.example.habittracker.dto.HabitStreakResponse;
import com.example.habittracker.model.HabitCheck;
import com.example.habittracker.service.HabitCheckService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/habits")
@CrossOrigin(origins = "http://localhost:4200")
public class HabitCheckController {

    private final HabitCheckService habitCheckService;

    public HabitCheckController(HabitCheckService habitCheckService) {
        this.habitCheckService = habitCheckService;
    }

    @PostMapping("/{id}/check")
    @ResponseStatus(HttpStatus.CREATED)
    public HabitCheck checkHabit(@PathVariable Long id, @RequestBody(required = false) HabitCheckRequest request) {
        return habitCheckService.checkHabit(id, request);
    }

    @DeleteMapping("/{id}/check/{date}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCheck(
            @PathVariable Long id,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        habitCheckService.deleteCheck(id, date);
    }

    @GetMapping("/{id}/checks")
    public List<HabitCheck> getChecks(@PathVariable Long id) {
        return habitCheckService.getChecks(id);
    }

    @GetMapping("/{id}/streak")
    public HabitStreakResponse getStreak(@PathVariable Long id) {
        return new HabitStreakResponse(id, habitCheckService.getCurrentStreak(id));
    }

    @GetMapping("/{id}/stats")
    public HabitStatsResponse getStats(@PathVariable Long id) {
        return habitCheckService.getStats(id);
    }
}

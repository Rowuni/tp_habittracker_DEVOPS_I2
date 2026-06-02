package com.example.habittracker.config;

import com.example.habittracker.model.Habit;
import com.example.habittracker.model.HabitCheck;
import com.example.habittracker.repository.HabitCheckRepository;
import com.example.habittracker.repository.HabitRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final HabitRepository habitRepository;
    private final HabitCheckRepository habitCheckRepository;
    private final Clock clock;

    public DataInitializer(HabitRepository habitRepository, HabitCheckRepository habitCheckRepository, Clock clock) {
        this.habitRepository = habitRepository;
        this.habitCheckRepository = habitCheckRepository;
        this.clock = clock;
    }

    @Override
    public void run(String... args) {
        if (habitRepository.count() > 0) {
            return;
        }

        Habit reading = habitRepository.save(new Habit(
                "Read 20 pages",
                "Read every evening to keep a calm and consistent learning rhythm.",
                "Learning",
                "Daily"
        ));
        Habit water = habitRepository.save(new Habit(
                "Drink water",
                "Reach the daily hydration goal before dinner.",
                "Health",
                "Daily"
        ));
        Habit stretch = habitRepository.save(new Habit(
                "Morning stretch",
                "Spend ten minutes stretching before starting work.",
                "Fitness",
                "Daily"
        ));

        LocalDate today = LocalDate.now(clock);

        habitCheckRepository.saveAll(List.of(
                new HabitCheck(reading.getId(), today, true),
                new HabitCheck(reading.getId(), today.minusDays(1), true),
                new HabitCheck(reading.getId(), today.minusDays(2), true),
                new HabitCheck(reading.getId(), today.minusDays(4), true),
                new HabitCheck(water.getId(), today, true),
                new HabitCheck(water.getId(), today.minusDays(2), true),
                new HabitCheck(water.getId(), today.minusDays(3), true),
                new HabitCheck(stretch.getId(), today.minusDays(1), true),
                new HabitCheck(stretch.getId(), today.minusDays(5), true)
        ));
    }
}

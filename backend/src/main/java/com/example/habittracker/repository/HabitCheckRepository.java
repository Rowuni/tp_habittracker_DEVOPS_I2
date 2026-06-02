package com.example.habittracker.repository;

import com.example.habittracker.model.HabitCheck;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitCheckRepository extends JpaRepository<HabitCheck, Long> {

    Optional<HabitCheck> findByHabitIdAndCheckDate(Long habitId, LocalDate checkDate);

    List<HabitCheck> findByHabitIdOrderByCheckDateDesc(Long habitId);

    long countByHabitIdAndCompletedTrueAndCheckDateBetween(Long habitId, LocalDate startDate, LocalDate endDate);

    void deleteByHabitId(Long habitId);
}

import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';

import { Habit, HabitStats } from '../../models/habit.model';
import { HabitService } from '../../services/habit.service';

interface HabitRow {
  habit: Habit;
  stats: HabitStats | null;
  checking: boolean;
  error: string;
}

@Component({
  selector: 'app-habit-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './habit-list.component.html'
})
export class HabitListComponent implements OnInit {
  rows: HabitRow[] = [];
  loading = true;
  error = '';

  constructor(private readonly habitService: HabitService) {
  }

  ngOnInit(): void {
    this.loadHabits();
  }

  loadHabits(): void {
    this.loading = true;
    this.error = '';

    this.habitService.getHabits().subscribe({
      next: (habits) => {
        if (habits.length === 0) {
          this.rows = [];
          this.loading = false;
          return;
        }

        forkJoin(
          habits.map((habit) =>
            this.habitService.getStats(habit.id).pipe(catchError(() => of(null)))
          )
        ).subscribe((stats) => {
          this.rows = habits.map((habit, index) => ({
            habit,
            stats: stats[index],
            checking: false,
            error: ''
          }));
          this.loading = false;
        });
      },
      error: () => {
        this.error = 'Backend unreachable. Start the Spring Boot API on port 8080.';
        this.loading = false;
      }
    });
  }

  markCompleted(row: HabitRow): void {
    row.checking = true;
    row.error = '';

    this.habitService.checkHabit(row.habit.id).subscribe({
      next: () => this.loadHabits(),
      error: (error) => {
        row.checking = false;
        row.error = error.status === 409 ? 'Already completed today.' : 'Unable to complete this habit.';
      }
    });
  }

  deleteHabit(row: HabitRow): void {
    const confirmed = window.confirm(`Delete "${row.habit.name}"?`);

    if (!confirmed) {
      return;
    }

    this.habitService.deleteHabit(row.habit.id).subscribe({
      next: () => this.loadHabits(),
      error: () => {
        row.error = 'Unable to delete this habit.';
      }
    });
  }

  trackByHabitId(_: number, row: HabitRow): number {
    return row.habit.id;
  }
}

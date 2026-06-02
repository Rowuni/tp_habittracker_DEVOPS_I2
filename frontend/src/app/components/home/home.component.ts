import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';

import { Habit, HabitStats } from '../../models/habit.model';
import { HabitService } from '../../services/habit.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  habits: Habit[] = [];
  stats: HabitStats[] = [];
  loading = true;
  error = '';

  constructor(private readonly habitService: HabitService) {
  }

  ngOnInit(): void {
    this.loadOverview();
  }

  get averageProgression(): number {
    if (this.stats.length === 0) {
      return 0;
    }

    const total = this.stats.reduce((sum, stat) => sum + stat.progressionPercentage, 0);
    return Math.round(total / this.stats.length);
  }

  get completedLast7(): number {
    return this.stats.reduce((sum, stat) => sum + stat.completedDaysLast7, 0);
  }

  private loadOverview(): void {
    this.loading = true;
    this.error = '';

    this.habitService.getHabits().subscribe({
      next: (habits) => {
        this.habits = habits;

        if (habits.length === 0) {
          this.stats = [];
          this.loading = false;
          return;
        }

        forkJoin(
          habits.map((habit) =>
            this.habitService.getStats(habit.id).pipe(catchError(() => of(null)))
          )
        ).subscribe((stats) => {
          this.stats = stats.filter((stat): stat is HabitStats => stat !== null);
          this.loading = false;
        });
      },
      error: () => {
        this.error = 'Backend unreachable. Start the Spring Boot API on port 8080.';
        this.loading = false;
      }
    });
  }
}

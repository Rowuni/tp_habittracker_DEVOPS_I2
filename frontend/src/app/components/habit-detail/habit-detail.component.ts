import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { Habit, HabitCheck, HabitStats } from '../../models/habit.model';
import { HabitService } from '../../services/habit.service';

@Component({
  selector: 'app-habit-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './habit-detail.component.html'
})
export class HabitDetailComponent implements OnInit {
  habit: Habit | null = null;
  stats: HabitStats | null = null;
  checks: HabitCheck[] = [];
  loading = true;
  saving = false;
  error = '';
  actionMessage = '';

  private habitId = 0;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly habitService: HabitService
  ) {
  }

  ngOnInit(): void {
    this.habitId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadDetail();
  }

  loadDetail(): void {
    this.loading = true;
    this.error = '';

    forkJoin({
      habit: this.habitService.getHabit(this.habitId),
      stats: this.habitService.getStats(this.habitId),
      checks: this.habitService.getChecks(this.habitId)
    }).subscribe({
      next: ({ habit, stats, checks }) => {
        this.habit = habit;
        this.stats = stats;
        this.checks = checks;
        this.loading = false;
      },
      error: () => {
        this.error = 'Habit not found or backend unreachable.';
        this.loading = false;
      }
    });
  }

  markCompleted(): void {
    if (this.saving) {
      return;
    }

    this.saving = true;
    this.actionMessage = '';

    this.habitService.checkHabit(this.habitId).subscribe({
      next: () => {
        this.saving = false;
        this.actionMessage = 'Completed for today.';
        this.loadDetail();
      },
      error: (error) => {
        this.saving = false;
        this.actionMessage = error.status === 409 ? 'Already completed today.' : 'Unable to complete this habit.';
      }
    });
  }

  deleteCheck(date: string): void {
    this.habitService.deleteCheck(this.habitId, date).subscribe({
      next: () => this.loadDetail(),
      error: () => {
        this.actionMessage = 'Unable to remove this check.';
      }
    });
  }

  deleteHabit(): void {
    if (this.habit === null) {
      return;
    }

    const confirmed = window.confirm(`Delete "${this.habit.name}"?`);

    if (!confirmed) {
      return;
    }

    this.habitService.deleteHabit(this.habit.id).subscribe({
      next: () => this.router.navigate(['/habits']),
      error: () => {
        this.actionMessage = 'Unable to delete this habit.';
      }
    });
  }

  trackByCheckId(_: number, check: HabitCheck): number {
    return check.id;
  }
}

import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { HabitRequest } from '../../models/habit.model';
import { HabitService } from '../../services/habit.service';

@Component({
  selector: 'app-habit-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './habit-form.component.html'
})
export class HabitFormComponent implements OnInit {
  habitId: number | null = null;
  form: HabitRequest = {
    name: '',
    description: '',
    category: '',
    targetFrequency: 'Daily'
  };
  loading = false;
  saving = false;
  error = '';

  readonly frequencies = ['Daily', 'Weekdays', '3 times a week', 'Weekly'];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly habitService: HabitService
  ) {
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id === null) {
      return;
    }

    this.habitId = Number(id);
    this.loading = true;

    this.habitService.getHabit(this.habitId).subscribe({
      next: (habit) => {
        this.form = {
          name: habit.name,
          description: habit.description || '',
          category: habit.category || '',
          targetFrequency: habit.targetFrequency
        };
        this.loading = false;
      },
      error: () => {
        this.error = 'Habit not found.';
        this.loading = false;
      }
    });
  }

  submit(formRef: NgForm): void {
    if (formRef.invalid || this.saving) {
      return;
    }

    this.saving = true;
    this.error = '';

    const request = {
      ...this.form,
      name: this.form.name.trim(),
      description: this.form.description.trim(),
      category: this.form.category.trim(),
      targetFrequency: this.form.targetFrequency.trim()
    };

    const save$ = this.habitId === null
      ? this.habitService.createHabit(request)
      : this.habitService.updateHabit(this.habitId, request);

    save$.subscribe({
      next: (habit) => this.router.navigate(['/habits', habit.id]),
      error: () => {
        this.error = 'Unable to save this habit.';
        this.saving = false;
      }
    });
  }
}

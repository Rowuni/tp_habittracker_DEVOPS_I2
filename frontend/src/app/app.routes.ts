import { Routes } from '@angular/router';

import { HabitDetailComponent } from './components/habit-detail/habit-detail.component';
import { HabitFormComponent } from './components/habit-form/habit-form.component';
import { HabitListComponent } from './components/habit-list/habit-list.component';
import { HomeComponent } from './components/home/home.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'habits', component: HabitListComponent },
  { path: 'habits/new', component: HabitFormComponent },
  { path: 'habits/:id', component: HabitDetailComponent },
  { path: 'habits/:id/edit', component: HabitFormComponent },
  { path: '**', redirectTo: '' }
];

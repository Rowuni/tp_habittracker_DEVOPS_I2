import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Habit, HabitCheck, HabitRequest, HabitStats, HabitStreak } from '../models/habit.model';

@Injectable({
  providedIn: 'root'
})
export class HabitService {
  private readonly apiUrl = 'http://localhost:8080/api/habits';

  constructor(private readonly http: HttpClient) {
  }

  getHabits(): Observable<Habit[]> {
    return this.http.get<Habit[]>(this.apiUrl);
  }

  getHabit(id: number): Observable<Habit> {
    return this.http.get<Habit>(`${this.apiUrl}/${id}`);
  }

  createHabit(request: HabitRequest): Observable<Habit> {
    return this.http.post<Habit>(this.apiUrl, request);
  }

  updateHabit(id: number, request: HabitRequest): Observable<Habit> {
    return this.http.put<Habit>(`${this.apiUrl}/${id}`, request);
  }

  deleteHabit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  checkHabit(id: number): Observable<HabitCheck> {
    return this.http.post<HabitCheck>(`${this.apiUrl}/${id}/check`, { completed: true });
  }

  deleteCheck(id: number, date: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/check/${date}`);
  }

  getChecks(id: number): Observable<HabitCheck[]> {
    return this.http.get<HabitCheck[]>(`${this.apiUrl}/${id}/checks`);
  }

  getStreak(id: number): Observable<HabitStreak> {
    return this.http.get<HabitStreak>(`${this.apiUrl}/${id}/streak`);
  }

  getStats(id: number): Observable<HabitStats> {
    return this.http.get<HabitStats>(`${this.apiUrl}/${id}/stats`);
  }
}

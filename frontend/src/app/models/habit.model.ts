export interface Habit {
  id: number;
  name: string;
  description: string;
  category: string;
  targetFrequency: string;
  createdAt: string;
}

export interface HabitRequest {
  name: string;
  description: string;
  category: string;
  targetFrequency: string;
}

export interface HabitCheck {
  id: number;
  habitId: number;
  checkDate: string;
  completed: boolean;
}

export interface HabitStreak {
  habitId: number;
  currentStreak: number;
}

export interface HabitStats {
  habitId: number;
  habitName: string;
  currentStreak: number;
  completedDaysLast7: number;
  progressionPercentage: number;
}

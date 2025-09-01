package com.healthmate.healthmate.domain.weeklylog.dto;

public class WeeklyLogDtos {

    public record UpsertCurrentWeekRequest(
            int weeklyExerciseMinutes,
            double weeklyAvgWeight,
            double weeklyAvgBmi,
            int caloriesBurned
    ) {}

    public record WeeklyLogResponse(
            Long id,
            int year,
            int week,
            int weeklyExerciseMinutes,
            double weeklyAvgWeight,
            double weeklyAvgBmi,
            int caloriesBurned
    ) {}
}



package com.healthmate.healthmate.domain.weeklylog.entity;

import com.healthmate.healthmate.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "weekly_logs", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_year_week", columnNames = {"user_id", "year", "week"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int week;

    @Column(nullable = false)
    private int exerciseMinutes;

    @Column(nullable = false)
    private double avgWeight;

    @Column(nullable = false)
    private double avgBmi;

    @Column(nullable = false)
    private int caloriesBurned;

    @Column(nullable = false)
    private int weightEntries;

    @Column(nullable = false)
    private int bmiEntries;

    public WeeklyLog(User user, int year, int week, int exerciseMinutes, double avgWeight, double avgBmi, int caloriesBurned) {
        this.user = user;
        this.year = year;
        this.week = week;
        this.exerciseMinutes = exerciseMinutes;
        this.avgWeight = avgWeight;
        this.avgBmi = avgBmi;
        this.caloriesBurned = caloriesBurned;
        this.weightEntries = 0;
        this.bmiEntries = 0;
    }

    public void update(int exerciseMinutes, double avgWeight, double avgBmi, int caloriesBurned) {
        this.exerciseMinutes += exerciseMinutes;
        this.caloriesBurned += caloriesBurned;

        if (avgWeight > 0) {
            if (this.weightEntries == 0) {
                this.avgWeight = roundToTwoDecimals(avgWeight);
            } else {
                double newAvgWeight = ((this.avgWeight * this.weightEntries) + avgWeight) / (this.weightEntries + 1);
                this.avgWeight = roundToTwoDecimals(newAvgWeight);
            }
            this.weightEntries += 1;
        }

        if (avgBmi > 0) {
            if (this.bmiEntries == 0) {
                this.avgBmi = roundToTwoDecimals(avgBmi);
            } else {
                double newAvgBmi = ((this.avgBmi * this.bmiEntries) + avgBmi) / (this.bmiEntries + 1);
                this.avgBmi = roundToTwoDecimals(newAvgBmi);
            }
            this.bmiEntries += 1;
        }
    }

    private static double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}



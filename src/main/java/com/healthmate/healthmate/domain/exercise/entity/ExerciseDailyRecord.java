package com.healthmate.healthmate.domain.exercise.entity;

import com.healthmate.healthmate.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "exercise_daily_records",
        uniqueConstraints = {@UniqueConstraint(name = "uk_user_date", columnNames = {"user_id", "record_date"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseDailyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "record_date", nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer totalDurationSeconds;

    @Column(nullable = false)
    private Integer totalSets;

    // 1~10 scale perceived intensity
    @Column(nullable = false)
    private Integer perceivedDifficulty;

    // 1~5 satisfaction
    @Column(nullable = false)
    private Integer satisfaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "representative_exercise_id")
    private Exercise representativeExercise;

    @Column(length = 500)
    private String notes;

    public ExerciseDailyRecord(User user,
                               LocalDate date,
                               Integer totalDurationSeconds,
                               Integer totalSets,
                               Integer perceivedDifficulty,
                               Integer satisfaction,
                               Exercise representativeExercise,
                               String notes) {
        this.user = user;
        this.date = date;
        this.totalDurationSeconds = totalDurationSeconds;
        this.totalSets = totalSets;
        this.perceivedDifficulty = perceivedDifficulty;
        this.satisfaction = satisfaction;
        this.representativeExercise = representativeExercise;
        this.notes = notes;
    }
}



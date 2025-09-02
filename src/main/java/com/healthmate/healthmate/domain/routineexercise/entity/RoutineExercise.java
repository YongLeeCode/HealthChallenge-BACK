package com.healthmate.healthmate.domain.routineexercise.entity;

import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import com.healthmate.healthmate.domain.routine.entity.Routine;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routine_exercises")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoutineExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    @Column(name = "count_number", nullable = false)
    private Integer countNumber;

    @Column(name = "time_exercise")
    private Integer timeExercise; // 초 단위, 선택사항

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    public RoutineExercise(Routine routine, Exercise exercise, Integer setNumber, Integer countNumber, Integer timeExercise, Integer orderNumber) {
        this.routine = routine;
        this.exercise = exercise;
        this.setNumber = setNumber;
        this.countNumber = countNumber;
        this.timeExercise = timeExercise;
        this.orderNumber = orderNumber;
    }
}

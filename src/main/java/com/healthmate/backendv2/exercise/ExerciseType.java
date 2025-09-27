package com.healthmate.backendv2.exercise;

public enum ExerciseType {
    STRENGTH("근력"),
    CARDIO("유산소"),
    FLEXIBILITY("유연성"),
    BALANCE("균형"),
    FUNCTIONAL("기능성");

    private final String description;

    ExerciseType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

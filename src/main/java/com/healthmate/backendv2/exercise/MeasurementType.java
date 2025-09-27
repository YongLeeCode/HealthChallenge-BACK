package com.healthmate.backendv2.exercise;

public enum MeasurementType {
    REPS("횟수"),
    WEIGHT("무게"),
    TIME("시간");

    private final String description;

    MeasurementType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

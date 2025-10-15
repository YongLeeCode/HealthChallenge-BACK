package com.healthmate.backendv2.exercise;

public enum MeasurementType {
    WORKING_TIME("작업시간"),
    WEIGHT("무게"),
    TIME_ATTACK("타임어택");

    private final String description;

    MeasurementType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

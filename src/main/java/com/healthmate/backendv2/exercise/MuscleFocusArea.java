package com.healthmate.backendv2.exercise;

public enum MuscleFocusArea {
    UPPER_BODY("상체"),
    LOWER_BODY("하체"),
    CORE("복근"),
    ENDURANCE("지구력");

    private final String description;

    MuscleFocusArea(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

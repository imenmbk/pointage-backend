package com.example.pointage.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AttendanceStatus {
    PRESENT("Présent"),
    ABSENT("Absent");

    private final String description;

    AttendanceStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static AttendanceStatus fromDescription(String description) {
        for (AttendanceStatus status : values()) {
            if (status.getDescription().equalsIgnoreCase(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No status with description: " + description);
    }
}

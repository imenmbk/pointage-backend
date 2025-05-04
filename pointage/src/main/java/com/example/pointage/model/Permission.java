package com.example.pointage.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter

public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    EMPLOYEE_READ("EMPLOYEE:read"),
    EMPLOYEE_UPDATE("EMPLOYEE:update"),
    EMPLOYEE_CREATE("EMPLOYEE:create"),
    EMPLOYEE_DELETE("EMPLOYEE:delete"),
    EMPLOYEE_CREATE_SCHEDULE("employee:create_schedule"),
    EMPLOYEE_VIEW_PROFILE_SCHEDULE("employee:view_profile_schedule");


    private final String permission;

    Permission(String permission) {

        this.permission = permission;
    }

    public String getPermission() {

        return permission;
    }
}


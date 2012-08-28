package com.gdiama.domain;

public class AppointmentRequest {

    private final AppointmentCategory appointmentCategory;

    public AppointmentRequest(String categoryAsString) {
        appointmentCategory = AppointmentCategory.valueOf(categoryAsString);
    }

    public AppointmentCategory getAppointmentCategory() {
        return appointmentCategory;
    }
}

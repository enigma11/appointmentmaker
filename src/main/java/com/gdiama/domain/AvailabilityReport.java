package com.gdiama.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document
public class AvailabilityReport {

    private Map<AppointmentCategory, Integer> availabilityPerCategory;

    public AvailabilityReport(Map<AppointmentCategory, Integer> availabilityPerCategory) {
        this.availabilityPerCategory = availabilityPerCategory;
    }

    public boolean hasAvailableSlotsFor(AppointmentCategory category) {
        return availabilityPerCategory.get(category) > 0;
    }
}

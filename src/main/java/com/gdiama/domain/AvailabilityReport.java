package com.gdiama.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Document
public class AvailabilityReport {

    private final Map<AppointmentCategory, Integer> availabilityPerCategory;
    private final Date fetchDate;

    public AvailabilityReport(Map<AppointmentCategory, Integer> availabilityPerCategory) {
        this.availabilityPerCategory = availabilityPerCategory;
        this.fetchDate = new Date();
    }

    public boolean hasAvailableSlotsFor(AppointmentCategory category) {
        return availabilityPerCategory.get(category) > 0;
    }
}

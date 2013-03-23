package com.gdiama.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

@Document
public class AvailabilityReport {

    @Field(value = "availabilityPerCategory")
    private Map<AppointmentCategory, Integer> availabilityPerCategory;
    @Field(value = "sampleDate")
    private final Date sampleDate = new Date();

    @Transient
    private Map<AppointmentCategory, Integer> availabilityPerCategoryMutable;

    public AvailabilityReport(Map<AppointmentCategory, Integer> availabilityPerCategory) {
        this.availabilityPerCategoryMutable = availabilityPerCategory;
        this.availabilityPerCategory = availabilityPerCategory;
    }

    public boolean hasAvailableSlotsFor(AppointmentCategory category) {
        Integer numberOfSlots = availabilityPerCategoryMutable.get(category);
        return numberOfSlots != null && numberOfSlots > 0;
    }

    public void decrementForCategory(AppointmentCategory category) {
        int newCount = availabilityPerCategoryMutable.get(category) - 1;
        availabilityPerCategoryMutable.put(category, newCount);
    }
}

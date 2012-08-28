package com.gdiama.infrastructure;

import com.gdiama.domain.AvailabilityReport;

public class AvailabilityReportRepository {
    private final MongoDB mongoDB;

    public AvailabilityReportRepository(MongoDB mongoDB) {
        this.mongoDB = mongoDB;
    }

    public void save(AvailabilityReport availabilityReport) {
        mongoDB.getMongoTemplate().save(availabilityReport);
    }
}

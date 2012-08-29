package com.gdiama;

import com.gdiama.domain.AvailabilityReport;
import com.gdiama.infrastructure.MongoDB;

public class AvailabilityReportRepository {
    private final MongoDB mongoDB;

    public AvailabilityReportRepository(MongoDB mongoDB) {
        this.mongoDB = mongoDB;
    }

    public void save(AvailabilityReport availabilityReport) {
        mongoDB.getMongoTemplate().save(availabilityReport);
    }
}

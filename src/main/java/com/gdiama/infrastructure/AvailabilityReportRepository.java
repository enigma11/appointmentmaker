package com.gdiama.infrastructure;

import com.gdiama.domain.AvailabilityReport;

public class AvailabilityReportRepository {
    private final DatabaseAccess dataAccess;

    public AvailabilityReportRepository(DatabaseAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void save(AvailabilityReport availabilityReport) {
        dataAccess.save(availabilityReport);
    }
}

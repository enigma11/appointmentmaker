package com.gdiama.infrastructure;

import com.gdiama.domain.AvailabilityReport;
import com.gdiama.pages.AvailabilityReportPage;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class AvailabilityReportService {

    private final AvailabilityReportRepository availabilityReportRepository;

    public AvailabilityReportService(AvailabilityReportRepository availabilityReportRepository) {
        this.availabilityReportRepository = availabilityReportRepository;
    }

    public AvailabilityReport fetchAvailabilityReport() throws Exception {
        AvailabilityReport availabilityReport = new AvailabilityReportPage(new HtmlUnitDriver(true)).fetch();
        availabilityReportRepository.save(availabilityReport);
        return availabilityReport;
    }
}

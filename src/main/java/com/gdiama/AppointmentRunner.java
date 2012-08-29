package com.gdiama;

import com.gdiama.app.AppointmentMaker;
import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.AvailabilityReport;
import com.gdiama.infrastructure.AppointmentRepository;
import com.gdiama.infrastructure.AuditRepository;
import com.gdiama.infrastructure.ContactsRepository;
import com.gdiama.infrastructure.MongoDB;
import com.gdiama.pages.AvailabilityReportPage;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.logging.Level;

public class AppointmentRunner {

    public static void main(String[] args) throws Exception {
        turnOffHtmlUnitLoggerOff();
        try {
            System.out.println("Running...");

            MongoDB mongoDB = MongoDB.get();
            AvailabilityReportRepository availabilityReportRepository = new AvailabilityReportRepository(mongoDB);
            AppointmentMaker appointmentMaker = new AppointmentMaker(
                    new ContactsRepository(mongoDB),
                    new AppointmentRepository(mongoDB),
                    new AuditRepository(mongoDB)
            );

            AvailabilityReport availabilityReport = new AvailabilityReportPage(new HtmlUnitDriver(true)).fetch();
            availabilityReportRepository.save(availabilityReport);
            appointmentMaker.run(new AppointmentRequest(args[0].toUpperCase()), availabilityReport);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done!");
    }

    private static void turnOffHtmlUnitLoggerOff() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }
}
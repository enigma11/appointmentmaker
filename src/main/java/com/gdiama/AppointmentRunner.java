package com.gdiama;

import com.gdiama.app.AppointmentMaker;
import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.AvailabilityReport;
import com.gdiama.infrastructure.*;
import com.gdiama.pages.AvailabilityReportPage;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class AppointmentRunner {


    public static void main(String[] args) throws Exception {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        MongoDB mongoDB = MongoDB.get();
        AvailabilityReportRepository availabilityReportRepository = new AvailabilityReportRepository(mongoDB);
        ContactsRepository contactsRepository = new ContactsRepository(mongoDB);
        AppointmentRepository appointmentRepository = new AppointmentRepository(mongoDB);
        AuditRepository auditRepository = new AuditRepository(mongoDB);

        while (true) {
            try {
                System.out.println("Running...");

                AvailabilityReport availabilityReport = new AvailabilityReportPage(new HtmlUnitDriver(true)).fetch();
                availabilityReportRepository.save(availabilityReport);

                AppointmentMaker appointmentMaker = new AppointmentMaker(
                        contactsRepository,
                        appointmentRepository,
                        auditRepository
                );

                appointmentMaker.run(new AppointmentRequest(args[0].toUpperCase()), availabilityReport);

                System.out.println("Sleeping...");
            } catch (Exception e) {
                e.printStackTrace();
            }
            TimeUnit.MINUTES.sleep(15);
        }
    }
}
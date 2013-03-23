package com.gdiama.app;

import com.gdiama.Audit;
import com.gdiama.domain.Appointment;
import com.gdiama.domain.AppointmentCategory;
import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.AvailabilityReport;
import com.gdiama.infrastructure.AppointmentRepository;
import com.gdiama.infrastructure.AuditRepository;
import com.gdiama.pages.AppointmentWizard;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;
import java.util.logging.Level;

public class AppointmentMaker {

    private final AppointmentRepository appointmentRepository;
    private final AuditRepository auditRepository;

    public AppointmentMaker(final AppointmentRepository appointmentRepository, final AuditRepository auditRepository) throws Exception {
        this.appointmentRepository = appointmentRepository;
        this.auditRepository = auditRepository;
        turnNoisyHtmlUnitLoggerOff();
    }

    public void run(AppointmentRequest request, AvailabilityReport availabilityReport) throws Exception {
        Audit audit = new Audit(auditRepository);
        HtmlUnitDriver driver = new HtmlUnitDriver(true);

        try {
            AppointmentCategory category = request.getAppointmentCategory();
            if (!availabilityReport.hasAvailableSlotsFor(category)) {
                audit.append("No available slot found for " + category.name());
            } else {
                List<Appointment> appointments = appointmentRepository.loadAppointmentsFor(category);

                AppointmentWizard appointmentWizard = new AppointmentWizard(driver, audit);
                appointmentWizard.goTo()
                        .fillOutContactDetails(request.getContactDetails())
                        .proceedToCategorySelection()
                        .selectAppointmentCategory(category)
                        .selectEarliestAvailableDayIfBefore(appointments)
                        .selectEarliestAVailableTime()
                        .confirmAppointment()
                        .done();

                if (appointmentWizard.hasBookedAppointment()) {
                    Appointment bookedAppointment = appointmentWizard.getBookedAppointment();
                    appointmentRepository.save(bookedAppointment);
                    audit.append("Booked appointment on " + bookedAppointment + " for " + category);
                }
            }
        } finally {
            audit.save();
            driver.close();
        }
    }

    private void turnNoisyHtmlUnitLoggerOff() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }
}

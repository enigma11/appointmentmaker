package com.gdiama.app;

import com.gdiama.Audit;
import com.gdiama.domain.Appointment;
import com.gdiama.domain.AppointmentCategory;
import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.AvailabilityReport;
import com.gdiama.infrastructure.AppointmentRepository;
import com.gdiama.infrastructure.AppointmentRequestRepository;
import com.gdiama.infrastructure.AuditRepository;
import com.gdiama.pages.AppointmentWizard;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;
import java.util.logging.Level;

public class AppointmentMaker {

    private final AppointmentRepository appointmentRepository;
    private final AuditRepository auditRepository;
    private final AppointmentRequestRepository appointmentRequestRepository;

    public AppointmentMaker(final AppointmentRepository appointmentRepository,
                            final AuditRepository auditRepository,
                            final AppointmentRequestRepository appointmentRequestRepository) throws Exception {
        this.appointmentRepository = appointmentRepository;
        this.auditRepository = auditRepository;
        this.appointmentRequestRepository = appointmentRequestRepository;
        turnNoisyHtmlUnitLoggerOff();
    }

    public void run(AppointmentRequest request) throws Exception {
        Audit audit = new Audit(auditRepository);
        HtmlUnitDriver driver = new HtmlUnitDriver(true);

        try {
            AppointmentCategory category = request.getAppointmentCategory();
            List<Appointment> appointments = appointmentRepository.loadAppointmentsFor(category, request.getContactDetails());

            AppointmentWizard appointmentWizard = new AppointmentWizard(driver, audit, request.getContactDetails());
            appointmentWizard.goTo()
                    .fillOutContactDetails()
                    .proceedToCategorySelection()
                    .selectAppointmentCategory(category)
                    .selectEarliestAvailableDayIfBefore(appointments)
                    .selectEarliestAVailableTime()
                    .confirmAppointment()
                    .done();

            if (appointmentWizard.hasBookedAppointment()) {
                Appointment bookedAppointment = appointmentWizard.getBookedAppointment();
                appointmentRepository.save(bookedAppointment);
                request.appointmentBooked();
                appointmentRequestRepository.update(request);
                audit.append("Booked appointment on " + bookedAppointment + " for " + category);
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

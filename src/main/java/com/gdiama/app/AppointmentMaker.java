package com.gdiama.app;

import com.gdiama.Audit;
import com.gdiama.domain.Appointment;
import com.gdiama.domain.AppointmentCategory;
import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.ContactDetails;
import com.gdiama.infrastructure.AppointmentRepository;
import com.gdiama.infrastructure.AuditRepository;
import com.gdiama.infrastructure.ContactsRepository;
import com.gdiama.pages.AppointmentWizard;
import com.gdiama.pages.AvailabilityReport;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;
import java.util.logging.Level;

public class AppointmentMaker {

    private final ContactsRepository contactsRepository;
    private final AppointmentRepository appointmentRepository;
    private final AuditRepository auditRepository;

    public AppointmentMaker(final ContactsRepository contactsRepository, final AppointmentRepository appointmentRepository, final AuditRepository auditRepository) throws Exception {
        this.contactsRepository = contactsRepository;
        this.appointmentRepository = appointmentRepository;
        this.auditRepository = auditRepository;
        turnOffHtmlUnitLoggerOff();
    }

    public void run(AppointmentRequest request) throws Exception {
        Audit audit = new Audit(auditRepository);
        HtmlUnitDriver driver = new HtmlUnitDriver(true);

        try {

            AppointmentCategory category = request.getAppointmentCategory();

            AvailabilityReport fetch = new AvailabilityReport(driver).fetch();
            if (!fetch.hasAvailableSlotsFor(category)) {
                audit.append("No available slot found for " + category.name());
            } else {
                ContactDetails contactDetails = contactsRepository.loadContactDetails();
                List<Appointment> appointments = appointmentRepository.loadAppointments(category);

                AppointmentWizard appointmentWizard = new AppointmentWizard(driver);
                appointmentWizard.goTo()
                        .fillOutContactDetails(contactDetails)
                        .proceedToCategorySelection()
                        .selectAppointmentCategory(category)
                        .selectEarliestAvailableDayIfBefore(appointments)
                        .selectEarliestAVailableTime()
                        .confirmAppointment()
                        .done()
                ;

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

    private void turnOffHtmlUnitLoggerOff() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }
}

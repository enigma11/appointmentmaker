package com.gdiama.pages;

import com.gdiama.Audit;
import com.gdiama.domain.Appointment;
import com.gdiama.domain.AppointmentCategory;
import com.gdiama.domain.ContactDetails;
import org.openqa.selenium.WebDriver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AppointmentWizard {
    private static final String GREEK_EMBASSY_SCHEDULE_APPOINTMENT_PAGE = "http://www.greekembassy.org.uk/ScheduleanAppointment.aspx";
    private static final SimpleDateFormat df = new SimpleDateFormat("dd MMMMMMMMM yyyy");

    private final WebDriver driver;
    private Audit audit;
    private final ContactDetails contactDetails;
    private AppointmentCategory category;
    private Appointment bookedAppointment;
    private boolean skipNextSteps;
    private Date appointmentCandidateDay;
    private Date MAGIC_DATE_IGNORE_NEXT_STEPS = new Date(0);

    public AppointmentWizard(WebDriver driver, Audit audit, ContactDetails contactDetails) {
        this.driver = driver;
        this.audit = audit;
        this.contactDetails = contactDetails;
    }

    public AppointmentWizard goTo() {
        driver.get(GREEK_EMBASSY_SCHEDULE_APPOINTMENT_PAGE);
        return this;
    }

    public AppointmentWizard fillOutContactDetails() {
        audit.append("Filling out form");
        System.out.println("Filling out form");
        new ContactForm(driver).enterContactDetails(contactDetails);
        return this;
    }

    public AppointmentWizard proceedToCategorySelection() {
        new ContactForm(driver).proceedToCategorySelection();
        return this;
    }

    public AppointmentWizard selectAppointmentCategory(AppointmentCategory category) throws Exception {
        this.category = category;
        new ChooseAppointmentCategoryForm(driver, audit).chooseCategory(category);
        return this;
    }

    public AppointmentWizard selectEarliestAvailableDayIfBefore(List<Appointment> appointments) throws Exception {
        appointmentCandidateDay = new AppointmentsCalendar(driver, audit, category, appointments).selectEarliestAvailableDayIfBefore();
        if (appointmentCandidateDay.equals(MAGIC_DATE_IGNORE_NEXT_STEPS)) {
            this.skipNextSteps = true;
        }
        return this;
    }

    public AppointmentWizard selectEarliestAVailableTime() throws Exception {
        if (skipNextSteps) {
            return this;
        }

        Date bookedDate = new AppointmentsTimes(driver, appointmentCandidateDay).getBookedDate();
        bookedAppointment = new Appointment(contactDetails, category, bookedDate);
        audit.append("Found " + category + " appointment on " + bookedDate);
        return this;
    }

    public AppointmentWizard confirmAppointment() throws Exception {
        if (skipNextSteps) {
            return this;
        }
        new ConfirmAppointmentForm(driver, audit).confirm();
        return this;
    }

    public AppointmentWizard done() throws Exception {
        if (skipNextSteps) {
            return this;
        }
        new DoneForm(driver, audit).done();
        return this;
    }

    public Appointment getBookedAppointment() {
        return bookedAppointment;
    }

    public boolean hasBookedAppointment() {
        return bookedAppointment != null;
    }
}

package com.gdiama.pages;

import com.gdiama.domain.Appointment;
import com.gdiama.domain.AppointmentCategory;
import com.gdiama.domain.ContactDetails;
import com.gdiama.util.ElementFinder;
import com.gdiama.util.WaitFor;
import org.apache.commons.lang.time.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.gdiama.util.WaitFor.waitForOptionToDisplayAndReturn;

public class AppointmentWizard {
    private static final String GREEK_EMBASSY_SCHEDULE_APPOINTMENT_PAGE = "http://www.greekembassy.org.uk/ScheduleanAppointment.aspx";
    private static final SimpleDateFormat df = new SimpleDateFormat("dd MMMMMMMMM yyyy");

    private final WebDriver driver;
    private boolean appointmentFoundForCategory;
    private AppointmentCategory category;
    private Appointment bookedAppointment;
    private boolean skipNextSteps;
    private Date appointmentCandidateDay;

    public AppointmentWizard(WebDriver driver) {
        this.driver = driver;
    }

    public AppointmentWizard goTo() {
        driver.get(GREEK_EMBASSY_SCHEDULE_APPOINTMENT_PAGE);
        return this;
    }

    public AppointmentWizard fillOutContactDetails(ContactDetails contactDetails) {
//        audit.append("Filling form");
        System.out.println("Filling form");
        WebElement fn = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_firstNameTextBox"));
        fn.sendKeys(contactDetails.getFirstName());

        WebElement ln = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_lastNameTextBox"));
        ln.sendKeys(contactDetails.getLastName());

        WebElement email = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_emailTextBox"));
        email.sendKeys(contactDetails.getEmail());

        WebElement tel = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_phoneTextBox"));
        tel.sendKeys(contactDetails.getTelephone());
        return this;
    }

    public AppointmentWizard proceedToCategorySelection() {
        WebElement nextStep = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_step2NextCommandButton"));
        nextStep.click();
        return this;
    }


    public AppointmentWizard selectAppointmentCategory(AppointmentCategory category) throws Exception {
        this.category = category;
        final int optionValue = category.optionValue();
        WebElement option = WaitFor.waitForOptionToDisplayAndReturn(new ElementFinder<WebElement>() {
            @Override
            public WebElement find() {
                return driver.findElement(By.xpath("//select/option[@value=" + optionValue + "]"));
            }
        });

        if (option == null) {
            noAppointmentAvailable(category);
        }

        String optionClass = driver.findElement(By.xpath("//select/option[@value=" + optionValue + "]")).getAttribute("class");
        if (!optionClass.isEmpty()) {
            noAppointmentAvailable(category);
        }

//        audit.append("Found appointments for " + category);
        System.out.println("Found appointments for " + category);
        option.click();
        this.appointmentFoundForCategory = true;
        return this;
    }

    public AppointmentWizard selectEarliestAvailableDayIfBefore(List<Appointment> appointments) throws Exception {
        AppointmentCandidate appointmentCandidate = earliestAvailableDate();
        if (!appointmentCandidate.isEarlierThanExistingAppointment(appointments)) {
            this.skipNextSteps = true;
            return this;
        }

//        audit.append("Finding available appointments for " + onDay);
        System.out.println("Finding available appointments for " + appointmentCandidate.onDay());
        appointmentCandidate.select();
        appointmentCandidateDay = appointmentCandidate.onDay();
        return this;
    }

    public AppointmentWizard selectEarliestAVailableTime() throws Exception {
        if (skipNextSteps) {
            return this;
        }

        List<WebElement> availableTimes = waitForOptionToDisplayAndReturn(new ElementFinder<List<WebElement>>() {
            @Override
            public List<WebElement> find() {
                return driver.findElements(By.xpath("//a[starts-with(@id, 'dnn_ctr549_ViewAppointmentWizard_availableTimesRepeater_ctl')]"));
            }
        });


        WebElement earliestTime = availableTimes.get(0);
        earliestTime.click();

        String timeText = earliestTime.getText();
        Date bookedDate = getBookedDate(timeText);
//        String appointment = category + " appointment on " + onDay + earliestTime.getText();
//        audit.append("Found " + appointment);

        bookedAppointment = new Appointment(category, bookedDate);
        return this;
    }

    public AppointmentWizard confirmAppointment() throws Exception {
        if (skipNextSteps) {
            return this;
        }

        WebElement confirmAppointment = waitForOptionToDisplayAndReturn(new ElementFinder<WebElement>() {
            @Override
            public WebElement find() {
                return driver.findElement(By.xpath("//table[@id='dnn_ctr549_ViewAppointmentWizard_step3NextTable']//a[@id='dnn_ctr549_ViewAppointmentWizard_step3NextCommandButton']"));
            }
        });

//        audit.append("Confirming appointment");
        System.out.println("Confirming appointment");
        confirmAppointment.click();

        return this;
    }

    public AppointmentWizard done() throws Exception {
        if (skipNextSteps) {
            return this;
        }

        WebElement doneButton = waitForOptionToDisplayAndReturn(new ElementFinder<WebElement>() {
            @Override
            public WebElement find() {
                return driver.findElement(By.xpath("//a[@id='dnn_ctr549_ViewAppointmentWizard_doneCommandButton']"));
            }
        });

        String bookedAppointment = driver.findElement(By.id("dnn_ctr549_ViewAppointmentWizard_appointmentDateTimeLabel")).getText();
        doneButton.click();
        return this;
    }

    private List<WebElement> availableDays(AppointmentCategory category) throws Exception {
        List<WebElement> availableDays = waitForOptionToDisplayAndReturn(new ElementFinder<List<WebElement>>() {
            @Override
            public List<WebElement> find() {
                return driver.findElements(By.xpath("//table[@id='dnn_ctr549_ViewAppointmentWizard_calendar']/tbody/tr/td[@class='LL_Modules_AppointmentWizard_DayStyle Normal']/a"));
            }
        });

        if (availableDays.isEmpty()) {
            noAppointmentAvailable(category);
        }

        return availableDays;
    }

    private void noAppointmentAvailable(AppointmentCategory category) {
        try {
//            audit.append("No appointment found for " + category);
            System.out.println("No appointment found for " + category);
            System.exit(-1);
        } finally {
            driver.close();
        }
    }

    private AppointmentCandidate earliestAvailableDate() throws Exception {
        WebElement earliestAvailableDay = availableDays(category).get(0);
        String monthYear = driver.findElement(By.xpath("//table[@id='dnn_ctr549_ViewAppointmentWizard_calendar']//table[@class='LL_Modules_AppointmentWizard_TitleStyle SubHead']//td[2]")).getText();
        String earliestAvailableDate = earliestAvailableDay.getText() + " " + monthYear;
        return new AppointmentCandidate(earliestAvailableDay, df.parse(earliestAvailableDate));
    }

    public Appointment getBookedAppointment() {
        return bookedAppointment;
    }

    public boolean hasBookedAppointment() {
        return bookedAppointment != null;
    }

    private static class AppointmentCandidate {
        private WebElement availableDateElement;
        private Date availableDate;

        private AppointmentCandidate(WebElement availableDateElement, Date availableDate) {
            this.availableDateElement = availableDateElement;
            this.availableDate = availableDate;
        }

        public Date onDay() {
            return availableDate;
        }

        public void select() {
            availableDateElement.click();
        }

        public boolean isEarlierThanExistingAppointment(List<Appointment> appointments) {
            if (appointments.isEmpty()) {
                return true;
            }

            for (Appointment appointment : appointments) {
//                if(appointment.category.equals(category)) {
                if (this.isBeforeOrOnSameDayAs(appointment.getDate())) {

                    return true;
                }
            }
            System.out.println("Found earlier existing appointment. Not booked new appointment");
//                    audit.append("Found earlier existing appointment. Not booked new appointment");
            return false;
        }

        public boolean isBeforeOrOnSameDayAs(Date otherDate) {
            Date truncateDate = DateUtils.truncate(otherDate, Calendar.DAY_OF_MONTH);
            boolean before = availableDate.before(truncateDate);
            System.out.println("before = " + before);
            return before;
        }
    }

    private Date getBookedDate(String timeText) {
        String time = timeText.replace("am", "").replace("pm", "");
        String hours = time.split(":")[0];
        String minutes = time.split(":")[1];

        Date bookedDate = DateUtils.addHours(appointmentCandidateDay, Integer.valueOf(hours));
        bookedDate = DateUtils.addMinutes(bookedDate, Integer.valueOf(minutes));
        return bookedDate;
    }
}

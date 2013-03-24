package com.gdiama.pages;

import com.gdiama.Audit;
import com.gdiama.domain.Appointment;
import com.gdiama.domain.AppointmentCategory;
import com.gdiama.util.ElementFinder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.gdiama.util.WaitFor.waitForOptionToDisplayAndReturn;

public class AppointmentsCalendar {

    private Audit audit;
    private WebDriver driver;
    private static final SimpleDateFormat df = new SimpleDateFormat("dd MMMMMMMMM yyyy");
    private AppointmentCategory category;
    private final List<Appointment> appointments;

    public AppointmentsCalendar(WebDriver driver, Audit audit, AppointmentCategory category, List<Appointment> appointments) {
        this.driver = driver;
        this.audit = audit;
        this.category = category;
        this.appointments = appointments;
    }


    public Date selectEarliestAvailableDayIfBefore() throws Exception {
        AppointmentCandidate appointmentCandidate = earliestAvailableDate();
        if (appointmentCandidate.isEarlierThanExistingAppointment(appointments)) {
            audit.append("Finding available appointments for " + appointmentCandidate.onDay());
            System.out.println("Finding available appointments for " + appointmentCandidate.onDay());

            appointmentCandidate.select();
            return appointmentCandidate.onDay();
        }
        return new Date(0);
    }

    private AppointmentCandidate earliestAvailableDate() throws Exception {
        WebElement earliestAvailableDayElement = availableDays(category).get(0);
        String monthYear = driver.findElement(By.xpath("//table[@id='dnn_ctr549_ViewAppointmentWizard_calendar']//table[@class='LL_Modules_AppointmentWizard_TitleStyle SubHead']//td[2]")).getText();
        String earliestAvailableDate = earliestAvailableDayElement.getText() + " " + monthYear;

        audit.append("Found appointment on date '" + earliestAvailableDate + "'");
        System.out.println("Found appointment on date '" + earliestAvailableDate + "'");
        return new AppointmentCandidate(earliestAvailableDayElement, df.parse(earliestAvailableDate), audit);
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
            audit.append("No appointment found for " + category);
            System.out.println("No appointment found for " + category);
            System.exit(-1);
        } finally {
            driver.close();
        }
    }

}

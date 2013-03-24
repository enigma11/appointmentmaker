package com.gdiama.pages;

import com.gdiama.util.ElementFinder;
import org.apache.commons.lang.time.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gdiama.util.WaitFor.waitForOptionToDisplayAndReturn;

public class AppointmentsTimes {


    private final WebDriver driver;
    private final Date appointmentCandidateDay;

    public AppointmentsTimes(WebDriver driver, Date appointmentCandidateDay) {
        this.driver = driver;
        this.appointmentCandidateDay = appointmentCandidateDay;
    }

    public Date getBookedDate() throws Exception {
        List<WebElement> availableTimes = waitForOptionToDisplayAndReturn(new ElementFinder<List<WebElement>>() {
            @Override
            public List<WebElement> find() {
                return driver.findElements(By.xpath("//a[starts-with(@id, 'dnn_ctr549_ViewAppointmentWizard_availableTimesRepeater_ctl')]"));
            }
        });

        WebElement earliestTime = availableTimes.get(0);
        earliestTime.click();

        String timeText = earliestTime.getText();
        return getBookedDate(timeText);

    }

    private Date getBookedDate(String timeText) {
        Matcher matcher = Pattern.compile("([0-9]+):([0-9]+)").matcher(timeText);
        matcher.find();

        String hours = matcher.group(1);
        String minutes = matcher.group(2);

        Date bookedDate = DateUtils.addHours(appointmentCandidateDay, Integer.valueOf(hours));
        bookedDate = DateUtils.addMinutes(bookedDate, Integer.valueOf(minutes));
        return bookedDate;
    }
}

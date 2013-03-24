package com.gdiama.pages;

import com.gdiama.Audit;
import com.gdiama.domain.Appointment;
import org.apache.commons.lang.time.DateUtils;
import org.openqa.selenium.WebElement;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

class AppointmentCandidate {
    private WebElement availableDateElement;
    private Date availableDate;
    private Audit audit;

    AppointmentCandidate(WebElement availableDateElement, Date availableDate, Audit audit) {
        this.availableDateElement = availableDateElement;
        this.availableDate = availableDate;
        this.audit = audit;
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
            if (this.isBefore(appointment.getDate())) {
                return true;
            }
        }
        System.out.println("Found earlier existing appointment. Not booked new appointment");
        audit.append("Found earlier existing appointment. Not booked new appointment");
        return false;
    }

    public boolean isBefore(Date existingAppointmentDate) {
        Date truncatedExistingAppointmentDate = DateUtils.truncate(existingAppointmentDate, Calendar.DAY_OF_MONTH);
        Date truncatedAvailableDate = DateUtils.truncate(availableDate, Calendar.DAY_OF_MONTH);
        return truncatedAvailableDate.before(truncatedExistingAppointmentDate);
    }
}

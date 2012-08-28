package com.gdiama.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document
public class Appointment {

    @Field("appointmentCategory")
    private AppointmentCategory appointmentCategory;
    @Field("appointmentSlot")
    private Date appointmentSlot;

    public Appointment(AppointmentCategory appointmentCategory, Date appointmentSlot) {
        this.appointmentCategory = appointmentCategory;
        this.appointmentSlot = appointmentSlot;
    }

    public boolean isBeforeOrOn(Date newAppointmentAsDate) {
        return appointmentSlot.before(newAppointmentAsDate) || appointmentSlot.equals(newAppointmentAsDate);
    }
}

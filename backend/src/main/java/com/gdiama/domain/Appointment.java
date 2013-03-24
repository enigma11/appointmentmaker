package com.gdiama.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document
public class Appointment {

    @Field("contact")
    private final ContactDetails contactDetails;
    @Field("appointmentCategory")
    private AppointmentCategory appointmentCategory;
    @Field("appointmentSlot")
    private Date appointmentSlot;
    private Date bookedOn = new Date();

    public Appointment(ContactDetails contactDetails, AppointmentCategory appointmentCategory, Date appointmentSlot) {
        this.contactDetails = contactDetails;
        this.appointmentCategory = appointmentCategory;
        this.appointmentSlot = appointmentSlot;
    }

    public Date getDate() {
        return appointmentSlot;
    }
}

package com.gdiama.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document
public class AppointmentRequest {

    @Id
    private ObjectId id;
    @Field(value = "category")
    private final AppointmentCategory appointmentCategory;
    @Field(value = "status")
    private AppointmentRequestStatus appointmentRequestStatus;
    @Field(value = "contact")
    private final ContactDetails contactDetails;
    @Field(value = "created_date")
    private final Date createdDate;


    public AppointmentRequest(AppointmentCategory appointmentCategory, ContactDetails contactDetails) {
        this.appointmentCategory = appointmentCategory;
        this.contactDetails = contactDetails;
        this.appointmentRequestStatus = AppointmentRequestStatus.PENDING;
        this.createdDate = new Date();
    }

    public AppointmentCategory getAppointmentCategory() {
        return appointmentCategory;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public void appointmentBooked() {
        this.appointmentRequestStatus = AppointmentRequestStatus.BOOKED;
    }

    public ObjectId getId() {
        return id;
    }

    public AppointmentRequestStatus getStatus() {
        return appointmentRequestStatus;
    }
}

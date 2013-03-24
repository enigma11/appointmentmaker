package com.gdiama.infrastructure;

import com.gdiama.domain.Appointment;
import com.gdiama.domain.AppointmentCategory;
import com.gdiama.domain.ContactDetails;

import java.util.List;

public class AppointmentRepository {
    private final DatabaseAccess databaseAccess;

    public AppointmentRepository(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public List<Appointment> loadAppointmentsFor(AppointmentCategory category, ContactDetails contactDetails) {
        return databaseAccess.load(category, contactDetails);
    }

    public void save(Appointment appointment) {
        databaseAccess.save(appointment);
    }
}

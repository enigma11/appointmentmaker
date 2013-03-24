package com.gdiama.infrastructure;

import com.gdiama.domain.Appointment;
import com.gdiama.domain.AppointmentCategory;

import java.util.List;

public class AppointmentRepository {
    private final DatabaseAccess databaseAccess;

    public AppointmentRepository(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public List<Appointment> loadAppointmentsFor(AppointmentCategory category) {
        return databaseAccess.load(category);
    }

    public void save(Appointment appointment) {
        databaseAccess.save(appointment);
    }
}

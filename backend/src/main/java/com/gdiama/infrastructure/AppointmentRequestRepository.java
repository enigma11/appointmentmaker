package com.gdiama.infrastructure;

import com.gdiama.domain.AppointmentRequest;

import java.util.List;

public class AppointmentRequestRepository {

    private DatabaseAccess databaseAccess;

    public AppointmentRequestRepository(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public List<AppointmentRequest> loadPending() {
        return databaseAccess.load();
    }

    public void save(AppointmentRequest request) {
        databaseAccess.save(request);
    }

    public void update(AppointmentRequest request) {
        databaseAccess.update(request);
    }
}

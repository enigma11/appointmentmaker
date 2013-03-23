package com.gdiama.infrastructure;

import com.gdiama.domain.AppointmentRequest;

import java.util.List;

public class AppointmentRequestService {

    private final AppointmentRequestRepository appointmentRequestRepository;

    public AppointmentRequestService(AppointmentRequestRepository appointmentRequestRepository) {
        this.appointmentRequestRepository = appointmentRequestRepository;
    }

    public List<AppointmentRequest> loadPendingRequests() {
        return appointmentRequestRepository.loadPending();
    }
}

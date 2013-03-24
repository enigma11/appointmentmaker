package com.gdiama.app;

import com.gdiama.infrastructure.AppointmentRepository;
import com.gdiama.infrastructure.AuditRepository;

public class AppointmentMakerFactory {
    private final AppointmentRepository appointmentRepository;
    private final AuditRepository auditRepository;

    public AppointmentMakerFactory(AppointmentRepository appointmentRepository, AuditRepository auditRepository) {
        this.appointmentRepository = appointmentRepository;
        this.auditRepository = auditRepository;
    }

    public AppointmentMaker newAppointmentMaker() throws Exception {
        return new AppointmentMaker(appointmentRepository, auditRepository);
    }
}

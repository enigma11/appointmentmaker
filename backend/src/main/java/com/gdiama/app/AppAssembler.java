package com.gdiama.app;

import com.gdiama.infrastructure.*;

import java.util.concurrent.ThreadFactory;

public class AppAssembler {

    public DatabaseAccess databaseAccess() throws Exception {
        return MongoDBFactory.get();
    }

    public AvailabilityReportRepository availabilityReportRepository() throws Exception {
        return new AvailabilityReportRepository(databaseAccess());
    }

    private AuditRepository auditRepository() throws Exception {
        return new AuditRepository(databaseAccess());
    }

    private AppointmentRepository appointmentRepository() throws Exception {
        return new AppointmentRepository(databaseAccess());
    }

    public AppointmentRequestRepository appointmentRequestRepository() throws Exception {
        return new AppointmentRequestRepository(databaseAccess());
    }

    public AppointmentMakerTaskFactory singleTaskFactory() throws Exception {
        return new AppointmentMakerTaskFactory(appointmentMakerFactory());
    }

    public AppointmentMakerFactory appointmentMakerFactory() throws Exception {
        return new AppointmentMakerFactory(appointmentRepository(), auditRepository(), appointmentRequestRepository());
    }

    public AvailabilityReportService availabilityReportService() throws Exception {
        return new AvailabilityReportService(availabilityReportRepository());
    }

    public AppointmentRequestService appointmentRequestService() throws Exception {
        return new AppointmentRequestService(appointmentRequestRepository());
    }

    public AppointmentMakerTasksFactory tasksFactory() throws Exception {
        return new AppointmentMakerTasksFactory(singleTaskFactory());
    }

    public ThreadFactory taskThreadFactory() {
        return new AppointmentRequestMakerThreadFactory();
    }

    public AppointmentRequestMakerTasksExecutor appointmentRequestMakerTasksExecutor() {
        return new AppointmentRequestMakerTasksExecutor(taskThreadFactory());
    }
}

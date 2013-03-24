package com.gdiama.app;

import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.AvailabilityReport;

import java.util.concurrent.Callable;

public class AppointmentMakerTask implements Callable<Void> {

    private final AppointmentMaker appointmentMaker;
    private final AppointmentRequest request;
    private final AvailabilityReport availabilityReport;

    public AppointmentMakerTask(AppointmentMaker appointmentMaker, AppointmentRequest request, AvailabilityReport availabilityReport) {
        this.appointmentMaker = appointmentMaker;
        this.request = request;
        this.availabilityReport = availabilityReport;
    }

    @Override
    public Void call() throws Exception {
        appointmentMaker.run(request, availabilityReport);
        return null;
    }
}

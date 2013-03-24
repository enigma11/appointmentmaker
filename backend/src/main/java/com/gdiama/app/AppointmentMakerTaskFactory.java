package com.gdiama.app;

import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.AvailabilityReport;

public class AppointmentMakerTaskFactory {

    private final AppointmentMakerFactory factory;

    public AppointmentMakerTaskFactory(AppointmentMakerFactory factory) {
        this.factory = factory;
    }

    public AppointmentMakerTask newTask(final AppointmentRequest request, final AvailabilityReport availabilityReport) throws Exception {
        return new AppointmentMakerTask(factory.newAppointmentMaker(), request, availabilityReport);
    }
}

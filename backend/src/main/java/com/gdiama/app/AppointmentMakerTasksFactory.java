package com.gdiama.app;

import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.AvailabilityReport;

import java.util.ArrayList;
import java.util.List;

public class AppointmentMakerTasksFactory {
    private final AppointmentMakerTaskFactory taskFactory;

    public AppointmentMakerTasksFactory(AppointmentMakerTaskFactory appointmentMakerTaskFactory) {
        this.taskFactory = appointmentMakerTaskFactory;
    }

    public List<AppointmentMakerTask> newTasks(List<AppointmentRequest> appointmentRequests, AvailabilityReport availabilityReport) throws Exception {
        List<AppointmentMakerTask> tasks = new ArrayList<AppointmentMakerTask>();
        for (AppointmentRequest appointmentRequest : appointmentRequests) {
            if (availabilityReport.hasAvailableSlotsFor(appointmentRequest.getAppointmentCategory())) {
                tasks.add(taskFactory.newTask(appointmentRequest, availabilityReport));
                availabilityReport.decrementForCategory(appointmentRequest.getAppointmentCategory());
            }
        }
        return tasks;
    }
}

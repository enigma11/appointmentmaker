package com.gdiama.app;

import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.AvailabilityReport;
import com.gdiama.infrastructure.AppointmentRequestService;
import com.gdiama.infrastructure.AvailabilityReportService;

import java.util.List;
import java.util.logging.Level;

public class AppointmentRunner {

    public static void main(String[] args) throws Exception {
        turnHtmlUnitLoggerOff();
        try {
            System.out.println("Running...");

            AppAssembler app = new AppointmentRunner().getApp();
            AppointmentRequestService appointmentRequestService = app.appointmentRequestService();
            AvailabilityReportService availabilityReportService = app.availabilityReportService();
            AppointmentMakerTasksFactory appointmentMakerTasksFactory = app.tasksFactory();
            AppointmentRequestMakerTasksExecutor executor = app.appointmentRequestMakerTasksExecutor();

            AvailabilityReport availabilityReport = availabilityReportService.fetchAvailabilityReport();
            List<AppointmentRequest> appointmentRequests = appointmentRequestService.loadPendingRequests();
            if (!appointmentRequests.isEmpty()) {
                List<AppointmentMakerTask> tasks = appointmentMakerTasksFactory.newTasks(appointmentRequests, availabilityReport);
                executor.executeTasks(tasks);
            } else {
                System.out.println("No pending appointment requests found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done!");
    }

    private static void turnHtmlUnitLoggerOff() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }

    public AppAssembler getApp() {
        return new AppAssembler();
    }
}
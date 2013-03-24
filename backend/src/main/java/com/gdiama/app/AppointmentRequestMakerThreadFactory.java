package com.gdiama.app;

import java.util.concurrent.ThreadFactory;

public class AppointmentRequestMakerThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, "appointment-request-maker-task");
    }
}

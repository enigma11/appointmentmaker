package com.gdiama.app;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AppointmentRequestMakerThreadFactoryTest {

    @Test
    public void ensureThreadNameSet() {
        Thread thread = new AppointmentRequestMakerThreadFactory().newThread(newRunnable());
        assertThat(thread.getName(), is("appointment-request-maker-task"));
    }

    private Runnable newRunnable() {
        return new Runnable() {
            @Override
            public void run() {

            }
        };
    }
}

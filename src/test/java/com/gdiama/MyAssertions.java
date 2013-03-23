package com.gdiama;

import com.gdiama.app.AppointmentMakerTask;
import org.fest.assertions.api.Assertions;

import java.util.List;

public class MyAssertions extends Assertions {

    public static AppointmentMakerTasksAssertion assertThat(List<AppointmentMakerTask> actual) {
        return new AppointmentMakerTasksAssertion(actual);
    }
}

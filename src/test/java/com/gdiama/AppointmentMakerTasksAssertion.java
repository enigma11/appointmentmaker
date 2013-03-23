package com.gdiama;


import com.gdiama.app.AppointmentMakerTask;
import com.gdiama.domain.AppointmentRequest;
import org.fest.assertions.api.AbstractAssert;

import java.util.List;

import static com.gdiama.MyAssertions.assertThat;

public class AppointmentMakerTasksAssertion extends AbstractAssert<AppointmentMakerTasksAssertion, List> {

    public AppointmentMakerTasksAssertion(List<AppointmentMakerTask> actual) {
        super(actual, List.class);
    }

    public void containsAppointmentMakerTasksFor(AppointmentRequest... expecedRequests) {
        isNotNull();

        assertThat(actual.size()).isEqualTo(expecedRequests.length);

    }
}

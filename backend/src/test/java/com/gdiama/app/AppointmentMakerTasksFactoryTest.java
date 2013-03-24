package com.gdiama.app;

import com.gdiama.domain.AppointmentCategory;
import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.AvailabilityReport;
import com.gdiama.domain.ContactDetails;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static com.gdiama.app.AppointmentMakerTasksFactoryTest.Tuple.tuple;
import static com.gdiama.domain.AppointmentCategory.CERTIFICATES;
import static com.gdiama.domain.AppointmentCategory.PASSPORT;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AppointmentMakerTasksFactoryTest {

    private AppointmentMaker appointmentMaker = mock(AppointmentMaker.class);
    private AppointmentMakerTaskFactory taskFactory = mock(AppointmentMakerTaskFactory.class);
    private AppointmentMakerTasksFactory appointmentMakerTasksFactory = new AppointmentMakerTasksFactory(taskFactory);


    private AppointmentRequest certificateRequest = new AppointmentRequest(CERTIFICATES, new ContactDetails("first1", "last1", "email1", "tel1"));
    private AppointmentRequest certificateRequest2 = new AppointmentRequest(CERTIFICATES, new ContactDetails("first12", "last12", "email12", "tel12"));
    private AppointmentRequest passportRequest = new AppointmentRequest(PASSPORT, new ContactDetails("first2", "last2", "email2", "tel2"));

    @Test
    public void newTasks() throws Exception {
        AvailabilityReport report = report(tuple(CERTIFICATES, 1), tuple(PASSPORT, 1));
        AppointmentMakerTask appointmentMakerTask1 = new AppointmentMakerTask(appointmentMaker, certificateRequest, report);
        AppointmentMakerTask appointmentMakerTask2 = new AppointmentMakerTask(appointmentMaker, passportRequest, report);

        when(taskFactory.newTask(certificateRequest, report)).thenReturn(appointmentMakerTask1);
        when(taskFactory.newTask(passportRequest, report)).thenReturn(appointmentMakerTask2);

        List<AppointmentMakerTask> appointmentMakerTasks = appointmentMakerTasksFactory.newTasks(asList(certificateRequest, passportRequest), report);

        assertThat(appointmentMakerTasks).hasSize(2);
        assertThat(appointmentMakerTasks).containsSequence(appointmentMakerTask1, appointmentMakerTask2);
    }

    @Test
    public void createOnlyTasksWhenAvailabilitySlotsForCategoryExists() throws Exception {
        AvailabilityReport report = report(tuple(CERTIFICATES, 1));
        AppointmentMakerTask appointmentMakerTask1 = new AppointmentMakerTask(appointmentMaker, certificateRequest, report);
        when(taskFactory.newTask(certificateRequest, report)).thenReturn(appointmentMakerTask1);

        List<AppointmentMakerTask> appointmentMakerTasks = appointmentMakerTasksFactory.newTasks(asList(certificateRequest, passportRequest), report);

        assertThat(appointmentMakerTasks).hasSize(1);
        assertThat(appointmentMakerTasks).containsSequence(appointmentMakerTask1);

    }

    @Test
    public void createOnlyTasksWhenEnoughAvailabilitySlotsForCategoryExists() throws Exception {
        AvailabilityReport report = report(tuple(CERTIFICATES, 1));
        AppointmentMakerTask appointmentMakerTask1 = new AppointmentMakerTask(appointmentMaker, certificateRequest, report);
        when(taskFactory.newTask(certificateRequest, report)).thenReturn(appointmentMakerTask1);

        List<AppointmentMakerTask> appointmentMakerTasks = appointmentMakerTasksFactory.newTasks(asList(certificateRequest, certificateRequest2), report);

        assertThat(appointmentMakerTasks).hasSize(1);
        assertThat(appointmentMakerTasks).containsSequence(appointmentMakerTask1);

    }

    private AvailabilityReport report(Tuple<AppointmentCategory, Integer>... tuples) {
        HashMap<AppointmentCategory, Integer> availabilityPerCategory = new HashMap<AppointmentCategory, Integer>();
        for (Tuple<AppointmentCategory, Integer> tuple : tuples) {
            availabilityPerCategory.put(tuple.getK(), tuple.getV());
        }
        return new AvailabilityReport(availabilityPerCategory);
    }

    static class Tuple<K, V> {
        private final K k;
        private final V v;

        public static <K, V> Tuple tuple(K k, V v) {
            return new Tuple<K, V>(k, v);
        }

        public Tuple(K k, V v) {
            this.k = k;
            this.v = v;
        }

        public K getK() {
            return k;
        }

        public V getV() {
            return v;
        }
    }
}

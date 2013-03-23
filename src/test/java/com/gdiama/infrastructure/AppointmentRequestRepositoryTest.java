package com.gdiama.infrastructure;


import com.gdiama.domain.AppointmentCategory;
import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.ContactDetails;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.net.UnknownHostException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AppointmentRequestRepositoryTest {

    private static AppointmentRequestRepository appointmentRequestRepository;

    @BeforeClass
    public static void clean() throws UnknownHostException {
        MongoURI mongoURI = new MongoURI(System.getenv("MONGOLAB_URI"));
        MongoTemplate mongoTemplate = new MongoTemplate(new Mongo(mongoURI), mongoURI.getDatabase());

        List<AppointmentRequest> all = mongoTemplate.findAll(AppointmentRequest.class);
        mongoTemplate.remove(Query.query(new Criteria().all(all)), AppointmentRequest.class);

        appointmentRequestRepository = new AppointmentRequestRepository(new MongoDB(mongoTemplate));
    }

    @Test
    public void save() {
        appointmentRequestRepository.save(
                new AppointmentRequest(
                        AppointmentCategory.CERTIFICATES,
                        new ContactDetails("fname", "lname", "email", "phone")));
    }

    @Test
    public void loadPending() {
        List<AppointmentRequest> appointmentRequests = appointmentRequestRepository.loadPending();
        assertThat(appointmentRequests.size(), is(1));
    }
}

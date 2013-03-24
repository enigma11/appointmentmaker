package com.gdiama.infrastructure;

import com.gdiama.domain.Appointment;
import com.gdiama.domain.AppointmentCategory;
import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.AppointmentRequestStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class MongoDB implements DatabaseAccess {

    private final MongoTemplate mongoTemplate;

    public MongoDB(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(Object objectToSave) {
        mongoTemplate.save(objectToSave);
    }

    @Override
    public List<Appointment> load(AppointmentCategory category) {
        Query query = Query.query(Criteria.where("appointmentCategory").is(category.name()));
        return mongoTemplate.find(query, Appointment.class);
    }

    @Override
    public List<AppointmentRequest> load() {
        return mongoTemplate.find(
                Query.query(Criteria.where("status").is(AppointmentRequestStatus.PENDING)),
                AppointmentRequest.class);
    }
}

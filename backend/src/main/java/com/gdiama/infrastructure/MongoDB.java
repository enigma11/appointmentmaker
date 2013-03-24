package com.gdiama.infrastructure;

import com.gdiama.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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
    public List<Appointment> load(AppointmentCategory category, ContactDetails contactDetails) {
        Query query = Query.query(
                Criteria.where("appointmentCategory").is(category.name()).and("contact.email").is(contactDetails.getEmail())
        );
        return mongoTemplate.find(query, Appointment.class);
    }

    @Override
    public List<AppointmentRequest> load() {
        return mongoTemplate.find(
                Query.query(Criteria.where("status").is(AppointmentRequestStatus.PENDING)),
                AppointmentRequest.class);
    }

    @Override
    public void update(AppointmentRequest objectToBeUpdated) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(objectToBeUpdated.getId())),
                Update.update("status", objectToBeUpdated.getStatus()),
                objectToBeUpdated.getClass()
        );
    }
}

package com.gdiama.infrastructure;

import com.gdiama.domain.Appointment;
import com.gdiama.domain.AppointmentCategory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class AppointmentRepository {

    private final MongoDB mongoDB;

    public AppointmentRepository(MongoDB mongoDB) {
        this.mongoDB = mongoDB;
    }

    public List<Appointment> loadAppointments(AppointmentCategory category) {
        Query query = Query.query(Criteria.where("appointmentCategory").is(category.name()));
        return mongoDB.getMongoTemplate().find(query, Appointment.class);
    }

    public void save(Appointment appointment) {
        mongoDB.getMongoTemplate().save(appointment);
    }
}

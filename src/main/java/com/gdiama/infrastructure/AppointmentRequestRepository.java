package com.gdiama.infrastructure;

import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.AppointmentRequestStatus;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class AppointmentRequestRepository {

    private MongoDB mongoDB;

    public AppointmentRequestRepository(MongoDB mongoDB) {
        this.mongoDB = mongoDB;
    }

    public List<AppointmentRequest> loadPending() {
        return mongoDB.getMongoTemplate().find(
                Query.query(Criteria.where("status").is(AppointmentRequestStatus.PENDING)),
                AppointmentRequest.class);
    }

    public void save(AppointmentRequest request) {
        mongoDB.getMongoTemplate().save(request);
    }

}

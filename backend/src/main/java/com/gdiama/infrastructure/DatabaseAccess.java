package com.gdiama.infrastructure;

import com.gdiama.domain.Appointment;
import com.gdiama.domain.AppointmentCategory;
import com.gdiama.domain.AppointmentRequest;
import com.gdiama.domain.ContactDetails;

import java.util.List;

public interface DatabaseAccess {
    void save(Object objectToSave);

    List<Appointment> load(AppointmentCategory category, ContactDetails contactDetails);

    List<AppointmentRequest> load();

    void update(AppointmentRequest objectToBeUpdated);
}

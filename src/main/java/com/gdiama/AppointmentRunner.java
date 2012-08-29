package com.gdiama;

import com.gdiama.app.AppointmentMaker;
import com.gdiama.domain.AppointmentRequest;
import com.gdiama.infrastructure.AppointmentRepository;
import com.gdiama.infrastructure.AuditRepository;
import com.gdiama.infrastructure.ContactsRepository;
import com.gdiama.infrastructure.MongoDB;

import java.util.concurrent.TimeUnit;

public class AppointmentRunner {

    public static void main(String[] args) throws Exception {
        try {
            System.out.println("Running...");

            MongoDB mongoDB = MongoDB.get();
            AppointmentMaker appointmentMaker = new AppointmentMaker(
                    new ContactsRepository(mongoDB),
                    new AppointmentRepository(mongoDB),
                    new AuditRepository(mongoDB)
            );

            appointmentMaker.run(new AppointmentRequest(args[0].toUpperCase()));
        } catch (Exception e) {
            throw new RuntimeException(e);
//            System.out.println("e = " + e);
//            e.printStackTrace();
        }
    }
}
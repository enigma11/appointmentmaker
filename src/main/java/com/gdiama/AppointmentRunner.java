package com.gdiama;

import com.gdiama.app.AppointmentMaker;
import com.gdiama.domain.AppointmentRequest;
import com.gdiama.infrastructure.ContactsRepository;
import com.gdiama.infrastructure.MongoDB;

import java.util.concurrent.TimeUnit;

public class AppointmentRunner {


    public static void main(String[] args) throws Exception {
        while (true) {
            try {
                System.out.println("Running...");

                MongoDB mongoDB = MongoDB.get();
                AppointmentMaker appointmentMaker = new AppointmentMaker(mongoDB, new ContactsRepository(mongoDB));
                appointmentMaker.run(new AppointmentRequest(args[0].toUpperCase()));

                System.out.println("Sleeping...");
            } catch (Exception e) {
                e.printStackTrace();
            }
            TimeUnit.MINUTES.sleep(15);
        }
    }
}
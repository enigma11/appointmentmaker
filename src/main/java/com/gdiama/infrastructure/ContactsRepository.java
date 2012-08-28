package com.gdiama.infrastructure;

import com.gdiama.domain.ContactDetails;

public class ContactsRepository {

    private final MongoDB mongoDB;

    public ContactsRepository(MongoDB mongoDB) {
        this.mongoDB = mongoDB;
    }

    public ContactDetails loadContactDetails () {
        return mongoDB.getMongoTemplate().findAll(ContactDetails.class).get(0);
    }
}

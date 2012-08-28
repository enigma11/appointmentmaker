package com.gdiama.infrastructure;

import com.gdiama.Audit;

public class AuditRepository {

    private final MongoDB mongoDB;

    public AuditRepository(MongoDB mongoDB) {
        this.mongoDB = mongoDB;
    }

    public void save(Audit audit) {
        mongoDB.getMongoTemplate().save(audit);
    }
}

package com.gdiama.infrastructure;

import com.gdiama.Audit;

public class AuditRepository {

    private final DatabaseAccess databaseAccess;

    public AuditRepository(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public void save(Audit audit) {
        databaseAccess.save(audit);
    }
}

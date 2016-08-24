package net.hawkengine.model;

import java.util.UUID;

public class DbEntry extends PermissionObject {
    protected final String id;

    public DbEntry() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return this.id;
    }
}

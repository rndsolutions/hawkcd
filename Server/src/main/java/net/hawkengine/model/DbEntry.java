package net.hawkengine.model;

import java.util.UUID;

public class DbEntry {
    private final String id;

    public DbEntry() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return this.id;
    }
}

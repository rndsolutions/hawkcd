package io.hawkcd.model.enums;

public enum PermissionEntity {
    SERVER(1),
    ALL_PIPELINE_GROUPS(2),
    ALL_PIPELINES(3),
    SPECIFIC_ENTITY(4);

    int priorityLevel;

    PermissionEntity(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public int getPriorityLevel() {
        return this.priorityLevel;
    }
}

package net.hawkengine.agent.enums;

import com.google.gson.annotations.SerializedName;

public enum ExecutionState {
    @SerializedName("None")
    NONE,
    @SerializedName("Scheduled")
    SCHEDULED,
    @SerializedName("Running")
    RUNNING,
    @SerializedName("Completed")
    COMPLETED,
    @SerializedName("Paused")
    PAUSED,
    @SerializedName("Canceled")
    CANCELED,
    @SerializedName("Incomplete")
    INCOMPLETE
}
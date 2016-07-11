package net.hawkengine.agent.enums;

import com.google.gson.annotations.SerializedName;

public enum ExecutionStatus
{
    @SerializedName("Passed")
    PASSED,
    @SerializedName("Failed")
    FAILED
}
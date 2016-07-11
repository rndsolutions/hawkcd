package net.hawkengine.agent.enums;

import com.google.gson.annotations.SerializedName;

public enum MaterialType
{
    @SerializedName("Git")
    GIT,
    @SerializedName("Tfs")
    TFS,
    @SerializedName("Nuget")
    NUGET,
    PIPELINE
}

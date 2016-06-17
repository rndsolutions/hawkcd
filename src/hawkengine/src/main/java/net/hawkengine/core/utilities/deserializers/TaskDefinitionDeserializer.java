package net.hawkengine.core.utilities.deserializers;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.hawkengine.model.ExecTask;
import net.hawkengine.model.FetchArtifactTask;
import net.hawkengine.model.FetchMaterialTask;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.UploadArtifactTask;
import net.hawkengine.model.enums.TaskType;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TaskDefinitionDeserializer implements JsonDeserializer<TaskDefinition> {
    private Map<String, Type> taskTypeMap;
    private Gson jsonConverter;

    public TaskDefinitionDeserializer() {
        this.taskTypeMap = new HashMap() {{
            put(TaskType.EXEC.toString(), ExecTask.class);
            put(TaskType.FETCH_ARTIFACT.toString(), FetchArtifactTask.class);
            put(TaskType.FETCH_MATERIAL.toString(), FetchMaterialTask.class);
            put(TaskType.UPLOAD_ARTIFACT.toString(), UploadArtifactTask.class);
        }};
        this.jsonConverter = new Gson();
    }

    @Override
    public TaskDefinition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.get("type") == null) {
            throw new JsonParseException("Field type is null!");
        }

        String typeOfTask = jsonObject.get("type").getAsString();
        Type taskClass = this.taskTypeMap.get(typeOfTask);
        if (taskClass == null) {
            throw new JsonParseException("Invalid Task Definition type!");
        }

        TaskDefinition result = this.jsonConverter.fromJson(json, taskClass);
        return result;
    }
}
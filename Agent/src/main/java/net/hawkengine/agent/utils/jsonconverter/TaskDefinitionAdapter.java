package net.hawkengine.agent.utils.jsonconverter;

import com.google.gson.*;
import net.hawkengine.agent.enums.TaskType;
import net.hawkengine.agent.models.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TaskDefinitionAdapter implements JsonSerializer<TaskDefinition>, JsonDeserializer<TaskDefinition> {
    private Map<String, Type> taskTypeMap;
    private Gson jsonConverter;

    public TaskDefinitionAdapter() {
        this.taskTypeMap = new HashMap() {{
            this.put(TaskType.EXEC.toString(), ExecTask.class);
            this.put(TaskType.FETCH_ARTIFACT.toString(), FetchArtifactTask.class);
            this.put(TaskType.FETCH_MATERIAL.toString(), FetchMaterialTask.class);
            this.put(TaskType.UPLOAD_ARTIFACT.toString(), UploadArtifactTask.class);
        }};
        this.jsonConverter = new Gson();
    }

    @Override
    public JsonElement serialize(TaskDefinition src, Type typeOfSrc, JsonSerializationContext context) {
        Type taskType = this.taskTypeMap.get(src.getType().toString());
        JsonObject element = (JsonObject) this.jsonConverter.toJsonTree(src, taskType);
        return element;
    }

    @Override
    public TaskDefinition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        try {
            if (type.equals("EXEC")) {
                return context.deserialize(jsonObject, ExecTask.class);
            } else if (type.equals("UPLOAD_ARTIFACT")) {
                return context.deserialize(jsonObject, UploadArtifactTask.class);
            } else if (type.equals("FETCH_MATERIAL")) {
                return context.deserialize(jsonObject, FetchMaterialTask.class);
            } else if (type.equals("FETCH_ARTIFACT")) {
                return context.deserialize(jsonObject, FetchArtifactTask.class);
            }

            return context.deserialize(jsonObject, Class.forName("com.googlecode.whiteboard.model." + type));
        } catch (
                ClassNotFoundException cnfe
                ) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
    }
}

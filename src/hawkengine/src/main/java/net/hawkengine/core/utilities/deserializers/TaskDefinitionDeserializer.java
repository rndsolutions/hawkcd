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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TaskDefinitionDeserializer implements JsonDeserializer<TaskDefinition> {

    private Map<String, Type> taskTypeMap;
    private Gson gson;

    public TaskDefinitionDeserializer() {
        this.taskTypeMap = new HashMap() {{
            put("EXEC", ExecTask.class);
            put("FETCH_ARTIFACT", FetchArtifactTask.class);
            put("FETCH_MATERIAL", FetchMaterialTask.class);
            put("UPLOAD_ARTIFACT", UploadArtifactTask.class);
        }};
        this.gson = new Gson();
    }

    @Override
    public TaskDefinition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonAsJsonObject = json.getAsJsonObject();
        String typeOfTask = jsonAsJsonObject.get("type").getAsString();
        Type taskClass = this.taskTypeMap.get(typeOfTask);
        if (taskClass == null) {
            throw new JsonParseException("Required field not found");
        }

        TaskDefinition result = this.gson.fromJson(json, taskClass);
        return result;
    }
}
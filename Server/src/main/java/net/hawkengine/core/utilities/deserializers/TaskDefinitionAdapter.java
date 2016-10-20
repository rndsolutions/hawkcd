/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.hawkengine.core.utilities.deserializers;

import com.google.gson.*;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.TaskType;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TaskDefinitionAdapter implements JsonDeserializer<TaskDefinition>, JsonSerializer<TaskDefinition> {
    private Map<String, Type> taskTypeMap;
    private Gson jsonConverter;

    public TaskDefinitionAdapter() {
        this.taskTypeMap = new HashMap() {{
            this.put(TaskType.EXEC.toString(), ExecTask.class);
            this.put(TaskType.FETCH_ARTIFACT.toString(), FetchArtifactTask.class);
            this.put(TaskType.FETCH_MATERIAL.toString(), FetchMaterialTask.class);
            this.put(TaskType.UPLOAD_ARTIFACT.toString(), UploadArtifactTask.class);
        }};
        this.jsonConverter = new GsonBuilder().registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter()).create();
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

    @Override
    public JsonElement serialize(TaskDefinition src, Type typeOfSrc, JsonSerializationContext context) {
        Type taskType = this.taskTypeMap.get(src.getType().toString());
        JsonObject element = (JsonObject) this.jsonConverter.toJsonTree(src, taskType);
        return element;
    }
}

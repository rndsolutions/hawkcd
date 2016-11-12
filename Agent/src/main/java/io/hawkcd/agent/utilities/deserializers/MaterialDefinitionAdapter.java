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

package io.hawkcd.agent.utilities.deserializers;

import com.google.gson.*;
import io.hawkcd.agent.enums.MaterialType;
import io.hawkcd.agent.models.GitMaterial;
import io.hawkcd.agent.models.NugetMaterial;
import io.hawkcd.agent.models.MaterialDefinition;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MaterialDefinitionAdapter implements JsonDeserializer<MaterialDefinition>, JsonSerializer<MaterialDefinition> {
    private Map<String, Type> materialTypeMap;
    private Gson jsonConverter;

    public MaterialDefinitionAdapter() {
        this.materialTypeMap = new HashMap() {{
            this.put(MaterialType.GIT.toString(), GitMaterial.class);
            this.put(MaterialType.NUGET.toString(), NugetMaterial.class);
        }};
        this.jsonConverter = new Gson();
    }

    @Override
    public MaterialDefinition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.get("type") == null) {
            throw new JsonParseException("Field type is null!");
        }

        String typeOfMaterial = jsonObject.get("type").getAsString();
        Type taskClass = this.materialTypeMap.get(typeOfMaterial);
        if (taskClass == null) {
            throw new JsonParseException("Invalid Material Definition type!");
        }

        MaterialDefinition result = this.jsonConverter.fromJson(json, taskClass);
        return result;
    }

    @Override
    public JsonElement serialize(MaterialDefinition src, Type typeOfSrc, JsonSerializationContext context) {
        Type taskType = this.materialTypeMap.get(src.getType().toString());
        JsonObject element = (JsonObject) this.jsonConverter.toJsonTree(src, taskType);
        return element;
    }
}
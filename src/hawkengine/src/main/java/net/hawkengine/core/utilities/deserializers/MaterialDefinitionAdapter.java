package net.hawkengine.core.utilities.deserializers;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.NugetMaterial;
import net.hawkengine.model.enums.MaterialType;

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
        JsonObject element = (JsonObject) this.jsonConverter.toJsonTree(src,taskType);
        return element;
    }
}

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
import net.hawkengine.model.dto.ConversionObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConversionObjectDeserializer implements JsonDeserializer<ConversionObject> {
    private final List<String> requiredFields;

    public ConversionObjectDeserializer() {
        this.requiredFields = new ArrayList<>(Arrays.asList("packageName", "object"));
    }

    @Override
    public ConversionObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        for (String fieldName : this.requiredFields) {
            if (jsonObject.get(fieldName) == null) {
                throw new JsonParseException("Required field not found");
            }
        }

        String packageName = jsonObject.get("packageName").getAsString();
        String object = jsonObject.get("object").toString();
        if ((packageName == null) || packageName.trim().isEmpty() || (object == null)) {
            return null;
        }

        ConversionObject result = new ConversionObject();
        result.setPackageName(packageName);
        result.setObject(object);
        return result;
    }
}

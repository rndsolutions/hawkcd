/*
 *   Copyright (C) 2016 R&D Solutions Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *
 */

package io.hawkcd.core.subscriber;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EnvelopeAdapter implements JsonDeserializer<Envelopе> {
    @Override
    public Envelopе deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement objectAsJsonElement = jsonObject.get("object");
        if (objectAsJsonElement == null || !objectAsJsonElement.isJsonArray() && !objectAsJsonElement.isJsonObject() && objectAsJsonElement.getAsString().equals("")) {
            return new Envelopе();
        }

        Type resultObjectType = null;
        try {
            resultObjectType = Class.forName(jsonObject.get("packageName").getAsString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Object result;
        if (objectAsJsonElement.isJsonArray()) {
            List<Object> resultAsList = new ArrayList<>();
            JsonArray objectAsJsonArray = objectAsJsonElement.getAsJsonArray();
            for (JsonElement element : objectAsJsonArray) {
                resultAsList.add(context.deserialize(element, resultObjectType));
            }

            result = resultAsList;
        } else {
            result = context.deserialize(objectAsJsonElement, resultObjectType);
        }

        return new Envelopе(result);
    }
}

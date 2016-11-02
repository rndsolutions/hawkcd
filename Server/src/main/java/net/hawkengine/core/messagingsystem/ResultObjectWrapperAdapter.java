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

package net.hawkengine.core.messagingsystem;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ResultObjectWrapperAdapter implements JsonDeserializer<ResultObjectWrapper> {
    @Override
    public ResultObjectWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement resultObjectAsString = jsonObject.get("resultObject");
        if (resultObjectAsString == null) {
            return null;
        }

        Type resultObjectType = null;
        try {
            String packageName = jsonObject.get("resultObjectType").getAsString();
            resultObjectType = Class.forName(packageName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Object resultObject = context.deserialize(resultObjectAsString, resultObjectType);

        ResultObjectWrapper result = new ResultObjectWrapper(resultObject);

        return result;
    }
}

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

package io.hawkcd.utilities.deserializers;

import com.google.gson.*;
import io.hawkcd.model.dto.ConversionObject;
import io.hawkcd.model.dto.WsContractDto;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WsContractDeserializer implements JsonDeserializer<WsContractDto> {
    private List<String> requiredFields;
    private Gson jsonConverter;

    public WsContractDeserializer() {
        this.requiredFields = new ArrayList<>(
                Arrays.asList("className", "packageName", "methodName", "result", "notificationType", "errorMessage", "args"));
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(ConversionObject.class, new ConversionObjectDeserializer())
                .create();
    }

    @Override
    public WsContractDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        for (String fieldName : this.requiredFields) {
            if (jsonObject.get(fieldName) == null) {
                throw new JsonParseException("Required field not found");
            }
        }

        WsContractDto result = this.jsonConverter.fromJson(json, WsContractDto.class);
        return result;
    }
}

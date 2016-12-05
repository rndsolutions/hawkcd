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

package io.hawkcd.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;
import io.hawkcd.utilities.deserializers.WsContractDeserializer;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.ws.Command;

import java.util.ArrayList;
import java.util.List;

/*
*Dispatche calls to lower level system components via reflection
*/

public class WsObjectProcessor {
    private Gson jsonConverter;

    public WsObjectProcessor() {
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }

    public Object call(WsContractDto contract) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String fullPackageName = String.format("%s.%s", contract.getPackageName(), contract.getClassName());
        Object service = Class.forName(fullPackageName).newInstance();
        List<Object> methodArgs = new ArrayList<>();
        int contractArgsLength = contract.getArgs().length;
        for (int i = 0; i < contractArgsLength; i++) {
            if (contract.getArgs()[i] != null) {
//                Class objectClass = Class.forName(contract.getArgs()[i].getPackageName());
//                Object object = this.jsonConverter.fromJson(contract.getArgs()[i].getObject(), objectClass);
//                methodArgs.add(object);
            }
        }

        Command command = new Command(service, contract.getMethodName(), methodArgs);
        return command.execute();
    }

    public Object call(String serviceToBeCalled, String methodName, String id) {
        try {
            Object service = Class.forName(serviceToBeCalled).newInstance();
            //Class.forName()
            List<Object> methodArgs = new ArrayList<>();
            methodArgs.add(id);
            Command command = new Command(service, methodName, methodArgs);
            return command.execute();
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}

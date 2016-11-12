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

package io.hawkcd.ws;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Command {
    private final Object object;
    private final String methodName;
    private final List<Object> methodArgs;

    public Command(Object object, String methodName, List<Object> methodArgs) {
        this.object = object;
        this.methodName = methodName;
        this.methodArgs = methodArgs;
    }

    public Object execute() {
        Class objectClass = this.object.getClass();
        Method method;
        try {
            if ((this.methodArgs == null) || this.methodArgs.isEmpty()) {
                method = objectClass.getMethod(this.methodName);
                return method.invoke(this.object);
            } else {
                Class[] argTypes = new Class[this.methodArgs.size()];
                for (int i = 0; i < this.methodArgs.size(); i++) {
                    argTypes[i] = this.methodArgs.get(i).getClass();
                }

                method = objectClass.getMethod(this.methodName, argTypes);
                return method.invoke(this.object, this.methodArgs.toArray());
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}

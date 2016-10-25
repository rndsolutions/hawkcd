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

package net.hawkengine.agent.models;

/// <summary>
/// If the environment variable is isSecured, its value is encrypted. If not, it is saved in plain text.
/// Also additional attribute "secure=true" is added in the XML.
/// </summary>
public class EnvironmentVariable {
    private String name;
    private String value;
    private Boolean isSecured;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isSecured() {
        return isSecured;
    }

    public void setSecured(Boolean isSecured) {
        this.isSecured = isSecured;
    }
}


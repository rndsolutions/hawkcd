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

package net.hawkengine.model.payload;

import net.hawkengine.model.*;

import java.util.ArrayList;

public class ServerResponseToAgent {
    private Pipeline pipeline;
    private JobDefinition jobDefinition;
    private Stage stage;
    private EnvironmentInfo environmentInfo;
    private ArrayList<MaterialDefinition> materialDefinitions;
    private ArrayList<Material> materials;
    private ArrayList<EnvironmentVariable> environmentVariables;

    public ServerResponseToAgent() {
        this.setEnvironmentVariables(new ArrayList<>());
    }

    public Pipeline getPipeline() {
        return this.pipeline;
    }

    public void setPipeline(Pipeline value) {
        this.pipeline = value;
    }

    public JobDefinition getJob() {
        return this.jobDefinition;
    }

    public void setJob(JobDefinition value) {
        this.jobDefinition = value;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage value) {
        this.stage = value;
    }

    public EnvironmentInfo getEnvironmentInfo() {
        return this.environmentInfo;
    }

    public void setEnvironmentInfo(EnvironmentInfo value) {
        this.environmentInfo = value;
    }

    public ArrayList<MaterialDefinition> getMaterials() {
        return this.materialDefinitions;
    }

    public void setMaterials(ArrayList<MaterialDefinition> value) {
        this.materialDefinitions = value;
    }

    public ArrayList<Material> getExecutionMaterials() {
        return this.materials;
    }

    public void setExecutionMaterials(ArrayList<Material> value) {
        this.materials = value;
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
        this.environmentVariables = value;
    }

}

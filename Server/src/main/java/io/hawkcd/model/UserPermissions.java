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

package io.hawkcd.model;

import io.hawkcd.model.payload.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class that contains sorted Permissions for specific Pipelines, specific PipelineGroups
 * and generic Permissions
 */
public class UserPermissions {
    private List<Permission> pipelineSpecific;
    private List<Permission> pipelineGroupSpecific;
    private List<Permission> generic;

    UserPermissions() {
        this.pipelineSpecific = new ArrayList<>();
        this.pipelineGroupSpecific = new ArrayList<>();
        this.generic = new ArrayList<>();
    }

    public UserPermissions(List<Permission> pipelineSpecific, List<Permission> pipelineGroupSpecific, List<Permission> generic) {
        this.pipelineSpecific = pipelineSpecific;
        this.pipelineGroupSpecific = pipelineGroupSpecific;
        this.generic = generic;
    }

    public List<Permission> getPipelineSpecific() {
        return this.pipelineSpecific;
    }

    public void setPipelineSpecific(List<Permission> pipelineSpecific) {
        this.pipelineSpecific = pipelineSpecific;
    }

    public List<Permission> getPipelineGroupSpecific() {
        return this.pipelineGroupSpecific;
    }

    public void setPipelineGroupSpecific(List<Permission> pipelineGroupSpecific) {
        this.pipelineGroupSpecific = pipelineGroupSpecific;
    }

    public List<Permission> getGeneric() {
        return this.generic;
    }

    public void setGeneric(List<Permission> generic) {
        this.generic = generic;
    }
}

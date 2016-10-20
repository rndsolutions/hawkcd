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

package net.hawkengine.services.interfaces;

import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;

public interface IPipelineDefinitionService extends ICrudService<PipelineDefinition> {
    ServiceResult getAllAutomaticallyScheduledPipelines();

    ServiceResult add(PipelineDefinition pipelineDefinition, MaterialDefinition materialDefinition);

    ServiceResult addWithMaterialDefinition(PipelineDefinition pipelineDefinition, GitMaterial materialDefinition);

    ServiceResult addWithMaterialDefinition(PipelineDefinition pipelineDefinition, String materialDefinitionId);

    ServiceResult unassignPipelineFromGroup(String pipelineDefinitionId);

    ServiceResult assignPipelineToGroup(String pipelineDefinitionId, String pipelineGroupId, String pipelineGroupName);
}

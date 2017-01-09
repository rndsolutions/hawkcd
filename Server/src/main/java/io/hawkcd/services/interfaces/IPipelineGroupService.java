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

package io.hawkcd.services.interfaces;

import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.PipelineGroup;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.dto.PipelineGroupDto;

import java.util.List;

public interface IPipelineGroupService extends ICrudService<PipelineGroup> {
    ServiceResult getAllPipelineGroupDTOs();

    List<PipelineGroup> placePipelinesIntoGroups(List<PipelineGroup> pipelineGroups, List<PipelineDefinition> pipelineDefinitions);

    List<PipelineGroupDto> convertGroupsToDtos(List<PipelineGroup> pipelineGroups);
}

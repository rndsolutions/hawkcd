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

import io.hawkcd.model.Entity;
import io.hawkcd.model.ServiceResult;

public interface IPipelineMongoService<T extends Entity>  {
    ServiceResult getByQuantity(String definitionId, Integer quantity, String firstId);

    ServiceResult getByDefinitionId(String definitionId);

    ServiceResult getAllUpdatedUnpreparedPipelinesInProgress();

    ServiceResult getAllPreparedAwaitingPipelines();

    ServiceResult getAllPreparedPipelinesInProgress();

    ServiceResult getAllNonupdatedPipelines();

    T GetLastPipeline(String definitionId);

    ServiceResult getAllPipelineArtifactDTOs(String searchCriteria, Integer numberOfPipelines, Integer skip, String pipelineId);
}

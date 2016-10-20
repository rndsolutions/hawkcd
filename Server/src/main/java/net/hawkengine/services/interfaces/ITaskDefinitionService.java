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

import net.hawkengine.model.*;

public interface ITaskDefinitionService extends ICrudService<TaskDefinition> {
    ServiceResult add(ExecTask taskDefinition);

    ServiceResult add(UploadArtifactTask taskDefinition);

    ServiceResult add(FetchMaterialTask taskDefinition);

    ServiceResult add(FetchArtifactTask taskDefinition);

    ServiceResult addTask(TaskDefinition taskDefinition);

    ServiceResult update(ExecTask taskDefintion);

    ServiceResult update(UploadArtifactTask taskDefintion);

    ServiceResult update(FetchMaterialTask taskDefintion);

    ServiceResult update(FetchArtifactTask taskDefintion);

    ServiceResult updateTask(TaskDefinition taskDefinition);
}

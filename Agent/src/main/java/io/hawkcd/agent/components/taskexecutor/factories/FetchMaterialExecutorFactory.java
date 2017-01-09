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

package io.hawkcd.agent.components.taskexecutor.factories;

import io.hawkcd.agent.components.taskexecutor.executors.FetchMaterialExecutor;
import io.hawkcd.agent.models.FetchMaterialTask;
import io.hawkcd.agent.models.TaskDefinition;
import io.hawkcd.agent.services.GitMaterialService;
import io.hawkcd.agent.services.NuGetMaterialService;
import io.hawkcd.agent.services.TFSMaterialService;

public class FetchMaterialExecutorFactory {
    public static FetchMaterialExecutor create(TaskDefinition task) {
        FetchMaterialTask taskDefinition = (FetchMaterialTask) task;
        switch (taskDefinition.getMaterialType()) {
            case GIT:
                return new FetchMaterialExecutor(new GitMaterialService());
            case TFS:
                return new FetchMaterialExecutor(new TFSMaterialService());
            case NUGET:
                return new FetchMaterialExecutor(new NuGetMaterialService());
            default:
                return null;
        }
    }
}

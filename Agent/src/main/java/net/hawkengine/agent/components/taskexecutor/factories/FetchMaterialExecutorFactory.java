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

package net.hawkengine.agent.components.taskexecutor.factories;

import net.hawkengine.agent.components.taskexecutor.executors.FetchMaterialExecutor;
import net.hawkengine.agent.models.FetchMaterialTask;
import net.hawkengine.agent.models.TaskDefinition;
import net.hawkengine.agent.services.GitMaterialService;
import net.hawkengine.agent.services.NuGetMaterialService;
import net.hawkengine.agent.services.TFSMaterialService;

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

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

import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.components.taskexecutor.executors.ExecTaskExecutor;
import net.hawkengine.agent.components.taskexecutor.executors.FetchArtifactExecutor;
import net.hawkengine.agent.components.taskexecutor.executors.UploadArtifactExecutor;
import net.hawkengine.agent.models.TaskDefinition;

public class TaskExecutorFactory {

    public static TaskExecutor create(TaskDefinition task) {
        switch (task.getType()) {
            case EXEC:
                return new ExecTaskExecutor();
            case FETCH_MATERIAL:
                return FetchMaterialExecutorFactory.create(task);
            case FETCH_ARTIFACT:
                return new FetchArtifactExecutor();
            case UPLOAD_ARTIFACT:
                return new UploadArtifactExecutor();
            default:
                return null;
        }
    }
}

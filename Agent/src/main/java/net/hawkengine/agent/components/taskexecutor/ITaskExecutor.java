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

package net.hawkengine.agent.components.taskexecutor;

import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.payload.WorkInfo;

public interface ITaskExecutor {
    Task executeTask(Task task, StringBuilder report, WorkInfo workInfo);

    Task nullProcessing(StringBuilder report, Task task, String errorMessage);
}
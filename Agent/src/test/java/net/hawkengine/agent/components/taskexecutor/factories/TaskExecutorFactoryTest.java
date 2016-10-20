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
import net.hawkengine.agent.models.ExecTask;
import net.hawkengine.agent.models.TaskDefinition;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TaskExecutorFactoryTest {

    private TaskExecutorFactory factory = new TaskExecutorFactory();
    private TaskDefinition task = null;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testBuild() throws Exception {
        this.task = new ExecTask();
        TaskExecutor actual = this.factory.create(this.task);
        TaskExecutor expected = new ExecTaskExecutor();

        Assert.assertEquals(expected.getClass(), actual.getClass());
    }
}
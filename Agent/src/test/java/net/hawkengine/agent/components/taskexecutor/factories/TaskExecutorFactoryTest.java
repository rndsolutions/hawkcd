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
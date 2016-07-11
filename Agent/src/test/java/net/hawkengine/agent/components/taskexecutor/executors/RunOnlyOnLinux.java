package net.hawkengine.agent.components.taskexecutor.executors;

import org.apache.commons.lang3.SystemUtils;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class RunOnlyOnLinux extends JUnit4ClassRunner {
    public RunOnlyOnLinux(Class className) throws InitializationError, org.junit.internal.runners.InitializationError {
        super(className);
    }

    @Override
    public void run(RunNotifier notifier) {
        if (SystemUtils.IS_OS_LINUX) {
            super.run(notifier);
        }
    }
}
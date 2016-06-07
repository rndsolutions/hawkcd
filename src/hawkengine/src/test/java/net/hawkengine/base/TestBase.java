package net.hawkengine.base;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class TestBase {

    protected void checkResult(AtomicBoolean testResult, StringBuilder errorMessages, boolean statement, String errorMsg) {

        testResult.set(testResult.get() && statement);
        if (!statement) {
            errorMessages.append(errorMsg + ";");
            errorMessages.append(System.getProperty("line.separator"));
        }
    }
}

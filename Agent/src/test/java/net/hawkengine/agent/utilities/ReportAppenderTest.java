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

package net.hawkengine.agent.utilities;

import net.hawkengine.agent.constants.MessageConstants;
import net.hawkengine.agent.enums.JobStatus;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.Job;
import net.hawkengine.agent.models.Task;
import org.junit.Assert;
import org.junit.Test;

public class ReportAppenderTest {
    @Test
    public void reportAppender_instantiated_notNull() {
        // Act
        ReportAppender reportAppender = new ReportAppender();

        // Assert
        Assert.assertNotNull(reportAppender);
    }

    @Test
    public void appendStartedMessage_jobStarted_correctMessage() {
        // Arrange
        String message = "Test message";
        StringBuilder report = new StringBuilder();
        Class classType = Job.class;
        String expectedResult =
                MessageConstants.CONSOLE_YELLOW + message + System.lineSeparator() + MessageConstants.CONSOLE_WHITE + MessageConstants.CONSOLE_THICK_LINE + System.lineSeparator();

        // Act
        String actualResult = ReportAppender.appendStartedMessage(message, report, classType).toString();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void appendStartedMessage_taskStarted_correctMessage() {
        // Arrange
        String message = "Test message";
        StringBuilder report = new StringBuilder();
        Class classType = Task.class;
        String expectedResult =
                MessageConstants.CONSOLE_YELLOW + message + System.lineSeparator() + MessageConstants.CONSOLE_WHITE + MessageConstants.CONSOLE_THIN_LINE + System.lineSeparator();

        // Act
        String actualResult = ReportAppender.appendStartedMessage(message, report, classType).toString();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void appendCompletedMessage_jobCompletedWithStatusPassed_correctMessage() {
        // Arrange
        String message = "Test message";
        StringBuilder report = new StringBuilder();
        JobStatus status = JobStatus.PASSED;
        String expectedResult = MessageConstants.CONSOLE_GREEN + message + System.lineSeparator();

        // Act
        String actualResult = ReportAppender.appendCompletedMessage(message, report, status).toString();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void appendCompletedMessage_jobCompletedWithStatusFailed_correctMessage() {
        // Arrange
        String message = "Test message";
        StringBuilder report = new StringBuilder();
        JobStatus status = JobStatus.FAILED;
        String expectedResult = MessageConstants.CONSOLE_RED + message + System.lineSeparator();

        // Act
        String actualResult = ReportAppender.appendCompletedMessage(message, report, status).toString();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void appendCompletedMessage_taskCompletedWithStatusPassed_correctMessage() {
        // Arrange
        String message = "Test message";
        StringBuilder report = new StringBuilder();
        TaskStatus status = TaskStatus.PASSED;
        String expectedResult = MessageConstants.CONSOLE_GREEN + message + System.lineSeparator();

        // Act
        String actualResult = ReportAppender.appendCompletedMessage(message, report, status).toString();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void appendCompletedMessage_taskCompletedWithStatusFailed_correctMessage() {
        // Arrange
        String message = "Test message";
        StringBuilder report = new StringBuilder();
        TaskStatus status = TaskStatus.FAILED;
        String expectedResult = MessageConstants.CONSOLE_RED + message + System.lineSeparator();

        // Act
        String actualResult = ReportAppender.appendCompletedMessage(message, report, status).toString();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void appendInfoMessage() {
        // Arrange
        String message = "Test message";
        StringBuilder report = new StringBuilder();
        String expectedResult = MessageConstants.CONSOLE_WHITE + message + System.lineSeparator();

        // Act
        String actualResult = ReportAppender.appendInfoMessage(message, report).toString();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

}
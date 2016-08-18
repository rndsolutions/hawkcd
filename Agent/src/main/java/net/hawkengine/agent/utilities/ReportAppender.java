package net.hawkengine.agent.utilities;

import net.hawkengine.agent.constants.MessageConstants;
import net.hawkengine.agent.enums.JobStatus;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.Job;

public class ReportAppender {
    public static StringBuilder appendStartedMessage(String message, StringBuilder report, Class classType) {
        message = MessageConstants.CONSOLE_YELLOW + message;
        report.append(message).append(System.lineSeparator());
        String line;
        if (classType == Job.class) {
            line = MessageConstants.CONSOLE_WHITE + MessageConstants.CONSOLE_THICK_LINE;
        } else {
            line = MessageConstants.CONSOLE_WHITE + MessageConstants.CONSOLE_THIN_LINE;
        }

        report.append(line).append(System.lineSeparator());

        return report;
    }

    public static StringBuilder appendCompletedMessage(String message, StringBuilder report, JobStatus status) {
        if (status == JobStatus.PASSED) {
            message = MessageConstants.CONSOLE_GREEN + message;
        } else {
            message = MessageConstants.CONSOLE_RED + message;
        }

        report.append(message).append(System.lineSeparator());

        return report;
    }

    public static StringBuilder appendCompletedMessage(String message, StringBuilder report, TaskStatus status) {
        if (status == TaskStatus.PASSED) {
            message = MessageConstants.CONSOLE_GREEN + message;
        } else {
            message = MessageConstants.CONSOLE_RED + message;
        }

        report.append(message).append(System.lineSeparator());

        return report;
    }


    public static StringBuilder appendInfoMessage(String message, StringBuilder report) {
        message = MessageConstants.CONSOLE_WHITE + message;
        report.append(message).append(System.lineSeparator());

        return report;
    }
}

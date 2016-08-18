package net.hawkengine.agent.utilities;

import net.hawkengine.agent.constants.MessageConstants;
import net.hawkengine.agent.enums.JobStatus;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.Job;

public class ReportAppender {
    public static StringBuilder appendStartedMessage(String message, StringBuilder report, Class classType) {
        message = MessageConstants.CONSOLE_YELLOW + message;
        report.append(message).append(System.getProperty("line.separator"));
        String line;
        if (classType == Job.class) {
            line = MessageConstants.CONSOLE_WHITE + MessageConstants.CONSOLE_THICK_LINE;
        } else {
            line = MessageConstants.CONSOLE_WHITE + MessageConstants.CONSOLE_THIN_LINE;
        }

        report.append(line).append(System.getProperty("line.separator"));

        return report;
    }

    public static StringBuilder appendCompletedMessage(String message, StringBuilder report, JobStatus jobStatus) {
        if (jobStatus == JobStatus.PASSED) {
            message = MessageConstants.CONSOLE_GREEN + message;
        } else {
            message = MessageConstants.CONSOLE_RED + message;
        }

        report.append(message).append(System.getProperty("line.separator"));

        return report;
    }

    public static StringBuilder appendCompletedMessage(String message, StringBuilder report, TaskStatus taskStatus) {
        if (taskStatus == TaskStatus.PASSED) {
            message = MessageConstants.CONSOLE_GREEN + message;
        } else {
            message = MessageConstants.CONSOLE_RED + message;
        }

        report.append(message).append(System.getProperty("line.separator"));

        return report;
    }


    public static StringBuilder appendInfoMessage(String message, StringBuilder report) {
        message = MessageConstants.CONSOLE_WHITE + message;
        report.append(message).append(System.getProperty("line.separator"));

        return report;
    }
}

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

package io.hawkcd.agent.utilities;

import io.hawkcd.agent.constants.MessageConstants;
import io.hawkcd.agent.enums.JobStatus;
import io.hawkcd.agent.enums.TaskStatus;
import io.hawkcd.agent.models.Job;

import org.apache.commons.lang3.StringEscapeUtils;

import java.time.LocalDateTime;

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

        //message = MessageConstants.CONSOLE_WHITE + message;
        if (!message.isEmpty()){
            String formattedMessage = String.format("%s %s", getTimeStamp(), message);

            System.out.println(StringEscapeUtils.unescapeJava(formattedMessage));

            report.append(formattedMessage).append(System.lineSeparator());
        }else {
            report.append(message).append(System.lineSeparator());
        }
        return report;
    }

    private static String getTimeStamp(){
        // e.g. [10:51:50:564]:
        LocalDateTime now = LocalDateTime.now();
        String format = String.format("[%02d:%02d:%02d]:", now.getHour(), now.getMinute(), now.getSecond());
        return format;
    }

}

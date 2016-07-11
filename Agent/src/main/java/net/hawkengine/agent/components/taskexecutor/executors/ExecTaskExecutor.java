package net.hawkengine.agent.components.taskexecutor.executors;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.constants.LoggerMessages;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.ExecTask;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.payload.WorkInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class ExecTaskExecutor extends TaskExecutor {
    @Override
    public Task executeTask(Task task, StringBuilder report, WorkInfo workInfo) {

        ExecTask execTask = (ExecTask) task.getTaskDefinition();

        if (report == null) {
            report = new StringBuilder();
        }

        String command = execTask.getCommand();
        String arguments = String.join(" ", execTask.getArguments());

        String commandAndArguments = command + " " + arguments;
        String[] args = commandAndArguments.split(" ");

        report.append(String.format("Command: %s true", command));
        report.append(String.format("Arguments: %s true", arguments));

        //ProcessBuilder builder = new ProcessBuilder(command, arguments);

        ProcessBuilder builder = new ProcessBuilder(command, "-c", arguments);
        builder.redirectErrorStream(true);

        if ((execTask.getWorkingDirectory() != null) && !execTask.getWorkingDirectory().isEmpty()) {
            String workingDir = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDirectoryPath(), workInfo.getPipelineDefinitionName(), execTask.getWorkingDirectory()).toString();
            LOGGER.info(workingDir);

            builder.directory(new File(workingDir));
        } else {
            String workingDir = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDirectoryPath(), workInfo.getPipelineDefinitionName()).toString();
            builder.directory(new File(workingDir));
        }

        Process process = null;
        try {
            String line;
            process = builder.start();
            updateTask(task, TaskStatus.PASSED, LocalDateTime.now(), null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null) {
                LOGGER.info(line);
                report.append(String.format("%s true", line));
            }

            reader.close();
            process.waitFor();
            process.destroy();
        } catch (IOException e) {
            LOGGER.error(String.format(LoggerMessages.TASK_THROWS_EXCEPTION, "22", e.getMessage()));
            report.append(String.format("%s true", e.getMessage()));

        } catch (InterruptedException e) {
            LOGGER.error(String.format(LoggerMessages.TASK_THROWS_EXCEPTION, "2", e.getMessage()));
            report.append(String.format("%s true", e.getMessage()));
        }
        int exitValue = process.exitValue();

        if (process != null && process.exitValue() == 0) {
            super.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());
        } else {
            super.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());
        }

        return task;
    }
}

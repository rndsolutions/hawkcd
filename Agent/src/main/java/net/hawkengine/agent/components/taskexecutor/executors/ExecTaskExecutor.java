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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class ExecTaskExecutor extends TaskExecutor {
    @Override
    public Task executeTask(Task task, StringBuilder report, WorkInfo workInfo) {

        ExecTask execTask = (ExecTask) task.getTaskDefinition();

        String command = execTask.getCommand();
        String arguments = String.join(" ", execTask.getArguments());

        report.append(String.format("Command: %s \n", command));
        report.append(String.format("Arguments: %s \n", arguments));

        ProcessBuilder builder = new ProcessBuilder(command, arguments);
        builder.redirectErrorStream(true);

        if ((execTask.getWorkingDirectory() != null) && !execTask.getWorkingDirectory().isEmpty()) {
            String workingDir = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDir(), workInfo.getPipelineDefinitionName(), execTask.getWorkingDirectory()).toString();
            LOGGER.info(workingDir);

            builder.directory(new File(workingDir));
        } else {
            String workingDir = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDir(), workInfo.getPipelineDefinitionName()).toString();
            builder.directory(new File(workingDir));
            LOGGER.info(workingDir);
        }

        Process process = null;
        try {
            Path path = builder.directory().toPath();
            if (Files.exists(path)) {
                process = builder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        reader.close();
                        break;
                    }
                    LOGGER.info(line);
                    report.append(String.format("%s", line));
                }

                try {
                    process.waitFor();
                    process.destroy();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if ((process != null) && (process.exitValue() == 0)) {
                    this.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());
                } else {
                    if (!execTask.isIgnoringErrors()) {
                        this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());
                    } else {
                        this.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());
                    }
                }
            } else {
                this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());
            }

        } catch (IOException e) {
            LOGGER.error(String.format(LoggerMessages.TASK_THROWS_EXCEPTION, "22", e.getMessage()));
            report.append(String.format("%s true \n", e.getMessage()));
            this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());
        }

        return task;
    }
}

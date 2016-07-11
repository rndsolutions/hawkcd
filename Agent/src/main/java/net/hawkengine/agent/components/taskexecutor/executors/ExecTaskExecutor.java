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

        if (report == null){
            report = new StringBuilder();
        }

        ExecTask execTask = (ExecTask) task.getTaskDefinition();

        String command = execTask.getCommand();
        String arguments = String.join(" ", execTask.getArguments());

        String commandAndArguments = command + " " + arguments;
        String[] args = commandAndArguments.split(" ");

        report.append(String.format("Command: %s true \n", command));
        report.append(String.format("Arguments: %s true \n", arguments));

        ProcessBuilder builder = new ProcessBuilder(args);

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
            Path path = builder.directory().toPath();
            if (Files.exists(path)) {
                process = builder.start();

                this.updateTask(task, TaskStatus.PASSED, LocalDateTime.now(), null);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        break;
                    }
                    LOGGER.info(line);
                    report.append(String.format("%s true", line));
                }
                reader.close();
                process.destroy();

                if ((process != null) && (process.exitValue() == 0)) {
                    this.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());
                } else {
                    if (!execTask.isIgnoringErrors()) {
                        this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());
                    } else {
                        this.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());
                    }
                }
            } else{
                this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());
            }

        } catch (IOException e) {
            LOGGER.error(String.format(LoggerMessages.TASK_THROWS_EXCEPTION, "22", e.getMessage()));
            report.append(String.format("%s true \n", e.getMessage()));
            this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());
        }


//        workInfo.getJob().setReport(report);

        return task;
    }
}

package net.hawkengine.agent.components.taskexecutor.executors;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.constants.MessageConstants;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.ExecTask;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.payload.WorkInfo;
import net.hawkengine.agent.utilities.ReportAppender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ExecTaskExecutor extends TaskExecutor {


    @Override
    public Task executeTask(Task task, StringBuilder report, WorkInfo workInfo) {

        ExecTask execTask = (ExecTask) task.getTaskDefinition();
        ProcessBuilder builder;
        String command = execTask.getCommand();
        boolean isArgumentValid = this.validateArguments(execTask);

        if (!isArgumentValid) {
            return this.nullProcessing(report, task, "Invalid Arguments");
        }

        String commandSwitch = execTask.getArguments().substring(0, 2);
        boolean isCommandSwitch = this.hasCommandSwitch(commandSwitch);

        if (isCommandSwitch) {
            builder = this.constructProcessBuilder(command, execTask, commandSwitch, true);
        } else {
            builder = this.constructProcessBuilder(command, execTask, true);
        }

        String commandMessage = String.format("Command: %s", execTask.getCommand());
        LOGGER.debug(commandMessage);
        ReportAppender.appendInfoMessage(commandMessage, report);
        String argumentsMessage = String.format("Arguments: %s", execTask.getArguments());
        LOGGER.debug(argumentsMessage);
        ReportAppender.appendInfoMessage(argumentsMessage, report);

        this.setProcessBuilderDirectory(builder, execTask, workInfo);

        Process process;
        try {
            Path path = builder.directory().toPath();
            if (Files.exists(path)) {
                process = builder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                this.execute(task, process, reader, isCommandSwitch, report);

            } else {
                this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());
            }
        } catch (IOException e) {
            LOGGER.debug(String.format(MessageConstants.TASK_THROWS_EXCEPTION, e.getMessage()));
            ReportAppender.appendInfoMessage(e.getMessage(), report);
            this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());
        }

        return task;
    }

    private void execute(Task task, Process process, BufferedReader reader, boolean commandSwitch, StringBuilder report) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            LOGGER.debug(line);
            ReportAppender.appendInfoMessage(line, report);
        }

        try {
            if (!commandSwitch) {
                process.destroy();
            } else {
                process.waitFor();
                process.destroy();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.manageExitProcess(process, task);
    }

    private void manageExitProcess(Process process, Task task) {
        ExecTask execTask = (ExecTask) task.getTaskDefinition();

        if ((process != null) && (process.exitValue() == 0)) {
            this.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());
        } else {
            if (!execTask.isIgnoringErrors()) {
                this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());
            } else {
                this.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());
            }
        }
    }

    private boolean validateArguments(ExecTask task) {
        List<String> arguments = Arrays.asList(task.getArguments().split(" "));
        if(arguments.size() == 1) { // if true not cmd switch is provied;
            arguments = Arrays.asList(task.getArguments().split("\n"));
            if (arguments.size() == 1) {
                return false;
            }
            //check for null or empty string
            for (String argumentToValidate : arguments) {
                if (argumentToValidate == null || argumentToValidate.isEmpty()) {
                    return false;
                }
            }
        }
        return  true;
    }

    private boolean hasCommandSwitch(String initialArgument) {
        if (initialArgument.equals("-c") || initialArgument.equals("/c")) {
            return true;
        }

        return false;
    }

    private ProcessBuilder constructProcessBuilder(String command, ExecTask execTask, String initialOption, boolean redirectErrorStream) {
        Integer subStringEndIndex = execTask.getArguments().length();
        String arguments = execTask.getArguments().substring(3, subStringEndIndex);
        ProcessBuilder builderToReturn = new ProcessBuilder(command, initialOption, arguments);
        builderToReturn.redirectErrorStream(redirectErrorStream);
        return builderToReturn;
    }

    private ProcessBuilder constructProcessBuilder(String command, ExecTask execTask, boolean redirectErrorStream) {
        String arguments = String.join(" ", execTask.getArguments());
        String commandAndArguments = command + " " + arguments;
        String[] args = commandAndArguments.split(" ");
        ProcessBuilder builderToReturn = new ProcessBuilder(args);
        builderToReturn.redirectErrorStream(redirectErrorStream);
        return builderToReturn;
    }

    private void setProcessBuilderDirectory(ProcessBuilder builder, ExecTask execTask, WorkInfo workInfo) {
        if ((execTask.getWorkingDirectory() != null) && !execTask.getWorkingDirectory().isEmpty()) {
            String workingDir = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDir(), workInfo.getPipelineDefinitionName(), execTask.getWorkingDirectory()).toString();
            builder.directory(new File(workingDir));
        } else {
            String workingDir = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDir(), workInfo.getPipelineDefinitionName()).toString();
            builder.directory(new File(workingDir));
        }
    }
}

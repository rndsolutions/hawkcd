package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.model.enums.Status;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelinePreparer extends Thread {
    private final Logger logger = Logger.getLogger(this.getClass());
    private IPipelineDefinitionService pipelineDefinitionService;
    private IPipelineService pipelineService;

    public PipelinePreparer() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.pipelineService = new PipelineService();
    }

    public PipelinePreparer(IPipelineService pipelineService, IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
        this.pipelineService = pipelineService;
    }

    @Override
    public synchronized void start() {
        super.start();
        this.logger.info(String.format(LoggerMessages.WORKER_STARTED, "Pipeline Preparer"));
        this.run();
    }

    @Override
    public void run() {
        try {
            while (true) {
                List<Pipeline> filteredPipelines = this.getAllUpdatedPipelines();

                for (Pipeline pipeline : filteredPipelines) {
                    Pipeline preparedPipeline = this.preparePipeline(pipeline);
                    this.pipelineService.update(preparedPipeline);
                }

                Thread.sleep(4 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.run();
    }

    public List<Pipeline> getAllUpdatedPipelines() {
        List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAll().getObject();

        List<Pipeline> filteredPipelines = pipelines
                .stream()
                .filter(p -> p.areMaterialsUpdated() && (p.getStatus() == Status.IN_PROGRESS) && !(p.isPrepared()))
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        return filteredPipelines;
    }

    public Pipeline preparePipeline(Pipeline pipelineToPrepare) {
        String pipelineDefinitionId = pipelineToPrepare.getPipelineDefinitionId();
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();

        List<StageDefinition> stages = pipelineDefinition.getStageDefinitions();
        List<JobDefinition> executionJobs = new ArrayList<>();
        List<Environment> pipelineDefinitionEnvironments = pipelineDefinition.getEnvironments();
        List<EnvironmentVariable> pipelineDefinitionEnvironmentVariables = pipelineDefinition.getEnvironmentVariables();

        for (StageDefinition stage : stages) {
            List<JobDefinition> stageJobs = stage.getJobDefinitions();
            executionJobs.addAll(stageJobs);
        }
        pipelineToPrepare.setPipelineDefinitionId(pipelineDefinitionId);
        pipelineToPrepare.setEnvironmentVariables(pipelineDefinitionEnvironmentVariables);
        pipelineToPrepare.setEnvironments(pipelineDefinitionEnvironments);
        pipelineToPrepare.setStages(this.preparePipelineStages(stages, pipelineToPrepare.getId()));
        pipelineToPrepare.setJobsForExecution(executionJobs);
        pipelineToPrepare.setPrepared(true);

        return pipelineToPrepare;
    }

    public List<Stage> preparePipelineStages(List<StageDefinition> stageDefinitions, String pipelineId) {
        List<Stage> stages = new ArrayList<>();

        int stageDefinitionCollectionSize = stageDefinitions.size();

        for (StageDefinition stageDefinition : stageDefinitions) {
            Stage currentStage = new Stage();
            currentStage.setStageDefinitionId(stageDefinition.getId());
            currentStage.setPipelineId(pipelineId);
            currentStage.setEnvironmentVariables(stageDefinition.getEnvironmentVariables());
            currentStage.setJobs(this.preparePipelineJobs(stageDefinition.getJobDefinitions(), pipelineId, currentStage.getId()));
            currentStage.setStatus(Status.IN_PROGRESS);

            stages.add(currentStage);
        }

        return stages;
    }

    public List<Job> preparePipelineJobs(List<JobDefinition> jobDefinitions, String pipelineId, String stageId) {
        List<Job> jobs = new ArrayList<>();

        int jobDefinitionCollectionSize = jobDefinitions.size();

        for (JobDefinition jobDefinition : jobDefinitions) {
            Job currentJob = new Job();
            currentJob.setJobDefinitionId(jobDefinition.getId());
            currentJob.setPipelineId(pipelineId);
            currentJob.setStageId(stageId);
            currentJob.setPipelineId(pipelineId);
            currentJob.setEnvironmentVariables(jobDefinition.getEnvironmentVariables());
            currentJob.setResources(jobDefinitions.get(0).getResources());
            currentJob.setTasks(this.prepareTasks(jobDefinition.getTaskDefinitions(), currentJob.getId(), stageId, pipelineId));
            currentJob.setStatus(JobStatus.AWAITING);

            jobs.add(currentJob);
        }

        return jobs;
    }

    public List<Task> prepareTasks(List<TaskDefinition> taskDefinitions, String jobId, String stageId, String pipelineId) {
        List<Task> tasks = new ArrayList<>();

        int taskDefinitionCollectionSize = taskDefinitions.size();

        for (TaskDefinition taskDefinition : taskDefinitions) {
            Task currentTask = new Task();
            currentTask.setTaskDefinition(taskDefinition);
            currentTask.setJobId(jobId);
            currentTask.setStageId(stageId);
            currentTask.setPipelineId(pipelineId);

            tasks.add(currentTask);
        }

        return tasks;
    }
}

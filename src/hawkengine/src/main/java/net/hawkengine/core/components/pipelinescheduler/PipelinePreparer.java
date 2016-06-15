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
    private IPipelineDefinitionService pipelineDefinitionService;
    private IPipelineService pipelineService;
    private final Logger logger = Logger.getLogger(this.getClass());

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
                .filter(p -> p.areMaterialsUpdated() && (p.getStatus() == Status.IN_PROGRESS))
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

    public List<Stage> preparePipelineStages(List<StageDefinition> stageDefinitions, String pipelineId){
        List<Stage> stages = new ArrayList<>();

        int stageDefinitionCollectionSize = stageDefinitions.size();

        for(int i=0; i<stageDefinitionCollectionSize; i++){
            Stage currentStage = new Stage();
            currentStage.setStageDefinitionId(stageDefinitions.get(i).getId());
            currentStage.setPipelineId(pipelineId);
            currentStage.setEnvironmentVariables(stageDefinitions.get(i).getEnvironmentVariables());
            currentStage.setJobs(this.preparePipelineJobs(stageDefinitions.get(i).getJobDefinitions(),pipelineId, currentStage.getId()));
            currentStage.setStatus(Status.IN_PROGRESS);

            stages.add(currentStage);
        }

        return stages;
    }

    public List<Job> preparePipelineJobs(List<JobDefinition> jobDefinitions, String pipelineId, String stageId){
        List<Job> jobs = new ArrayList<>();

        int jobDefinitionCollectionSize = jobDefinitions.size();

        for(int i=0; i<jobDefinitionCollectionSize; i++){
            Job currentJob = new Job();
            currentJob.setJobDefinitionId(jobDefinitions.get(i).getId());
            currentJob.setPipelineId(pipelineId);
            currentJob.setStageId(stageId);
            currentJob.setEnvironmentVariables(jobDefinitions.get(i).getEnvironmentVariables());
            currentJob.setTasks(this.generateTasks(jobDefinitions.get(i).getTaskDefinitions(), currentJob.getId()));
            currentJob.setStatus(JobStatus.AWAITING);

            jobs.add(currentJob);
        }

        return jobs;
    }

    public List<Task> generateTasks (List<TaskDefinition> taskDefinitions, String jobId){
        List<Task> tasks = new ArrayList<>();

        int taskDefinitionCollectionSize = taskDefinitions.size();

        if (taskDefinitionCollectionSize == 0){
            Task currentTask = new Task();

            tasks.add(currentTask);
        }
        else{
            for (int i = 0; i < taskDefinitionCollectionSize; i++){
                Task currentTask = new Task();
                currentTask.setTaskDefinition(taskDefinitions.get(i));
                currentTask.setJobId(jobId);

                tasks.add(currentTask);
            }
        }

        return tasks;
    }
}

package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.model.*;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnnecessaryLocalVariable")
public class PipelinePreparer extends Thread {
    private final IPipelineDefinitionService pipelineDefinitionService;
    private final IPipelineService pipelineService;

    public PipelinePreparer(IPipelineService pipelineService, IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineService = pipelineService;
        this.pipelineDefinitionService = pipelineDefinitionService;
    }


    @Override
    public synchronized void start() {
        super.start();
        this.run();
    }


    @Override
    public void run() {
        // this.logger.info(String.format(LoggerMessages.PREPARER_RUN, "Pipeline Preparer"));
        List<Pipeline> filteredPipelines = this.getAllUpdatedPipelines();
        for (Pipeline filteredPipeline : filteredPipelines) {
            Pipeline preparedPipeline = this.preparePipeline(filteredPipeline);
            this.pipelineService.update(preparedPipeline);
        }
        super.run();
    }

    public List<Pipeline> getAllUpdatedPipelines() {

        ServiceResult serviceResult = this.pipelineService.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) serviceResult.getObject();

        List<Pipeline> filteredPipelines = pipelines.stream()
                .filter(Pipeline::areMaterialsUpdated)
                .filter(p -> p.getStatus() == Status.IN_PROGRESS)
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        return filteredPipelines;
    }

    public Pipeline preparePipeline(Pipeline pipelineToPrepare) {
        String pipelineDefinitionId = pipelineToPrepare.getPipelineDefinitionId();
        ServiceResult serviceResult = this.pipelineDefinitionService.getById(pipelineDefinitionId);
        PipelineDefinition pipelineDefinition = (PipelineDefinition) serviceResult.getObject();

        //TODO: Modify Pipeline Definition Model
        //pipelineToPrepare.setEnvironments(pipelineDefinition.getEnvironments());
        pipelineToPrepare.setEnvironmentVariables(pipelineDefinition.getEnvironmentVariables());

        List<StageDefinition> stages = pipelineDefinition.getStageDefinitions();
        List<JobDefinition> executionJobs = new ArrayList<>();

        //TODO: Validate each stage

        for (StageDefinition stage : stages) {
            List<JobDefinition> stageJobs = stage.getJobDefinitions();
            executionJobs.addAll(stageJobs);
        }

        pipelineToPrepare.setJobsForExecution(executionJobs);
        //TODO: Change to List of JobDefinitions in Stage Model

        pipelineToPrepare.setPrepared(true);

        return pipelineToPrepare;
    }
}

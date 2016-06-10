package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.*;
import net.hawkengine.services.IPipelineDefinitionService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelinePreparer extends Thread {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final IPipelineDefinitionService pipelineDefinitionService;
    private final IPipelineService pipelineService;

    public PipelinePreparer(PipelineService pipelineService, PipelineDefinitionService pipelineDefinitionService) {
        this.pipelineService = pipelineService;
        this.pipelineDefinitionService = pipelineDefinitionService;
    }


    @Override
    public synchronized void start() {
        this.logger.info(String.format(LoggerMessages.PREPARER_STARTED, "Pipeline Preparer"));
        super.start();
    }


    @Override
    public void run() {
        this.logger.info(String.format(LoggerMessages.PREPARER_RUN, "Pipeline Preparer"));
        List<Pipeline> filteredPipelines = this.getAllUpdatedPipelines();
        for (Pipeline filteredPipeline: filteredPipelines) {
            Pipeline preparedPipeline = this.preparePipeline(filteredPipeline);
            this.pipelineService.update(preparedPipeline);
        }
        super.run();
    }

    private List<Pipeline> getAllUpdatedPipelines() {

        ServiceResult serviceResult = this.pipelineDefinitionService.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) serviceResult.getObject();

        List<Pipeline> filteredPipelines = pipelines.stream()
                .filter(Pipeline::areMaterialsUpdated)
                .filter(p -> p.getStatus() == Status.IN_PROGRESS)
                .sorted((p1, p2) -> p2.getStartTime().compareTo(p1.getStartTime()))
                .collect(Collectors.toList());

        return filteredPipelines;
    }

    private Pipeline preparePipeline(Pipeline pipelineToPrepare) {
        String pipelineDefinitionId = pipelineToPrepare.getPipelineDefinitionId();
        ServiceResult serviceResult = this.pipelineDefinitionService.getById(pipelineDefinitionId);
        PipelineDefinition pipelineDefinition = (PipelineDefinition) serviceResult.getObject();

        //TODO: Modify Pipeline Definition Model
        //pipelineToPrepare.setEnvironments(pipelineDefinition.getEnvironments());
        pipelineToPrepare.setEnvironmentVariables(pipelineDefinition.getEnvironmentVariables());

        List<Stage> stages = pipelineDefinition.getStages();
        List<JobDefinition> executionJobs = new ArrayList<>();

        //TODO: Validate each stage

        for (Stage stage : stages) {
            List<JobDefinition> stageJobs = stage.getJobs();
            executionJobs.addAll(stageJobs);
        }

        //pipelineToPrepare.setJobsForExecution(executionJobs);
        //TODO: Change to List of JobDefinitions in Stage Model

        pipelineToPrepare.setPrepared(true);

        return pipelineToPrepare;
    }
}

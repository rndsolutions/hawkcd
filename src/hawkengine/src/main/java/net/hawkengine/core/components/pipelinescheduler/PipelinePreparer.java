package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.model.*;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelinePreparer extends Thread {
    private final IPipelineDefinitionService pipelineDefinitionService;
    private final IPipelineService pipelineService;

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
        // TODO: Log this
        this.run();
    }

    @Override
    public void run() {
        // TODO: Redo worker threading
        List<Pipeline> filteredPipelines = this.getAllUpdatedPipelines();
        for (Pipeline pipeline : filteredPipelines) {
            Pipeline preparedPipeline = this.preparePipeline(pipeline);
            this.pipelineService.update(preparedPipeline);
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

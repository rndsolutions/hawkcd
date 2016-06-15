package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.*;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelinePreparer extends Thread {
    private final IPipelineDefinitionService pipelineDefinitionService;
    private final IPipelineService pipelineService;
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

        pipelineToPrepare.setEnvironments(pipelineDefinitionEnvironments);
        pipelineToPrepare.setEnvironmentVariables(pipelineDefinitionEnvironmentVariables);
        pipelineToPrepare.setJobsForExecution(executionJobs);
        pipelineToPrepare.setPrepared(true);

        return pipelineToPrepare;
    }
}

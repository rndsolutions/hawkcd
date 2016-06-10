package net.hawkengine.core.components.pipelinescheduler;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelinePreparerTest {
    IDbRepository pipelineRepo;
    IDbRepository pipelineDefinitionRepo;
    PipelineService pipelineService;
    PipelineDefinitionService pipelineDefinitionService;
    PipelinePreparer pipelinePreparer;

    @Before
    public void setUp() throws Exception {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineService");
        this.pipelineRepo = new RedisRepository(Pipeline.class, mockedPool);
        this.pipelineDefinitionRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.pipelineService = new PipelineService(this.pipelineRepo);
        this.pipelineDefinitionService = new PipelineDefinitionService(this.pipelineDefinitionRepo);
        this.pipelinePreparer = new PipelinePreparer(pipelineService, pipelineDefinitionService);
    }

    @Test
    public void getAllPreparedPipelines_withValidInput_shouldReturnValidModelState() throws Exception {
        ArrayList<Stage> stages = new ArrayList<>();
        ArrayList<JobDefinition> jobDefinitions = new ArrayList<>();

        PipelineDefinition pipelineDefinition = new PipelineDefinition();

        for (int i = 0; i < 5; i++) {
            Stage stage = new Stage();
            JobDefinition jobDefinition = new JobDefinition();

            stages.add(stage);
            jobDefinitions.add(jobDefinition);
        }

        for (Stage stage : stages) {
            stage.setJobs(jobDefinitions);
        }
        pipelineDefinition.setStages(stages);

        this.pipelineDefinitionService.add(pipelineDefinition);

        ServiceResult serv = this.pipelineDefinitionService.getAll();

        Pipeline firstPipeline = new Pipeline();
        firstPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        firstPipeline.setStatus(Status.IN_PROGRESS);


        Pipeline secondPipeline = new Pipeline();
        secondPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        secondPipeline.setAreMaterialsUpdated(true);
        secondPipeline.setStatus(Status.IN_PROGRESS);

        Pipeline thirdPipeline = new Pipeline();
        thirdPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        thirdPipeline.setAreMaterialsUpdated(true);
        thirdPipeline.setStatus(Status.FAILED);

        Pipeline fourthPipeline = new Pipeline();
        fourthPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        fourthPipeline.setAreMaterialsUpdated(true);
        fourthPipeline.setStatus(Status.IN_PROGRESS);

        int expectedCollectionSize = 2;

        this.pipelineService.add(firstPipeline);
        this.pipelineService.add(secondPipeline);
        this.pipelineService.add(thirdPipeline);
        this.pipelineService.add(fourthPipeline);


        pipelinePreparer.run();

        ServiceResult serviceResult = this.pipelineService.getAll();
        List<Pipeline> actualObjects = (List<Pipeline>) serviceResult.getObject();

        List<Pipeline> filteredPipelines = actualObjects.stream()
                .filter(Pipeline::areMaterialsUpdated)
                .filter(p -> p.getStatus() == Status.IN_PROGRESS)
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        Assert.assertNotNull(filteredPipelines);
        Assert.assertEquals(expectedCollectionSize, filteredPipelines.size());
        Assert.assertFalse(serviceResult.hasError());

        for (Pipeline preparedPipeline : filteredPipelines) {
            Assert.assertEquals(preparedPipeline.isPrepared(), true);
        }
    }
}

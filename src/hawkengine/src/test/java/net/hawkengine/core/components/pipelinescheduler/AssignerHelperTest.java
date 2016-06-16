package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssignerHelperTest {
    private AssignerHelper service = new AssignerHelper();

    @Before
    public void setUp() {

    }

    @Test
    public void getEligibleAgents_matchingResources_oneObject() {
        // Arrange
        int agentZero = 0;
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getAgentsWithResources(1, 1);
        String expectedId = agents.get(agentZero).getId();
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        // Act
        List<Agent> actualResult = this.service.getEligibleAgents(job, agents);
        String actualId = actualResult.get(agentZero).getId();
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedId, actualId);
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void getEligibleAgents_nonmathchingResources_noObject() {
        // Arrange
        int agentZero = 0;
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getAgentsWithResources(1, 1);
        agents.get(agentZero).getResources().remove("Resource0");
        agents.get(agentZero).getResources().add("Non-matching resource");
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS;

        // Act
        List<Agent> actualResult = this.service.getEligibleAgents(job, agents);
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void getEligibleAgents_notEnoughResources_noObject() {
        // Arrange
        int agentZero = 0;
        Job job = this.getJobWithResources(2);
        List<Agent> agents = this.getAgentsWithResources(1, 1);
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS;

        // Act
        List<Agent> actualResult = this.service.getEligibleAgents(job, agents);
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void getEligibleAgents_moreResourcesThanNecessary_oneObject() {
        // Arrange
        int agentZero = 0;
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getAgentsWithResources(1, 2);
        String expectedId = agents.get(agentZero).getId();
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        // Act
        List<Agent> actualResult = this.service.getEligibleAgents(job, agents);
        String actualId = actualResult.get(agentZero).getId();
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedId, actualId);
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void getEligibleAgents_twoAgentsOneWithMatchingResources_oneObject() {
        // Arrange
        int agentZero = 0;
        int agentOne = 1;
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getAgentsWithResources(2, 1);
        agents.get(agentOne).getResources().remove("Resource0");
        agents.get(agentOne).getResources().add("Non-matching resource");
        String expectedId = agents.get(agentZero).getId();
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        // Act
        List<Agent> actualResult = this.service.getEligibleAgents(job, agents);
        String actualId = actualResult.get(agentZero).getId();
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedId, actualId);
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    private Job getJobWithResources(int numberOfResources) {
        Job job = new Job();
        Set<String> jobResources = job.getResources();
        for (int i = 0; i < numberOfResources; i++) {
            jobResources.add("Resource" + i);
        }

        job.setResources(jobResources);
        return job;
    }

    private List<Agent> getAgentsWithResources(int numberOfAgents, int numberOfResources) {
        List<Agent> agents = new ArrayList<>();
        for (int i = 0; i < numberOfAgents; i++) {
            Agent agent = new Agent();
            Set<String> agentResources = agent.getResources();
            for (int j = 0; j < numberOfResources; j++) {
                agentResources.add("Resource" + j);
            }

            agent.setResources(agentResources);
            agents.add(agent);
        }

        return agents;
    }
}
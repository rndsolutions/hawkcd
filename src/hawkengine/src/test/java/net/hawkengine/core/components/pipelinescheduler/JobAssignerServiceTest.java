package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JobAssignerServiceTest {private JobAssignerService jobAssignerService = new JobAssignerService();

    @Test
    public void getEligibleAgentsForJob_matchingResources_oneObject() {
        // Arrange
        int agentZero = 0;
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getAgentsWithResources(1, 1);
        String expectedId = agents.get(agentZero).getId();
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        // Act
        List<Agent> actualResult = this.jobAssignerService.getEligibleAgentsForJob(job, agents);
        String actualId = actualResult.get(agentZero).getId();
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedId, actualId);
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void getEligibleAgentsForJob_nonMatchingResources_noObject() {
        // Arrange
        int agentZero = 0;
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getAgentsWithResources(1, 1);
        agents.get(agentZero).getResources().remove("Resource0");
        agents.get(agentZero).getResources().add("Non-matching resource");
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS;

        // Act
        List<Agent> actualResult = this.jobAssignerService.getEligibleAgentsForJob(job, agents);
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void getEligibleAgentsForJob_notEnoughResources_noObject() {
        // Arrange
        Job job = this.getJobWithResources(2);
        List<Agent> agents = this.getAgentsWithResources(1, 1);
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS;

        // Act
        List<Agent> actualResult = this.jobAssignerService.getEligibleAgentsForJob(job, agents);
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void getEligibleAgentsForJob_moreResourcesThanNecessary_oneObject() {
        // Arrange
        int agentZero = 0;
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getAgentsWithResources(1, 2);
        String expectedId = agents.get(agentZero).getId();
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        // Act
        List<Agent> actualResult = this.jobAssignerService.getEligibleAgentsForJob(job, agents);
        String actualId = actualResult.get(agentZero).getId();
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedId, actualId);
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void getEligibleAgentsForJob_twoAgentsOneWithMatchingResources_oneObject() {
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
        List<Agent> actualResult = this.jobAssignerService.getEligibleAgentsForJob(job, agents);
        String actualId = actualResult.get(agentZero).getId();
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedId, actualId);
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void pickMostSuitableAgent_oneAgent_sameAgent() {
        // Arrange
        int agentZero = 0;
        List<Agent> agents = this.getAgentsWithResources(1, 1);
        String expectedResult = agents.get(agentZero).getId();

        //Act
        String actualResult = this.jobAssignerService.pickMostSuitableAgent(agents).getId();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void pickMostSuitableAgent_twoAgents_AgentWithLessResources() {
        // Arrange
        int agentZero = 0;
        List<Agent> agents = this.getAgentsWithResources(2, 1);
        agents.get(agentZero).getResources().remove("Resource0");
        String expectedResult = agents.get(agentZero).getId();

        //Act
        String actualResult = this.jobAssignerService.pickMostSuitableAgent(agents).getId();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void assignJobToAgent_noAgents_null() {
        // Arrange
        List<Agent> agents = this.getAgentsWithResources(0, 0);

        //Act
        Agent actualResult = this.jobAssignerService.pickMostSuitableAgent(agents);

        // Assert
        Assert.assertNull(actualResult);
    }

    @Test
    public void isAgentEligibleForJob_eligibleAgent_true() {
        // Arrange
        int agentZero = 0;
        Job job = this.getJobWithResources(3);
        List<Agent> agents = this.getAgentsWithResources(1, 3);
        Agent agent = agents.get(agentZero);
        agent.setConnected(true);
        agent.setEnabled(true);
        agent.setRunning(false);

        // Act
        boolean actualResult = this.jobAssignerService.isAgentEligibleForJob(job, agent);

        // Assert
        Assert.assertTrue(actualResult);
    }

    @Test
    public void isAgentEligibleForJob_agentIsNull_false() {
        // Arrange
        Job job = new Job();
        Agent agent = null;

        // Act
        boolean actualResult = this.jobAssignerService.isAgentEligibleForJob(job, agent);

        // Assert
        Assert.assertFalse(actualResult);
    }

    @Test
    public void isAgentEligibleForJob_agentIsNotConnected_false() {
        // Arrange
        Job job = new Job();
        Agent agent = new Agent();
        agent.setConnected(false);
        agent.setEnabled(true);
        agent.setRunning(false);

        // Act
        boolean actualResult = this.jobAssignerService.isAgentEligibleForJob(job, agent);

        // Assert
        Assert.assertFalse(actualResult);
    }

    @Test
    public void isAgentEligibleForJob_agentIsNotEnabled_false() {
        // Arrange
        Job job = new Job();
        Agent agent = new Agent();
        agent.setConnected(true);
        agent.setEnabled(false);
        agent.setRunning(false);

        // Act
        boolean actualResult = this.jobAssignerService.isAgentEligibleForJob(job, agent);

        // Assert
        Assert.assertFalse(actualResult);
    }

    @Test
    public void isAgentEligibleForJob_agentIsRunning_false() {
        // Arrange
        Job job = new Job();
        Agent agent = new Agent();
        agent.setConnected(true);
        agent.setEnabled(true);
        agent.setRunning(true);

        // Act
        boolean actualResult = this.jobAssignerService.isAgentEligibleForJob(job, agent);

        // Assert
        Assert.assertFalse(actualResult);
    }

    @Test
    public void isAgentEligibleForJob_agentHasWrongResources_false() {
        // Arrange
        int agentZero = 0;
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getAgentsWithResources(1, 0);
        Agent agent = agents.get(agentZero);
        agent.setConnected(true);
        agent.setEnabled(true);
        agent.setRunning(false);
        agents.get(agentZero).getResources().add("Non-matching resource");

        // Act
        boolean actualResult = this.jobAssignerService.isAgentEligibleForJob(job, agent);

        // Assert
        Assert.assertFalse(actualResult);
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
package net.hawkengine.core.pipelinescheduler;

import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;
import net.hawkengine.model.enums.JobStatus;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JobAssignerServiceTest {
    private static final int AGENT_ZERO = 0;
    private static final int AGENT_ONE = 1;

    private JobAssignerService jobAssignerService = new JobAssignerService();

    @Test
    public void assignAgentToJob_awaitingJobViableAgent_assigned() {
        // Arrange
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getViableAgentsWithResources(1, 1);

        // Act
        Agent actualResult = this.jobAssignerService.assignAgentToJob(job, agents);

        // Assert
        Assert.assertTrue(actualResult.isAssigned());
        Assert.assertEquals(JobStatus.SCHEDULED, job.getStatus());
        Assert.assertEquals(actualResult.getId(), job.getAssignedAgentId());
    }

    @Test
    public void getEligibleAgentsForJob_matchingResources_oneObject() {
        // Arrange
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getViableAgentsWithResources(1, 1);
        String expectedId = agents.get(AGENT_ZERO).getId();
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        // Act
        List<Agent> actualResult = this.jobAssignerService.getEligibleAgentsForJob(job, agents);
        String actualId = actualResult.get(AGENT_ZERO).getId();
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedId, actualId);
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void getEligibleAgentsForJob_agentAssigned_noObject() {
        // Arrange
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getViableAgentsWithResources(1, 1);
        agents.get(AGENT_ZERO).setAssigned(true);
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS;

        // Act
        List<Agent> actualResult = this.jobAssignerService.getEligibleAgentsForJob(job, agents);
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void getEligibleAgentsForJob_nonMatchingResources_noObject() {
        // Arrange
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getViableAgentsWithResources(1, 1);
        agents.get(AGENT_ZERO).getResources().remove("Resource0");
        agents.get(AGENT_ZERO).getResources().add("Non-matching resource");
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
        List<Agent> agents = this.getViableAgentsWithResources(1, 1);
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
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getViableAgentsWithResources(1, 2);
        String expectedId = agents.get(AGENT_ZERO).getId();
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        // Act
        List<Agent> actualResult = this.jobAssignerService.getEligibleAgentsForJob(job, agents);
        String actualId = actualResult.get(AGENT_ZERO).getId();
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedId, actualId);
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void getEligibleAgentsForJob_someWithMatchingResources_oneObject() {
        // Arrange
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getViableAgentsWithResources(2, 1);
        agents.get(AGENT_ONE).getResources().remove("Resource0");
        agents.get(AGENT_ONE).getResources().add("Non-matching resource");
        String expectedId = agents.get(AGENT_ZERO).getId();
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        // Act
        List<Agent> actualResult = this.jobAssignerService.getEligibleAgentsForJob(job, agents);
        String actualId = actualResult.get(AGENT_ZERO).getId();
        int actualCollectionSize = actualResult.size();

        // Assert
        Assert.assertEquals(expectedId, actualId);
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }

    @Test
    public void pickMostSuitableAgent_oneAgent_sameAgent() {
        // Arrange
        List<Agent> agents = this.getViableAgentsWithResources(1, 1);
        String expectedResult = agents.get(AGENT_ZERO).getId();

        //Act
        String actualResult = this.jobAssignerService.pickMostSuitableAgent(agents).getId();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void pickMostSuitableAgent_twoAgents_AgentWithLessResources() {
        // Arrange
        List<Agent> agents = this.getViableAgentsWithResources(2, 1);
        agents.get(AGENT_ZERO).getResources().remove("Resource0");
        String expectedResult = agents.get(AGENT_ZERO).getId();

        //Act
        String actualResult = this.jobAssignerService.pickMostSuitableAgent(agents).getId();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void assignJobToAgent_noAgents_null() {
        // Arrange
        List<Agent> agents = this.getViableAgentsWithResources(0, 0);

        //Act
        Agent actualResult = this.jobAssignerService.pickMostSuitableAgent(agents);

        // Assert
        Assert.assertNull(actualResult);
    }

    @Test
    public void isAgentEligibleForJob_eligibleAgent_true() {
        // Arrange
        Job job = this.getJobWithResources(3);
        List<Agent> agents = this.getViableAgentsWithResources(1, 3);
        Agent agent = agents.get(AGENT_ZERO);
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
        Job job = this.getJobWithResources(1);
        List<Agent> agents = this.getViableAgentsWithResources(1, 0);
        Agent agent = agents.get(AGENT_ZERO);
        agent.setConnected(true);
        agent.setEnabled(true);
        agent.setRunning(false);
        agents.get(AGENT_ZERO).getResources().add("Non-matching resource");

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

    private List<Agent> getViableAgentsWithResources(int numberOfAgents, int numberOfResources) {
        List<Agent> agents = new ArrayList<>();
        for (int i = 0; i < numberOfAgents; i++) {
            Agent agent = new Agent();
            agent.setConnected(true);
            agent.setEnabled(true);
            agent.setRunning(false);
            agent.setAssigned(false);
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
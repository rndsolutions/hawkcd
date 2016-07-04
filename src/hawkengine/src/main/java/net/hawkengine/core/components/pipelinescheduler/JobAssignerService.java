package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;
import net.hawkengine.model.enums.JobStatus;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class JobAssignerService {
    private static final Logger LOGGER = Logger.getLogger(JobAssignerService.class.getName());

    public Agent assignAgentToJob(Job job, List<Agent> agents) {
        Agent result = null;
        if (job.getStatus() == JobStatus.SCHEDULED) {
            Agent assignedAgent = agents.stream().filter(a -> a.getId().equals(job.getAssignedAgentId())).findFirst().orElse(null);
            boolean isEligible = this.isAgentEligibleForJob(job, assignedAgent);
            if (!isEligible) {
                job.setStatus(JobStatus.AWAITING);
                assignedAgent.setAssigned(false);
                result = assignedAgent;
                LOGGER.info(String.format("Job %s unassigned from Agent %s", job.getJobDefinitionId(), assignedAgent.getName()));
            }
        }

        if (job.getStatus() == JobStatus.AWAITING) {
            List<Agent> eligibleAgents = this.getEligibleAgentsForJob(job, agents);
            Agent agentForJob = this.pickMostSuitableAgent(eligibleAgents);
            if (agentForJob != null) {
                job.setAssignedAgentId(agentForJob.getId());
                job.setStatus(JobStatus.SCHEDULED);
                agentForJob.setAssigned(true);
                result = agentForJob;
                LOGGER.info(String.format("Job %s assigned to Agent %s", job.getJobDefinitionId(), agentForJob.getName()));
            }
        }

        return result;
    }

    public List<Agent> getEligibleAgentsForJob(Job job, List<Agent> agents) {
        List<Agent> eligibleAgents = new ArrayList<>();
        for (Agent agent : agents) {
            boolean isEligible = this.isAgentEligibleForJob(job, agent);

            if (isEligible) {
                eligibleAgents.add(agent);
            }
        }

        return eligibleAgents;
    }

    public Agent pickMostSuitableAgent(List<Agent> agents) {
        Agent agentForJob = null;
        if (agents.size() == 1) {
            agentForJob = agents.get(0);
        } else if (agents.size() > 1) {
            int numberOfResources = Integer.MAX_VALUE;
            for (Agent agent : agents) {
                if (agent.getResources().size() < numberOfResources) {
                    numberOfResources = agent.getResources().size();
                    agentForJob = agent;
                }
            }
        }

        return agentForJob;
    }

    public boolean isAgentEligibleForJob(Job job, Agent agent) {
        boolean isEligible = true;
        if ((agent == null) || !agent.isConnected() || !agent.isEnabled() || agent.isRunning() || agent.isAssigned()) {
            isEligible = false;
        } else {
            for (String resource : job.getResources()) {
                if (!(agent.getResources().contains(resource))) {
                    isEligible = false;
                    break;
                }
            }
        }

        return isEligible;
    }
}

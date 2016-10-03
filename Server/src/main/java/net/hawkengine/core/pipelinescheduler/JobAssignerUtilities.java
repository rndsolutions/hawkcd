package net.hawkengine.core.pipelinescheduler;

import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;
import net.hawkengine.model.enums.JobStatus;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class JobAssignerUtilities {
    private static final Logger LOGGER = Logger.getLogger(JobAssignerUtilities.class.getName());

    public Agent assignAgentToJob(Job job, List<Agent> agents) {
        Agent result = null;
        if (job.getStatus() == JobStatus.ASSIGNED) {
            Agent assignedAgent = agents.stream().filter(a -> a.getId().equals(job.getAssignedAgentId())).findFirst().orElse(null);
            result = assignedAgent;
            boolean isEligible = this.isAssignedAgentEligibleForJob(job, assignedAgent);
            if (!isEligible) {
                job.setStatus(JobStatus.UNASSIGNED);
                if (assignedAgent != null) {
                    assignedAgent.setAssigned(false);
                }

                LOGGER.info(String.format("Job %s unassigned from Agent %s", job.getJobDefinitionName(), assignedAgent.getName()));
            }
        }

        if (job.getStatus() == JobStatus.UNASSIGNED) {
            List<Agent> eligibleAgents = this.getEligibleAgentsForJob(job, agents);
            Agent agentForJob = this.pickMostSuitableAgent(eligibleAgents);
            if (agentForJob != null) {
                job.setAssignedAgentId(agentForJob.getId());
                job.setStatus(JobStatus.ASSIGNED);
                agentForJob.setAssigned(true);
                result = agentForJob;
                LOGGER.info(String.format("Job %s assigned to Agent %s", job.getJobDefinitionName(), agentForJob.getName()));
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
        int numberOfResources = Integer.MAX_VALUE;
        for (Agent agent : agents) {
            if (agent.getResources().size() < numberOfResources) {
                numberOfResources = agent.getResources().size();
                agentForJob = agent;
            }
        }

        return agentForJob;
    }


//    public Agent pickMostSuitableAgent(List<Agent> agents) {
//        Agent agentForJob = null;
//        if (agents.size() == 1) {
//            agentForJob = agents.get(0);
//        } else if (agents.size() > 1) {
//            int numberOfResources = Integer.MAX_VALUE;
//            for (Agent agent : agents) {
//                if (agent.getResources().size() < numberOfResources) {
//                    numberOfResources = agent.getResources().size();
//                    agentForJob = agent;
//                }
//            }
//        }
//
//        return agentForJob;
//    }

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

    public boolean hasAssignableAgent(Job job, List<Agent> agents) {
        for (Agent agent : agents) {
            boolean agentIsAssignable = true;
            for (String resource : job.getResources()) {
                if (!(agent.getResources().contains(resource))) {
                    agentIsAssignable = false;
                }
            }

            if (agentIsAssignable) {
                return true;
            }
        }

        return false;
    }

    public boolean isAssignedAgentEligibleForJob(Job job, Agent agent) {
        boolean isEligible = true;
        if ((agent == null) || !agent.isConnected() || !agent.isEnabled() || agent.isRunning() || !agent.isAssigned()) {
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

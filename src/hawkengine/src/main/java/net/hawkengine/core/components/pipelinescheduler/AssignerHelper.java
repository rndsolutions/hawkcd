package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;

import java.util.ArrayList;
import java.util.List;

public class AssignerHelper {
    public List<Agent> getEligibleAgentsForJob(Job job, List<Agent> agents) {
        List<Agent> eligibleAgents = new ArrayList<>();
        for (Agent agent : agents) {
            boolean isEligible = true;
            for (String resource : job.getResources()) {
                if (!(agent.getResources().contains(resource))) {
                    isEligible = false;
                    break;
                }
            }

            if (isEligible) {
                eligibleAgents.add(agent);
            }
        }

        return eligibleAgents;
    }

    public Agent getAgentForJob(Job job, List<Agent> agents) {
        Agent agentForJob = null;

        return agentForJob;
//        if (agents.size() == 1) {
//            job.setAssignedAgentId(agents.get(0).getId());
//        } else if (agents.size() > 1) {
//            int numberOfResources = Integer.MAX_VALUE;
//            String agentId = null;
//            for (Agent agent : agents) {
//                if (agent.getResources().size() < numberOfResources) {
//                    numberOfResources = agent.getResources().size();
//                    agentId = agent.getId();
//                }
//            }
//
//            job.setAssignedAgentId(agentId);
//        }
//
//        return job;
    }

    public boolean isAgentEligibleForJob(Job job, Agent agent) {
        boolean isEligible = true;
        if (!agent.isConnected() || !agent.isEnabled() || agent.isRunning()) {
            isEligible = false;
        }

        for (String resource : job.getResources()) {
            if (!(agent.getResources().contains(resource))) {
                isEligible = false;
                break;
            }
        }

        return isEligible;
    }
}

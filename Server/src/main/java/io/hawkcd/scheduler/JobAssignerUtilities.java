/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawkcd.scheduler;

import io.hawkcd.model.Agent;
import io.hawkcd.model.Job;
import io.hawkcd.model.enums.JobStatus;
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

                LOGGER.info(String.format("Job %s unassigned from Agent", job.getJobDefinitionName()));
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

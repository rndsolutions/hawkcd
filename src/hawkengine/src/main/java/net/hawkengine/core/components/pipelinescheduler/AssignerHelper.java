package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;

import java.util.ArrayList;
import java.util.List;

public class AssignerHelper {
    public List<Agent> getEligibleAgents(Job job, List<Agent> agents) {
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
}

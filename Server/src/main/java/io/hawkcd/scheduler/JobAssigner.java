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

import io.hawkcd.Config;
import io.hawkcd.utilities.constants.LoggerMessages;
import io.hawkcd.model.Agent;
import io.hawkcd.services.AgentService;
import io.hawkcd.services.PipelineService;
import io.hawkcd.services.interfaces.IAgentService;
import org.apache.log4j.Logger;

import java.util.List;

public class JobAssigner implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(PipelinePreparer.class.getName());
    private static final int POLL_INTERVAL = Config.getConfiguration().getPipelineSchedulerPollInterval() * 1000;

    private JobAssignerService jobAssignerService;
    private StatusUpdaterService statusUpdaterService;
    private IAgentService agentService;

    public JobAssigner() {
        this.jobAssignerService = new JobAssignerService();
        this.statusUpdaterService = new StatusUpdaterService();
        this.agentService = new AgentService();
    }

    @Override
    public void run() {
        LOGGER.info(String.format(LoggerMessages.WORKER_STARTED, this.getClass().getSimpleName()));
        try {
            while (true) {
                List<Agent> agents = (List<Agent>) this.agentService.getAll().getObject();

                PipelineService.lock.lock();
                this.statusUpdaterService.updateStatuses();
                this.jobAssignerService.checkUnassignedJobs(agents);
                this.jobAssignerService.checkAwaitingJobs(agents);
                this.jobAssignerService.assignJobs(agents);
                PipelineService.lock.unlock();

                Thread.sleep(POLL_INTERVAL);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

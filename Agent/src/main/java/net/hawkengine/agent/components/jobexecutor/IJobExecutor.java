package net.hawkengine.agent.components.jobexecutor;

import net.hawkengine.agent.models.Job;
import net.hawkengine.agent.models.payload.WorkInfo;

public interface IJobExecutor {

    void executeJob(WorkInfo job);

    void initJobExecutionInfo(WorkInfo workInfo);

    void resetJobExecutionInfo();

    Job getCurrentJob();

    void setCurrentJob(Job job);
}

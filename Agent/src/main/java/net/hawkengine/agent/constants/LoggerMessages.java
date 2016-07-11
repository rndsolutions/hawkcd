package net.hawkengine.agent.constants;

public final class LoggerMessages {
    public static final String AGENT_STARTED = "Agent started.";
    public static final String AGENT_STOPPED = "Agent stopped.";
    public static final String AGENT_CHECKING_FOR_WORK = "Checking for work.";
    public static final String AGENT_WORK_FOUND = "Work found.";
    public static final String AGENT_REPORT_SENT = "Agent sent to server.";
    public static final String AGENT_COULD_NOT_CONNECT = "Could not connect to server.";

    public static final String JOB_STARTED = "Job with name %s started.";
    public static final String JOB_REPORT_SENT = "Job sent to server.";
    public static final String JOB_COMPLETED = "Job with name %s completed with status %s.";

    public static final String TASK_STARTED = "%s task with id %s started.";
    public static final String TASK_COMPLETED = "%s task with id %s completed with status %s.";
    public static final String TASK_THROWS_EXCEPTION = "Task with ID %s throws Exception with message: %s.";
}

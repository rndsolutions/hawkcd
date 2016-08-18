package net.hawkengine.agent.constants;

public final class MessageConstants {
    public static final String AGENT_STARTED = "Agent started.";
    public static final String AGENT_STOPPED = "Agent stopped.";
    public static final String AGENT_CHECKING_FOR_WORK = "Checking for work.";
    public static final String AGENT_WORK_FOUND = "Work found.";
    public static final String AGENT_REPORT_SENT = "Agent sent to server.";
    public static final String AGENT_COULD_NOT_CONNECT = "Could not connect to server.";

    public static final String JOB_STARTED_ON_AGENT = "Job %s started on Agent %s";
    public static final String JOB_REPORT_SENT = "Job sent to server.";
    public static final String JOB_COMPLETED_WITH_STATUS = "Job %s completed with status %s.";

    public static final String TASK_STARTED = "Task %s started. Type: %s";
    public static final String TASK_COMPLETED = "Task %s completed with status %s.";
    public static final String TASK_THROWS_EXCEPTION = "Task threw Exception with message: %s.";

    public static final String CONSOLE_RED = (char)27 + "[1;31m";
    public static final String CONSOLE_GREEN = (char)27 + "[32m";
    public static final String CONSOLE_YELLOW = (char)27 + "[1;33m";
    public static final String CONSOLE_WHITE = (char)27 + "[m";
    public static final String CONSOLE_THICK_LINE = "=====================================================";
    public static final String CONSOLE_THIN_LINE = "-----------------------------------------------------";
}

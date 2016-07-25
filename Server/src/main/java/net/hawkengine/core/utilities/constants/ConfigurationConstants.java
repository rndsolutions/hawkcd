package net.hawkengine.core.utilities.constants;

public final class ConfigurationConstants {
    public static final String CONFIG_FILE_NAME = "config.yaml";
    public static final String FAILED_TO_CREATE_CONFIG = "Could not create file %s." + System.getProperty("line.separator");
    public static final String FAILED_TO_LOCATE_CONFIG = "Could not locate file %s." + System.getProperty("line.separator");
    public static final String DATABASE_CONFIG_MESSAGE = "Database type '%s': ";
    public static final String INVALID_CONFIG_PROPERTY = "Property '%s' is invalid." + System.getProperty("line.separator");
    public static final String EMPTY_CONFIG_PROPERTY = "Property '%s' cannot be empty." + System.getProperty("line.separator");

    public static final String PROPERTY_SERVER_HOST = "serverHost";
    public static final String PROPERTY_DATABASE_NAME = "name";
    public static final String PROPERTY_DATABASE_HOST = "host";
    public static final String PROPERTY_MATERIALS_DESTINATION = "Materials";
    public static final String PROPERTY_ARTIFACTS_DESTINATION = "Artifacts";
    public static final String PROPERTY_SCHEDULER_POLL_INTERVAL = "pipelineSchedulerPollInterval";
    public static final String PROPERTY_TRACKER_POLL_INTERVAL = "materialTrackerPollInterval";

    public static final int MIN_WORKER_POLL_INTERVAL = 1000;
    public static final int MAX_WORKER_POLL_INTERVAL = 30000;
    public static final String WORKER_POLL_INTERVAL_ERROR = "Property '%s' must be set between %n and %n." + System.getProperty("line.separator");
}

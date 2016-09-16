package net.hawkengine.agent.constants;

import java.nio.file.Paths;

public final class ConfigConstants {
    public static final String AGENT_SANDBOX = System.getProperty("user.dir");
    public static final String AGENT_NAME = "Agent";
    public static final String AGENT_CONFIG_DIR = "Config";
    public static final String AGENT_CONFIG_FILE_NAME = "config.properties";
    public static final String AGENT_CONFIG_FILE_LOCATION = Paths.get(ConfigConstants.AGENT_SANDBOX, ConfigConstants.AGENT_CONFIG_DIR, ConfigConstants.AGENT_CONFIG_FILE_NAME).toString();
    public static final String AGENT_LOG_DIR = "Log";
    public static final String AGENT_PIPELINES_DIR = "Pipelines";
    public static final String AGENT_TEMP_DIR = "Temp";
    public static final String SERVER_NAME = "localhost";
    public static final int SERVER_PORT = 8080;
    public static final String SERVER_REPORT_AGENT_API_ADDRESS = "agents";
    public static final String SERVER_REPORT_JOB_API_ADDRESS = "agents/work";
    public static final String SERVER_CHECK_FOR_WORK_API_ADDRESS = "agents/%s/work";
    public static final String SERVER_CREATE_ARTIFACT_API_ADDRESS = "Artifacts/%s/%s";
    public static final String SERVER_FETCH_ARTIFACT_API_ADDRESS = "pipeline-definitions";
    public static final String ARTIFACTS_DIRECTORY = "Artifacts";
}

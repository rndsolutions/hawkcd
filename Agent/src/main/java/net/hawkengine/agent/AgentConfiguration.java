package net.hawkengine.agent;

import com.sun.management.OperatingSystemMXBean;
import net.hawkengine.agent.constants.ConfigConstants;
import net.hawkengine.agent.models.InstallInfo;
import net.hawkengine.agent.models.payload.AgentInfo;
import net.hawkengine.agent.models.payload.EnvironmentInfo;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;

public final class AgentConfiguration {

    private static AgentInfo agentInfo;
    private static EnvironmentInfo environmentInfo;
    private static InstallInfo installInfo;

    public static void configure() {
        agentInfo = new AgentInfo();
        environmentInfo = new EnvironmentInfo();
        installInfo = new InstallInfo();
        getUserEnteredSettings();
    }

    public static AgentInfo getAgentInfo() {
        return agentInfo;
    }

    public static EnvironmentInfo getEnvironmentInfo() {
        configureEnvironmentInfo();
        return environmentInfo;
    }

    public static InstallInfo getInstallInfo() {
        return installInfo;
    }

    private static void getUserEnteredSettings() {
        Properties configFileProperties = fetchConfigFileProperties();

        String agentId = configFileProperties.getProperty("agentId");
        agentInfo.setId((agentId != null && !agentId.isEmpty()) ? agentId : generateAgentId(configFileProperties));

        String agentName = configFileProperties.getProperty("agentName");
        agentInfo.setName((agentName != null && !agentName.isEmpty()) ? agentName : ConfigConstants.AGENT_NAME);
        agentInfo.setConnected(true);
        agentInfo.setEnabled(false);
        agentInfo.setRootPath(ConfigConstants.AGENT_SANDBOX);
        agentInfo.setLastReportedTime(null);
        try {
            agentInfo.setHostName(InetAddress.getLocalHost().getHostName());
            agentInfo.setIpAddress(InetAddress.getLocalHost().getHostAddress());
            agentInfo.setOperatingSystem(System.getProperty("os.name"));

        } catch (UnknownHostException e) {
            agentInfo.setHostName("unknown");
            agentInfo.setIpAddress("unknown");
            agentInfo.setOperatingSystem("not available");
        }

        String agentPipelinesDir = configFileProperties.getProperty("agentPipelinesDir");
        installInfo.setAgentPipelinesDir((agentPipelinesDir != null && !agentPipelinesDir.isEmpty()) ? agentPipelinesDir : ConfigConstants.AGENT_PIPELINES_DIR);

        String serverName = configFileProperties.getProperty("serverName");
        installInfo.setServerName((serverName != null && !serverName.isEmpty()) ? serverName : ConfigConstants.SERVER_NAME);

        String serverPort = configFileProperties.getProperty("serverPort");
        installInfo.setServerPort((serverPort != null && !serverPort.isEmpty()) ? Integer.parseInt(serverPort) : ConfigConstants.SERVER_PORT);

        installInfo.setServerAddress(String.format("http://%s:%s", installInfo.getServerName(), installInfo.getServerPort()));

        installInfo.setReportJobApiAddress(String.format("%s/%s", installInfo.getServerAddress(), String.format(ConfigConstants.SERVER_REPORT_JOB_API_ADDRESS, getAgentInfo().getId())));

        installInfo.setReportAgentApiAddress(String.format("%s/%s/%s/%s", installInfo.getServerAddress(), ConfigConstants.SERVER_REPORT_AGENT_API_ADDRESS, getAgentInfo().getId(), "report"));

        installInfo.setCheckForWorkApiAddress(String.format("%s/%s", installInfo.getServerAddress(), String.format(ConfigConstants.SERVER_CHECK_FOR_WORK_API_ADDRESS, getAgentInfo().getId())));

        installInfo.setCreateArtifactApiAddress(String.format("%s/%s", installInfo.getServerAddress(), ConfigConstants.SERVER_CREATE_ARTIFACT_API_ADDRESS));

        installInfo.setFetchArtifactApiAddress(String.format("%s/%s", installInfo.getServerAddress(), ConfigConstants.SERVER_FETCH_ARTIFACT_API_ADDRESS));

        installInfo.setAgentSandbox(Paths.get(ConfigConstants.AGENT_SANDBOX).toString());

        installInfo.setAgentTempDirectoryPath(Paths.get(ConfigConstants.AGENT_SANDBOX, ConfigConstants.AGENT_TEMP_DIR).toString());

        installInfo.setAgentArtifactsDirectoryPath(Paths.get(ConfigConstants.AGENT_SANDBOX, ConfigConstants.ARTIFACTS_DIRECTORY).toString());
    }

    private static void configureEnvironmentInfo() {
        File file = new File("/");
        int gb = 1024 * 1024 * 1024;

        environmentInfo.setOsName(System.getProperty("os.name"));
        environmentInfo.setTotalDiskSpaceGBytes(file.getFreeSpace() / gb);
        environmentInfo.setFreeDiskSpaceGBytes(file.getTotalSpace() / gb);
        environmentInfo.setTotalRamMBytes(((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() * 1024);
        environmentInfo.setFreeRamMBytes(((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize() * 1024);
    }

    private static Properties fetchConfigFileProperties() {
        File configFile = new File(ConfigConstants.AGENT_CONFIG_FILE_LOCATION);
        if (!configFile.exists()) {
            createConfigFile(configFile);
        }

        Properties properties = new Properties();
        try {
            InputStream inputStream = new FileInputStream(configFile);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    private static void createConfigFile(File configFile) {
        configFile.getParentFile().mkdir();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(ConfigConstants.AGENT_CONFIG_FILE_NAME);
        try {
            FileUtils.copyInputStreamToFile(inputStream, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateAgentId(Properties properties) {
        UUID agentId = UUID.randomUUID();

        try {
            File configFile = new File(ConfigConstants.AGENT_CONFIG_FILE_LOCATION);
            OutputStream output = new FileOutputStream(configFile);
            properties.setProperty("agentId", agentId.toString());
            properties.store(output, null);
            output.close();
        } catch (IOException io) {
            io.printStackTrace();
        }

        return agentId.toString();
    }
}

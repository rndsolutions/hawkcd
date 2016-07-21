package net.hawkengine.core;

import net.hawkengine.core.utilities.constants.ConfigurationConstants;
import net.hawkengine.model.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

public class ServerConfiguration {
    private static Configuration configuration;

    public static void configure() {
        configuration = generateConfiguration();
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    private static Configuration generateConfiguration() {
        File configFile = new File(ConfigurationConstants.CONFIGURATION_FILE_NAME);
        if (!configFile.exists()) {
            createConfigFile(configFile);
        }

        Configuration configuration = null;
        try {
            Yaml yaml = new Yaml();
            FileInputStream fileInputStream = new FileInputStream(configFile);
            configuration = yaml.loadAs(fileInputStream, Configuration.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return configuration;
    }

    private static void createConfigFile(File configFile) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFile.getName());
        try {
            FileUtils.copyInputStreamToFile(stream, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

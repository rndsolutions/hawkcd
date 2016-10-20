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

package net.hawkengine.core;

import net.hawkengine.model.configuration.Configuration;
import net.hawkengine.model.configuration.DatabaseConfig;
import net.hawkengine.model.enums.DatabaseType;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.hawkengine.core.utilities.constants.ConfigurationConstants.*;

public class ServerConfiguration {
    private static Configuration configuration;

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static String configure() {
        File configFile = new File(CONFIG_FILE_NAME);
        String errorMessage = createConfigFile(configFile);
        if (!errorMessage.isEmpty()) {
            return errorMessage;
        }

        errorMessage = loadConfiguration(configFile);
        if (!errorMessage.isEmpty()) {
            return errorMessage;
        }

        errorMessage = validateConfiguration(configuration);
        return errorMessage;
    }

    public static String createConfigFile(File configFile) {
        String errorMessage = "";
        if (!configFile.exists()) {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
            try {
                FileUtils.copyInputStreamToFile(stream, configFile);
            } catch (IOException e) {
                errorMessage = String.format(FAILED_TO_CREATE_CONFIG, configFile.getName());
            }
        }

        return errorMessage;
    }

    public static String loadConfiguration(File configFile) {
        String errorMessage = "";

        try (FileInputStream fileInputStream = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            configuration = yaml.loadAs(fileInputStream, Configuration.class);
        } catch (FileNotFoundException e) {
            errorMessage = String.format(FAILED_TO_LOCATE_CONFIG, configFile.getName());
        } catch (YAMLException e) {
            errorMessage = e.getMessage();
            String pattern = "property=(.*?)\\s";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(errorMessage);
            if (matcher.find()) {
                errorMessage = String.format(INVALID_CONFIG_PROPERTY, matcher.group(1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return errorMessage;
    }

    public static String validateConfiguration(Configuration configuration) {
        StringBuilder errorMessage = new StringBuilder();

        // Server settings
        String serverHost = configuration.getServerHost();
        if (serverHost == null || serverHost.isEmpty()) {
            errorMessage.append(String.format(EMPTY_CONFIG_PROPERTY, PROPERTY_SERVER_HOST));
        }

        // Database settings
        DatabaseType databaseType = configuration.getDatabaseType();
        DatabaseConfig databaseConfig = configuration.getDatabaseConfigs().get(databaseType);

        String name = databaseConfig.getName();
        if (name == null || name.isEmpty()) {
            errorMessage.append(DATABASE_CONFIG_MESSAGE);
            errorMessage.append(String.format(EMPTY_CONFIG_PROPERTY, PROPERTY_DATABASE_NAME));
        }

        String host = databaseConfig.getHost();
        if (host == null || host.isEmpty()) {
            errorMessage.append(DATABASE_CONFIG_MESSAGE);
            errorMessage.append(String.format(EMPTY_CONFIG_PROPERTY, PROPERTY_DATABASE_HOST));
        }

        // Material settings
        String materialsDestination = configuration.getMaterialsDestination();
        if (materialsDestination == null || materialsDestination.isEmpty()) {
            errorMessage.append(String.format(EMPTY_CONFIG_PROPERTY, PROPERTY_MATERIALS_DESTINATION));
        }

        String artifactsDestination = configuration.getArtifactsDestination();
        if (artifactsDestination == null || artifactsDestination.isEmpty()) {
            errorMessage.append(String.format(EMPTY_CONFIG_PROPERTY, PROPERTY_ARTIFACTS_DESTINATION));
        }

        // Worker settings
        int pipelineSchedulerPollInterval = configuration.getPipelineSchedulerPollInterval();
        if (pipelineSchedulerPollInterval < MIN_WORKER_POLL_INTERVAL || pipelineSchedulerPollInterval > MAX_WORKER_POLL_INTERVAL) {
            errorMessage.append(String.format(WORKER_POLL_INTERVAL_ERROR, PROPERTY_SCHEDULER_POLL_INTERVAL, MIN_WORKER_POLL_INTERVAL, MAX_WORKER_POLL_INTERVAL));
        }

        int materialTrackerPollInterval = configuration.getMaterialTrackerPollInterval();
        if (materialTrackerPollInterval < MIN_WORKER_POLL_INTERVAL || materialTrackerPollInterval > MAX_WORKER_POLL_INTERVAL) {
            errorMessage.append(String.format(WORKER_POLL_INTERVAL_ERROR, PROPERTY_TRACKER_POLL_INTERVAL, MIN_WORKER_POLL_INTERVAL, MAX_WORKER_POLL_INTERVAL));
        }

        return errorMessage.toString();
    }
}

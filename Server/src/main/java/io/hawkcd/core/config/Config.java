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

package io.hawkcd.core.config;

import io.hawkcd.model.configuration.Configuration;
import io.hawkcd.model.configuration.DatabaseConfig;
import io.hawkcd.model.enums.DatabaseType;
import io.hawkcd.utilities.constants.ConfigurationConstants;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {
    private static Configuration configuration;
    public static Configuration getConfiguration() {
        return configuration;
    }

    public static String configure() {
        File configFile = new File(ConfigurationConstants.CONFIG_FILE_NAME);
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
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(ConfigurationConstants.CONFIG_FILE_NAME);
            try {
                FileUtils.copyInputStreamToFile(stream, configFile);
            } catch (IOException e) {
                errorMessage = String.format(ConfigurationConstants.FAILED_TO_CREATE_CONFIG, configFile.getName());
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
            errorMessage = String.format(ConfigurationConstants.FAILED_TO_LOCATE_CONFIG, configFile.getName());
        } catch (YAMLException e) {
            errorMessage = e.getMessage();
            String pattern = "property=(.*?)\\s";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(errorMessage);
            if (matcher.find()) {
                errorMessage = String.format(ConfigurationConstants.INVALID_CONFIG_PROPERTY, matcher.group(1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return errorMessage;
    }

    private static class NullRepresenter extends Representer {
        public NullRepresenter() {
            super();
            // null representer is exceptional and it is stored as an instance
            // variable.
            this.nullRepresenter = new RepresentNull();
        }
        private class RepresentNull implements Represent {
            public Node representData(Object data) {
                // possible values are here http://yaml.org/type/null.html
                return representScalar(Tag.NULL, "");
            }
        }
    }

    public static void addServerId(String serverId) throws IOException {

        FileWriter writer = new FileWriter(ConfigurationConstants.CONFIG_FILE_NAME);

        DumperOptions options=new DumperOptions();
        options.setIndent(1);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml=new Yaml(new NullRepresenter(),options);
        String conf = yaml.dump(configuration);
        yaml.dump(conf, writer);

        //remove |- literal from the config file
        File file = new File(ConfigurationConstants.CONFIG_FILE_NAME);
        String s1 = FileUtils.readFileToString(file);
        String substring = s1.substring(1, s1.length() - 1);
        FileUtils.writeStringToFile(file,substring);
    }

    public static String validateConfiguration(Configuration configuration) {
        StringBuilder errorMessage = new StringBuilder();

        // HServer settings
        String serverHost = configuration.getServerHost();
        if (serverHost == null || serverHost.isEmpty()) {
            errorMessage.append(String.format(ConfigurationConstants.EMPTY_CONFIG_PROPERTY, ConfigurationConstants.PROPERTY_SERVER_HOST));
        }

        // Database settings
        DatabaseType databaseType = configuration.getDatabaseType();
        DatabaseConfig databaseConfig = configuration.getDatabaseConfigs().get(databaseType);

        String name = databaseConfig.getName();
        if (name == null || name.isEmpty()) {
            errorMessage.append(ConfigurationConstants.DATABASE_CONFIG_MESSAGE);
            errorMessage.append(String.format(ConfigurationConstants.EMPTY_CONFIG_PROPERTY, ConfigurationConstants.PROPERTY_DATABASE_NAME));
        }

        String host = databaseConfig.getHost();
        if (host == null || host.isEmpty()) {
            errorMessage.append(ConfigurationConstants.DATABASE_CONFIG_MESSAGE);
            errorMessage.append(String.format(ConfigurationConstants.EMPTY_CONFIG_PROPERTY, ConfigurationConstants.PROPERTY_DATABASE_HOST));
        }

        // Material settings
        String materialsDestination = configuration.getMaterialsDestination();
        if (materialsDestination == null || materialsDestination.isEmpty()) {
            errorMessage.append(String.format(ConfigurationConstants.EMPTY_CONFIG_PROPERTY, ConfigurationConstants.PROPERTY_MATERIALS_DESTINATION));
        }

        String artifactsDestination = configuration.getArtifactsDestination();
        if (artifactsDestination == null || artifactsDestination.isEmpty()) {
            errorMessage.append(String.format(ConfigurationConstants.EMPTY_CONFIG_PROPERTY, ConfigurationConstants.PROPERTY_ARTIFACTS_DESTINATION));
        }

        // Worker settings
        int pipelineSchedulerPollInterval = configuration.getPipelineSchedulerPollInterval();
        if (pipelineSchedulerPollInterval < ConfigurationConstants.MIN_WORKER_POLL_INTERVAL || pipelineSchedulerPollInterval > ConfigurationConstants.MAX_WORKER_POLL_INTERVAL) {
            errorMessage.append(String.format(ConfigurationConstants.WORKER_POLL_INTERVAL_ERROR, ConfigurationConstants.PROPERTY_SCHEDULER_POLL_INTERVAL, ConfigurationConstants.MIN_WORKER_POLL_INTERVAL, ConfigurationConstants.MAX_WORKER_POLL_INTERVAL));
        }

        int materialTrackerPollInterval = configuration.getMaterialTrackerPollInterval();
        if (materialTrackerPollInterval < ConfigurationConstants.MIN_WORKER_POLL_INTERVAL || materialTrackerPollInterval > ConfigurationConstants.MAX_WORKER_POLL_INTERVAL) {
            errorMessage.append(String.format(ConfigurationConstants.WORKER_POLL_INTERVAL_ERROR, ConfigurationConstants.PROPERTY_TRACKER_POLL_INTERVAL, ConfigurationConstants.MIN_WORKER_POLL_INTERVAL, ConfigurationConstants.MAX_WORKER_POLL_INTERVAL));
        }

        return errorMessage.toString();
    }
}




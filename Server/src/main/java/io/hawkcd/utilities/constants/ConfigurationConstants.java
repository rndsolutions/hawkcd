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

package io.hawkcd.utilities.constants;

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

    public static final int MIN_WORKER_POLL_INTERVAL = 1;
    public static final int MAX_WORKER_POLL_INTERVAL = 30;
    public static final String WORKER_POLL_INTERVAL_ERROR = "Property '%s' must be set between %d and %d seconds." + System.getProperty("line.separator");
}

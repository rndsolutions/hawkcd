package net.hawkengine.core.utilities.constants;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.model.enums.DatabaseType;

public final class ConfigurationConstants {
    public static final String CONFIGURATION_FILE_NAME = "config.yaml";
    public static final DatabaseType DATABASE_TYPE = ServerConfiguration.getConfiguration().getDatabaseType();
}

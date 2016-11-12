package io.hawkcd.core;

import io.hawkcd.Config;
import io.hawkcd.utilities.constants.ConfigurationConstants;
import io.hawkcd.model.configuration.Configuration;
import io.hawkcd.model.configuration.DatabaseConfig;
import io.hawkcd.model.enums.DatabaseType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ServerConfigurationTests {
    private File testConfigFile = new File("test" + ConfigurationConstants.CONFIG_FILE_NAME);

    @After
    public void tearDown() {
        this.testConfigFile.delete();
    }

    @Test
    public void serverConfiguration_instantiated_notNull() {
        // Act
        Config config = new Config();

        // Assert
        Assert.assertNotNull(config);
    }

    @Test
    public void getConfiguration_validConfigFile_configurationClass() {
        // Arrange
        Config.createConfigFile(this.testConfigFile);
        Config.loadConfiguration(this.testConfigFile);
        Class expectedResult = Configuration.class;

        // Act
        Class actualResult = Config.getConfiguration().getClass();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void configure_configurationSuccessful_noErrorMessage() {
        // Arrange
        String expectedResult = "";

        // Act
        String actualResult = Config.configure();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void createConfigFile_fileDoesNotExist_fileCreated() {
        // Act
        Config.createConfigFile(this.testConfigFile);

        // Assert
        Assert.assertTrue(this.testConfigFile.exists());
    }

    @Test
    public void loadConfiguration_successfullyLoaded_noErrorMessage() {
        // Arrange
        Config.createConfigFile(this.testConfigFile);
        String expectedResult = "";

        // Act
        String actualResult = Config.loadConfiguration(this.testConfigFile);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void loadConfiguration_configFileNotFound_correctErrorMessage() {
        // Arrange
        String expectedResult = String.format(ConfigurationConstants.FAILED_TO_LOCATE_CONFIG, this.testConfigFile.getName());

        // Act
        String actualResult = Config.loadConfiguration(this.testConfigFile);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void loadConfiguration_invalidConfigProperty_correctErrorMessage() {
        // Arrange
        String invalidProperty = "invalidProperty";
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(this.testConfigFile, true));
            bufferedWriter.write(invalidProperty + ":");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String expectedResult = String.format(ConfigurationConstants.INVALID_CONFIG_PROPERTY, invalidProperty);

        // Act
        String actualResult = Config.loadConfiguration(this.testConfigFile);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_validConfiguration_noErrorMessage() {
        // Arrange
        Config.createConfigFile(this.testConfigFile);
        Config.loadConfiguration(this.testConfigFile);
        Configuration configuration = Config.getConfiguration();
        String expectedResult = "";

        // Act
        String actualResult = Config.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidHostName_correctErrorMessage() {
        // Arrange
        Config.createConfigFile(this.testConfigFile);
        Config.loadConfiguration(this.testConfigFile);
        Configuration configuration = Config.getConfiguration();
        configuration.setServerHost(null);
        String expectedResult = String.format(ConfigurationConstants.EMPTY_CONFIG_PROPERTY, ConfigurationConstants.PROPERTY_SERVER_HOST);

        // Act
        String actualResult = Config.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidDatabaseName_correctErrorMessage() {
        // Arrange
        Config.createConfigFile(this.testConfigFile);
        Config.loadConfiguration(this.testConfigFile);
        Configuration configuration = Config.getConfiguration();
        DatabaseType databaseType = configuration.getDatabaseType();
        DatabaseConfig databaseConfig = configuration.getDatabaseConfigs().get(databaseType);
        databaseConfig.setName(null);
        String expectedResult = ConfigurationConstants.DATABASE_CONFIG_MESSAGE + String.format(ConfigurationConstants.EMPTY_CONFIG_PROPERTY, ConfigurationConstants.PROPERTY_DATABASE_NAME);

        // Act
        String actualResult = Config.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidDatabaseHost_correctErrorMessage() {
        // Arrange
        Config.createConfigFile(this.testConfigFile);
        Config.loadConfiguration(this.testConfigFile);
        Configuration configuration = Config.getConfiguration();
        DatabaseType databaseType = configuration.getDatabaseType();
        DatabaseConfig databaseConfig = configuration.getDatabaseConfigs().get(databaseType);
        databaseConfig.setHost(null);
        String expectedResult = ConfigurationConstants.DATABASE_CONFIG_MESSAGE + String.format(ConfigurationConstants.EMPTY_CONFIG_PROPERTY, ConfigurationConstants.PROPERTY_DATABASE_HOST);

        // Act
        String actualResult = Config.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidMaterialsDestination_correctErrorMessage() {
        // Arrange
        Config.createConfigFile(this.testConfigFile);
        Config.loadConfiguration(this.testConfigFile);
        Configuration configuration = Config.getConfiguration();
        configuration.setMaterialsDestination(null);
        String expectedResult = String.format(ConfigurationConstants.EMPTY_CONFIG_PROPERTY, ConfigurationConstants.PROPERTY_MATERIALS_DESTINATION);

        // Act
        String actualResult = Config.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidArtifactsDestination_correctErrorMessage() {
        // Arrange
        Config.createConfigFile(this.testConfigFile);
        Config.loadConfiguration(this.testConfigFile);
        Configuration configuration = Config.getConfiguration();
        configuration.setArtifactsDestination(null);
        String expectedResult = String.format(ConfigurationConstants.EMPTY_CONFIG_PROPERTY, ConfigurationConstants.PROPERTY_ARTIFACTS_DESTINATION);

        // Act
        String actualResult = Config.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidSchedulerPollInterval_correctErrorMessage() {
        // Arrange
        Config.createConfigFile(this.testConfigFile);
        Config.loadConfiguration(this.testConfigFile);
        Configuration configuration = Config.getConfiguration();
        configuration.setPipelineSchedulerPollInterval(0);
        String expectedResult = String.format(ConfigurationConstants.WORKER_POLL_INTERVAL_ERROR, ConfigurationConstants.PROPERTY_SCHEDULER_POLL_INTERVAL, ConfigurationConstants.MIN_WORKER_POLL_INTERVAL, ConfigurationConstants.MAX_WORKER_POLL_INTERVAL);

        // Act
        String actualResult = Config.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidTrackerPollInterval_correctErrorMessage() {
        // Arrange
        Config.createConfigFile(this.testConfigFile);
        Config.loadConfiguration(this.testConfigFile);
        Configuration configuration = Config.getConfiguration();
        configuration.setMaterialTrackerPollInterval(0);
        String expectedResult = String.format(ConfigurationConstants.WORKER_POLL_INTERVAL_ERROR, ConfigurationConstants.PROPERTY_TRACKER_POLL_INTERVAL, ConfigurationConstants.MIN_WORKER_POLL_INTERVAL, ConfigurationConstants.MAX_WORKER_POLL_INTERVAL);

        // Act
        String actualResult = Config.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }
}
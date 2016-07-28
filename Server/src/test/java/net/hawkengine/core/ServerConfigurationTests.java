package net.hawkengine.core;

import net.hawkengine.core.utilities.constants.ConfigurationConstants;
import net.hawkengine.model.configuration.Configuration;
import net.hawkengine.model.configuration.DatabaseConfig;
import net.hawkengine.model.enums.DatabaseType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static net.hawkengine.core.utilities.constants.ConfigurationConstants.*;

public class ServerConfigurationTests {
    private File testConfigFile = new File("test" + ConfigurationConstants.CONFIG_FILE_NAME);

    @After
    public void tearDown() {
        this.testConfigFile.delete();
    }

    @Test
    public void serverConfiguration_instantiated_notNull() {
        // Act
        ServerConfiguration serverConfiguration = new ServerConfiguration();

        // Assert
        Assert.assertNotNull(serverConfiguration);
    }

    @Test
    public void getConfiguration_validConfigFile_configurationClass() {
        // Arrange
        ServerConfiguration.createConfigFile(this.testConfigFile);
        ServerConfiguration.loadConfiguration(this.testConfigFile);
        Class expectedResult = Configuration.class;

        // Act
        Class actualResult = ServerConfiguration.getConfiguration().getClass();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void configure_configurationSuccessful_noErrorMessage() {
        // Arrange
        String expectedResult = "";

        // Act
        String actualResult = ServerConfiguration.configure();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void createConfigFile_fileDoesNotExist_fileCreated() {
        // Act
        ServerConfiguration.createConfigFile(this.testConfigFile);

        // Assert
        Assert.assertTrue(this.testConfigFile.exists());
    }

    @Test
    public void loadConfiguration_successfullyLoaded_noErrorMessage() {
        // Arrange
        ServerConfiguration.createConfigFile(this.testConfigFile);
        String expectedResult = "";

        // Act
        String actualResult = ServerConfiguration.loadConfiguration(this.testConfigFile);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void loadConfiguration_configFileNotFound_correctErrorMessage() {
        // Arrange
        String expectedResult = String.format(FAILED_TO_LOCATE_CONFIG, this.testConfigFile.getName());

        // Act
        String actualResult = ServerConfiguration.loadConfiguration(this.testConfigFile);

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

        String expectedResult = String.format(INVALID_CONFIG_PROPERTY, invalidProperty);

        // Act
        String actualResult = ServerConfiguration.loadConfiguration(this.testConfigFile);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_validConfiguration_noErrorMessage() {
        // Arrange
        ServerConfiguration.createConfigFile(this.testConfigFile);
        ServerConfiguration.loadConfiguration(this.testConfigFile);
        Configuration configuration = ServerConfiguration.getConfiguration();
        String expectedResult = "";

        // Act
        String actualResult = ServerConfiguration.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidHostName_correctErrorMessage() {
        // Arrange
        ServerConfiguration.createConfigFile(this.testConfigFile);
        ServerConfiguration.loadConfiguration(this.testConfigFile);
        Configuration configuration = ServerConfiguration.getConfiguration();
        configuration.setServerHost(null);
        String expectedResult = String.format(EMPTY_CONFIG_PROPERTY, PROPERTY_SERVER_HOST);

        // Act
        String actualResult = ServerConfiguration.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidDatabaseName_correctErrorMessage() {
        // Arrange
        ServerConfiguration.createConfigFile(this.testConfigFile);
        ServerConfiguration.loadConfiguration(this.testConfigFile);
        Configuration configuration = ServerConfiguration.getConfiguration();
        DatabaseType databaseType = configuration.getDatabaseType();
        DatabaseConfig databaseConfig = configuration.getDatabaseConfigs().get(databaseType);
        databaseConfig.setName(null);
        String expectedResult = DATABASE_CONFIG_MESSAGE + String.format(EMPTY_CONFIG_PROPERTY, PROPERTY_DATABASE_NAME);

        // Act
        String actualResult = ServerConfiguration.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidDatabaseHost_correctErrorMessage() {
        // Arrange
        ServerConfiguration.createConfigFile(this.testConfigFile);
        ServerConfiguration.loadConfiguration(this.testConfigFile);
        Configuration configuration = ServerConfiguration.getConfiguration();
        DatabaseType databaseType = configuration.getDatabaseType();
        DatabaseConfig databaseConfig = configuration.getDatabaseConfigs().get(databaseType);
        databaseConfig.setHost(null);
        String expectedResult = DATABASE_CONFIG_MESSAGE + String.format(EMPTY_CONFIG_PROPERTY, PROPERTY_DATABASE_HOST);

        // Act
        String actualResult = ServerConfiguration.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidMaterialsDestination_correctErrorMessage() {
        // Arrange
        ServerConfiguration.createConfigFile(this.testConfigFile);
        ServerConfiguration.loadConfiguration(this.testConfigFile);
        Configuration configuration = ServerConfiguration.getConfiguration();
        configuration.setMaterialsDestination(null);
        String expectedResult = String.format(EMPTY_CONFIG_PROPERTY, PROPERTY_MATERIALS_DESTINATION);

        // Act
        String actualResult = ServerConfiguration.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidArtifactsDestination_correctErrorMessage() {
        // Arrange
        ServerConfiguration.createConfigFile(this.testConfigFile);
        ServerConfiguration.loadConfiguration(this.testConfigFile);
        Configuration configuration = ServerConfiguration.getConfiguration();
        configuration.setArtifactsDestination(null);
        String expectedResult = String.format(EMPTY_CONFIG_PROPERTY, PROPERTY_ARTIFACTS_DESTINATION);

        // Act
        String actualResult = ServerConfiguration.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidSchedulerPollInterval_correctErrorMessage() {
        // Arrange
        ServerConfiguration.createConfigFile(this.testConfigFile);
        ServerConfiguration.loadConfiguration(this.testConfigFile);
        Configuration configuration = ServerConfiguration.getConfiguration();
        configuration.setPipelineSchedulerPollInterval(0);
        String expectedResult = String.format(WORKER_POLL_INTERVAL_ERROR, PROPERTY_SCHEDULER_POLL_INTERVAL, MIN_WORKER_POLL_INTERVAL, MAX_WORKER_POLL_INTERVAL);

        // Act
        String actualResult = ServerConfiguration.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateConfiguration_invalidTrackerPollInterval_correctErrorMessage() {
        // Arrange
        ServerConfiguration.createConfigFile(this.testConfigFile);
        ServerConfiguration.loadConfiguration(this.testConfigFile);
        Configuration configuration = ServerConfiguration.getConfiguration();
        configuration.setMaterialTrackerPollInterval(0);
        String expectedResult = String.format(WORKER_POLL_INTERVAL_ERROR, PROPERTY_TRACKER_POLL_INTERVAL, MIN_WORKER_POLL_INTERVAL, MAX_WORKER_POLL_INTERVAL);

        // Act
        String actualResult = ServerConfiguration.validateConfiguration(configuration);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }
}
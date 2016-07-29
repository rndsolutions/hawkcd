package net.hawkengine;

import net.hawkengine.core.HawkServer;
import net.hawkengine.core.ServerConfiguration;
import org.apache.log4j.Logger;

public class Main {
    private static final String FAILED_TO_LOAD_CONFIG_ERROR = "Failed to load configuration file: ";
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            String errorMessage = ServerConfiguration.configure();
            if (errorMessage.isEmpty()) {
                HawkServer hawkServer = new HawkServer();
                hawkServer.configureJetty();
                hawkServer.start();
            } else {
                LOGGER.error(FAILED_TO_LOAD_CONFIG_ERROR + errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
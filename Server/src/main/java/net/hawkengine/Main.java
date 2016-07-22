package net.hawkengine;

import net.hawkengine.core.HawkServer;
import net.hawkengine.core.ServerConfiguration;
import org.apache.log4j.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            ServerConfiguration.configure();
            if (ServerConfiguration.getConfiguration() != null) {
                HawkServer hawkServer = new HawkServer();
                hawkServer.configureJetty();
                hawkServer.start();
            } else {
                LOGGER.error("Failed to load configuration file!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
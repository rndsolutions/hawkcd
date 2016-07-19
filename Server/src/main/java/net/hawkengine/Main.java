package net.hawkengine;

import net.hawkengine.core.HawkServer;
import net.hawkengine.core.ServerConfiguration;

public class Main {
    public static void main(String[] args) {
        try {
            ServerConfiguration.configure();
            if (ServerConfiguration.getConfiguration() != null) {
                HawkServer hawkServer = new HawkServer();
                hawkServer.configureJetty();
                hawkServer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
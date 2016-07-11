package net.hawkengine.agent.interfaces;

public interface IAgent {

    void reportJobToServer();

    void reportAgentToServer();

    void checkForWork();

    void start();

    void stop();
}

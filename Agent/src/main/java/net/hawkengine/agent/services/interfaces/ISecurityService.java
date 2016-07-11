package net.hawkengine.agent.services.interfaces;

public interface ISecurityService {
    String encrypt(String raw);

    String decrypt(String encrypted);
}

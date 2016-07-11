package net.hawkengine.agent.services;

import com.sun.jersey.api.client.Client;
import net.hawkengine.agent.services.interfaces.IFileManagementService;
import net.hawkengine.agent.services.interfaces.IMaterialService;
import net.hawkengine.agent.services.interfaces.ISecurityService;

public abstract class MaterialService implements IMaterialService {
    protected IFileManagementService fileManagementService ;
    protected ISecurityService securityService;
    protected Client restClient;

    public MaterialService() {
        this.fileManagementService = new FileManagementService();
        this.securityService = new SecurityService();
        this.restClient = new Client();
    }

    public IFileManagementService getFileManagementService() {
        return fileManagementService;
    }

    public void setFileManagementService(FileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }

    public ISecurityService getSecurityService() {
        return securityService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}

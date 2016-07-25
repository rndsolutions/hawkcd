package net.hawkengine.services.filters.factories;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.AgentAuthorizationService;
import net.hawkengine.services.filters.PipelineAuthorizationService;
import net.hawkengine.services.filters.PipelineDefinitionAuthorizationService;
import net.hawkengine.services.filters.PipelineGroupAuthorizationService;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;

import java.util.List;

public class AuthorizationFactory<T extends DbEntry> {
    private IAuthorizationService authorizationService;

    public IAuthorizationService filter(String service){
        switch (service){
            case "PipelineDefinitionService":
                this.authorizationService = new PipelineDefinitionAuthorizationService();
                return this.authorizationService;
            case "AgentService":
                this.authorizationService = new AgentAuthorizationService();
                return this.authorizationService;
            case "PipelineService":
                this.authorizationService = new PipelineAuthorizationService();
                return this.authorizationService;
            case "PipelineGroupService":
                this.authorizationService = new PipelineGroupAuthorizationService();
                return this.authorizationService;
            default:
                return null;
        }
    }
}

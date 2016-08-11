package net.hawkengine.services.filters.factories;

import net.hawkengine.services.filters.*;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;

public class AuthorizationServiceFactory {
    private static IAuthorizationService authorizationService;

    public static IAuthorizationService create(String service) {
        switch (service) {
            case "PipelineDefinitionService":
            case "StageDefinitionService":
            case "JobDefinitionService":
            case "TaskDefinitionService":
                authorizationService = new PipelineDefinitionAuthorizationService();
                return authorizationService;
            case "AgentService":
                authorizationService = new AgentAuthorizationService();
                return authorizationService;
            case "PipelineService":
            case "StageService":
            case "JobService":
                authorizationService = new PipelineAuthorizationService();
                return authorizationService;
            case "PipelineGroupService":
                authorizationService = new PipelineGroupAuthorizationService();
                return authorizationService;
            case "UserGroupService":
                authorizationService = new UserGroupAuthorizationService();
                return authorizationService;
            case "UserService":
                authorizationService = new UserAuthorizationService();
                return authorizationService;
            default:
                return null;
        }
    }
}

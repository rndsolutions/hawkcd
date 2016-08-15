package net.hawkengine.services.filters.factories;

import net.hawkengine.services.filters.*;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;

public class AuthorizationServiceFactory {
    private static IAuthorizationService authorizationService;

    public static IAuthorizationService create(String service) {
        switch (service) {
            case "PipelineDefinitionService":
                authorizationService = new PipelineDefinitionAuthorizationService();
                return authorizationService;
            case "StageDefinitionService":
                authorizationService = new StageDefinitionAuthorizationService();
                return authorizationService;
            case "JobDefinitionService":
                authorizationService = new JobDefinitionAuthorizationService();
                return authorizationService;
            case "TaskDefinitionService":
                authorizationService = new TaskDefinitionAuthorizationService();
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

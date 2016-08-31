'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['$rootScope', 'pipeStatsService', 'agentService', 'viewModel', 'validationService', 'toaster', 'viewModelUpdater', 'adminGroupService', 'adminService', 'pipeConfigService', 'loginService', 'pipeExecService', 'notificationService',
        function($rootScope, pipeStatsService, agentService, viewModel, validationService, toaster, viewModelUpdater, adminGroupService, adminService, pipeConfigService, loginService, pipeExecService, notificationService) {
            var webSocketReceiverService = this;

            webSocketReceiverService.processEvent = function(data) {
                if (!validationService.isValid(data)) {
                    toaster.error('Invalid JSON format!');
                    return;
                }

                invoker(data, dispatcher);
                $rootScope.$apply();
            };

            var invoker = function(obj, dispatcher) {
                var className = obj['className'];
                var methodName = obj['methodName'];
                dispatcher[className][methodName](obj);
            };

            var dispatcher = {
                NotificationService: {
                    sendMessage: function(object) {
                        validationService.dispatcherFlow(object, [], true);
                    }
                },
                UserInfo: {
                    getUser: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.getUser]);
                    },
                    logoutSession: function(object) {
                        validationService.dispatcherFlow(object, [loginService.logout], true);
                    }
                },
                UserService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.getUsers]);
                    },
                    addUserWithoutProvider: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.addUser], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updateUser], true);
                    },
                    changeUserPassword: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updateUser], true)
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [adminService.getAllUsers], true);
                    },
                    assignUserToGroup: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updateUser, adminService.getAllUserGroupDTOs], true);
                    },
                    logout: function(object) {
                        validationService.dispatcherFlow(object, [loginService.logoutUser], true)
                    }
                },
                UserGroupService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.getUserGroups]);
                    },
                    getAllUserGroups: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.getUserGroupDTOs]);
                    },
                    addUserGroupDto: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.addUserGroup], true);
                    },
                    updateUserGroupDto: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updateUserGroup], true);
                    },
                    assignUserToGroup: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updateUserGroup], true);
                    },
                    unassignUserFromGroup: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updateUserGroup], true);
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [adminService.getAllUserGroupDTOs], true);
                    }
                },
                AuthorizationService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.getPermissions]);
                    },
                    updatePermission: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updatePermissions], true);
                    }
                },
                AgentService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updateAgents]);
                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.addAgent], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updateAgent]);
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [agentService.getAllAgents], true);
                    }
                },
                PipelineGroupService: {
                    getAll: function(object) {
                        //viewModelUpdater.getAllPipelineGroups(pipelineGroups);
                        //viewModelUpdater.updatePipelineGroupDTOs(pipelineGroups);
                    },
                    getAllPipelineGroupDTOs: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updatePipelineGroupDTOs]);
                    },
                    getById: function(object) {

                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.addPipelineGroup], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updatePipelineGroup], true);
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [pipeConfigService.getAllPipelineGroupDTOs], true);
                    }
                },
                PipelineDefinitionService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.getAllPipelineDefinitions]);
                    },
                    getById: function(object) {

                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.addPipelineDefinition], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updatePipelineDefinition], true);
                        // pipeConfigService.getAllPipelineGroupDTOs();
                        // pipeConfigService.getAllPipelineDefinitions();
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [pipeConfigService.getAllPipelineDefinitions, pipeExecService.getAllPipelines, pipeConfigService.getAllPipelineGroupDTOs]);
                    },
                    assignPipelineToGroup: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updatePipelineDefinition, pipeConfigService.getAllPipelineGroupDTOs, pipeConfigService.getAllPipelineDefinitions], true);
                    },
                    unassignPipelineFromGroup: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updatePipelineDefinition, pipeConfigService.getAllPipelineGroupDTOs, pipeConfigService.getAllPipelineDefinitions], true);
                    },
                    addWithMaterialDefinition: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.addPipelineDefinition], true);
                    }
                },
                PipelineService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.getAllPipelines]);
                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.addPipeline], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updatePipeline]);
                    },
                    delete: function(object) {

                    }
                },
                MaterialDefinitionService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.getAllMaterialDefinitions]);
                    },
                    getById: function(object) {

                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.addMaterialDefinition], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updateMaterialDefinition], true);
                        // pipeConfigService.getAllMaterialDefinitions();
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [pipeConfigService.getAllPipelineDefinitions, pipeConfigService.getAllPipelineGroupDTOs, pipeConfigService.getAllMaterialDefinitions], true);
                    }
                },
                StageDefinitionService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.getAllStageDefinitions]);
                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.addStageDefinition], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updateStageDefinition], true);
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [pipeConfigService.getAllPipelineDefinitions, pipeConfigService.getAllPipelineGroupDTOs], true);
                    }
                },
                JobDefinitionService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.getAllJobDefinitions]);
                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.addJobDefinition], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updateJobDefinition], true);
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [pipeConfigService.getAllPipelineDefinitions, pipeConfigService.getAllPipelineGroupDTOs], true);
                    }
                },
                TaskDefinitionService: {
                    getAll: function(object) {
                        //viewModelUpdater.getAllTaskDefinitions(object.result);
                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.addTaskDefinition], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [viewModelUpdater.updateTaskDefinition], true);
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [pipeConfigService.getAllPipelineDefinitions, pipeConfigService.getAllPipelineGroupDTOs], true);
                    }
                }
            };

            return webSocketReceiverService;

        }
    ]);

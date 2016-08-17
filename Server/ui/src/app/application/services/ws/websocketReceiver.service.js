'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['$rootScope', 'pipeStatsService', 'agentService', 'viewModel', 'validationService', 'toaster', 'viewModelUpdater', 'adminGroupService', 'adminService', 'pipeConfigService', 'loginService', 'pipeExecService',
        function ($rootScope, pipeStatsService, agentService, viewModel, validationService, toaster, viewModelUpdater, adminGroupService, adminService, pipeConfigService, loginService, pipeExecService) {
            var webSocketReceiverService = this;

            webSocketReceiverService.processEvent = function (data) {
                if (!validationService.isValid(data)) {
                    toaster.error('Invalid JSON format!');
                    return;
                }

                invoker(data, dispatcher);
                $rootScope.$apply();
            };

            var invoker = function (obj, dispatcher) {
                var className = obj['className'];
                var methodName = obj['methodName'];
                dispatcher[className][methodName](obj);
            };

            var dispatcher = {
                UserInfo: {
                    getUser: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.getUser);
                    }
                },
                UserService: {
                    getAll: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.getUsers);
                    },
                    addUserWithoutProvider: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addUser);
                    },
                    update: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updateUser);
                    },
                    delete: function (object) {
                        if (object.error == false) {
                            adminService.getAllUsers();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    assignUserToGroup: function (object) {
                        viewModelUpdater.updateUser(object.result);
                        adminService.getAllUserGroupDTOs();
                    },
                    logout: function (object) {
                       loginService.logoutUser();
                   }
                },
                UserGroupService: {
                    getAll: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.getUserGroups);
                    },
                    getAllUserGroups: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.getUserGroupDTOs);
                    },
                    addUserGroupDto: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addUserGroup);
                    },
                    updateUserGroupDto: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updateUserGroup);
                    },
                    assignUserToGroup: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updateUserGroup);
                    },
                    unassignUserFromGroup: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updateUserGroup);
                    },
                    delete: function (object) {
                        if (object.error == false) {
                            adminService.getAllUserGroupDTOs();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                AuthorizationService: {
                    getAll: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.getPermissions);
                    },
                    updatePermission: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updatePermissions);
                    }
                },
                AgentService: {
                    getAll: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updateAgents);
                    },
                    getById: function (object) {
                        validationService.dispatcherFlow(object,viewModelUpdater.updateAgent);
                    },
                    add: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addAgent);
                    },
                    update: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updateAgent);
                    },
                    delete: function (object) {
                        if (object.error == false) {
                            agentService.getAllAgents();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                PipelineGroupService: {
                    getAll: function (object) {
                        //viewModelUpdater.getAllPipelineGroups(pipelineGroups);
                        //viewModelUpdater.updatePipelineGroupDTOs(pipelineGroups);
                    },
                    getAllPipelineGroupDTOs: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updatePipelineGroupDTOs);
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addPipelineGroup);
                    },
                    update: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updatePipelineGroup);
                    },
                    delete: function (object) {
                        if (object.error == false) {
                            pipeConfigService.getAllPipelineGroupDTOs();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                PipelineDefinitionService: {
                    getAll: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.getAllPipelineDefinitions);
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addPipelineDefinition);
                    },
                    update: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updatePipelineDefinition(object.result);
                            pipeConfigService.getAllPipelineGroupDTOs();
                            pipeConfigService.getAllPipelineDefinitions();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    delete: function (object) {
                        if (object.error == false) {
                            pipeConfigService.getAllPipelineDefinitions();
                            pipeConfigService.getAllPipelineGroupDTOs();
                            pipeExecService.getAllPipelines();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    assignPipelineToGroup: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updatePipelineDefinition(object.result);
                            pipeConfigService.getAllPipelineGroupDTOs();
                            pipeConfigService.getAllPipelineDefinitions();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    unassignPipelineFromGroup: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updatePipelineDefinition(object.result);
                            pipeConfigService.getAllPipelineGroupDTOs();
                            pipeConfigService.getAllPipelineDefinitions();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    addWithMaterialDefinition:function(object){
                      validationService.dispatcherFlow(object,viewModelUpdater.addPipelineDefinition);
                    }
                },
                PipelineService: {
                    getAll: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.getAllPipelines);
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.addPipeline(object.result);
                            toaster.pop('success', "Notification", object.errorMessage);
                        }
                        else {
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    update: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updatePipeline);
                    },
                    delete: function (object) {
                        if (object.error == false) {

                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                MaterialDefinitionService: {
                    getAll: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.getAllMaterialDefinitions);
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addMaterialDefinition);
                    },
                    update: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updateMaterialDefinition(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    delete: function (object) {
                        if(object.error == false) {
                            pipeConfigService.getAllPipelineDefinitions();
                            pipeConfigService.getAllPipelineGroupDTOs();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                StageDefinitionService: {
                    getAll: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.getAllStageDefinitions(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.addStageDefinition(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    update: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updateStageDefinition(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    delete: function (object) {
                        if(object.error == false) {
                            pipeConfigService.getAllPipelineDefinitions();
                            pipeConfigService.getAllPipelineGroupDTOs();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                JobDefinitionService: {
                    getAll: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.getAllJobDefinitions(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.addJobDefinition(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    update: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updateJobDefinition(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    delete: function (object) {
                        if(object.error == false) {
                            pipeConfigService.getAllPipelineDefinitions();
                            pipeConfigService.getAllPipelineGroupDTOs();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                TaskDefinitionService: {
                    getAll: function (object) {
                        //viewModelUpdater.getAllTaskDefinitions(object.result);
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.addTaskDefinition(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    update: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updateTaskDefinition(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    delete: function (object) {
                        if(object.error == false) {
                            pipeConfigService.getAllPipelineDefinitions();
                            pipeConfigService.getAllPipelineGroupDTOs();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                }
            };

            return webSocketReceiverService;

        }]);

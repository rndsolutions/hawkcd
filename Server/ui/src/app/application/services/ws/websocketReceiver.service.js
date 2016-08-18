'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['$rootScope', 'pipeStatsService', 'agentService', 'viewModel', 'validationService', 'toaster', 'viewModelUpdater', 'adminGroupService', 'adminService', 'pipeConfigService', 'loginService',
        function($rootScope, pipeStatsService, agentService, viewModel, validationService, toaster, viewModelUpdater, adminGroupService, adminService, pipeConfigService, loginService) {
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
                    changeUserPassword: function(object) {
                        if (object.error == false) {
                            viewModelUpdater.updateUser(object.result);
                            toaster.pop('success', "Notification", "User updated!");
                        } else {
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    delete: function (object) {
                      validationService.flowNoParameters(object,adminService.getAllUsers);
                    },
                    assignUserToGroup: function(object) {
                        viewModelUpdater.updateUser(object.result);
                        adminService.getAllUserGroupDTOs();
                    },
                    logout: function(object) {           
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
                      validationService.flowNoParameters(object,adminService.getAllUserGroupDTOs);
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
                    add: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addAgent);
                    },
                    update: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updateAgent);
                    },
                    delete: function (object) {
                      validationService.flowNoParameters(object,agentService.getAllAgents);
                    }
                },
                PipelineGroupService: {
                    getAll: function(object) {
                        //viewModelUpdater.getAllPipelineGroups(pipelineGroups);
                        //viewModelUpdater.updatePipelineGroupDTOs(pipelineGroups);
                    },
                    getAllPipelineGroupDTOs: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updatePipelineGroupDTOs);
                    },
                    add: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addPipelineGroup);
                    },
                    update: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updatePipelineGroup);
                    },
                    delete: function (object) {
                      validationService.flowNoParameters(object,agentService.getAllPipelineGroupDTOs);
                    }
                },
                PipelineDefinitionService: {
                    getAll: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.getAllPipelineDefinitions);
                    },
                    add: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addPipelineDefinition);
                    },
                    update: function(object) {
                        if (object.error == false) {
                            viewModelUpdater.updatePipelineDefinition(object.result);
                            pipeConfigService.getAllPipelineGroupDTOs();
                            pipeConfigService.getAllPipelineDefinitions();
                        } else {
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    delete: function(object) {
                        if (object.error == false) {
                            pipeConfigService.getAllPipelineGroupDTOs();
                            pipeConfigService.getAllPipelineDefinitions();
                        } else {
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    assignPipelineToGroup: function(object) {
                        if (object.error == false) {
                            viewModelUpdater.updatePipelineDefinition(object.result);
                            pipeConfigService.getAllPipelineGroupDTOs();
                            pipeConfigService.getAllPipelineDefinitions();
                        } else {
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    unassignPipelineFromGroup: function(object) {
                        if (object.error == false) {
                            viewModelUpdater.updatePipelineDefinition(object.result);
                            pipeConfigService.getAllPipelineGroupDTOs();
                            pipeConfigService.getAllPipelineDefinitions();
                        } else {
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
                    add: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.addPipeline(object.result);
                            toaster.pop('success', "Notification", object.errorMessage);
                        } else {
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    update: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updatePipeline);
                    },
                    delete: function(object) {
                        if (object.error == false) {

                        } else {
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                MaterialDefinitionService: {
                    getAll: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.getAllMaterialDefinitions);
                    },
                    add: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addMaterialDefinition);
                    },
                    update: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updateMaterialDefinition);
                      pipeConfigService.getAllMaterialDefinitions();
                    },
                    delete: function(object) {
                        if (object.error == false) {
                            pipeConfigService.getAllPipelineDefinitions();
                            pipeConfigService.getAllPipelineGroupDTOs();
                            pipeConfigService.getAllMaterialDefinitions();
                        }
                        else{
                          debugger;
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                StageDefinitionService: {
                    getAll: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.getAllStageDefinitions);
                    },
                    add: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addStageDefinition);
                    },
                    update: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updateStageDefinition);
                    },
                    delete: function(object) {
                        if (object.error == false) {
                            pipeConfigService.getAllPipelineDefinitions();
                            pipeConfigService.getAllPipelineGroupDTOs();
                        } else {
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                JobDefinitionService: {
                    getAll: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.getAllJobDefinitions);
                    },
                    add: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addJobDefinition);
                    },
                    update: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updateJobDefinition);
                    },
                    delete: function(object) {
                        if (object.error == false) {
                            pipeConfigService.getAllPipelineDefinitions();
                            pipeConfigService.getAllPipelineGroupDTOs();
                        } else {
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                TaskDefinitionService: {
                    getAll: function(object) {
                        //viewModelUpdater.getAllTaskDefinitions(object.result);
                    },
                    add: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.addTaskDefinition);
                    },
                    update: function (object) {
                      validationService.dispatcherFlow(object,viewModelUpdater.updateTaskDefinition);
                    },
                    delete: function(object) {
                        if (object.error == false) {
                            pipeConfigService.getAllPipelineDefinitions();
                            pipeConfigService.getAllPipelineGroupDTOs();
                        } else {
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                }
            };

            return webSocketReceiverService;

        }
    ]);

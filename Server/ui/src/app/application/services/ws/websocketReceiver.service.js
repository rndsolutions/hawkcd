'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['$rootScope', 'pipeStatsService', 'agentService', 'viewModel', 'validationService', 'toaster', 'viewModelUpdater', 'adminGroupService', 'adminService', 'pipeConfigService',
        function ($rootScope, pipeStatsService, agentService, viewModel, validationService, toaster, viewModelUpdater, adminGroupService, adminService, pipeConfigService) {
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
                        viewModelUpdater.getUser(object.result);
                    }
                },
                UserService: {
                    getAll: function (object) {
                        viewModelUpdater.getUsers(object.result);
                    },
                    addUserWithoutProvider: function (object) {
                        viewModelUpdater.addUser(object.result);
                    },
                    update: function (object) {
                        viewModelUpdater.updateUser(object.result);
                    },
                    delete: function (object) {
                        if (object.error == false) {
                            adminService.getAllUsers();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                UserGroupService: {
                    getAll: function (object) {
                        viewModelUpdater.getUserGroups(object.result);
                    },
                    getAllUserGroups: function (object) {
                        viewModelUpdater.getUserGroupDTOs(object.result);
                    },
                    add: function (object) {
                        viewModelUpdater.addUserGroup(object.result);
                    }
                },
                AuthorizationService: {
                    getAll: function (object) {
                        viewModelUpdater.getPermissions(object.result);
                    },
                    updatePermission: function (object) {
                        viewModelUpdater.updatePermissions(object.result);
                    }
                },
                AgentService: {
                    getAll: function (object) {
                        viewModelUpdater.updateAgents(object.result);
                    },
                    getById: function (object) {
                        viewModelUpdater.updateAgent(object.result);
                    },
                    add: function (object) {
                        viewModelUpdater.addAgent(object.result);
                    },
                    update: function (object) {

                        if(object.error == false) {
                            viewModelUpdater.updateAgent(object.result);
                        } else {
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    delete: function (object) {
                        if (object.error == false) {
                            agentService.getAllAgents();
                        }
                        else{

                        }
                    }
                },
                PipelineGroupService: {
                    getAll: function (object) {
                        //viewModelUpdater.getAllPipelineGroups(pipelineGroups);
                        //viewModelUpdater.updatePipelineGroupDTOs(pipelineGroups);
                    },
                    getAllPipelineGroupDTOs: function (object) {
                        viewModelUpdater.updatePipelineGroupDTOs(object.result);
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        viewModelUpdater.addPipelineGroup(object.result);
                    },
                    update: function (object) {
                        viewModelUpdater.updatePipelineGroup(object.result);
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
                        viewModelUpdater.getAllPipelineDefinitions(object.result);
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        viewModelUpdater.addPipelineDefinition(object.result);
                    },
                    update: function (object) {
                        viewModelUpdater.updatePipelineDefinition(object.result);
                        pipeConfigService.getAllPipelineGroupDTOs();
                        pipeConfigService.getAllPipelineDefinitions();
                    },
                    delete: function (object) {
                        if (object.error == false) {
                            pipeConfigService.getAllPipelineDefinitions();
                            pipeConfigService.getAllPipelineGroupDTOs();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    assignPipelineToGroup: function (object) {
                        viewModelUpdater.updatePipelineDefinition(object.result);
                        pipeConfigService.getAllPipelineGroupDTOs();
                        pipeConfigService.getAllPipelineDefinitions();
                    },
                    unassignPipelineFromGroup: function (object) {
                        viewModelUpdater.updatePipelineDefinition(object.result);
                        pipeConfigService.getAllPipelineGroupDTOs();
                        pipeConfigService.getAllPipelineDefinitions();
                    }
                },
                PipelineService: {
                    getAll: function (object) {
                        viewModelUpdater.getAllPipelines(object.result);
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        viewModelUpdater.addPipeline(object.result);
                        if(object.error == false) {
                        toaster.pop('success', "Notification", object.errorMessage);
                        } else {
                        toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    update: function (object) {
                        viewModelUpdater.updatePipeline(object.result);
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
                        viewModelUpdater.getAllMaterialDefinitions(object.result);
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        viewModelUpdater.addMaterialDefinition(object.result);
                    },
                    update: function (object) {
                        viewModelUpdater.updateMaterialDefinition(object.result);
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
                        viewModelUpdater.getAllStageDefinitions(object.result);
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        viewModelUpdater.addStageDefinition(object.result);
                    },
                    update: function (object) {
                        viewModelUpdater.updateStageDefinition(object.result);
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
                        viewModelUpdater.getAllJobDefinitions(object.result);
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        viewModelUpdater.addJobDefinition(object.result);
                    },
                    update: function (object) {
                        viewModelUpdater.updateJobDefinition(object.result);
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
                        viewModelUpdater.addTaskDefinition(object.result);
                    },
                    update: function (object) {
                        viewModelUpdater.updateTaskDefinition(object.result);
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

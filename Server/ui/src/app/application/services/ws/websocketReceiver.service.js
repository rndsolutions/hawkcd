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
                        if(object.error == false) {
                            viewModelUpdater.getUser(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                UserService: {
                    getAll: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.getUsers(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    addUserWithoutProvider: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.addUser(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    update: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updateUser(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
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
                    }
                },
                UserGroupService: {
                    getAll: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.getUserGroups(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    getAllUserGroups: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.getUserGroupDTOs(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    add: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.addUserGroup(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    updateUserGroupDto: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updateUserGroup(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    assignUserToGroup: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updateUserGroup(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    unassignUserFromGroup: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updateUserGroup(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
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
                        if(object.error == false) {
                            viewModelUpdater.getPermissions(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    updatePermission: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updatePermissions(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    }
                },
                AgentService: {
                    getAll: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updateAgents(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    getById: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updateAgent(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    add: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.addAgent(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
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
                        if(object.error == false) {
                            viewModelUpdater.updatePipelineGroupDTOs(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.addPipelineGroup(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    update: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.updatePipelineGroup(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
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
                        if(object.error == false) {
                            viewModelUpdater.getAllPipelineDefinitions(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.addPipelineDefinition(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
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
                    }
                },
                PipelineService: {
                    getAll: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.getAllPipelines(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
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
                        if(object.error == false) {
                            viewModelUpdater.updatePipeline(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
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
                        if(object.error == false) {
                            viewModelUpdater.getAllMaterialDefinitions(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        if(object.error == false) {
                            viewModelUpdater.addMaterialDefinition(object.result);
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
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

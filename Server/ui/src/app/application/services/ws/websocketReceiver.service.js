/* Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['$rootScope', 'agentService', 'viewModel', 'validationService', 'toaster', 'viewModelUpdater', 'adminGroupService', 'adminService', 'loggerService', 'pipeConfigService', 'loginService', 'pipeExecService', 'agentUpdater', 'jobDefinitionUpdater', 'loggedUserUpdater', 'materialDefinitionUpdater', 'pipelineDefinitionUpdater', 'pipelineGroupUpdater', 'pipelineUpdater', 'stageDefinitionUpdater', 'taskDefinitionUpdater', 'userGroupUpdater', 'userUpdater',
        function($rootScope, agentService, viewModel, validationService, toaster, viewModelUpdater, adminGroupService, adminService, loggerService, pipeConfigService, loginService, pipeExecService, agentUpdater, jobDefinitionUpdater, loggedUserUpdater, materialDefinitionUpdater, pipelineDefinitionUpdater, pipelineGroupUpdater, pipelineUpdater, stageDefinitionUpdater, taskDefinitionUpdater, userGroupUpdater, userUpdater) {
            var webSocketReceiverService = this;

            webSocketReceiverService.processEvent = function(data) {


                loggerService.log('Received JSON from Server:');
                loggerService.log(data);
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
                        validationService.dispatcherFlow(object, [loggedUserUpdater.getUser]);
                    },
                    logoutSession: function(object) {
                        validationService.dispatcherFlow(object, [loginService.logout], true);
                    }
                },
                UserService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [userUpdater.getUsers]);
                    },
                    addUserWithoutProvider: function(object) {
                        validationService.dispatcherFlow(object, [userUpdater.addUser], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [userUpdater.updateUser], true);
                    },
                    changeUserPassword: function(object) {
                        validationService.dispatcherFlow(object, [userUpdater.updateUser], true)
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [adminService.getAllUsers], true);
                    },
                    resetUserPassword:function(object) {
                    },
                    logout: function(object) {
                        validationService.dispatcherFlow(object, [loginService.logoutUser])
                    }
                },
                UserUpdaterService: {
                    assignUsers: function(object) {

                    },
                    updateUserPermissions: function(object) {
                        validationService.dispatcherFlow(object, [userUpdater.updateUser], true);
                    },
                    updateUserGroupPermissions: function(object) {
                        validationService.dispatcherFlow(object, [userGroupUpdater.updateUserGroupPermissions], true);
                    }
                },
                UserGroupService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [userGroupUpdater.getUserGroups]);
                    },
                    getAllUserGroups: function(object) {
                        validationService.dispatcherFlow(object, [userGroupUpdater.getUserGroupDTOs]);
                    },
                    addUserGroupDto: function(object) {
                        validationService.dispatcherFlow(object, [userGroupUpdater.addUserGroup], true);
                    },
                    updateUserGroupDto: function(object) {
                        validationService.dispatcherFlow(object, [userGroupUpdater.updateUserGroup], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [userGroupUpdater.updateUserGroupWithoutUsers], true);
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [adminService.getAllUserGroupDTOs], true);
                    },
                    assignUsers: function(object) {
                        validationService.dispatcherFlow(object, [userGroupUpdater.assignUsers], true);
                    }
                },
                AgentService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [agentUpdater.updateAgents]);
                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [agentUpdater.addAgent], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [agentUpdater.updateAgent]);
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
                        validationService.dispatcherFlow(object, [pipelineGroupUpdater.updatePipelineGroupDTOs]);
                    },
                    getById: function(object) {

                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [pipelineGroupUpdater.addPipelineGroup], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [pipelineGroupUpdater.updatePipelineGroup], true);
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [pipeConfigService.getAllPipelineGroupDTOs], true);
                    }
                },
                PipelineDefinitionService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [pipelineDefinitionUpdater.getAllPipelineDefinitions]);
                    },
                    getById: function(object) {

                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [pipelineDefinitionUpdater.addPipelineDefinition, pipeConfigService.getAllPipelineGroupDTOs], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [pipelineDefinitionUpdater.updatePipelineDefinition, pipeConfigService.getAllPipelineGroupDTOs], true);
                        // pipeConfigService.getAllPipelineGroupDTOs();
                        // pipeConfigService.getAllPipelineDefinitions();
                    },
                    delete: function(object) {
                        var criteria = '';
                        validationService.dispatcherFlow(object, [pipeConfigService.getAllPipelineDefinitions, pipeConfigService.getAllPipelineGroupDTOs]);
                        for(var i = 0; i < viewModel.artifactPipelines.length - 1; i++){
                            if(viewModel.artifactPipelines[i] && viewModel.artifactPipelines[i].searchCriteria){
                                criteria = angular.copy(viewModel.artifactPipelines[i].searchCriteria);
                                break;
                            }
                        }

                        pipeExecService.getAllArtifactPipelines(criteria, viewModel.artifactPipelines.length, '');
                    },
                    assignPipelineToGroup: function(object) {
                        validationService.dispatcherFlow(object, [pipelineDefinitionUpdater.updatePipelineDefinition, pipeConfigService.getAllPipelineGroupDTOs, pipeConfigService.getAllPipelineDefinitions], true);
                    },
                    unassignPipelineFromGroup: function(object) {
                        validationService.dispatcherFlow(object, [pipelineDefinitionUpdater.updatePipelineDefinition, pipeConfigService.getAllPipelineGroupDTOs, pipeConfigService.getAllPipelineDefinitions], true);
                    },
                    addWithMaterialDefinition: function(object) {
                        validationService.dispatcherFlow(object, [pipelineDefinitionUpdater.addPipelineDefinition, pipeConfigService.getAllPipelineGroupDTOs], true);
                    }
                },
                PipelineService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [pipelineUpdater.getAllPipelines]);
                    },
                    getById: function (object) {
                        validationService.dispatcherFlow(object, [pipelineUpdater.getRunManagementPipeline])
                    },
                    getAllPipelineHistoryDTOs: function (object) {
                        validationService.dispatcherFlow(object, [pipelineUpdater.getAllHistoryPipelines]);
                    },
                    getAllPipelineArtifactDTOs: function (object) {
                        if(object.result[0]){
                            if(object.result[0].isScrollCall){
                                for(var i = 0; i < viewModel.artifactPipelines.length - 1; i++){
                                    if(viewModel.artifactPipelines[i].searchCriteria){
                                        object.result[0].searchCriteria = viewModel.artifactPipelines[i].searchCriteria;
                                        break;
                                    }
                                }
                            } else {
                                viewModel.artifactPipelines = [];
                            }
                        }
                        // viewModel.artifactPipelines = [];
                        validationService.dispatcherFlow(object, [pipelineUpdater.getAllArtifactPipelines]);
                    },
                    add: function(object) {
                        var criteria = '';
                        validationService.dispatcherFlow(object, [pipelineUpdater.addPipeline], true);
                        for(var i = 0; i < viewModel.artifactPipelines.length - 1; i++){
                            if(viewModel.artifactPipelines[i].searchCriteria){
                                criteria = viewModel.artifactPipelines[i].searchCriteria;
                                break;
                            }
                        }
                        pipeExecService.getAllArtifactPipelines(criteria, viewModel.artifactPipelines.length, '');
                    },
                    pausePipeline: function(object) {
                        validationService.dispatcherFlow(object, []);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [pipelineUpdater.updatePipeline]);
                    },
                    delete: function(object) {

                    }
                },
                MaterialDefinitionService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [materialDefinitionUpdater.getAllMaterialDefinitions]);
                    },
                    getById: function(object) {

                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [materialDefinitionUpdater.addMaterialDefinition], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [materialDefinitionUpdater.updateMaterialDefinition], true);
                        // pipeConfigService.getAllMaterialDefinitions();
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [pipeConfigService.getAllPipelineDefinitions, pipeConfigService.getAllPipelineGroupDTOs, pipeConfigService.getAllMaterialDefinitions], true);
                    }
                },
                StageDefinitionService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [stageDefinitionUpdater.getAllStageDefinitions]);
                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [stageDefinitionUpdater.addStageDefinition], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [stageDefinitionUpdater.updateStageDefinition], true);
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [pipeConfigService.getAllPipelineDefinitions, pipeConfigService.getAllPipelineGroupDTOs], true);
                    }
                },
                JobDefinitionService: {
                    getAll: function(object) {
                        validationService.dispatcherFlow(object, [jobDefinitionUpdater.getAllJobDefinitions]);
                    },
                    add: function(object) {
                        validationService.dispatcherFlow(object, [jobDefinitionUpdater.addJobDefinition], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [jobDefinitionUpdater.updateJobDefinition], true);
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
                        validationService.dispatcherFlow(object, [taskDefinitionUpdater.addTaskDefinition], true);
                    },
                    update: function(object) {
                        validationService.dispatcherFlow(object, [taskDefinitionUpdater.updateTaskDefinition], true);
                    },
                    delete: function(object) {
                        validationService.dispatcherFlow(object, [pipeConfigService.getAllPipelineDefinitions, pipeConfigService.getAllPipelineGroupDTOs], true);
                    }
                }
            };

            return webSocketReceiverService;

        }
    ]);

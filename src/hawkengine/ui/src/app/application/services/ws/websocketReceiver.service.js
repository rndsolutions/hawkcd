'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['$rootScope', 'pipeStatsService', 'agentService', 'viewModel', 'validationService', 'toaster', 'viewModelUpdater', 'adminGroupService', 'pipeConfigService',
        function ($rootScope, pipeStatsService, agentService, viewModel, validationService, toaster, viewModelUpdater, adminGroupService, pipeConfigService) {
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
                AgentService: {
                    getAll: function (object) {
                        viewModelUpdater.updateAgents(object.result);
                    },
                    getById: function (object) {
                        viewModelUpdater.updateAgent(object.result);
                    },
                    update: function (object) {
                        viewModelUpdater.updateAgent(object.result);
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

                    },
                    delete: function (object) {
                        if (object.error == false) {
                            pipeConfigService.getAllPipelineDefinitions();
                            pipeConfigService.getAllPipelineGroupDTOs();
                        }
                        else{
                            toaster.pop('error', "Notification", object.errorMessage);
                        }
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
                StageDefinitionService: {
                    getAll:function (object) {
                        viewModelUpdater.getAllStageDefinitions(object.result);
                    },
                    getById: function (object) {

                    },
                    add: function (object) {
                        viewModelUpdater.addStageDefinition(object.result);
                    },
                    update: function (object) {

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
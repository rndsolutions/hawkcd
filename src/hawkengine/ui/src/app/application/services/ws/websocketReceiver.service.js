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
                dispatcher[className][methodName](obj.result);
            };

            var dispatcher = {
                AgentService: {
                    getAll: function (agents) {
                        viewModelUpdater.updateAgents(agents);
                    },
                    getById: function (agent) {
                        viewModelUpdater.updateAgent(agent);
                    },
                    update: function (agent) {
                        viewModelUpdater.updateAgent(agent);
                    },
                    delete: function (isDeleted) {
                        if (isDeleted) {
                            agentService.getAllAgents();
                        }
                    }
                },
                PipelineGroupService: {
                    getAll: function (pipelineGroups) {
                        //viewModelUpdater.getAllPipelineGroups(pipelineGroups);
                        //viewModelUpdater.updatePipelineGroupDTOs(pipelineGroups);
                    },
                    getAllPipelineGroupDTOs: function (pipelineGroups) {
                        viewModelUpdater.updatePipelineGroupDTOs(pipelineGroups);
                    },
                    getById: function (pipelineGroup) {

                    },
                    add: function (pipelineGroup) {
                        viewModelUpdater.updatePipelineGroup(pipelineGroup);
                    },
                    update: function (pipelineGroup) {

                    },
                    delete: function (isDeleted) {
                        if (isDeleted) {
                            adminGroupService.getAllPipelineGroups();
                        }
                    }
                },
                PipelineDefinitionService: {
                    getAll: function (pipelines) {
                        viewModelUpdater.getAllPipelineDefinitions(pipelines);
                    },
                    getById: function (pipeline) {

                    },
                    add: function (pipeline) {
                        viewModelUpdater.addPipelineDefinition(pipeline);
                    },
                    update: function (pipeline) {

                    },
                    delete: function (isDeleted) {
                        if (isDeleted) {
                            pipeConfigService.getAllPipelineGroupDTOs();
                        }
                    }
                },
                StageDefinitionService: {
                    getAll:function (stages) {
                        viewModelUpdater.getAllStageDefinitions(stages);
                    },
                    getById: function (stage) {

                    },
                    add: function (stage) {

                    },
                    update: function (stage) {

                    },
                    delete: function (isDeleted) {
                        if(isDeleted) {

                        }
                    }
                }
            };

            return webSocketReceiverService;

        }]);
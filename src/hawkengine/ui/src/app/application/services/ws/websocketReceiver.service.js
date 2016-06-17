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
                    getById: function (agent) {
                        viewModelUpdater.updateAgent(agent);
                    },
                    getAll: function (agents) {
                        viewModelUpdater.updateAgents(agents);
                    },
                    setAgentConfigState: function (data) {
                        viewModel.updateAgentStatus(data);
                    },
                    delete: function (isDeleted) {
                        if (isDeleted) {
                            agentService.getAllAgents();
                        }
                    },
                    update: function (agent) {
                        viewModelUpdater.updateAgent(agent);
                    }
                },
                PipelineGroupService: {
                    add: function (pipelineGroup) {
                        viewModelUpdater.updatePipelineGroup(pipelineGroup);
                    },
                    getAll: function (pipelineGroups) {
                        viewModelUpdater.updatePipelineGroups(pipelineGroups);
                    },
                    delete: function (isDeleted) {
                        if (isDeleted) {
                            adminGroupService.getAllPipelineGroups();
                        }
                    },
                    getAllPipelineGroupDTOs: function (pipelineGroups) {
                        viewModelUpdater.updatePipelineGroupDTOs(pipelineGroups);
                    }
                },
                PipelineDefinitionService: {
                    add: function (pipeline) {
                        viewModelUpdater.addPipelineDefinition(pipeline);
                    },
                    getAll: function (pipelines) {
                        viewModelUpdater.updatePipelineDefinitions(pipelines);
                    },
                    delete: function (isDeleted) {
                        if (isDeleted) {
                            pipeConfigService.getAllPipelineGroupDTOs();
                        }
                    }
                }
            };

            return webSocketReceiverService;

        }]);
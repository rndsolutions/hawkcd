'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['$rootScope', 'pipeStatsService', 'agentService', 'viewModel', 'validationService', 'toaster', 'viewModelUpdater', 'adminGroupService',
        function ($rootScope, pipeStatsService, agentService, viewModel, validationService, toaster, viewModelUpdater, adminGroupService) {
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
                    }
                }
            };

            return webSocketReceiverService;

        }]);
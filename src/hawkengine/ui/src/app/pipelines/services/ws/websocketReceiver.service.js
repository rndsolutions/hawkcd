'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['pipeStatsService', 'agentService', 'viewModel', 'validationService',
        function (pipeStatsService, agentService, viewModel, validationService) {
            var webSocketReceiverService = this;

            webSocketReceiverService.processEvent = function (data) {
                invoker(data, dispatcher);
            };

            var invoker = function (obj, dispatcher) {
                validationService.validateNull(obj);
                validationService.validateResult(obj);
                var className = obj['className'];
                var methodName = obj['methodName'];
                dispatcher[className][methodName](obj.result);
            };

            var dispatcher = {
                AgentService: {
                    getById: function (data) {
                        agentService.updatewsAgent(data);
                    },
                    getAll: function (data) {
                        viewModel.updateAgents(data);
                    },
                    setAgentConfigState: function (data) {
                        viewModel.updateAgentStatus(data);
                    }
                }
            };

            return webSocketReceiverService;

        }]);
'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['pipeStatsService', 'agentService', 'viewModel', 'validationService', 'toaster',
        function (pipeStatsService, agentService, viewModel, validationService, toaster) {
            var webSocketReceiverService = this;

            webSocketReceiverService.processEvent = function (data) {
                if(!validationService.isValid(data)){
                    toaster.error('Invalid JSON format!');
                    return;
                }

                invoker(data,dispatcher);
            };

            var invoker = function (obj, dispatcher) {
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
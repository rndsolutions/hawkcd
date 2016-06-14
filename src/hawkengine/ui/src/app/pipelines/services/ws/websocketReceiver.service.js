'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['pipeStatsService', 'agentService', 'viewModel', function (pipeStatsService, agentService, viewModel) {
        var webSocketReceiverService = this;

        webSocketReceiverService.processEvent = function (data) {
            invoker(data, dispatcher);
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
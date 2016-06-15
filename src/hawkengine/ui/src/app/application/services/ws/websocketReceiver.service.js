'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['pipeStatsService', 'agentService', 'viewModel', 'validationService', 'toaster', 'viewModelUpdater',
        function (pipeStatsService, agentService, viewModel, validationService, toaster, viewModelUpdater) {
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
                    getById: function (agent) {
                        viewModelUpdater.updateAgent(agent);
                    },
                    getAll: function (agents) {
                        viewModelUpdater.updateAgents(agents);
                    },
                    setAgentConfigState: function (data) {
                        viewModel.updateAgentStatus(data);
                    },
                    delete: function (isDeleted){
                        if(isDeleted){
                            agentService.getAllAgents();
                        }
                    },
                    update: function (agent) {
                        viewModelUpdater.updateAgent(agent);
                    }
                }
            };

            return webSocketReceiverService;

        }]);
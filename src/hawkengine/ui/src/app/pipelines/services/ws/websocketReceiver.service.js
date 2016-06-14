'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['pipeStatsService', 'agentService', 'viewModel', 'toaster', 'invokerService',
        function (pipeStatsService, agentService, viewModel, toaster, invokerService) {
            var websocketReceiverService = this;

            this.processEvent = function (data) {
                console.log(data);
                invokerService(data, dispatcher);
            };

            var dispatcher = {
                AgentService: {
                    getById: function (data) {
                        agentService.updatewsAgent(data)
                    },
                    getAll: function (data) {
                        viewModel.updateAgents(data);
                    },
                    setAgentConfigState: function (data) {
                        viewModel.updateAgentStatus(data);
                    }
                },
                Pipeline: {
                    getById: function () {
                        console.log('Pipeline : getAll');
                    },
                    getAll: function () {
                        console.log('Pipeline : getAll');
                    }
                }
            };

            return websocketReceiverService;

        }]);


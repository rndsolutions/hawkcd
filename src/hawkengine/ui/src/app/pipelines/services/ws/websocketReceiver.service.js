'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketReceiverService', ['pipeStatsService', 'agentService', 'viewModel', function (pipeStatsService, agentService, viewModel) {
        var websocketReceiverService = this;

        this.processEvent = function (data) {

            switch (data.methodName){
                case "GetAllPipelines":
                    pipeConfigService.updatePipelines(data);
                    break;
                case "GetAllPipelineDefs":
                    break;
                case "getAgentById":
                    agentService.updatewsAgent(data);
                    console.log(data);
                    break;
                case "getAllAgents":
                    viewModel.updateAgents(data);
                    console.log(data);
                    break;
                case "setAgentConfigState":
                    viewModel.updateAgentStatus(data);
                    break;
                case "getAllPipelineGroups":
                    viewModel.updatePipelineGroups(data);
                    break;
            }
        };

        return websocketReceiverService;

    }]);
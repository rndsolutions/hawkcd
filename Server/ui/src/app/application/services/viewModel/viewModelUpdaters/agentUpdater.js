'use strict';

angular
    .module('hawk')
    .factory('agentUpdater', ['viewModel', function (viewModel) {
        var agentUpdater = this;

        agentUpdater.updateAgents = function (agents) {
            viewModel.allAgents = agents;
        };

        agentUpdater.addAgent = function (agent) {
            viewModel.allAgents.push(agent);
        };

        agentUpdater.updateAgent = function (agent) {
            viewModel.allAgents.forEach(function (currentAgent, index, array) {
                if (currentAgent.id == agent.id) {
                    viewModel.allAgents[index] = agent;
                }
            })
        };

        return agentUpdater;
    }]);

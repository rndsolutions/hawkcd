'use strict';

angular
    .module('hawk')
    .factory('viewModelUpdater', ['viewModel', 'toaster', function (viewModel, toaster) {
        var viewModelUpdater = this;

        viewModelUpdater.updateAgents = function (agents) {
            viewModel.allAgents = agents;
            toaster.pop('success', "Notification", "Agents updated!");
        };

        viewModelUpdater.updateAgent = function (agent) {
            viewModel.allAgents.forEach(function (currentAgent, index, array) {
                if (currentAgent.id == agent.id) {
                    array[index] = agent;
                    toaster.pop('success', "Notification", "Agent " + agent.hostName + "-" + agent.id.substr(0, 8) + " updated!");
                }
            })
        };

        viewModelUpdater.updatePipelineGroup = function (pipelineGroup) {
            viewModel.allPipelineGroups.push(pipelineGroup);
        }

        viewModelUpdater.updatePipelineGroups = function (pipelineGroups) {
            viewModel.allPipelineGroups = pipelineGroups;
            toaster.pop('success', 'Notification', 'Pipeline Groups updated!');
        }

        return viewModelUpdater;
    }]);
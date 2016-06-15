'use strict';

angular
    .module('hawk')
    .factory('viewModelUpdater', ['viewModel', 'toaster', function (viewModel, toaster) {
        var viewModelUpdater = this;

        viewModelUpdater.updateAgents = function (agents){
            viewModel.allAgents = agents;
            toaster.pop('success', "Notification", "Agents updated!");
        };

        viewModelUpdater.updateAgent = function (agent){
            viewModel.allAgents.forEach(function(currentAgent, index, array){
                if(currentAgent.id == agent.id){
                    array[index] = agent;
                    toaster.pop('success', "Notification", "Agent " + agent.hostName + "-" + agent.id.substr(0,8) + " updated!");
                }
            })
        };

        return viewModelUpdater;
    }]);
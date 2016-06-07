'use strict';

angular
    .module('hawk')
    .factory('viewModel', ['$rootScope', 'agentService', 'toaster', function ($rootScope, agentService, toaster) {
        var viewModel = this;

        viewModel.isAgentFirstRun = true;

        viewModel.allAgents = {};

        viewModel.allPipelines = {};

        viewModel.allMaterials = {};

        viewModel.init = function() {
            viewModel.getAllAgents();
            viewModel.getAllPipelines();
            viewModel.getAllMaterials();
            agentService.getAllAgents();
        };

        //region Getters

        viewModel.getAllAgents = function() {

        };

        viewModel.getAllPipelines = function() {

        };

        viewModel.getAllMaterials = function() {

        };

        //endregion

        //region Updaters

        viewModel.updateAgents = function(object) {
            viewModel.allAgents = object.result;
            toaster.pop('success', "Notification", "Agents updated!");
        };

        viewModel.updateAgentStatus = function(data){
            findAndReplaceState(viewModel.allAgents, data.result.id, data.result.configState);
        };

        function findAndReplaceState(object, id, newConfigState) {
            object.map(function (a) {
                if (a.id == id) {
                    a.configState = newConfigState;
                    var text = "";
                    if(newConfigState == 'Enabled'){
                        text = " enabled!";
                    }
                    else{
                        text = " disabled!";
                    }
                    toaster.pop('success', "Notification", "Agent " + a.hostName + "-" + a.id.substr(0,8) + text);
                }
            })
        }

        // $rootScope.$on('updateAgents', function(event, args) {
        //     var object = args.object;
        //     viewModel.currentAgents = object;
        //     console.log(object);
        // });

        //endregion

        return viewModel;
    }]);
'use strict';

angular
    .module('hawk')
    .factory('viewModel', ['$rootScope', 'agentService', 'toaster', function ($rootScope, agentService, toaster) {
        var viewModel = this;

        viewModel.isAgentFirstRun = true;

        viewModel.allAgents = {};

        viewModel.allPipelines = {};

        viewModel.allMaterials = {};

        viewModel.allPipelineGroups = [];

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
            viewModel.allAgents = object;
            toaster.pop('success', "Notification", "Agents updated!");
        };

        viewModel.updatePipelineGroups = function(object) {
            viewModel.allPipelineGroups = object.result;
        };

        // $rootScope.$on('updateAgents', function(event, args) {
        //     var object = args.object;
        //     viewModel.currentAgents = object;
        //     console.log(object);
        // });

        //endregion

        return viewModel;
    }]);
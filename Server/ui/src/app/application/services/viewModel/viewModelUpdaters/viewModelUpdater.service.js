'use strict';

angular
    .module('hawk')
    .factory('viewModelUpdater', ['viewModel', function (viewModel) {
        var viewModelUpdater = this;

        viewModelUpdater.flushViewModel = function () {
            viewModel.allAgents = [];
            viewModel.user = {};
            viewModel.allPipelines = [];
            viewModel.assignedPipelines = [];
            viewModel.unassignedPipelines = [];
            viewModel.allMaterials = [];
            viewModel.userGroups = [];
            viewModel.users = [];
            viewModel.allMaterialDefinitions = [];
            viewModel.allPipelineGroups = [];
            viewModel.allPipelineRuns = [];
        };

        return viewModelUpdater;
    }]);

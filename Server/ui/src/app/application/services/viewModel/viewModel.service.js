'use strict';

angular
    .module('hawk')
    .factory('viewModel', [function () {
        var viewModel = this;

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

        return viewModel;
    }]);
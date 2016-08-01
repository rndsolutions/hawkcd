'use strict';

angular
    .module('hawk')
    .factory('viewModel', [function () {
        var viewModel = this;

        viewModel.allAgents = [];

        viewModel.allPipelines = [];

        viewModel.assignedPipelines = [];

        viewModel.unassignedPipelines = [];

        viewModel.allMaterials = [];

        viewModel.allMaterialDefinitions = [];

        viewModel.allPipelineGroups = [];
            
        viewModel.allPipelineRuns = [];

        return viewModel;
    }]);
'use strict';

angular
    .module('hawk')
    .factory('viewModel', [function () {
        var viewModel = this;

        viewModel.allAgents = [];

        viewModel.allPipelines = [];

        viewModel.allMaterials = [];

        viewModel.allPipelineGroups = [];
            
        viewModel.allPipelineRuns = [];

        return viewModel;
    }]);
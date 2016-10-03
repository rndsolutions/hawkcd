'use strict';

angular
    .module('hawk')
    .factory('pipelineGroupUpdater', ['viewModel', function (viewModel) {
        var pipelineGroupUpdater = this;

        pipelineGroupUpdater.updatePipelineGroupDTOs = function (pipelineGroupDTOs) {
            viewModel.allPipelineGroups = pipelineGroupDTOs;
        };

        pipelineGroupUpdater.addPipelineGroup = function (pipelineGroup) {
            viewModel.allPipelineGroups.push(pipelineGroup);
        };

        pipelineGroupUpdater.updatePipelineGroup = function (pipelineGroup) {
            viewModel.allPipelineGroups.forEach(function (currentPipelineGroup, index, array) {
                if (currentPipelineGroup.id == pipelineGroup.id) {
                    array[index] = pipelineGroup;
                }
            })
        };

        pipelineGroupUpdater.getAllPipelineGroups = function (pipelineGroups) {
            viewModel.allPipelineGroups = pipelineGroups;
        };

        return pipelineGroupUpdater;
    }]);

'use strict';

angular
    .module('hawk')
    .factory('stageDefinitionUpdater', ['viewModel', function (viewModel) {
        var stageDefinitionUpdater = this;

        stageDefinitionUpdater.getAllStageDefinitions = function (stageDefinitions) {
            viewModel.allStages = stageDefinitions;
        };

        stageDefinitionUpdater.addStageDefinition = function (stageDefinition) {
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if (currentPipeline.id == stageDefinition.pipelineDefinitionId) {
                    viewModel.allPipelines[index].stageDefinitions.push(stageDefinition);
                }
            });
        };

        stageDefinitionUpdater.updateStageDefinition = function (stageDefinition) {
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if(currentPipeline.id == stageDefinition.pipelineDefinitionId) {
                    viewModel.allPipelines[index].stageDefinitions.forEach(function (currentStage, stageIndex, array) {
                        if(currentStage.id == stageDefinition.id) {
                            viewModel.allPipelines[index].stageDefinitions[stageIndex] = stageDefinition;
                        }
                    });
                }
            });
        };

        return stageDefinitionUpdater;
    }]);

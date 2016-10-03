'use strict';

angular
    .module('hawk')
    .factory('jobDefinitionUpdater', ['viewModel', function (viewModel) {
        var jobDefinitionUpdater = this;

        jobDefinitionUpdater.getAllJobDefinitions = function (jobDefinitions) {
            viewModel.allJobs = jobDefinitions;
        };

        jobDefinitionUpdater.addJobDefinition = function (jobDefinition) {
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if (currentPipeline.id == jobDefinition.pipelineDefinitionId) {
                    viewModel.allPipelines[index].stageDefinitions.forEach(function (currentStage, stageIndex, stageArray) {
                        if(currentStage.id == jobDefinition.stageDefinitionId){
                            viewModel.allPipelines[index].stageDefinitions[stageIndex].jobDefinitions.push(jobDefinition);
                        }
                    });
                }
            });

        };

        jobDefinitionUpdater.updateJobDefinition = function (jobDefinition) {
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if(currentPipeline.id == jobDefinition.pipelineDefinitionId) {
                    viewModel.allPipelines[index].stageDefinitions.forEach(function (currentStage, stageIndex, stageArray) {
                        if(currentStage.id == jobDefinition.stageDefinitionId) {
                            viewModel.allPipelines[index].stageDefinitions[stageIndex].jobDefinitions.forEach(function (currentJob, jobIndex, jobArray) {
                                if(currentJob.id == jobDefinition.id) {
                                    viewModel.allPipelines[index].stageDefinitions[stageIndex].jobDefinitions[jobIndex] = jobDefinition;
                                }
                            });
                        }
                    });
                }
            });
        };

        return jobDefinitionUpdater;
    }]);

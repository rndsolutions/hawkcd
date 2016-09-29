'use strict';

angular
    .module('hawk')
    .factory('taskDefinitionUpdater', ['viewModel', function (viewModel) {
        var taskDefinitionUpdater = this;

        taskDefinitionUpdater.addTaskDefinition = function (taskDefinition) {
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if (currentPipeline.id == taskDefinition.pipelineDefinitionId) {
                    viewModel.allPipelines[index].stageDefinitions.forEach(function (currentStage, stageIndex, stageArray) {
                        if(currentStage.id == taskDefinition.stageDefinitionId){
                            viewModel.allPipelines[index].stageDefinitions[stageIndex].jobDefinitions.forEach(function (currentJob, jobIndex, array) {
                                if(currentJob.id == taskDefinition.jobDefinitionId){
                                    viewModel.allPipelines[index].stageDefinitions[stageIndex].jobDefinitions[jobIndex].taskDefinitions.push(taskDefinition);
                                }
                            });
                        }
                    });
                }
            });
        };

        taskDefinitionUpdater.updateTaskDefinition = function (taskDefinition) {
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if (currentPipeline.id == taskDefinition.pipelineDefinitionId) {
                    viewModel.allPipelines[index].stageDefinitions.forEach(function (currentStage, stageIndex, stageArray) {
                        if(currentStage.id == taskDefinition.stageDefinitionId){
                            viewModel.allPipelines[index].stageDefinitions[stageIndex].jobDefinitions.forEach(function (currentJob, jobIndex, array) {
                                if(currentJob.id == taskDefinition.jobDefinitionId){
                                    viewModel.allPipelines[index].stageDefinitions[stageIndex].jobDefinitions[jobIndex].taskDefinitions.forEach(function (currentTask, taskIndex, array) {
                                        if(currentTask.id == taskDefinition.id){
                                            viewModel.allPipelines[index].stageDefinitions[stageIndex].jobDefinitions[jobIndex].taskDefinitions[taskIndex] = taskDefinition;
                                        }
                                    });

                                }
                            });
                        }
                    });
                }
            });
        };

        return taskDefinitionUpdater;
    }]);

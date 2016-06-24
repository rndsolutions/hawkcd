'use strict';

angular
    .module('hawk')
    .factory('viewModelUpdater', ['viewModel', 'toaster', function (viewModel, toaster) {
        var viewModelUpdater = this;

        viewModelUpdater.updateAgents = function (agents) {
            viewModel.allAgents = agents;
            toaster.pop('success', "Notification", "Agents updated!");
        };

        viewModelUpdater.updateAgent = function (agent) {
            viewModel.allAgents.forEach(function (currentAgent, index, array) {
                if (currentAgent.id == agent.id) {
                    array[index] = agent;
                    toaster.pop('success', "Notification", "Agent " + agent.hostName + "-" + agent.id.substr(0, 8) + " updated!");
                }
            })
        };

        viewModelUpdater.getPipelineGroupById = function (pipelineGroup) {
            return pipelineGroup;
        };

        viewModelUpdater.addPipelineGroup = function (pipelineGroup) {
            viewModel.allPipelineGroups.push(pipelineGroup);
        };

        viewModelUpdater.updatePipelineGroup = function (pipelineGroup) {
            viewModel.allPipelineGroups.forEach(function (currentPipelineGroup, index, array) {
                if (currentPipelineGroup.id == pipelineGroup.id) {
                    array[index] = pipelineGroup;
                    toaster.pop('success', "Notification", "Pipeline Group " + pipelineGroup.name + " updated!");
                }
            })
        };

        viewModelUpdater.getAllPipelineGroups = function (pipelineGroups) {
            viewModel.allPipelineGroups = pipelineGroups;
            toaster.pop('success', 'Notification', 'Pipeline Groups updated!');
        };

        viewModelUpdater.getAllPipelineDefinitions = function (pipelineDefinitions){
            viewModel.allPipelines = pipelineDefinitions;
            toaster.pop('success', "Notification", "Pipelines updated!");
        };

        viewModelUpdater.updatePipelineGroupDTOs = function (pipelineGroupDTOs) {
            //viewModel.allPipelineDefinitions = pipelineGroupDTOs;
            viewModel.allPipelineGroups = pipelineGroupDTOs;
            toaster.pop('success', "Notification", "Pipelines updated!");
        };

        viewModelUpdater.addPipelineDefinition = function (pipelineDefinition) {
            viewModel.allPipelines.forEach(function (currentPipelineGroupDTO, index, array) {
                if(currentPipelineGroupDTO.id == pipelineDefinition.pipelineGroupId){
                    array[index].pipelines.push(pipelineDefinition);
                    toaster.pop('success', "Notification", "Pipeline Definition " + pipelineDefinition.name + " added!")
                }
            });
        };

        viewModelUpdater.updatePipelineDefinition = function (pipelineDefinition) {
            viewModel.allPipelineDefinitions.forEach(function (currentPipelineDefinition, index, array) {
                if(currentPipelineDefinition.id == pipelineDefinition.id){
                    array[index].pipelines.push(pipelineDefinition);
                    toaster.pop('success', "Notification", "Pipeline Definition " + pipelineDefinition.name + " updated!")
                }
            });
        };

        viewModelUpdater.getAllStageDefinitions = function (stageDefinitions) {
            viewModel.allStages = stageDefinitions;
            toaster.pop('success', "Notification", "Stages updated!")
        };

        viewModelUpdater.getStageDefinitionById = function (stageDefinition) {
            return stageDefinition;
        };

        viewModelUpdater.addStageDefinition = function (stageDefinition) {
            //viewModel.allStages.push(stageDefinition);
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if (currentPipeline.id == stageDefinition.pipelineDefinitionId) {
                    viewModel.allPipelines[index].stageDefinitions.push(stageDefinition);
                }
            });
            toaster.pop('success', "Notification", "Stage " + stageDefinition.name + " added!")
        };

        viewModelUpdater.updateStageDefinition = function (stageDefinition) {

        };
        
        viewModelUpdater.deleteStageDefinition = function (stageDefinition) {
            
        };
        
        viewModelUpdater.getAllJobDefinitions = function (jobDefinitions) {
            viewModel.allJobs = jobDefinitions;
            toaster.pop('success', "Notification", "Jobs updated!")
        };
        
        viewModelUpdater.getJobDefinitionById = function (jobDefinition) {
            
        };
        
        viewModelUpdater.addJobDefinition = function (jobDefinition) {
            //viewModel.allJobs.push(jobDefinition);
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if (currentPipeline.id == jobDefinition.pipelineDefinitionId) {
                    viewModel.allPipelines[index].stageDefinitions.forEach(function (currentStage, stageIndex, stageArray) {
                        if(currentStage.id == jobDefinition.stageDefinitionId){
                            viewModel.allPipelines[index].stageDefinitions[stageIndex].jobDefinitions.push(jobDefinition);
                            toaster.pop('success', "Notification", "Job " + jobDefinition.name + " added!")
                        }
                    });
                }
            });

        };
        
        viewModelUpdater.updateJobDefinition = function (jobDefinition) {
            
        };
        
        viewModelUpdater.deleteJobDefinition = function (jobDefinition) {
            
        };

        return viewModelUpdater;
    }]);
'use strict';

angular
    .module('hawk')
    .factory('viewModelUpdater', ['viewModel', 'toaster', 'adminGroupService', function (viewModel, toaster, adminGroupService) {
        var viewModelUpdater = this;

        viewModelUpdater.getUser = function (user) {
            viewModel.user = user;
            console.log(user);
        };

        viewModelUpdater.addUser = function (user) {
            viewModel.users.push(user);
        };

        viewModelUpdater.updateUser = function (user) {
            viewModel.users.forEach(function (currentUser, userIndex, userArray) {
                if(currentUser.id == user.id){
                    viewModel.users[userIndex] = user;
                }
            });
        };

        viewModelUpdater.updateUsers = function (users) {
            viewModel.users.forEach(function (currentUser, userIndex, userArray) {
                users.forEach(function (currentUpdatedUser, updatedUserIndex, updatedUserArray) {
                    if(currentUser.id == currentUpdatedUser.id) {
                        viewModel.users[userIndex] = users[updatedUserIndex];
                    }
                });
            });
        };

        viewModelUpdater.getPermissions = function (permissions) {
            viewModel.allPermissions = permissions;
        };

        viewModelUpdater.updatePermissions = function (permission) {
            //TODO: Implement this
        };

        viewModelUpdater.getUserGroups = function (userGroups) {
            viewModel.userGroups = userGroups;
            // debugger;
        };

        viewModelUpdater.getUserGroupDTOs = function (userGroups) {
            viewModel.userGroups = userGroups;
        };

        viewModelUpdater.updateUserGroup = function (userGroup) {
            viewModel.userGroups.forEach(function (currentUserGroup, userGroupIndex, userGroupArray) {
                if(currentUserGroup.id == userGroup.id){
                    viewModel.userGroups[userGroupIndex] = userGroup;
                }
            });
        };

        viewModelUpdater.getUsers = function (users) {
            viewModel.users = users;
        };

        viewModelUpdater.addUserGroup = function (userGroup) {
            viewModel.userGroups.push(userGroup);
        };

        viewModelUpdater.updateAgents = function (agents) {
            viewModel.allAgents = agents;
        };

        viewModelUpdater.addAgent = function (agent) {
            viewModel.allAgents.push(agent);
        };

        viewModelUpdater.updateAgent = function (agent) {
            viewModel.allAgents.forEach(function (currentAgent, index, array) {
                if (currentAgent.id == agent.id) {
                    viewModel.allAgents[index] = agent;
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
                }
            })
        };

        viewModelUpdater.deletePipelineGroup = function () {
            adminGroupService.getAllPipelineGroups();
        };

        viewModelUpdater.getAllPipelineGroups = function (pipelineGroups) {
            viewModel.allPipelineGroups = pipelineGroups;
        };

        viewModelUpdater.getAllPipelineDefinitions = function (pipelineDefinitions){
            viewModel.allPipelines = pipelineDefinitions;
            var isFound = false;
            viewModel.allPipelines.forEach(function (currentPipeline, pipelineIndex, pipelineArray) {
                if(currentPipeline.pipelineGroupId == ''){
                    viewModel.unassignedPipelines.forEach(function (currentUnassignedPipeline, unassignedPipelineIndex, unassignedPipelineArray) {
                        if(currentPipeline.id == currentUnassignedPipeline.id) {
                            viewModel.unassignedPipelines[unassignedPipelineIndex] = currentPipeline;
                            isFound = true;
                        }
                    });
                    if(!isFound) {
                        viewModel.unassignedPipelines.push(currentPipeline);
                        isFound = false;
                    }
                    viewModel.assignedPipelines.forEach(function (currentAssignedPipeline, assignedPipelineIndex, assignedPipelineArray) {
                        if(currentPipeline.id == currentAssignedPipeline.id) {
                            viewModel.assignedPipelines.splice(assignedPipelineIndex);
                        }
                    });
                    isFound = false;
                } else {
                    viewModel.assignedPipelines.forEach(function (currentAssignedPipeline, assignedPipelineIndex, assignedPipelineArray) {
                        if(currentPipeline.id == currentAssignedPipeline.id) {
                            viewModel.assignedPipelines[assignedPipelineIndex] = currentPipeline;
                            isFound = true;
                        }
                    });
                    if(!isFound){
                        viewModel.assignedPipelines.push(currentPipeline);
                        isFound = false;
                    }
                    viewModel.unassignedPipelines.forEach(function (currentUnassignedPipeline, unAssignedPipelineIndex, unAssignedPipelineArray) {
                        if(currentPipeline.id == currentUnassignedPipeline.id) {
                            viewModel.unassignedPipelines.splice(unAssignedPipelineIndex);
                        }
                    });
                    isFound = false;
                }
            });
            isFound = false;
        };

        viewModelUpdater.updatePipelineGroupDTOs = function (pipelineGroupDTOs) {
            //viewModel.allPipelineDefinitions = pipelineGroupDTOs;
            viewModel.allPipelineGroups = pipelineGroupDTOs;
        };

        viewModelUpdater.addPipelineDefinition = function (pipelineDefinition) {
            viewModel.allPipelineGroups.forEach(function (currentPipelineGroupDTO, index, array) {
                if(currentPipelineGroupDTO.id == pipelineDefinition.pipelineGroupId){
                    array[index].pipelines.push(pipelineDefinition);
                    viewModel.assignedPipelines.push(pipelineDefinition);
                } else if (pipelineDefinition.pipelineGroupId == '') {
                    viewModel.unassignedPipelines.push(pipelineDefinition);
                }
            });

            viewModel.allPipelines.push(pipelineDefinition);
        };

        viewModelUpdater.updatePipelineDefinition = function (pipelineDefinition) {
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if(currentPipeline.id == pipelineDefinition.id){
                    viewModel.allPipelines[index] = pipelineDefinition;
                }
            });

            viewModel.allPipelineGroups.forEach(function (currentPipelineGroupDTO, index, array) {
                if(currentPipelineGroupDTO.id == pipelineDefinition.pipelineGroupId) {
                    viewModel.allPipelineGroups[index].pipelines.forEach(function (currentPipeline, pipelineIndex, array) {
                        if(currentPipeline.id == pipelineDefinition.id) {
                            viewModel.allPipelineGroups[index].pipelines[pipelineIndex] = pipelineDefinition;
                        }
                    });
                }

            });
        };

        viewModelUpdater.getAllMaterialDefinitions = function (materialDefinitions) {
            //viewModel.allMaterials = materialDefinitions;
            viewModel.allMaterialDefinitions = materialDefinitions;
        };

        viewModelUpdater.getMaterialDefinitionById = function (materialDefinition) {
            return materialDefinition;
        };

        viewModelUpdater.addMaterialDefinition = function (materialDefinition) {
            viewModel.allMaterialDefinitions.push(materialDefinition);
        };

        viewModelUpdater.updateMaterialDefinition = function (materialDefinition) {
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if(currentPipeline.id == materialDefinition.pipelineDefinitionId) {
                    viewModel.allPipelines[index].materials.forEach(function (currentMaterial, materialIndex, array) {
                        if(currentMaterial.id == materialDefinition.id){
                            viewModel.allPipelines[index].materialDefinitions[materialIndex] = materialDefinition;
                        }
                    });
                }
            });
        };

        viewModelUpdater.deleteMaterialDefinition = function (materialDefinition) {

        };

        viewModelUpdater.getAllStageDefinitions = function (stageDefinitions) {
            viewModel.allStages = stageDefinitions;
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
        };

        viewModelUpdater.updateStageDefinition = function (stageDefinition) {
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

        viewModelUpdater.deleteStageDefinition = function (stageDefinition) {

        };

        viewModelUpdater.getAllJobDefinitions = function (jobDefinitions) {
            viewModel.allJobs = jobDefinitions;
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
                        }
                    });
                }
            });

        };

        viewModelUpdater.updateJobDefinition = function (jobDefinition) {
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

        viewModelUpdater.deleteJobDefinition = function (jobDefinition) {

        };

        viewModelUpdater.getAllPipelines = function (pipelines) {
            viewModel.allPipelineRuns = pipelines;
        };

        viewModelUpdater.addPipeline = function (pipeline) {
            viewModel.allPipelineRuns.push(pipeline);
            //toaster.pop('success', "Notification", "Pipeline run started successfully!")
        };

        viewModelUpdater.updatePipeline = function (pipeline) {
            viewModel.allPipelineRuns.forEach(function (currentPipeline, index, array) {
                if(currentPipeline.id == pipeline.id) {
                    viewModel.allPipelineRuns[index] = pipeline;
                }
            });
        };

        viewModelUpdater.addTaskDefinition = function (taskDefinition) {
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

        viewModelUpdater.updateTaskDefinition = function (taskDefinition) {
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

'use strict';

angular
    .module('hawk')
    .factory('pipelineUpdater', ['viewModel', 'commonUtitlites', 'moment', function (viewModel, commonUtitlites, moment) {
        var pipelineUpdater = this;

        pipelineUpdater.truncateGitFromUrl = function(repoUrl, commitId) {
            return commonUtitlites.truncateGitFromUrl(repoUrl,commitId);
        };

        pipelineUpdater.getAllHistoryPipelines = function (pipelines) {
            viewModel.historyPipelines.push(pipelines);
            viewModel.historyPipelines[0].disabled = false;
        };

        pipelineUpdater.flushAllHistoryPipelines = function () {
            viewModel.historyPipelines = [];
        };

        pipelineUpdater.getAllArtifactPipelines = function (pipelines) {
            pipelines.forEach(function (currentPipelineRun, runIndex, runArray) {
                currentPipelineRun.materials.forEach(function(currentMaterial, index, array) {
                    var definition = currentMaterial.materialDefinition;
                    currentMaterial.gitLink = pipelineUpdater.truncateGitFromUrl(definition.repositoryUrl, definition.commitId);
                });

                currentPipelineRun.stages.forEach(function (currentStage, stageIndex, stageArray) {
                    if(currentStage.endTime) {
                        currentPipelineRun.lastStage = currentStage;
                        currentPipelineRun.lastStage.localEndDate = moment.formatDateUTCToLocal(currentStage.endTime);
                        currentPipelineRun.lastStage.localEndTime = moment.formatTimeInLocal(currentStage.endTime.time);
                    }
                });
                viewModel.artifactPipelines.push(currentPipelineRun);
                if(viewModel.artifactPipelines[0]){
                    viewModel.artifactPipelines[0].disabled = false;
                }
            });
        };

        pipelineUpdater.flushAllArtifactPipelines = function () {
            viewModel.artifactPipelines = [];
        };

        pipelineUpdater.getRunManagementPipeline = function (pipeline) {
            viewModel.runManagementPipeline = pipeline;
        };

        pipelineUpdater.flushRunManagementPipeline = function () {
            viewModel.runManagementPipeline = {};
        };

        pipelineUpdater.getAllPipelines = function (pipelines) {
            viewModel.allPipelineRuns = pipelines;
        };

        pipelineUpdater.addPipeline = function (pipeline) {
            viewModel.allPipelineGroups.forEach(function (currentGroup, groupIndex, groupArray) {
                currentGroup.pipelines.forEach(function (currentPipeline, pipelineIndex, pipelineArray) {
                    if(currentPipeline.id == pipeline.pipelineDefinitionId) {
                        currentPipeline.lastRun = {};
                        currentPipeline.lastRun.id = pipeline.id;
                        currentPipeline.lastRun.executionId = pipeline.executionId;
                        currentPipeline.lastRun.status = pipeline.status;
                        currentPipeline.lastRun.startTime = pipeline.startTime;
                        currentPipeline.lastRun.endTime = pipeline.endTime;
                        currentPipeline.lastRun.duration = pipeline.duration;
                        currentPipeline.lastRun.triggerReason = pipeline.triggerReason;
                        currentPipeline.lastRun.stages = pipeline.stages;
                    }
                });
            });
        };

        pipelineUpdater.updatePipeline = function (pipeline) {
            // viewModel.allPipelineRuns.forEach(function (currentPipeline, index, array) {
            //     if(currentPipeline.id == pipeline.id) {
            //         viewModel.allPipelineRuns[index] = pipeline;
            //     }
            // });
            if(!jQuery.isEmptyObject(viewModel.runManagementPipeline)){
                if(viewModel.runManagementPipeline.id == pipeline.id){
                    viewModel.runManagementPipeline = pipeline;
                }
            } else if(viewModel.artifactPipelines.length > 0){
                viewModel.artifactPipelines.forEach(function (currentPipeline, pipelineIndex, pipelineArray) {
                    if(currentPipeline.id == pipeline.id) {
                        currentPipeline.executionId = pipeline.executionId;
                        pipeline.materials.forEach(function(currentMaterial, index, array) {
                            var definition = currentMaterial.materialDefinition;
                            currentMaterial.gitLink = pipelineUpdater.truncateGitFromUrl(definition.repositoryUrl, definition.commitId);
                        });
                        currentPipeline.materials = pipeline.materials;
                        currentPipeline.status = pipeline.status;
                        currentPipeline.startTime = pipeline.startTime;
                        currentPipeline.endTime = pipeline.endTime;
                        currentPipeline.duration = pipeline.duration;
                        currentPipeline.triggerReason = pipeline.triggerReason;
                        currentPipeline.artifactsFileStructure = pipeline.artifactsFileStructure;
                        pipeline.stages.forEach(function (currentStage, stageIndex, stageArray) {
                            if(currentStage.endTime) {
                                currentPipeline.lastStage = currentStage;
                                currentPipeline.lastStage.localEndDate = moment.formatDateUTCToLocal(currentStage.endTime);
                                currentPipeline.lastStage.localEndTime = moment.formatTimeInLocal(currentStage.endTime.time);
                            }
                        });
                        currentPipeline.stages = pipeline.stages;
                    }
                });
            } else if(viewModel.historyPipelines.length > 0) {
                viewModel.historyPipelines.forEach(function (currentPipeline, pipelineIndex, pipelineArray) {
                    if(currentPipeline.id == pipeline.id) {
                        currentPipeline.executionId = pipeline.executionId;
                        currentPipeline.materials = pipeline.materials;
                        currentPipeline.status = pipeline.status;
                        currentPipeline.startTime = pipeline.startTime;
                        currentPipeline.endTime = pipeline.endTime;
                        currentPipeline.duration = pipeline.duration;
                        currentPipeline.triggerReason = pipeline.triggerReason;
                        currentPipeline.stages = pipeline.stages;
                    }
                });
            }

            viewModel.allPipelineGroups.forEach(function (currentGroup, groupIndex, groupArray) {
                currentGroup.pipelines.forEach(function (currentPipeline, pipelineIndex, pipelineArray) {
                    if(currentPipeline.lastRun.id == pipeline.id) {
                        currentPipeline.lastRun.executionId = pipeline.executionId;
                        currentPipeline.lastRun.status = pipeline.status;
                        currentPipeline.lastRun.startTime = pipeline.startTime;
                        currentPipeline.lastRun.endTime = pipeline.endTime;
                        currentPipeline.lastRun.duration = pipeline.duration;
                        currentPipeline.lastRun.triggerReason = pipeline.triggerReason;
                        currentPipeline.lastRun.stages = pipeline.stages;
                    }
                });
            });
        };

        return pipelineUpdater;
    }]);

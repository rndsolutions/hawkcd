/* Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

angular
    .module('hawk')
    .factory('pipelineUpdater', ['viewModel', 'commonUtitlites', 'moment', 'loggerService', function (viewModel, commonUtitlites, moment, loggerService) {
        var pipelineUpdater = this;

        pipelineUpdater.truncateGitFromUrl = function(repoUrl, commitId) {
            return commonUtitlites.truncateGitFromUrl(repoUrl,commitId);
        };

        pipelineUpdater.getAllHistoryPipelines = function (pipelines) {
            pipelines.forEach(function (currentPipelineRun, runIndex, runArray) {
                viewModel.historyPipelines.push(currentPipelineRun);
            });
            viewModel.historyPipelines[0].disabled = false;
        };

        pipelineUpdater.flushAllHistoryPipelines = function () {
            viewModel.historyPipelines = [];
            loggerService.log('History Pipelines flushed.');
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
            loggerService.log('Artifact Pipelines flushed.');
        };

        pipelineUpdater.getRunManagementPipeline = function (pipeline) {
            viewModel.runManagementPipeline = pipeline;
        };

        pipelineUpdater.flushRunManagementPipeline = function () {
            viewModel.runManagementPipeline = {};
            loggerService.log('Run Management Pipeline flushed.');
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
                        currentPipeline.lastRun.stages = pipeline.stageRuns[pipeline.stageRuns.length - 1].stages;
                    }
                });
            });
        };

        return pipelineUpdater;
    }]);

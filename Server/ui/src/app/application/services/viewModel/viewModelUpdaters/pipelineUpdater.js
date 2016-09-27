'use strict';

angular
    .module('hawk')
    .factory('pipelineUpdater', ['viewModel', function (viewModel) {
        var pipelineUpdater = this;

        pipelineUpdater.getAllHistoryPipelines = function (pipelines) {
            viewModel.historyPipelines = pipelines;
        };

        pipelineUpdater.flushAllHistoryPipelines = function () {
            viewModel.historyPipelines = [];
        };

        pipelineUpdater.getAllArtifactPipelines = function (pipelines) {
            viewModel.artifactPipelines = pipelines;
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
            if(viewModel.artifactPipelines.length > 0){
                var newPipeline = {};
                newPipeline.executionId = pipeline.executionId;
                newPipeline.materials = pipeline.materials;
                newPipeline.status = pipeline.status;
                newPipeline.startTime = pipeline.startTime;
                newPipeline.endTime = pipeline.endTime;
                newPipeline.duration = pipeline.duration;
                newPipeline.triggerReason = pipeline.triggerReason;
                newPipeline.artifactsFileStructure = pipeline.artifactsFileStructure;
                newPipeline.stages = pipeline.stages;
                viewModel.artifactPipelines.push(newPipeline);
            } else if(viewModel.historyPipelines.length > 0) {
                var newPipeline = {};
                newPipeline.executionId = pipeline.executionId;
                newPipeline.materials = pipeline.materials;
                newPipeline.status = pipeline.status;
                newPipeline.startTime = pipeline.startTime;
                newPipeline.endTime = pipeline.endTime;
                newPipeline.duration = pipeline.duration;
                newPipeline.triggerReason = pipeline.triggerReason;
                newPipeline.stages = pipeline.stages;
                viewModel.historyPipelines.push(newPipeline);
            }

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
                        currentPipeline.materials = pipeline.materials;
                        currentPipeline.status = pipeline.status;
                        currentPipeline.startTime = pipeline.startTime;
                        currentPipeline.endTime = pipeline.endTime;
                        currentPipeline.duration = pipeline.duration;
                        currentPipeline.triggerReason = pipeline.triggerReason;
                        currentPipeline.artifactsFileStructure = pipeline.artifactsFileStructure;
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

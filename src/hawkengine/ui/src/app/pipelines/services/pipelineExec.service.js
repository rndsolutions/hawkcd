'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('pipeExecService', [function () {
        var pipeExecService = this;

        //region /pipelines
        pipeExecService.scheduleLatestPipeline = function (pipeName, token) {

        };
        pipeExecService.schedulePipelineWithRevision = function (pipeName, changes) {

        };
        pipeExecService.pausePipeline = function (pipeId) {

        };
        pipeExecService.resumePipeline = function (pipeId) {

        };
        pipeExecService.restartPipeline = function (pipeId) {

        };
        pipeExecService.cancelPipeline = function (pipeId) {

        };
        //endregion

        //region /stages
        pipeExecService.scheduleStage = function (pipeName, pipeRunId, stageName) {

        };
        pipeExecService.scheduleStageWithJobs = function (pipeName, pipeRunId, stageName, jobsForExec, token) {

        };
        //endregion

        return pipeExecService;
    }]);
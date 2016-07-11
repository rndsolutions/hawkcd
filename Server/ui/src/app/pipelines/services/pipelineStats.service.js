'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('pipeStatsService', ['jsonHandlerService', 'websocketSenderService', function (jsonHandlerService, websocketSenderService) {
        var pipeStatsService = this;

        //region Senders

        //region /pipelines
        pipeStatsService.getAllPipelines = function (token) {

        };
        pipeStatsService.getAllPipelinesByState = function (state) {

        };
        pipeStatsService.getPipelineByNameAndRun = function (pipeName, pipeRunId) {

        };
        pipeStatsService.getAllRunsByName = function (pipeName, token) {

        };
        pipeStatsService.getAllRunsByNameAndState = function (pipeName, state) {

        };
        pipeStatsService.getLastRunByName = function (pipeName) {

        };
        pipeStatsService.getLastSuccessRunByName = function (pipeName) {

        };
        //returns a double
        pipeStatsService.getPassRateByName = function (pipeName) {

        };
        pipeStatsService.getAllStages = function (pipeName, pipeRunId, token) {

        };
        pipeStatsService.getStageByName = function (pipeName, pipeRunId, stageName) {

        };
        pipeStatsService.getStageRunById = function (pipeName, pipeRunId, stageName, stageRunId) {

        };
        pipeStatsService.getJobByName = function (pipeName, pipeRunId, stageName, stageRunId, jobName) {

        };
        //endregion

        //region /materials
        pipeStatsService.getAllLatestMaterialChanges = function () {

        };
        pipeStatsService.getAllMaterialChangesForPipeline = function (pipeName) {

        };
        pipeStatsService.getMaterialChangeByName = function (pipeName, materialName) {

        };
        //endregion

        // region /agents
        
        pipeStatsService.getAllPipelineGroups = function () {
            var methodName = "getAll";
            var className = "PipelineGroupService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        //endregion

        //endregion

        //region Receivers

        // pipeStatsService.updateAgents = function (data) {
        //     console.log(data);
        // };

        //endregion

        return pipeStatsService;
    }]);
'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('pipeExecService', ['jsonHandlerService', 'websocketSenderService', function (jsonHandlerService, websocketSenderService) {
        var pipeExecService = this;

        //region /pipelines

        pipeExecService.startPipeline = function (pipeline) {
            var methodName = "add";
            var className = "PipelineService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.Pipeline\", \"object\": " + JSON.stringify(pipeline) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeExecService.reRunStage = function (stage) {
            var methodName = "add";
            var className = "StageService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.Stage\", \"object\": " + JSON.stringify(stage) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeExecService.stopPipeline = function (pipeline) {
            var methodName = "update";
            var className = "PipelineService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.Pipeline\", \"object\": " + JSON.stringify(pipeline) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeExecService.getAllPipelines = function () {
            var methodName = "getAll";
            var className = "PipelineService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

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
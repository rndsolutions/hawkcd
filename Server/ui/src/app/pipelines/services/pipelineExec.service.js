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
    .module('hawk.pipelinesManagement')
    .factory('pipeExecService', ['jsonHandlerService', 'websocketSenderService', function (jsonHandlerService, websocketSenderService) {
        var pipeExecService = this;

        //region /pipelines

        pipeExecService.startPipeline = function (pipeline) {
            var methodName = "add";
            var className = "PipelineService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.Pipeline\", \"object\": " + JSON.stringify(pipeline) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeExecService.pausePipeline = function (id) {
            var methodName = "pausePipeline";
            var className = "PipelineService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeExecService.stopPipeline = function (id) {
            var methodName = "cancelPipeline";
            var className = "PipelineService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeExecService.getAllPipelines = function () {
            var methodName = "getAll";
            var className = "PipelineService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeExecService.getAllHistoryPipelines = function (pipelineDefinitionId, numberOfPipelines, pipelineId) {
            var methodName = "getAllPipelineHistoryDTOs";
            var className = "PipelineService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + pipelineDefinitionId + "\"}",
                "{\"packageName\": \"java.lang.Integer\", \"object\": " + numberOfPipelines + "}",
                "{\"packageName\": \"java.lang.String\", \"object\": \"" + pipelineId + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeExecService.getAllArtifactPipelines = function (searchCriteria, numberOfPipelines, pipelineId) {
            var methodName = "getAllPipelineArtifactDTOs";
            var className = "PipelineService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + searchCriteria + "\"}",
                        "{\"packageName\": \"java.lang.Integer\", \"object\": " + numberOfPipelines + "}",
                        "{\"packageName\": \"java.lang.String\", \"object\": \"" + pipelineId + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeExecService.getPipelineById = function (id) {
            var methodName = "getById";
            var className = "PipelineService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeExecService.update = function (pipeline) {
            var methodName = "update";
            var className = "PipelineService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.Pipeline\", \"object\": " + JSON.stringify(pipeline) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeExecService.scheduleLatestPipeline = function (pipeName, token) {

        };
        pipeExecService.schedulePipelineWithRevision = function (pipeName, changes) {

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
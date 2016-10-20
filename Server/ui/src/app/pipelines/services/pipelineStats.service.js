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
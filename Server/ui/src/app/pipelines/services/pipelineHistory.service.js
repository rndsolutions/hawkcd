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
    .factory('pipeHistoryService', ['jsonHandlerService', 'websocketSenderService', function (jsonHandlerService, websocketSenderService) {
        var pipeHistoryService = this;

        //TODO: Send Pipeline Definition to display the Pipelines of, the number of Pipelines to be shown and the last Pipeline the UI has displayed
        pipeHistoryService.getAllHistoryPipelines = function (pipelineDefinitionId, numberOfPipelines, pipelineId) {
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
        };

        return pipeHistoryService;
    }]);
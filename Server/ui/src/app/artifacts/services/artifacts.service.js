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
    .module('hawk.artifactManagement')
    .factory('artifactService', ['CONSTANTS', 'websocketSenderService', 'jsonHandlerService', function(CONSTANTS, websocketSenderService, jsonHandlerService) {
        var artifactService = this;

        var fileEndpoint = CONSTANTS.SERVER_URL + '/';

        artifactService.getFile = function (filePath) {
            window.location.replace(fileEndpoint + filePath);
        };

        //TODO: Send the search criteria, the number of Pipelines to be shown and the last Pipeline the UI has displayed
        artifactService.getAllArtifactPipelines = function (searchCriteria, numberOfPipelines, skip, pipelineId) {
            var methodName = "getAllPipelineArtifactDTOs";
            var className = "PipelineService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + searchCriteria + "\"}",
                "{\"packageName\": \"java.lang.Integer\", \"object\": " + numberOfPipelines + "}",
                "{\"packageName\": \"java.lang.Integer\", \"object\": " + skip + "}",
                "{\"packageName\": \"java.lang.String\", \"object\": \"" + pipelineId + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        artifactService.deleteRun = function (pipelineDefinitionId, runToDelete) {
            // var methodName = "deleteRun";
            // var className = "PipelineService";
            // var packageName = "io.hawkcd.services";
            // var result = "";
            // var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + pipelineDefinitionId + "\"}",
            //     "{\"packageName\": \"java.lang.Integer\", \"object\": " + numberOfPipelines + "}",
            //     "{\"packageName\": \"java.lang.String\", \"object\": \"" + pipelineId + "\"}"];
            // var error = "";
            // var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            // websocketSenderService.call(json);
        };

        return artifactService;
    }]);

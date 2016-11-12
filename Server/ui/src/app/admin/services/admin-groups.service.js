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
    .factory('adminGroupService', ['jsonHandlerService', 'websocketSenderService', function (jsonHandlerService, websocketSenderService) {
        var adminGroupService = this;

        adminGroupService.checkIfDataIsDifferent = function (currentGroupAndPipelines, newGroupAndPipelines) {
            var isTheSame = true;

            for (var i = 0; i < newGroupAndPipelines.length; i++) {
                if (newGroupAndPipelines[i].Name != currentGroupAndPipelines[i].Name ||
                    newGroupAndPipelines[i].Pipelines.length != currentGroupAndPipelines[i].Pipelines.length) {
                    isTheSame = false;
                    break;
                }
            }

            return isTheSame;
        };

        adminGroupService.addNewPipelineGroup = function (pipelineGroup) {
            var methodName = "add";
            var className = "PipelineGroupService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.PipelineGroup\", \"object\": " + JSON.stringify(pipelineGroup) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminGroupService.getAllPipelineGroups = function () {
            var methodName = "getAll";
            var className = "PipelineGroupService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

         adminGroupService.deletePipelineGroup = function (id) {
            var methodName = "delete";
            var className = "PipelineGroupService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        return adminGroupService;
    }]);

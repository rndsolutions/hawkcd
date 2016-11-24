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
    .factory('agentService', ['jsonHandlerService', 'websocketSenderService', '$rootScope', '$timeout', function (jsonHandlerService, websocketSenderService, $rootScope, $timeout) {
        var agentService = this;

        //region Senders

        // agentService.changeAgentStatus = function (id, configState){
        //     var methodName = "update";
        //     var className = "AgentService";
        //     var packageName = "io.hawkcd.services";
        //     var result = "";
        //     var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}",
        //                 "{\"packageName\": \"io.hawkcd.model.ConfigState\", \"object\": \"" + configState + "\"}"];
        //     var error = "";
        //     var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
        //     websocketSenderService.call(json);
        //     console.log(json);
        // };

        //endregion

        agentService.getAllAgents = function () {
            var methodName = "getAll";
            var className = "AgentService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        agentService.getAgentById = function (id) {
            var methodName = "getById";
            var className = "AgentService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        //TODO: Send Agent to be deleted
        agentService.deleteAgent = function (agent) {
            var methodName = "delete";
            var className = "AgentService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.Agent\", \"object\": \"" + JSON.stringify(agent) + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        agentService.update = function (agent) {
            var methodName = "update";
            var className = "AgentService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.Agent\", \"object\": " + JSON.stringify(agent) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        //endregion

        return agentService;
    }]);
'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('agentService', ['jsonHandlerService', 'websocketSenderService', '$rootScope', '$timeout', function (jsonHandlerService, websocketSenderService, $rootScope, $timeout) {
        var agentService = this;

        //region Senders

        // agentService.changeAgentStatus = function (id, configState){
        //     var methodName = "update";
        //     var className = "AgentService";
        //     var packageName = "net.hawkengine.services";
        //     var result = "";
        //     var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}",
        //                 "{\"packageName\": \"net.hawkengine.model.ConfigState\", \"object\": \"" + configState + "\"}"];
        //     var error = "";
        //     var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
        //     websocketSenderService.call(json);
        //     console.log(json);
        // };

        //endregion

        agentService.getAllAgents = function () {
            var methodName = "getAll";
            var className = "AgentService";
            var packageName = "net.hawkengine.services";
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
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        agentService.delete = function (id) {
            var methodName = "delete";
            var className = "AgentService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        agentService.update = function (agent) {
            var methodName = "update";
            var className = "AgentService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.Agent\", \"object\": " + JSON.stringify(agent) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        //endregion

        return agentService;
    }]);
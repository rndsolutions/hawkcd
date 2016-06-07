'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('agentService', ['jsonHandlerService', 'websocketSenderService', '$rootScope', '$timeout', function (jsonHandlerService, websocketSenderService, $rootScope, $timeout) {
        var agentService = this;

        //region Senders

        agentService.changeAgentStatus = function (id, configState){
            var methodName = "setAgentConfigState";
            var className = "AgentService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}",
                        "{\"packageName\": \"net.hawkengine.model.ConfigState\", \"object\": \"" + configState + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        //endregion

        //region Receivers

        agentService.updatewsAgent = function (data) {
            $timeout(function(){
                $rootScope.$broadcast('updatewsAgent', { object: data.result });
            });
        };

        agentService.updateAgents = function (data) {
            $timeout(function(){
                $rootScope.$broadcast('updateAgents', { object: data.result });
            });
        };

        

        //endregion

        return agentService;
    }]);
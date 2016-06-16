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
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.PipelineGroup\", \"object\": " + JSON.stringify(pipelineGroup) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminGroupService.getAllPipelineGroups = function () {
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

         adminGroupService.deletePipelineGroup = function (id) {
            var methodName = "delete";
            var className = "PipelineGroupService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        return adminGroupService;
    }]);

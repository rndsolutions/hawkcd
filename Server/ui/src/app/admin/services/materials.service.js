'use strict';

angular
    .module('hawk')
    .factory('adminMaterialService', ['jsonHandlerService', 'websocketSenderService', function (jsonHandlerService, websocketSenderService) {
        var adminMaterialService = this;

        adminMaterialService.getAllMaterialDefinitions = function() {
            var methodName = "getAll";
            var className = "MaterialDefinitionService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        return adminMaterialService;
    }]);
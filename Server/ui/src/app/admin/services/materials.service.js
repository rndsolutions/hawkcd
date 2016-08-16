'use strict';

angular
    .module('hawk')
    .factory('adminMaterialService', ['jsonHandlerService', 'websocketSenderService', 'websocketReceiverService', 'pipeConfigService', function(jsonHandlerService, websocketSenderService,websocketReceiverService, pipeConfigService) {
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

        adminMaterialService.addMaterial = function(newMaterial) {
            var material = {};

            if (newMaterial.type == 'GIT') {
                material = {
                    "name": newMaterial.name,
                    "type": newMaterial.type,
                    "repositoryUrl": newMaterial.url,
                    "isPollingForChanges": newMaterial.poll,
                    "destination": newMaterial.name,
                    "branch": newMaterial.branch || 'master'
                };
                if (newMaterial.credentials) {
                    material.username = newMaterial.username;
                    material.password = newMaterial.password;
                }
                pipeConfigService.addGitMaterialDefinition(material);
            }

            // if (materialType == 'NUGET') {
            //     material = {
            //         "pipelineName": vm.currentPipeline,
            //         "name": newMaterial.name,
            //         "repositoryUrl": newMaterial.repositoryUrl,
            //         "type": 'NUGET',
            //         "isPollingForChanges": newMaterial.isPollingForChanges,
            //         "destination": newMaterial.name,
            //         "packageId": newMaterial.packageId,
            //         "includePrerelease": newMaterial.includePrerelease
            //     };
            //     pipeConfigService.addNugetMaterialDefinition(material);
            //     //TODO
            //     // if (nugetMaterial.credentials) {
            //     //   nuget.MaterialSpecificDetails.username = nugetMaterial.username;
            //     //   nuget.MaterialSpecificDetails.password = nugetMaterial.password;
            //     // }
            // }

            //TODO
            // if (tfsMaterial) {
            //   var tfs = {
            //     "PipelineName": vm.currentPipeline,
            //     "Name": tfsMaterial.name,
            //     "Type": 'TFS',
            //     "AutoTriggerOnChange": tfsMaterial.poll,
            //     "Destination": tfsMaterial.name,
            //     "MaterialSpecificDetails": {
            //       "domain": tfsMaterial.domain,
            //       "projectPath": tfsMaterial.projectPath,
            //       "username": tfsMaterial.username,
            //       "password": tfsMaterial.password
            //     }
            //   };
            // }
            //
        };

        return adminMaterialService;
    }]);

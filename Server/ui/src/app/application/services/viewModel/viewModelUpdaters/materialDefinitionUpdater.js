'use strict';

angular
    .module('hawk')
    .factory('materialDefinitionUpdater', ['viewModel', function (viewModel) {
        var materialDefinitionUpdater = this;

        materialDefinitionUpdater.getAllMaterialDefinitions = function (materialDefinitions) {
            viewModel.allMaterialDefinitions = materialDefinitions;
        };

        materialDefinitionUpdater.getMaterialDefinitionById = function (materialDefinition) {
            return materialDefinition;
        };

        materialDefinitionUpdater.addMaterialDefinition = function (materialDefinition) {
            viewModel.allMaterialDefinitions.push(materialDefinition);
        };

        materialDefinitionUpdater.updateMaterialDefinition = function (materialDefinition) {
            viewModel.allMaterialDefinitions.forEach(function (currentMaterial, materialIndex, array) {
                if(currentMaterial.id == materialDefinition.id){
                    viewModel.allMaterialDefinitions[materialIndex] = materialDefinition;
                }
            });
        };

        materialDefinitionUpdater.deleteMaterialDefinition = function (materialDefinition) {

        };

        return materialDefinitionUpdater;
    }]);

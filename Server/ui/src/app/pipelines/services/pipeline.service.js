'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('pipeService', [function () {
        var pipeService = this;

        pipeService.getLastStageRunForLastPipeline = function (arrayOfPipelinesAndGroups) {

        };

        pipeService.combineDefinitionAndRuns = function (arrayOfRuns) {

        };

        pipeService.arrangePipelinesByNameAndExecution = function (arrayOfRuns) {

        };

        return pipeService;
    }]);
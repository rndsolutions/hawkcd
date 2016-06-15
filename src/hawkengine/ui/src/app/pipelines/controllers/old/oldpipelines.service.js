'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('pipesService', [function () {
        var pipesService = this;

        pipesService.getLastStageRunForLastPipeline = function (arrayOfPipelinesAndGroups) {
            for (var prop in arrayOfPipelinesAndGroups) {
                for (var key in arrayOfPipelinesAndGroups[prop]) {

                    var length = arrayOfPipelinesAndGroups[prop][key].length - 1;

                    //get the number of stages definitions
                    var numberOfStages = arrayOfPipelinesAndGroups[prop][key][0].Stages.length;

                    for (var i = 0; i < numberOfStages; i++) {
                        var currentStage = arrayOfPipelinesAndGroups[prop][key][length].Stages[i];
                        //   console.log(currentStage.Name);
                        for (var q = numberOfStages; q < arrayOfPipelinesAndGroups[prop][key][length].Stages.length; q++) {
                            if (arrayOfPipelinesAndGroups[prop][key][length].Stages[q].Name == currentStage.Name &&
                                arrayOfPipelinesAndGroups[prop][key][length].Stages[q].ExecutionID > currentStage.ExecutionID) {
                                arrayOfPipelinesAndGroups[prop][key][length].Stages[i] = arrayOfPipelinesAndGroups[prop][key][length].Stages[q];
                                arrayOfPipelinesAndGroups[prop][key][length].Stages.splice(q, 1);
                                q--;
                            }
                            if (arrayOfPipelinesAndGroups[prop][key][length].Stages[q].Name == currentStage.Name &&
                                arrayOfPipelinesAndGroups[prop][key][length].Stages[q].ExecutionID <= currentStage.ExecutionID) {
                                arrayOfPipelinesAndGroups[prop][key][length].Stages.splice(q, 1);
                                q--;
                            }
                        }
                    }
                }
            }
            return arrayOfPipelinesAndGroups;
        }

        pipesService.combineDefinitionAndRuns = function (arrayOfRuns) {
            var combinedDefsAndRuns = [];

            for (var i = 0; i < arrayOfRuns.length; i++) {
                for (var q = 0; q < arrayOfRuns[i].length; q++) {
                    combinedDefsAndRuns.push(arrayOfRuns[i][q]);
                }
            }

            return combinedDefsAndRuns;
        }

        pipesService.arrangePipelinesByNameAndExecution = function (arrayOfRuns) {
            arrayOfRuns = _.groupBy(arrayOfRuns, 'GroupName');

            for (var key in arrayOfRuns) {
                arrayOfRuns[key] = _.sortBy(arrayOfRuns[key], 'ExecutionID');
                arrayOfRuns[key] = _.groupBy(arrayOfRuns[key], 'Name');
            }
            
            return arrayOfRuns;
        }






        return pipesService;
  }]);

'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('runManagementService', function () {

        var currentPipeline = [];


        function groupStages(stages) {
            stages = _.groupBy(stages, 'Name');

            var sortedStagesById = [];
            for (var key in stages) {
                var stage = _.sortBy(stages[key], 'ExecutionID');
                //Remove stages that are not executed and we already have their definitions
                if (stage[0].ExecutionID == 0 && stage.length > 1) {
                    stage.splice(0, 1);
                }
                sortedStagesById.push(stage);
            }

            return sortedStagesById;
        }


        function addLastExecution(arrayOfStages) {
            arrayOfStages = _.map(arrayOfStages, function (value, key) {
                return {
                    LastRun: _.last(value),
                    Runs: value
                }
            });

            return arrayOfStages;
        }

        function checkForChanges(currentResult, lastResult) {
            var resultIsTheSame = true;

            for (var i = 0; i < currentResult.length; i++) {
                if (currentResult[i].State != lastResult[i].State) {
                    resultIsTheSame = false;
                    break;
                }
            }
            return resultIsTheSame;
        }



        return {

            groupStages: function (stages) {
                return groupStages(stages)
            },
            addLastExecution: function (arrayOfStages) {
                return addLastExecution(arrayOfStages);
            },
            checkForChanges: function (currentResult, lastResult) {
                return checkForChanges(currentResult, lastResult);
            }
        }
    });

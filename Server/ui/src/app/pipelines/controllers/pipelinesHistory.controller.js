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
    .controller('PipelinesHistoryController',['$state', '$rootScope','$scope','$stateParams','$interval','pipeHistoryService','authDataService','viewModel', 'pipelineUpdater','moment','$sce','commonUtitlites', 'loggerService',
     function($state, $rootScope, $scope, $stateParams, $interval, pipeHistoryService, authDataService, viewModel, pipelineUpdater, moment, $sce,commonUtitlites, loggerService) {
        var vm = this;

        vm.labels = {
            headers: {
                history: 'History'
            },
            breadCrumb: {
                pipelines: 'Pipelines'
            },
            table: {
                run: 'Run',
                repo: 'Repository',
                commitId: 'Commit',
                branch: 'Branch',
                start: 'Start',
                trigger: 'Trigger',
                state: 'State',
                execution: 'Execution'
            }
        };

        vm.currentPipelineRuns = [];

        vm.currentJob = [];

        //Get the current group and pipeline name
        vm.groupName = $stateParams.groupName;
        vm.pipelineName = $stateParams.pipelineName;
         vm.pipelineId = $stateParams.pipelineId;

        vm.allPipelineRuns = angular.copy(viewModel.historyPipelines);

        vm.spinIcon = false;

        vm.updateClock = function(pipelineRun) {

        };

        pipeHistoryService.getAllHistoryPipelines(vm.pipelineId, 10);

        vm.getLastRunAction = function(pipelineRun) {
            return moment.getLastRunAction(pipelineRun);
        };

        vm.truncateGitFromUrl = function(repoUrl, commitId) {
          return commonUtitlites.truncateGitFromUrl(repoUrl,commitId);
        };

        vm.currentPipelineObject = {};
        vm.allJobReportsFromStages = [];
        $scope.$watch(function() {
            return viewModel.historyPipelines
        }, function(newVal, oldVal) {
            vm.allPipelineRuns = angular.copy(viewModel.historyPipelines);
            vm.currentPipelineRuns = [];
            vm.allPipelineRuns.forEach(function(currentPipelineRun, index, array) {
                vm.currentPipelineObject = currentPipelineRun;

                var result = vm.getLastRunAction(currentPipelineRun);
                currentPipelineRun.lastPipelineAction = result;
                currentPipelineRun.materials.forEach(function(currentMaterial, index, array) {
                    var definition = currentMaterial.materialDefinition;
                    currentMaterial.gitLink = vm.truncateGitFromUrl(definition.repositoryUrl, definition.commitId);
                });

                if (currentPipelineRun.triggerReason == null) {
                    currentPipelineRun.triggerReason = viewModel.user.username;
                }
                vm.currentPipelineRuns.push(currentPipelineRun);
            });

            loggerService.log('History Pipelines watcher :');
            loggerService.log(vm.allPipelineRuns);
        }, true);

         vm.scrollCall = function() {
             if(vm.allPipelineRuns[0]){
                 if(vm.allPipelineRuns[0].disabled == false){
                     loggerService.log('PipelinesHistoryController.scrollCall');
                     pipeHistoryService.getAllHistoryPipelines(vm.pipelineId, 10, vm.allPipelineRuns[vm.allPipelineRuns.length - 1].id);
                 }
                 vm.allPipelineRuns[0].disabled = true;
             }
         };

         vm.getRunNumber = function(run){
            vm.runNumber = run.executionId;
            vm.runToDelete = run;
         };

         vm.deleteRun = function(runToDelete){
            pipeHistoryService.deleteRun(runToDelete.id);
         };

         $scope.$on("$destroy", function() {
             pipelineUpdater.flushAllHistoryPipelines();
         });
    }]);

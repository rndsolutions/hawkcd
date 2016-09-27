'use strict';

angular
    .module('hawk.pipelinesManagement')
    .controller('PipelinesHistoryController',['$state', '$rootScope','$scope','$stateParams','$interval','pipeStats','pipeExecService','authDataService','viewModel', 'pipelineUpdater','moment','$sce','commonUtitlites',
     function($state, $rootScope, $scope, $stateParams, $interval, pipeStats, pipeExecService, authDataService, viewModel, pipelineUpdater, moment, $sce,commonUtitlites) {
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

         vm.getAllHistoryPipelines = function(id) {
             pipeExecService.getAllHistoryPipelines(id);
         };

         vm.getAllHistoryPipelines(vm.pipelineId);

        vm.getLastRunAction = function(pipelineRun) {
            return moment.getLastRunAction(pipelineRun)
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

                if (currentPipelineRun.pipelineDefinitionName == $stateParams.pipelineName) {
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
                }
            });
            vm.currentPipelineRuns.sort(function(a, b) {
                return b.executionId - a.executionId;
            });

            // vm.lastRun = {};
            // vm.lastRun = vm.currentPipelineRuns[0];
            // if (vm.lastRun !== undefined && vm.allJobReportsFromStages.length === 0) {
            //     vm.lastRun.stages.forEach(function(currentStage, stagesIndex, stagesArray) {
            //         currentStage.jobs.forEach(function(currentJob, jobsIndex, jobsArray) {
            //             var buffer = ansi_up.ansi_to_html(jobsArray[jobsIndex].report);
            //             var reportToAdd = $sce.trustAsHtml(buffer);
            //             vm.allJobReportsFromStages.push(reportToAdd);
            //         });
            //     });
            //
            // }


            // if (vm.currentPipelineRuns.length > 0) {
            //     vm.currentJob = vm.currentPipelineRuns[0].stages[vm.currentPipelineRuns[0].stages.length - 1].jobs[vm.currentPipelineRuns[0].stages[vm.currentPipelineRuns[0].stages.length - 1].jobs.length - 1];
            //     vm.currentJob.report = ansi_up.ansi_to_html(vm.currentJob.report);
            //     vm.currentJob.report = $sce.trustAsHtml(vm.currentJob.report);
            //
            //
            // }
            // console.log(vm.allPipelineRuns);
            // console.log(vm.currentPipelineRuns);
        }, true);

         $scope.$on("$destroy", function() {
             pipelineUpdater.flushAllHistoryPipelines();
         });
    }]);

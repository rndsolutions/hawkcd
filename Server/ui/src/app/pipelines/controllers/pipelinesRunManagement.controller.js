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
    .controller('PipelinesRunManagement', ['$state','$scope','$stateParams','$interval','pipeExecService','authDataService',
                                            'viewModel', 'pipelineUpdater','moment','ansi_up','$sce','commonUtitlites', 'loggerService',
                                            function ($state, $scope, $stateParams, $interval, pipeExecService,
                                                    authDataService, viewModel, pipelineUpdater, moment, ansi_up, $sce,
                                                    commonUtilities, loggerService) {
        var vm = this;
        vm.disabledBtn = false;

        vm.glued = true;
        vm.labels = {
            headers: {
                runManagement: 'Run Management'
            },
            breadCrumb: {
                pipelines: 'Pipelines'
            }
        };

        //Selected jobs from re-run modal.
        vm.selectedJobsForReRun = [];

        vm.lastRunSelected = 1;

        vm.groupName = $stateParams.groupName;
        vm.pipelineName = $state.params.pipelineName;
        vm.pipelineExecutionID = $state.params.executionID;
        vm.pipelineId = $state.params.pipelineId;

        vm.defaultHeaders = {
            breadCrumbPipelines: "Pipelines",
            breadCrumbHistory: "Pipeline's history",
            materialsTitle: "Materials",
            commitId: "Commit id: "
        };

        vm.stageDefaultText = {
            run: "Run",
            state: "State",
            start: "Started",
            duration: "Duration",
            trigger: "Trigger",
            stageNotStarted: "Not started yet",
            notAvailable: "Not available",
            of: "of",
            expandHide: "Expand/Colapse"
        };

        //Partials' content begin

        //Re-run stage

        vm.reRunStage = {
            selectAll: "Select all",
            reRun: "Rerun",
            cancel: "Cancel"
        };

        //Run-info

        vm.runInfoDefaultText = {
            scheduled: "Scheduled On: ",
            state: "State: ",
            duration: "Duration: ",
            agent: "Agent: ",
            currentTask: "Current Task: ",
            trigger: "Trigger reason: ",
            tabConsole: "Console",
            tabArtifacts: "Artifacts",
            tabTests: "Tests"
        };

        //Stage

        vm.runsInfoMenu = {
            active: " Active",
            skipped: " Skipped",
            scheduled: " Scheduled",
            inProgress: " In progress",
            completed: " Completed",
            passed: " Passed",
            failed: " Failed"
        };

        //Stage panel

        vm.stagePanelDefault = {
            run: "Run"
        };
        //Partials' content end
        vm.resultIsTheSame = true;

        //Used to determine if this is the first call to the controller
        var isInitial = true,
            firstLoadOfController = true,
            lastResult;

        //Initialization of the controller
        var nameOfStage = '',
            resLength = 0;
        vm.selectedStageIndexInMainDB = 0;

        vm.allPipelineRuns = [];

        vm.currentPipelineRun = [];

        vm.currentPipelineRunStages = [];

        vm.temporaryStages = [];

        vm.allPipelines = [];

        vm.isFirstLoad = true;

        vm.isFullScreen = false;

        vm.runManagementPipeline = {};

        vm.getRunManagementPipeline = function(id) {
            pipeExecService.getPipelineById(id);
        };

        vm.getRunManagementPipeline(vm.pipelineId);

        vm.getLastRunAction = function(pipelineRun) {
          return moment.getLastRunAction(pipelineRun);
        };

        vm.truncateGitFromUrl = function(repoUrl, commitId) {
          return commonUtilities.truncateGitFromUrl(repoUrl,commitId);
        };

        $(document).keyup(function (e) {
            var keyCode = e.keyCode;
            if(keyCode == 27){
                vm.isFullScreen = false;
            }
        });

        vm.toggleFullScreen = function() {
            var element = document.getElementById("console");
            if(!vm.isFullScreen){
                if(element.requestFullscreen) {
                    element.requestFullscreen();
                } else if(element.mozRequestFullScreen) {
                    element.mozRequestFullScreen();
                } else if(element.webkitRequestFullscreen) {
                    element.webkitRequestFullscreen();
                } else if(element.msRequestFullscreen) {
                    element.msRequestFullscreen();
                }
            } else {
                if(document.exitFullscreen) {
                    document.exitFullscreen();
                } else if(document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if(document.webkitExitFullscreen) {
                    document.webkitExitFullscreen();
                } else if(document.webkitIsFullScreen) {
                    document.cancelFullScreen();
                }
            }

            vm.isFullScreen = !vm.isFullScreen;
        };

        $('#console').bind('webkitfullscreenchange mozfullscreenchange fullscreenchange', function(e) {
            var state = document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen;
            var event = state ? 'FullscreenOn' : 'FullscreenOff';

            // Now do something interesting
            alert('Event: ' + event);
        });

        vm.continueStage = function (stage) {
            var currentPipelineRun = angular.copy(vm.currentPipelineRun);
            pipeExecService.pausePipeline(currentPipelineRun);
        };

        vm.pause = function (pipeline) {
            pipeExecService.pausePipeline(pipeline);
        };
                                                
        vm.stop = function(pipeline) {
            pipeExecService.stopPipeline(pipeline);
        };

        // $scope.$watch(function() { return viewModel.allPipelineRuns }, function(newVal, oldVal) {
        //     vm.allPipelineRuns = viewModel.allPipelineRuns;
        // }, true);

        $scope.$watch(function() { return viewModel.allPipelines}, function (newVal, oldVal) {
            vm.allPipelines = angular.copy(viewModel.allPipelines);
        }, true);

        $scope.$watch(function() { return viewModel.runManagementPipeline}, function (newVal, oldVal) {
            vm.currentPipelineRun = angular.copy(viewModel.runManagementPipeline);

            if(vm.currentPipelineRun.stages && vm.isFirstLoad){
                vm.selectJob(0, 0);
                vm.isFirstLoad = false;
            }

            if(!jQuery.isEmptyObject(vm.currentPipelineRun)){
                vm.currentPipelineRun.materials.forEach(function(currentMaterial,index,array){
                    currentMaterial.gitLink = vm.truncateGitFromUrl(currentMaterial.materialDefinition.repositoryUrl,currentMaterial.materialDefinition.commitId);
                });
                var result = vm.getLastRunAction(vm.currentPipelineRun);
                vm.currentPipelineRun.lastPipelineAction = result;
                if (vm.currentPipelineRun.triggerReason == null) {
                    vm.currentPipelineRun.triggerReason = viewModel.user.username;
                }
                vm.allPipelines.forEach(function (currentPipeline, pipelineIndex, pipelineArray) {
                    if(vm.currentPipelineRun.pipelineDefinitionId == currentPipeline.id){
                        vm.currentPipelineRunStages = angular.copy(currentPipeline.stageDefinitions);
                        vm.currentPipelineRunStages.forEach(function (currentStageDefinition, stageDefinitionIndex, stageDefinitionArray) {
                            vm.currentPipelineRun.stages.forEach(function (currentStageRun, stageRunIndex, stageRunarray) {
                                if(currentStageDefinition.id == currentStageRun.stageDefinitionId) {
                                    vm.temporaryStages.push(currentStageRun);
                                }
                            });
                            vm.currentPipelineRunStages[stageDefinitionIndex] = vm.temporaryStages;
                            vm.temporaryStages = [];
                        });
                    }
                });
                vm.currentPipelineRun.stages.forEach(function (currentStage, stageIndex, stageArray) {
                    currentStage.jobs.forEach(function (currentJob, jobIndex, jobArray) {
                        currentJob.processedReport = ansi_up.ansi_to_html(currentJob.report);
                        currentJob.processedReport = $sce.trustAsHtml(currentJob.processedReport);
                    });
                });
            }
        });

        $scope.$watch(function() { return viewModel.allPipelineRuns }, function(newVal, oldVal) {
            vm.allPipelineRuns = angular.copy(viewModel.allPipelineRuns);
            vm.allPipelineRuns.forEach(function (currentPipelineRun, index, array) {
                if(currentPipelineRun.pipelineDefinitionName == vm.pipelineName && currentPipelineRun.executionId == vm.pipelineExecutionID){
                    vm.currentPipelineRun = currentPipelineRun;
                    vm.currentPipelineRun.materials.forEach(function(currentMaterial,index,array){
                      currentMaterial.gitLink = vm.truncateGitFromUrl(currentMaterial.materialDefinition.repositoryUrl,currentMaterial.materialDefinition.commitId);
                    });
                    var result = vm.getLastRunAction(currentPipelineRun);
                    vm.currentPipelineRun.lastPipelineAction = result;
                    if (currentPipelineRun.triggerReason == null) {
                        vm.currentPipelineRun.triggerReason = viewModel.user.username;
                    }
                    vm.allPipelines.forEach(function (currentPipeline, pipelineIndex, pipelineArray) {
                        if(currentPipelineRun.pipelineDefinitionId == currentPipeline.id){
                            vm.currentPipelineRunStages = angular.copy(currentPipeline.stageDefinitions);
                            vm.currentPipelineRunStages.forEach(function (currentStageDefinition, stageDefinitionIndex, stageDefinitionArray) {
                                vm.currentPipelineRun.stages.forEach(function (currentStageRun, stageRunIndex, stageRunarray) {
                                    if(currentStageDefinition.id == currentStageRun.stageDefinitionId) {
                                        vm.temporaryStages.push(currentStageRun);
                                    }
                                });
                                vm.currentPipelineRunStages[stageDefinitionIndex] = vm.temporaryStages;
                                vm.temporaryStages = [];
                            });
                        }
                    });
                    currentPipelineRun.stages.forEach(function (currentStage, stageIndex, stageArray) {
                        currentStage.jobs.forEach(function (currentJob, jobIndex, jobArray) {
                            currentJob.processedReport = ansi_up.ansi_to_html(currentJob.report);
                            currentJob.processedReport = $sce.trustAsHtml(currentJob.processedReport);

                        });
                    });
                }

            });
            // if(vm.currentPipelineRunStages.length > 1){
            //     if(vm.selectedJob == null && vm.currentPipelineRunStages[0][vm.currentPipelineRunStages[0].length - 1].jobs != null){
            //         vm.selectedJob = vm.currentPipelineRunStages[0][vm.currentPipelineRunStages[0].length - 1].jobs[vm.currentPipelineRunStages[0][vm.currentPipelineRunStages[0].length - 1].jobs.length - 1];
            //     }
            // }
        }, true);

        //Change the class of selected stage - needed for the initialization
        vm.toggleRun = 0;

        vm.selectStage = function (index) {
            //Needed for class change
            vm.toggleRun = index;

            vm.selectedStageIndexInMainDB = index;

            // //  Get the selected stage - with LastRun + all Runs
            vm.selectedStage = vm.currentPipelineRun.stages[index];
            // nameOfStage = vm.selectedStage.Runs[0].Name;
            //
            // //Need this variable, so the runs are displayed in the modal for re run stage
            // vm.selectedStageRunsShow = vm.groupStages[index];
            //
            // var runsLength = vm.selectedStage.Runs.length - 1;
            //
            // vm.selectedJob = vm.selectedStage.Runs[runsLength].Jobs[0];
            // vm.selectedRunIndex = runsLength;
            // vm.selectedRunID = runsLength;
            // vm.jobIndex = 0;
            //
            // //Check if selected stage does not have LastRunSelected - in this case assign it to the value of LastRun
            // if (vm.selectedStage.LastRunSelected == undefined) {
            //     vm.selectedStage.LastRunSelected = vm.selectedStage.LastRun.ExecutionID;
            // }
            //
            // //Every time the selected stage is changed, selected jobs is set to empty array
            // vm.selectedJobsForReRun = [];
        };

        vm.selectRun = function () {
            //vm.selectedRunID = vm.groupStages[vm.selectedStageIndexInMainDB].LastRunSelected - 1;

            // vm.groupStages[vm.selectedStageIndexInMainDB].LastRun = vm.selectedStage.Runs[vm.selectedRunID - 1];
            //
            // if (vm.groupStages[vm.selectedStageIndexInMainDB].Runs[vm.selectedRunID - 1] != undefined) {
            //     nameOfStage = vm.groupStages[vm.selectedStageIndexInMainDB].Runs[vm.selectedRunID - 1].Name;
            // }
            //
            // //Prevents errors in console when stage re-run
            // if (vm.selectedStage.Runs[vm.selectedRunID - 1] != undefined) {
            //     vm.selectedJob = vm.selectedStage.Runs[vm.selectedRunID - 1].Jobs[0];
            // }
            // vm.selectedRunIndex = vm.selectedRunID - 1;
            // vm.jobIndex = 0;

        };

        //$(function () {
        //  $('#tree_1').jstree();
        //});

        vm.selectJob = function (stageIndex, jobIndex) {
            vm.selectStage(stageIndex);
            var selectedRunIndex = vm.currentPipelineRun.stages[vm.toggleRun] - 1;

            vm.selectedJob = vm.currentPipelineRun.stages[vm.toggleRun].jobs[jobIndex];
            vm.selectedRunIndex = selectedRunIndex;
            vm.jobIndex = jobIndex;
        };

        $scope.$on("$destroy", function() {
            pipelineUpdater.flushRunManagementPipeline();
        });
    }]);

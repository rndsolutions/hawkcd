'use strict';

angular
    .module('hawk.pipelinesManagement')
    .controller('PipelinesRunManagement', ['$state','$scope','$stateParams','$interval','pipeStats',
                                            'runManagementService','pipeExec','pipeConfig','authDataService','viewModel','moment','ansi_up','$sce','commonUtitlites',
    function ($state, $scope, $stateParams, $interval, pipeStats,
                                                    runManagementService, pipeExec, pipeConfig,
                                                    authDataService, viewModel, moment, ansi_up, $sce,
                                                    commonUtilities) {
        var vm = this;

        $scope.$on("$destroy", function () {
            if (angular.isDefined($scope.Timer)) {
                $interval.cancel($scope.Timer);
            }
        });
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

        vm.getLastRunAction = function(pipelineRun){
          return moment.getLastRunAction(pipelineRun);
        };

        vm.truncateGitFromUrl = function(repoUrl, commitId) {
          return commonUtilities.truncateGitFromUrl(repoUrl,commitId);
        }

        // $scope.$watch(function() { return viewModel.allPipelineRuns }, function(newVal, oldVal) {
        //     vm.allPipelineRuns = viewModel.allPipelineRuns;
        // }, true);

        $scope.$watch(function() { return viewModel.allPipelines}, function (newVal, oldVal) {
            vm.allPipelines = angular.copy(viewModel.allPipelines);
        }, true);

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
                            currentJob.report = ansi_up.ansi_to_html(currentJob.report);
                            currentJob.report = $sce.trustAsHtml(currentJob.report);

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

        // vm.getAll = function () {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         pipeStats.getAllStages(vm.pipelineName, vm.pipelineExecutionID, token)
        //             .then(function (res) {
        //                 var currentStateIsRunning = false;
        //                 if (vm.groupStages != undefined && vm.groupStages[vm.selectedStageIndexInMainDB].Runs[vm.selectedRunID] != undefined && vm.groupStages[vm.selectedStageIndexInMainDB] != undefined) {
        //                     var runsLength = vm.groupStages[vm.selectedStageIndexInMainDB].Runs.length;
        //                     vm.resultIsTheSame = runManagementService.checkForChanges(lastResult, res);
        //
        //                     currentStateIsRunning = vm.groupStages[vm.selectedStageIndexInMainDB].Runs[runsLength - 1].State == 'Running';
        //                     if (currentStateIsRunning) {
        //                         vm.selectedRunID += 1;
        //                     }
        //                 }
        //
        //                 //This means that this is the first call to the server and the controller should proceed
        //                 if (vm.groupStages == undefined || vm.groupStages[vm.selectedStageIndexInMainDB].Runs[vm.selectedRunID] == undefined || vm.groupStages[vm.selectedStageIndexInMainDB] == undefined) {
        //                     firstLoadOfController = true;
        //                 }
        //
        //                 // Checking if in state where the stage has been just scheduled but still not executed.
        //                 if (vm.groupStages != undefined && nameOfStage != '' && vm.resultIsTheSame) {
        //                     if (res[res.length - 1].ExecutionID == 0 && res[res.length - 1].Name == nameOfStage) {
        //                         return;
        //                     }
        //                 }
        //
        //                 if (!vm.resultIsTheSame && res[res.length - 1].ExecutionID == 0 && !firstLoadOfController) {
        //                     return;
        //                 }
        //
        //                 //The result is the same as last request and stage is not running
        //                 if (res.length == resLength && vm.resultIsTheSame && !firstLoadOfController) {
        //                     return;
        //                 }
        //
        //                 // success
        //                 vm.currentPipeline = res;
        //                 firstLoadOfController = false;
        //
        //                 //Sorts the stages by name and by Execution ID
        //                 vm.sortedData = runManagementService.groupStages(vm.currentPipeline);
        //
        //                 //Adds a new array LastRun in the sortedData, to present it in the view
        //                 vm.groupStages = runManagementService.addLastExecution(vm.sortedData);
        //
        //                 //Default LastRunSelected - the last ExecutionID in the array
        //                 for (var i = 0; i < vm.groupStages.length; i += 1) {
        //                     var runsLength = vm.groupStages[i].Runs.length;
        //                     vm.groupStages[i].LastRunSelected = vm.groupStages[i].Runs[runsLength - 1].ExecutionID;
        //                 }
        //
        //
        //
        //                 //Save the last result, used form comparing and checking for changes
        //                 lastResult = res;
        //                 resLength = res.length;
        //
        //                 //Initialize the Selected Stage, if this is the first call to the controller. Otherwise the selected stage is defined by the user!
        //                 if (isInitial) {
        //                     vm.selectedStage = vm.groupStages[0];
        //
        //
        //                     var runsLength = vm.selectedStage.Runs.length - 1;
        //                     var jobsLength = vm.selectedStage.Runs[0].Jobs.length - 1;
        //                     vm.selectedJob = vm.selectedStage.Runs[runsLength].Jobs[jobsLength];
        //                     nameOfStage = vm.selectedStage.Runs[0].Name;
        //
        //                     //Initially displays the last run on each stage
        //                     vm.selectedRunIndex = vm.selectedStage.Runs.length - 1;
        //                     vm.selectedRunID = vm.selectedRunIndex;
        //
        //                     vm.selectedStageRunsShow = vm.groupStages[0];
        //                 }
        //                 if (!isInitial) {
        //                     //Gets the numbers in the menu left from the console
        //                     vm.getJobResults();
        //
        //                     //This if statements prevents clearing of Run selection
        //                     if (vm.selectedRunID != undefined) {
        //                         vm.groupStages[vm.selectedStageIndexInMainDB].LastRunSelected = vm.selectedRunID;
        //                     }
        //
        //                     vm.selectedStage = vm.groupStages[vm.selectedStageIndexInMainDB];
        //                     nameOfStage = vm.selectedStage.Runs[0].Name;
        //                     if (vm.selectedStage.Runs[vm.selectedRunIndex] != undefined) {
        //                         vm.selectedJob = vm.selectedStage.Runs[vm.selectedRunIndex].Jobs[vm.jobIndex];
        //                     }
        //
        //                     for (var i = 0; i < vm.groupStages.length; i++) {
        //                         if (vm.groupStages[i].Runs[0].ExecutionID == 0 && vm.groupStages[i].Runs[0].length > 0) {
        //                             vm.groupStages[i].Runs.shift();
        //                         }
        //                     }
        //
        //                 }
        //                 isInitial = false;
        //
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //             .then(function (res) {
        //                 var token = res.access_token;
        //                 pipeStats.getAllStages(vm.pipelineName, vm.pipelineExecutionID, token)
        //                     .then(function (res) {
        //                         var currentStateIsRunning = false;
        //                         if (vm.groupStages != undefined && vm.groupStages[vm.selectedStageIndexInMainDB].Runs[vm.selectedRunID] != undefined && vm.groupStages[vm.selectedStageIndexInMainDB] != undefined) {
        //                             var runsLength = vm.groupStages[vm.selectedStageIndexInMainDB].Runs.length;
        //                             vm.resultIsTheSame = runManagementService.checkForChanges(lastResult, res);
        //
        //                             currentStateIsRunning = vm.groupStages[vm.selectedStageIndexInMainDB].Runs[runsLength - 1].State == 'Running';
        //                             if (currentStateIsRunning) {
        //                                 vm.selectedRunID += 1;
        //                             }
        //                         }
        //
        //                         //This means that this is the first call to the server and the controller should proceed
        //                         if (vm.groupStages == undefined || vm.groupStages[vm.selectedStageIndexInMainDB].Runs[vm.selectedRunID] == undefined || vm.groupStages[vm.selectedStageIndexInMainDB] == undefined) {
        //                             firstLoadOfController = true;
        //                         }
        //
        //                         // Checking if in state where the stage has been just scheduled but still not executed.
        //                         if (vm.groupStages != undefined && nameOfStage != '' && vm.resultIsTheSame) {
        //                             if (res[res.length - 1].ExecutionID == 0 && res[res.length - 1].Name == nameOfStage) {
        //                                 return;
        //                             }
        //                         }
        //
        //                         if (!vm.resultIsTheSame && res[res.length - 1].ExecutionID == 0 && !firstLoadOfController) {
        //                             return;
        //                         }
        //
        //                         //The result is the same as last request and stage is not running
        //                         if (res.length == resLength && vm.resultIsTheSame && !firstLoadOfController) {
        //                             return;
        //                         }
        //
        //                         // success
        //                         vm.currentPipeline = res;
        //                         firstLoadOfController = false;
        //
        //                         //Sorts the stages by name and by Execution ID
        //                         vm.sortedData = runManagementService.groupStages(vm.currentPipeline);
        //
        //                         //Adds a new array LastRun in the sortedData, to present it in the view
        //                         vm.groupStages = runManagementService.addLastExecution(vm.sortedData);
        //
        //                         //Default LastRunSelected - the last ExecutionID in the array
        //                         for (var i = 0; i < vm.groupStages.length; i += 1) {
        //                             var runsLength = vm.groupStages[i].Runs.length;
        //                             vm.groupStages[i].LastRunSelected = vm.groupStages[i].Runs[runsLength - 1].ExecutionID;
        //                         }
        //
        //
        //
        //                         //Save the last result, used form comparing and checking for changes
        //                         lastResult = res;
        //                         resLength = res.length;
        //
        //                         //Initialize the Selected Stage, if this is the first call to the controller. Otherwise the selected stage is defined by the user!
        //                         if (isInitial) {
        //                             vm.selectedStage = vm.groupStages[0];
        //
        //
        //                             var runsLength = vm.selectedStage.Runs.length - 1;
        //                             var jobsLength = vm.selectedStage.Runs[0].Jobs.length - 1;
        //                             vm.selectedJob = vm.selectedStage.Runs[runsLength].Jobs[jobsLength];
        //                             nameOfStage = vm.selectedStage.Runs[0].Name;
        //
        //                             //Initially displays the last run on each stage
        //                             vm.selectedRunIndex = vm.selectedStage.Runs.length - 1;
        //                             vm.selectedRunID = vm.selectedRunIndex;
        //
        //                             vm.selectedStageRunsShow = vm.groupStages[0];
        //                         }
        //                         if (!isInitial) {
        //                             //Gets the numbers in the menu left from the console
        //                             vm.getJobResults();
        //
        //                             //This if statements prevents clearing of Run selection
        //                             if (vm.selectedRunID != undefined) {
        //                                 vm.groupStages[vm.selectedStageIndexInMainDB].LastRunSelected = vm.selectedRunID;
        //                             }
        //
        //                             vm.selectedStage = vm.groupStages[vm.selectedStageIndexInMainDB];
        //                             nameOfStage = vm.selectedStage.Runs[0].Name;
        //                             if (vm.selectedStage.Runs[vm.selectedRunIndex] != undefined) {
        //                                 vm.selectedJob = vm.selectedStage.Runs[vm.selectedRunIndex].Jobs[vm.jobIndex];
        //                             }
        //
        //                             for (var i = 0; i < vm.groupStages.length; i++) {
        //                                 if (vm.groupStages[i].Runs[0].ExecutionID == 0 && vm.groupStages[i].Runs[0].length > 0) {
        //                                     vm.groupStages[i].Runs.shift();
        //                                 }
        //                             }
        //
        //                         }
        //                         isInitial = false;
        //
        //                     }, function (err) {
        //                         console.log(err);
        //                     })
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     }
        //
        //
        // };

        // vm.getAllMaterials = function () {
        //     pipeConfig.getAllMaterials(vm.pipelineName)
        //         .then(function (res) {
        //             vm.allMaterials = res;
        //         }, function (err) {
        //
        //         })
        // };
        //
        // vm.reRunStageJobs = function (stage) {
        //
        //     vm.selectedStage = stage;
        //     vm.deSelectAll();
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         pipeExec.scheduleStageWithJobs(vm.pipelineName, vm.pipelineExecutionID, vm.selectedStage.Runs[0].Name, vm.selectedJobsForReRun, token)
        //             .then(function (res) {
        //                     vm.disabledBtn = false;
        //                     console.log(res);
        //                 },
        //                 function (err) {
        //                     vm.disabledBtn = false;
        //                     console.log(err);
        //                 });
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //             .then(function (res) {
        //                 var token = res.access_token;
        //                 pipeExec.scheduleStageWithJobs(vm.pipelineName, vm.pipelineExecutionID, vm.selectedStage.Runs[0].Name, vm.selectedJobsForReRun, token)
        //                     .then(function (res) {
        //                             vm.disabledBtn = false;
        //                             console.log(res);
        //                         },
        //                         function (err) {
        //                             vm.disabledBtn = false;
        //                             console.log(err);
        //                         });
        //             }, function (err) {
        //                 vm.disabledBtn = false;
        //                 console.log(err);
        //             })
        //     }
        //     vm.selectedJobsForReRun = [];
        // };


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

        //Get all job results numbers to populate the list left from the console
        // vm.getJobResults = function () {
        //     var allJobs = {
        //         skippedJobs: 0,
        //         scheduledJobs: 0,
        //         inProgress: 0,
        //         passedJobs: 0,
        //         failedJobs: 0
        //     };
        //
        //     //Prevents initial console errors
        //     //if (vm.selectedStage != undefined && vm.selectedStage.LastRunSelected >= 1 && vm.selectedStage.Runs[vm.selectedStage.LastRunSelected - 1] != undefined) {
        //     if(vm.currentPipelineRunStages.length > 0 ) {
        //         for (var i = 0; i < vm.currentPipelineRunStages[vm.toggleRun][vm.lastRunSelected - 1].jobs.length; i += 1) {
        //             if (vm.currentPipelineRunStages[vm.toggleRun][vm.lastRunSelected - 1].jobs[i].status == 'AWAITING') {
        //                 allJobs.skippedJobs += 1;
        //             }
        //             if (vm.currentPipelineRunStages[vm.toggleRun][vm.lastRunSelected - 1].jobs[i].status == 'SCHEDULED') {
        //                 allJobs.scheduledJobs += 1;
        //             }
        //             if (vm.currentPipelineRunStages[vm.toggleRun][vm.lastRunSelected - 1].jobs[i].status == 'RUNNING') {
        //                 allJobs.inProgress += 1;
        //             }
        //             if (vm.currentPipelineRunStages[vm.toggleRun][vm.lastRunSelected - 1].jobs[i].status == 'PASSED') {
        //                 allJobs.passedJobs += 1;
        //             }
        //             if (vm.currentPipelineRunStages[vm.toggleRun][vm.lastRunSelected - 1].jobs[i].status == 'FAILED') {
        //                 allJobs.failedJobs += 1;
        //             }
        //         }
        //         console.log(vm.lastRunSelected);
        //         //}
        //     }
        //     return allJobs;
        // };


        //Selects all input job checkboxes
        // vm.selectAll = function () {
        //     var checkboxes = document.getElementsByName('jobCheck');
        //     for (var i = 0, n = checkboxes.length; i < n; i++) {
        //         if (checkboxes[i].checked == false) {
        //             //Makes all checkboxes checked
        //             checkboxes[i].checked = true;
        //             var jobNameToPush = checkboxes[i].value;
        //
        //             //Adds a job name for a stage re-run
        //             vm.selectedJobsForReRun.push(jobNameToPush);
        //         }
        //     }
        //
        // };
        //
        // vm.getSelectedJobsForReRun = function () {
        //     var checkboxes = document.getElementsByName('jobCheck');
        //     for (var i = 0, n = checkboxes.length; i < n; i++) {
        //         if (checkboxes[i].checked == true) {
        //             //Gets all checked boxes
        //             var jobNameToPush = checkboxes[i].value;
        //
        //             //Adds a job name for a stage re-run
        //             vm.selectedJobsForReRun.push(jobNameToPush);
        //         }
        //     }
        //
        // };
        //
        // //Deselects all input job checkboxes
        // vm.deSelectAll = function () {
        //     var checkboxes = document.getElementsByName('jobCheck');
        //     for (var i = 0, n = checkboxes.length; i < n; i++) {
        //         if (checkboxes[i].checked == true) {
        //             checkboxes[i].checked = false;
        //         }
        //     }
        // };
        //
        // vm.setSelectedStageForReRun = function (stage) {
        //     vm.selectedStage = stage;
        // };
        //
        // vm.selectAlllJobs = function (stage) {
        //     for (var i = 0; i < stage.Runs[stage.Runs.length - 1].Jobs.length; i++) {
        //         vm.selectedJobsForReRun.push(stage.Runs[stage.Runs.length - 1].Jobs[i].Name);
        //     }
        // };

        //Initialize the controller
        // vm.getAll(true);
        // vm.getAllMaterials();

        //Reloads the data at a given interval. Parameter is false, because it is not the initial loading
        // var intervalRunManagement = $interval(function () {
        //     vm.getAll(false);
        // }, 4000);
        //
        // $scope.$on('$destroy', function () {
        //     $interval.cancel(intervalRunManagement);
        //     intervalRunManagement = undefined;
        // });
    }]);

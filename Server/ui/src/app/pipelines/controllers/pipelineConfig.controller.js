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
    .controller('PipelineConfigController', function($state, $interval, $timeout, $scope, $window, authDataService, viewModel, pipeConfigService, loggerService) {
        var vm = this;
        vm.toggleLogo = 1;
        vm.materialType = "git";

        vm.pipeline = {};
        vm.updatedPipeline = {};
        vm.newPipeline = {};

        vm.stage = {};
        vm.updatedStage = {};
        vm.newStage = {};

        vm.job = {};
        vm.updatedJob = {};
        vm.newJob = {};
        vm.newArtifact = {};

        vm.material = {};
        vm.materials = {};
        vm.newMaterial = {};

        vm.allPermissions = [];
        vm.allPipelineRuns = [];
        vm.allPipelines = angular.copy(viewModel.allPipelines);;
        vm.allJobs = [];
        vm.allTasks = [];
        vm.allStages = [];
        vm.allMaterials = angular.copy(viewModel.allMaterialDefinitions);
        vm.allPipelineVars = {};
        vm.allStageVars = {};
        vm.allJobVars = {};

        vm.pipelineIndex = {};
        vm.stageIndex = {};
        vm.jobIndex = {};
        vm.materialIndex = {};

        vm.shouldUseLatest = "";

        vm.currentStageRuns = [];

        vm.currentView = '';

        vm.tabView = '';

        vm.isFirstPipelineLoad = true;
        vm.isFirstStageLoad = true;
        vm.isFirstJobLoad = true;

        vm.wizardInfo = {
            labels: {
                autoSchedule: 'Auto Scheduling',
                name: 'Name',
                pipelineName: 'Pipeline Name',
                gitUrl: 'Git URL',
                branch: 'Branch',
                username: 'Username',
                password: 'Password',
                tfsDomain: 'Domain',
                projectPath: 'Path',
                stageName: 'Stage Name',
                trigger: 'Trigger option',
                newLabel: 'new'
            },
            placeholders: {
                pipelineName: 'Enter your pipeline name',
                gitUrl: 'Enter your git url',
                username: 'Enter TFS username',
                password: 'Enter TFS password',
                tfsDomain: 'Enter TFS domain',
                projectPath: 'Enter TFS project path',
                stageName: 'Enter stage name',
                materialName: 'Enter material name',
                gitUsername: 'Enter GIT username',
                gitPassword: 'Enter GIT password'
            }
        };

        vm.popOverOptions = {
            popOverTitles: {
                automaticScheduling: 'If selected, the Pipeline will trigger automatically, creating a new run, when its Material is updated.',
                triggeredManually: 'True - set by default. If selected the Stage will trigger automatically if the one before it completed successfully (has StatusPASSED). \n\n False - if selected, the execution of the Pipeline will stop at this Stage. Both Pipeline and Stage will be with Status AWAITING until the user decides to continue the process and manually triggers the Stage.',
                jobCount: 'Number of Jobs in the Stage.',
                triggeredManuallyGeneral: 'On Success - set by default. If selected the Stage will trigger automatically if the one before it completed successfully (has StatusPASSED). \n\n Manual - if selected, the execution of the Pipeline will stop at this Stage. Both Pipeline and Stage will be with Status AWAITING until the user decides to continue the process and manually triggers the Stage.',
                jobResources: 'Resources, also called Tags, can be used to route Jobs to specific Agents. A Job with a specific Resource can be executed only by an Agent with the same Resource assigned. Also aJob may have more than one resource assigned.',
                environmentVariableName: 'The word or words used to refer to the Environment Variable.',
                environmentVariableValue: 'The value that is actually used when the Exec Task is executed.',
                environmentVariableSecured: 'If checked, the Value is displayed by ****** and it will be known only to users able to edit the Pipeline the Environment Variable belongs to.',
                environmentVariable: 'Environment Variables allow the User to hide long values, like directory paths and commands, behind simple words (e.g., %PATH% or %COMMAND%), simplifying the creation of Exec Tasks that reuse the same values in their Arguments.',
                taskCommand: 'The options chosen when the Task was created.',
                taskType: 'There are 4 types of Tasks - Exec, Fetch Material, Upload Artifact and Fetch Artifact.',
                taskCondition: 'All Tasks have a Run If Condition option. Available options are Passed (set by default), Failed and Any. \n\n Passed - if selected, the Task will be executed only if the previous one completed successfully. If the Task is first in order, then the Run If Condition is ignored. \n\n Failed - if selected, the Task will be executed only if the previous one failed to complete successfully. \n\n Any - if selected, the Task will be executed regardless of the status of the previous one.',
                execCommand: 'Executable name to run, usually for Linux /bin/bash, for Windows cmd',
                execArguments: 'Arguments to be passed to the executable (e.g., Linux - -c cp -r dir dir1 , Windows /c echo %PATH%).',
                execWorkingDir: 'The directory in which the process will run, starting at Agent/Pipelines/\<PipelineName>/.',
                runIfCondition: 'Passed - if selected, the Task will be executed only if the previous one completed successfully. If the Task is first in order, then the Run If Condition is ignored. \n\n Failed - if selected, the Task will be executed only if the previous one failed to complete successfully. \n\n Any - if selected, the Task will be executed regardless of the status of the previous one.',
                ignoreErrors: 'If selected, the Task\'s status is set to PASSED, regardless if it completed successfully or not.',
                pipelineToBeSelected: 'The name of the Pipeline which previously uploaded the Artifact. The user can select any Pipeline for which he/she has at least permission type Viewer.',
                runToBeSelected: 'The specific Pipeline run which previously uploaded the Artifact. The latest option is useful when the user wants to fetch an Artifact the will be uploaded with the current execution of the Pipeline.',
                fetchArtifactSource: 'The path to the Artifact starting at Server/Artifacts/\<PipelineName>/<PipelineRun>/ If no Source is selected, the entire contents of the folder are fetched.',
                fetchArtifactDestination: 'Folder(s) to be created where the Artifact is fetched. If no Destination is selected the Artifact is saved in Agent/Pipelines/\<PipelineName>/ with no additional folders.',
                uploadArtifactSource: 'The path to the Artifact starting at Agent/Pipelines/\<PipelineName>/. If no Source is selected the entire contents of the folder are uploaded.',
                uploadArtifactDestination: 'Folder(s) to be created where the Artifact is stored. If no Destination is selected the Artifact is saved in Server/Arttifacts/\<PipelineName>/<PipelineRun>/ with no additional folders.',
                pipeline: 'The Pipeline allows crafting the entire application release process from start to finish. A Pipeline consists of Stages, which in turn consist of Jobs, which consist of Tasks.',
                stage: 'A Stage can be thought of as a container for Jobs. Stages are a major component when it comes to automation release processing. Each step of building a new feature into a large project can be separated into Stages.',
                job: 'A Job consists of multiple Tasks. Jobs are assigned to Agents and then executed by them.',
                task: 'A Task is an action that is performed on a server/machine or inside a container where an Agent is installed.',
                materialToBeSelected: 'The predefined material to be fetched.'
            }
        };

        vm.windowWidth = $window.innerWidth;

        $window.onresize = function(event) {
            $timeout(function() {
                vm.windowWidth = $window.innerWidth;
                $scope.$apply();
                // debugger;
            });
        };

        vm.initTabView = function() {
            if(vm.state.params.jobName){
                vm.currentView = 'job';
                vm.tabView = 'jobSettings';
            } else if(vm.state.params.stageName){
                vm.currentView = 'stage';
                vm.tabView = 'stageSettings';
            } else if(vm.state.params.pipelineName) {
                vm.currentView = 'pipeline';
                vm.tabView = 'pipelineSettings';
            }
        };

        vm.initPipelineTabs = function() {
            var currentState = $state.current.name;

            if(currentState.indexOf('settings') !== -1){
                vm.tabView = 'pipelineSettings';
            } else if(currentState.indexOf('stages') !== -1){
                vm.tabView = 'pipelineStages';
            } else if(currentState.indexOf('variables') !== -1){
                vm.tabView = 'pipelineVariables';
            }
        };

        vm.initStageTabs = function() {
            var currentState = $state.current.name;

            if(currentState.indexOf('settings') !== -1){
                vm.tabView = 'stageSettings';
            } else if(currentState.indexOf('jobs') !== -1){
                vm.tabView = 'stageJobs';
            } else if(currentState.indexOf('variables') !== -1){
                vm.tabView = 'stageVariables';
            }
        };

        vm.initJobTabs = function() {
            var currentState = $state.current.name;

            if(currentState.indexOf('settings') !== -1){
                vm.tabView = 'jobSettings';
            } else if(currentState.indexOf('tasks') !== -1){
                vm.tabView = 'jobTasks';
            } else if(currentState.indexOf('variables') !== -1){
                vm.tabView = 'jobVariables';
            } else if(currentState.indexOf('resources') !== -1){
                vm.tabView = 'jobResources';
            }
        };

        vm.selectTabView = function(view) {
            vm.tabView = view;
        };

        // vm.selectInitialTabView = function(view, pipelineExists, stageExists, jobExists) {
        //     if(jobExists){
        //         vm.tabView = view;
        //     }
        // };

        $scope.$on('$includeContentLoaded', function(){
            $timeout(function() {
                if(vm.currentView == 'pipeline'){
                    $(window).scrollTop($('#pipelineConfig').position().top);
                } else if(vm.currentView == 'stage'){
                    $(window).scrollTop($('#stageConfig').position().top);
                } else if(vm.currentView == 'job'){
                    $(window).scrollTop($('#jobConfig').position().top);

                }
            });
        });

        $scope.$on('$locationChangeSuccess', function(){
            $timeout(function() {
                if(vm.currentView == 'pipeline' && $('#pipelineConfig').position()){
                    $(window).scrollTop($('#pipelineConfig').position().top);
                } else if(vm.currentView == 'stage' && $('#stageConfig').position()){
                    $(window).scrollTop($('#stageConfig').position().top);
                } else if(vm.currentView == 'job' && $('#jobConfig').position()){
                    $(window).scrollTop($('#jobConfig').position().top);
                }
            });
        });

        // angular.element(window.document.body).ready(function() {
        //     debugger;
        //     if(vm.currentView == 'pipeline'){
        //         $(window).scrollTop($('#pipelineConfig').position().top);
        //     } else if(vm.currentView == 'stage'){
        //         $(window).scrollTop($('#stageConfig').position().top);
        //     } else if(vm.currentView == 'job'){
        //         $(window).scrollTop($('#jobConfig').position().top);
        //
        //     }
        // });

        // vm.scrollToPipelines = function() {
        //     // debugger;
        //     if($(window).scrollTop($('#pipelineConfig').position())){
        //     }
        // };
        //
        // vm.scrollToStages = function() {
        //     // debugger;
        //     if($(window).scrollTop($('#stageConfig').position())){
        //     }
        // };
        //
        // vm.scrollToJobs = function() {
        //     // debugger;
        //     if($(window).scrollTop($('#jobConfig').position())){
        //     }
        // };

        $scope.$watch(function() {
            return viewModel.allMaterialDefinitions;
        }, function(newVal, oldVal) {
            vm.allMaterials = angular.copy(viewModel.allMaterialDefinitions);
            loggerService.log('Material watcher :');
            loggerService.log(vm.allPipelines);
        }, true);

        $scope.$watch(function() {
            return viewModel.allPipelines
        }, function(newVal, oldVal) {
            vm.allPipelines = angular.copy(viewModel.allPipelines);
            vm.getPipelineForConfig(vm.state.params.pipelineName);
            loggerService.log('Pipeline watcher :');
            loggerService.log(vm.allPipelines);
        }, true);

        $scope.$watch(function() {
            return viewModel.allPipelineGroups
        }, function(newVal, oldVal) {
            vm.allPipelineGroups = angular.copy(viewModel.allPipelineGroups);

            vm.allPipelinesForTask = [];

            vm.allPipelineGroups.forEach(function(currentPipelineGroup, pipelineGroupIndex, pipelineGroupArray) {
                currentPipelineGroup.pipelines.forEach(function(currentPipeline, pipelineIndex, pipelineArray){
                    vm.allPipelinesForTask.push(currentPipeline);
                });
            });
        });

        vm.stageDeleteButton = false;
        vm.jobDeleteButton = false;
        vm.materialDeleteButton = false;
        vm.variableDeleteButton = false;
        vm.taskDeleteButton = false;

        vm.task = {};
        vm.taskIndex = -1;
        vm.newTask = {};
        vm.updatedTask = {};

        vm.state = $state;
        vm.currentPipeline = vm.state.params.pipelineName;
        vm.currentGroup = vm.state.params.groupName;
        vm.currentStage = vm.state.params.stageName;
        vm.currentJob = vm.state.params.jobName;

        vm.sortableOptions = {};

        vm.newPipelineVar = {};

        vm.otherStageExpanded = false;

        //region select manipulation
        vm.isExpanded = function(stageName) {
            // vm.isOtherStageExpanded(stageName);
            return stageName == vm.currentStage.name;
        };

        vm.isOtherStageExpanded = function(stageName) {
            // if($state.params.stageName == stageName) {
            //     vm.otherStageExpanded = false;
            // } else {
            //     vm.otherStageExpanded = true;
            // }

            vm.pipeline.stageDefinitions.forEach(function(currentStage, stageIndex, stageArray) {
                if (currentStage.name != stageName) {
                    var isExpanded = vm.isExpanded(currentStage.name);
                    if (isExpanded) {
                        return true;
                    }
                }
            });
            return false;
        };

        vm.isPipelineSelected = function() {
            return $state.params.stageName == undefined && $state.params.jobName == undefined;
        };

        vm.isStageSelected = function(stageName) {
            return $state.params.stageName == stageName && $state.params.jobName == undefined;
        };

        vm.isJobSelected = function(jobName) {
            return $state.params.jobName == jobName && $state.params.stageName != undefined;
        };
        //endregion

        vm.setSpecific = function() {
            vm.specificVersion = true;
            vm.latestVersion = false;
        };

        vm.setLatest = function() {
            vm.latestVersion = true;
            vm.specificVersion = false;
        };

        vm.close = function() {
            vm.newMaterial = {};
            vm.newTask = {};
            vm.newStage = {};
            vm.newMaterial = {};
            vm.newJob = {};
            vm.task.Type = '';
            vm.newPipelineVar = {};
            vm.newStageVar = {};
            vm.newJobVar = {};
            vm.environmentVariableUtils.pipelines.variableToEdit = {};
            vm.environmentVariableUtils.stages.variableToEdit = {};
            vm.environmentVariableUtils.jobs.variableToEdit = {};
            vm.materialType = "";
            vm.resourceToAdd = "";
            vm.oldResource = "";
            vm.newResource = "";
            vm.resourceToDelete = "";
            vm.stageToDelete = {};
            vm.jobToDelete = {};
        };

        vm.closeTabsModal = function() {
            addTabForm.tabName.value = '';
            addTabForm.tabPath.value = '';
        };

        vm.filteredMaterialDefinitions = [];
        vm.taskMaterial = {};
        vm.getPipelineForConfig = function(pipeName) {
            $scope.$on('$locationChangeStart', function(event) {
                if(!$state.params.stageName && !$state.params.jobName){
                    vm.currentView = 'pipeline';
                }
            });

            if (vm.allPipelines != null && vm.allPipelines.length > 0) {
                vm.allPipelines.forEach(function(currentPipeline, index, array) {
                    if (currentPipeline.name == pipeName) {
                        vm.pipeline = array[index];
                        vm.allPipelineVars = vm.pipeline.environmentVariables;
                        vm.pipelineIndex = index;
                        loggerService.log('PipelineConfigController.getPipelineForConfig :');
                        loggerService.log(vm.pipeline);

                        currentPipeline.materialDefinitionIds.forEach(function(currentDefinition, definitionIndex, definitionArray) {
                            vm.allMaterials.forEach(function(currentMaterial, materialIndex, materialArray) {
                                if (currentDefinition === currentMaterial.id) {
                                    var isContained = false;
                                    vm.filteredMaterialDefinitions.forEach(function(currentFilteredMaterial, filteredMaterialIndex, filteredMaterialArray) {
                                        if (currentFilteredMaterial.id === currentMaterial.id) {
                                            isContained = true;
                                        }
                                    });
                                    if (!isContained) {
                                        vm.filteredMaterialDefinitions.push(currentMaterial);
                                    }
                                }
                            });
                        });
                    }
                });


                //vm.pipeline = pipeName;

                vm.newStage = {};
                vm.newMaterials = {};

                vm.updatedPipeline.name = vm.pipeline.name;
                vm.updatedPipeline.autoScheduling = vm.pipeline.isAutoSchedulingEnabled;
                vm.currentPipeline = vm.pipeline;
            } else {
                setTimeout(function() {
                    vm.getPipelineForConfig(pipeName);
                }, 500);
            }
        };

        vm.getPipelineForTask = function(pipeline) {
            vm.newTask.stage = '';
            vm.selectedPipelineStages = {};
            vm.selectedStageJobs = {};
            for (var i = 0; i < vm.allPipelines.length; i++) {
                if (vm.allPipelines[i].name == pipeline) {
                    vm.selectedPipelineStages = vm.allPipelines[i].stageDefinitions;
                    break;
                }
            }
        };

        vm.getRunsFromPipelineDefinition = function (pipeline) {
            vm.newTask.pipelineRun = '';
            vm.selectedPipelineForTask = JSON.parse(angular.copy(pipeline));
            vm.currentPipelineRuns = [];
            vm.allPipelinesForTask.forEach(function (currentPipeline, pipelineIndex, pipelineArray) {
                // if(vm.selectedPipelineForTask.id == currentPipelineRun.pipelineDefinitionId){
                //     vm.currentPipelineRuns.push(currentPipelineRun);
                // }
                if(currentPipeline.id == vm.selectedPipelineForTask.id) {
                    currentPipeline.pipelineExecutionIds.forEach(function(currentExecutionId, executionIdArray, executionIdIndex){
                        vm.currentPipelineRuns.push(currentExecutionId);
                    });
                }
            });
        };

        vm.getRunsFromPipelineDefinitionForUpdate = function (name) {
            vm.selectedPipelineForTaskUpdateName = angular.copy(name);
            vm.currentPipelineRuns = [];
            vm.allPipelinesForTask.forEach(function (currentPipelineRun, runIndex, runArray) {
                if(vm.selectedPipelineForTaskUpdateName == currentPipelineRun.name){
                    // vm.currentPipelineRuns.push(currentPipelineRun);

                    currentPipelineRun.pipelineExecutionIds.forEach(function(currentExecutionId, executionIdArray, executionIdIndex) {
                        vm.currentPipelineRuns.push(currentExecutionId);
                    });
                }
            });
        };

        vm.selectRunFromPipelineDefinition = function (pipelineRun) {
            vm.selectedPipelineRunForTask = JSON.parse(angular.copy(pipelineRun));
        };

        vm.selectRunFromPipelineDefinitionUpdate = function (executionId) {
            vm.currentPipelineRuns.forEach(function (currentPipelineRun, runIndex, runArray) {
                if(currentPipelineRun == executionId) {
                    vm.updatedTask.pipelineRun = angular.copy(currentPipelineRun);
                }
            });
        };

        vm.getPipelineForTaskById = function(id) {
            vm.allPipelines.forEach(function(currentPipeline, pipelineIndex, pipelineArray) {
                if (currentPipeline.id == id) {
                    vm.selectedPipelineStages = angular.copy(currentPipeline.stageDefinitions);
                }
            });
        };

        vm.getPipelineForTaskUpdate = function (name) {
            vm.allPipelinesForTask.forEach(function(currentPipeline, pipelineIndex, pipelineArray) {
                if (currentPipeline.name == name) {
                    vm.updatedTask.pipelineObject = angular.copy(currentPipeline);
                }
            });
        };

        vm.getRunForTaskUpdate = function (executionId) {
            vm.currentPipelineRuns = [];
            // vm.allPipelines.forEach(function(currentPipeline, pipelineIndex, pipelineArray) {
            //     if (vm.updatedTask.pipelineObject && currentPipeline.name == vm.updatedTask.pipelineObject.name) {
            //         vm.allPipelineRuns.forEach(function(currentPipelineRun, runIndex, runArray) {
            //             if (currentPipelineRun.pipelineDefinitionName == currentPipeline.name) {
            //                 vm.currentPipelineRuns.push(currentPipelineRun);
            //                 if(currentPipelineRun.executionId == executionId) {
            //                     vm.updatedTask.pipelineRun = angular.copy(currentPipelineRun);
            //                 }
            //             }
            //         });
            //     }
            // });

           vm.allPipelinesForTask.forEach(function(currentPipeline, pipelineIndex, pipelineArray) {
               if(vm.updatedTask.pipelineObject && currentPipeline.name == vm.updatedTask.pipelineObject.name) {
                   if(vm.updatedTask.shouldUseLatestRun) {
                       vm.updatedTask.pipelineRun = -1;
                   }
                   currentPipeline.pipelineExecutionIds.forEach(function (currentExecutionId, executionIdArray, executionIdIndex) {
                       vm.currentPipelineRuns.push(currentExecutionId);
                       if(!vm.updatedTask.shouldUseLatestRun && currentExecutionId == executionId) {
                           vm.updatedTask.pipelineRun = angular.copy(currentExecutionId);
                       }
                   });
               }
           });

            // vm.currentPipelineRuns.sort(function(a, b) {
            //     return a.executionId - b.executionId;
            // });

        };

        vm.editPipeline = function(pipeline) {
            var newPipeline = angular.copy(vm.allPipelines[vm.pipelineIndex]);
            newPipeline.name = pipeline.name;
            newPipeline.isAutoSchedulingEnabled = pipeline.autoScheduling;
            $state.go('index.pipelineConfig.pipeline.general', {
                groupName: vm.pipeline.groupName,
                pipelineName: pipeline.name,
                autoScheduling: pipeline.isAutoSchedulingEnabled
            });

            pipeConfigService.updatePipelineDefinition(newPipeline);
        };

        vm.getStage = function(stage) {
            vm.currentView = 'stage';
            vm.allPipelines[vm.pipelineIndex].stageDefinitions.forEach(function(currentStage, index, array) {
                if (currentStage.name == stage.name) {
                    vm.stage = array[index];
                    vm.allStageVars = vm.stage.environmentVariables;
                    vm.stageIndex = index;

                    loggerService.log('PipelineConfigController.getStage :');
                    loggerService.log(vm.stage);
                }
            });
            vm.stageDeleteButton = false;
            //vm.stage = res;

            vm.newJob = {};
            vm.newStageVariable = {};

            vm.updatedStage.name = vm.stage.name;
            vm.updatedStage.isTriggeredManually = vm.stage.isTriggeredManually;

            vm.currentStage = stage;
        };

        vm.setStageToDelete = function(stage) {
            vm.stageToDelete = stage;
        };

        vm.setJobToDelete = function(job) {
            vm.jobToDelete = job;
        };

        vm.getStageByName = function(stageName) {
            // if(vm.isFirstStageLoad && stageName){
            //     vm.currentView = 'stage';
            //     vm.isFirstStageLoad = false;
            // }
            vm.allPipelines[vm.pipelineIndex].stageDefinitions.forEach(function(currentStage, index, array) {
                if (currentStage.name == stageName) {
                    vm.stage = array[index];
                    vm.stageIndex = index;

                    loggerService.log('PipelineConfigController.getStageByName :');
                    loggerService.log(vm.stage);
                }
            });
            vm.stageDeleteButton = false;
            //vm.stage = res;

            vm.newJob = {};
            vm.newStageVariable = {};

            vm.updatedStage.name = vm.stage.name;
            vm.updatedStage.isTriggeredManually = vm.stage.isTriggeredManually;

            vm.currentStage = vm.stage;
        };

        vm.getStageForTask = function(stage) {
            vm.newTask.job = '';
            vm.selectedStageJobs = {};
            for (var i = 0; i < vm.selectedPipelineStages.length; i++) {
                if (vm.selectedPipelineStages[i].name == stage) {
                    vm.selectedStageJobs = vm.selectedPipelineStages[i].jobDefinitions;
                    break;
                }
            }
        };

        vm.getStageForTaskById = function(id) {
            vm.selectedPipelineStages.forEach(function(currentStage, stageIndex, stageArray) {
                if (currentStage.id == id) {
                    vm.selectedStageJobs = angular.copy(currentStage.jobDefinitions);
                }
            });
        };

        vm.newStage.selectedMaterialForNewStage = {};
        vm.addStage = function(newStage, stageForm) {
            if (newStage.jobDefinitions.taskDefinitions.type == 'EXEC') {
                var stage = {
                    name: newStage.name,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    jobDefinitions: [{
                        name: newStage.jobDefinitions.name,
                        pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                        taskDefinitions: [{
                            pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                            type: newStage.jobDefinitions.taskDefinitions.type,
                            command: newStage.jobDefinitions.taskDefinitions.command,
                            arguments: newStage.jobDefinitions.taskDefinitions.arguments,
                            workingDirectory: newStage.jobDefinitions.taskDefinitions.workingDirectory,
                            runIfCondition: newStage.jobDefinitions.taskDefinitions.runIfCondition,
                            isIgnoringErrors: newStage.jobDefinitions.taskDefinitions.isIgnoringErrors || false
                        }]
                    }]
                };
            }
            if (newStage.jobDefinitions.taskDefinitions.type == 'FETCH_ARTIFACT') {
                var stage = {
                    name: newStage.name,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    jobDefinitions: [{
                        name: newStage.jobDefinitions.name,
                        pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                        taskDefinitions: [{
                            type: newStage.jobDefinitions.taskDefinitions.type,
                            pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                            pipelineDefinitionName: vm.allPipelines[vm.pipelineIndex].name,
                            designatedPipelineDefinitionName: JSON.parse(newStage.jobDefinitions.taskDefinitions.pipelineObject).name,
                            designatedPipelineDefinitionId: JSON.parse(newStage.jobDefinitions.taskDefinitions.pipelineObject).id,
                            source: (typeof newStage.jobDefinitions.taskDefinitions.source === 'undefined') ? '' : newStage.jobDefinitions.taskDefinitions.source,
                            destination: (typeof newStage.jobDefinitions.taskDefinitions.destination === 'undefined') ? '' : newStage.jobDefinitions.taskDefinitions.destination,

                            runIfCondition: newStage.jobDefinitions.taskDefinitions.runIfCondition
                        }]
                    }]
                };
                if(newStage.jobDefinitions.taskDefinitions.pipelineRun == 'true'){
                    stage.jobDefinitions[0].taskDefinitions[0].shouldUseLatestRun = true;
                } else {
                    stage.jobDefinitions[0].taskDefinitions[0].designatedPipelineExecutionId = newStage.jobDefinitions.taskDefinitions.pipelineRun;
                }
            }
            if (newStage.jobDefinitions.taskDefinitions.type == 'FETCH_MATERIAL') {
                var selectedMaterialForJob = JSON.parse(vm.newStage.selectedMaterialForNewStage);
                var stage = {
                    name: newStage.name,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    jobDefinitions: [{
                        name: newStage.jobDefinitions.name,
                        pipelineName: vm.allPipelines[vm.pipelineIndex].name,
                        pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                        taskDefinitions: [{
                            // var selectedMaterial = JSON.parse(newTask.material);
                            pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                            pipelineName: vm.allPipelines[vm.pipelineIndex].name,
                            materialDefinitionId: selectedMaterialForJob.id,
                            type: newStage.jobDefinitions.taskDefinitions.type,
                            materialType: selectedMaterialForJob.type,
                            materialName: selectedMaterialForJob.name,
                            destination: selectedMaterialForJob.name,
                            runIfCondition: newStage.jobDefinitions.taskDefinitions.runIfCondition
                                // pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                                // type: newStage.jobDefinitions.taskDefinitions.type,
                                // materialName: selectedMaterialForJob.name,
                                // runIfCondition: newStage.jobDefinitions.taskDefinitions.runIfCondition
                        }]
                    }]
                };
            }
            if (newStage.jobDefinitions.taskDefinitions.type == 'UPLOAD_ARTIFACT') {
                var stage = {
                    name: newStage.name,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    jobDefinitions: [{
                        name: newStage.jobDefinitions.name,
                        pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                        taskDefinitions: [{
                            pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                            type: newStage.jobDefinitions.taskDefinitions.type,
                            source: (typeof newStage.jobDefinitions.taskDefinitions.source === 'undefined') ? '' : newStage.jobDefinitions.taskDefinitions.source,
                            destination: (typeof newStage.jobDefinitions.taskDefinitions.destination === 'undefined') ? '' : newStage.jobDefinitions.taskDefinitions.destination,
                        }]
                    }]
                };
            }
            pipeConfigService.addStageDefinition(stage);
            loggerService.log('PipelineConfigController.addStage :');
            loggerService.log(stage);
            stageForm.$setPristine();
            stageForm.$setUntouched();
            stageForm.stageName.$setViewValue('');
            vm.resetObjectProperties(newStage);
            stageForm.stageName.$render();

        };

        vm.resetObjectProperties = function(object) {
            for (var property in object) {
                if (object[property]) {
                    object[property] = '';
                }
            }
        }

        vm.editStage = function(stage) {
            var newStage = angular.copy(vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex]);
            newStage.name = stage.name;
            newStage.isTriggeredManually = stage.isTriggeredManually;
            $state.go('index.pipelineConfig.stage.settings', {
                groupName: vm.pipeline.groupName,
                pipelineName: vm.pipeline.name,
                stageName: stage.name
            });
            pipeConfigService.updateStageDefinition(newStage);
            loggerService.log('PipelineConfigController.editStage :');
            loggerService.log(newStage);
        };

        vm.deleteStage = function(stage) {
            pipeConfigService.deleteStageDefinition(stage);
            loggerService.log('PipelineConfigController.deleteStage :');
            loggerService.log(stage);
        };

        vm.selectedTask = {};
        vm.selectedJobTasks = [];
        vm.getJob = function(job) {
            vm.currentView = 'job';
            if (vm.job != null) {
                vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions.forEach(function(currentJob, index, array) {
                    if (currentJob.name == job.name) {
                        vm.job = array[index];
                        vm.allJobVars = vm.job.environmentVariables;
                        vm.jobIndex = index;
                        vm.selectedJobTasks = angular.copy(array[index].taskDefinitions);

                        loggerService.log('PipelineConfigController.getJob :');
                        loggerService.log(vm.job);
                    }
                });



                vm.jobDeleteButton = false;
                //vm.job = res;

                vm.newTask = {};
                vm.newArtifact = {};
                vm.newJobVariable = {};
                vm.newCustomTab = {};

                vm.updatedJob.name = vm.job.name;

                vm.currentJob = job;
            }
        };

        vm.getJobByName = function(jobName) {
            // if(vm.isFirstJobLoad && jobName){
            //     vm.currentView = 'job';
            //     vm.isFirstJobLoad = false;
            // }
            if (vm.job != null) {
                vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions.forEach(function(currentJob, index, array) {
                    if (currentJob.name == jobName) {
                        vm.job = array[index];
                        vm.jobIndex = index;

                        loggerService.log('PipelineConfigController.getJobByName :');
                        loggerService.log(vm.job);
                    }
                });

                vm.jobDeleteButton = false;
                //vm.job = res;

                vm.newTask = {};
                vm.newArtifact = {};
                vm.newJobVariable = {};
                vm.newCustomTab = {};

                vm.updatedJob.name = vm.job.name;

                vm.currentJob = vm.job.name;

            }
        };


        vm.addJob = function(newJob) {
            if (newJob.taskDefinitions.type == 'EXEC') {
                var job = {
                    name: newJob.name,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    taskDefinitions: [{
                        type: newJob.taskDefinitions.type,
                        pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                        stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                        command: newJob.taskDefinitions.command,
                        arguments: newJob.taskDefinitions.arguments,
                        workingDirectory: newJob.taskDefinitions.workingDirectory,
                        runIfCondition: newJob.taskDefinitions.runIfCondition,
                        isIgnoringErrors: newJob.taskDefinitions.isIgnoringErrors || false
                    }]
                }
            }

            if (newJob.taskDefinitions.type == 'FETCH_ARTIFACT') {
                var job = {
                    name: newJob.name,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    taskDefinitions: [{
                        type: newJob.taskDefinitions.type,
                        pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                        stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                        designatedPipelineDefinitionName: JSON.parse(newJob.taskDefinitions.pipelineObject).name,
                        designatedPipelineDefinitionId: JSON.parse(newJob.taskDefinitions.pipelineObject).id,
                        source: (typeof newJob.taskDefinitions.source === 'undefined') ? '' : newJob.taskDefinitions.source,
                        destination: (typeof newJob.taskDefinitions.destination === 'undefined') ? '' : newJob.taskDefinitions.destination,
                        runIfCondition: newJob.taskDefinitions.runIfCondition
                    }]
                };
                if(newJob.taskDefinitions.pipelineRun == 'true'){
                    job.taskDefinitions[0].shouldUseLatestRun = true;
                } else {
                    job.taskDefinitions[0].designatedPipelineExecutionId = newJob.taskDefinitions.pipelineRun;
                }
            }
            if (newJob.taskDefinitions.type == 'FETCH_MATERIAL') {
                var materialForJob = JSON.parse(newJob.taskDefinitions.material);
                var job = {
                    name: newJob.name,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    taskDefinitions: [{
                        pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                        pipelineName: vm.allPipelines[vm.pipelineIndex].name,
                        stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                        materialDefinitionId: materialForJob.id,
                        type: newJob.taskDefinitions.type,
                        runIfCondition: newJob.taskDefinitions.runIfCondition,
                        materialName: materialForJob.name,
                        destination: materialForJob.name,
                        materialType: materialForJob.type
                    }]
                };
            }
            if (newJob.taskDefinitions.type == 'UPLOAD_ARTIFACT') {
                var job = {
                    name: newJob.name,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    taskDefinitions: [{
                        type: newJob.taskDefinitions.type,
                        pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                        stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                        source: (typeof newJob.taskDefinitions.source === 'undefined') ? '' : newJob.taskDefinitions.source,
                        destination: (typeof newJob.taskDefinitions.destination === 'undefined') ? '' : newJob.taskDefinitions.destination,
                        runIfCondition: newJob.taskDefinitions.runIfCondition
                    }]
                };
            }
            pipeConfigService.addJobDefinition(job);
            loggerService.log('PipelineConfigController.addJob :');
            loggerService.log(job);
        };

        vm.editJob = function(job) {
            var newJob = angular.copy(vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex]);
            newJob.name = job.name;
            $state.go('index.pipelineConfig.job.settings', {
                groupName: vm.pipeline.groupName,
                pipelineName: vm.pipeline.name,
                stageName: vm.stage.name,
                jobName: job.name
            });
            pipeConfigService.updateJobDefinition(newJob);
            loggerService.log('PipelineConfigController.editJob :');
            loggerService.log(newJob);
        };

        vm.deleteJob = function(job) {
            pipeConfigService.deleteJobDefinition(job);
            loggerService.log('PipelineConfigController.deleteJob :');
            loggerService.log(job);
        };

        vm.assignMaterialToPipeline = function(material) {
            var buffer = JSON.parse(material);

        }

        vm.addMaterial = function(newMaterial) {
            var material = {};

            if (vm.materialType == 'GIT') {
                material = {
                    "name": newMaterial.name,
                    "type": 'GIT',
                    "repositoryUrl": newMaterial.repositoryUrl,
                    "isPollingForChanges": newMaterial.isPollingForChanges,
                    "destination": newMaterial.name,
                    "branch": newMaterial.branch || 'master'
                };
                if (newMaterial.credentials) {
                    material.username = newMaterial.username;
                    material.password = newMaterial.password;
                }
                pipeConfigService.addGitMaterialDefinition(material);
                loggerService.log('PipelineConfigController.addMaterial :');
                loggerService.log(material);
            }

            if (vm.materialType == 'NUGET') {
                material = {
                    "pipelineName": vm.currentPipeline,
                    "name": newMaterial.name,
                    "repositoryUrl": newMaterial.repositoryUrl,
                    "type": 'NUGET',
                    "isPollingForChanges": newMaterial.isPollingForChanges,
                    "destination": newMaterial.name,
                    "packageId": newMaterial.packageId,
                    "includePrerelease": newMaterial.includePrerelease
                };
                pipeConfigService.addNugetMaterialDefinition(material);
                loggerService.log('PipelineConfigController.addMaterial :');
                loggerService.log(material);
                //TODO
                // if (nugetMaterial.credentials) {
                //   nuget.MaterialSpecificDetails.username = nugetMaterial.username;
                //   nuget.MaterialSpecificDetails.password = nugetMaterial.password;
                // }
            }

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


        vm.getMaterial = function(material) {
            if (vm.material != null) {
                vm.allPipelines[vm.pipelineIndex].materialDefinitions.forEach(function(currentMaterial, index, array) {
                    if (currentMaterial.name == material.name) {
                        vm.material = array[index];
                        vm.materialIndex = index;

                        loggerService.log('PipelineConfigController.getMaterial :');
                        loggerService.log(vm.material);
                    }
                });
            }

            //vm.materialDeleteButton = false;
            //vm.material = res;

            if (typeof(vm.material.username) !== 'undefined' &&
                typeof(vm.material.password) !== 'undefined') {
                vm.hasCredentials = true;
            } else {
                vm.hasCredentials = false;
            }

            vm.currentMaterial = material;
        };

        vm.editMaterial = function(newMaterial) {
            var material = angular.copy(vm.allPipelines[vm.pipelineIndex].materialDefinitions[vm.materialIndex]);
            if (newMaterial.type == 'GIT') {
                material = {
                    id: material.id,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    name: newMaterial.name,
                    type: 'GIT',
                    repositoryUrl: newMaterial.repositoryUrl,
                    isPollingForChanges: newMaterial.isPollingForChanges,
                    destination: newMaterial.name,
                    branch: newMaterial.branch || 'master'
                };
                if (vm.hasCredentials) {
                    material.username = newMaterial.username;
                    material.password = newMaterial.password;
                }
                pipeConfigService.updateGitMaterialDefinition(material);
                loggerService.log('PipelineConfigController.editMaterial :');
                loggerService.log(material);
            }

            if (newMaterial.type == 'NUGET') {
                material = {
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    name: newMaterial.name,
                    repositoryUrl: newMaterial.repositoryUrl,
                    type: 'NUGET',
                    isPollingForChanges: newMaterial.isPollingForChanges,
                    destination: newMaterial.name,
                    packageId: newMaterial.packageId,
                    includePrerelease: newMaterial.includePrerelease
                };
                pipeConfigService.updateNugetMaterialDefinition(material);
                loggerService.log('PipelineConfigController.editMaterial :');
                loggerService.log(material);
                //TODO
                // if (nugetMaterial.credentials) {
                //   nuget.MaterialSpecificDetails.username = nugetMaterial.username;
                //   nuget.MaterialSpecificDetails.password = nugetMaterial.password;
                // }
            }
        };

        vm.deleteMaterial = function(material) {
            pipeConfigService.deleteMaterialDefinition(material);
            loggerService.log('PipelineConfigController.deleteMaterial :');
            loggerService.log(material);
        };

        vm.getTask = function(task) {
            if (vm.task != null) {
                vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].taskDefinitions.forEach(function(currentTask, index, array) {
                    if (currentTask.id == task.id) {
                        vm.task = array[index];
                        vm.taskIndex = index;

                        loggerService.log('PipelineConfigController.getTask :');
                        loggerService.log(vm.task);
                    }
                });
            }

            //vm.task = res;
            vm.updatedTask = vm.task;

            //vm.taskIndex = taskIndex;
        };

        vm.selectedTaskMaterial = {};
        vm.getTaskForUpdate = function(task) {
            if (vm.task != null) {
                vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].taskDefinitions.forEach(function(currentTask, index, array) {
                    if (currentTask.id == task.id) {
                        vm.task = array[index];
                        vm.taskIndex = index;

                        loggerService.log('PipelineConfigController.getTaskForUpdate :');
                        loggerService.log(vm.task);
                    }
                });
            }

            vm.selectedTaskMaterial = vm.allMaterials.find(function(materialDefinition,index,array){
              return task.materialDefinitionId === materialDefinition.id;
            });
            //vm.task = res;
            vm.updatedTask = angular.copy(vm.task);
            // vm.getPipelineForTaskById(vm.updatedTask.pipelineDefinitionId);
            // vm.getStageForTaskById(vm.updatedTask.stageDefinitionId);
            vm.getPipelineForTaskUpdate(vm.updatedTask.designatedPipelineDefinitionName);
            vm.getRunForTaskUpdate(vm.updatedTask.designatedPipelineExecutionId);

            //vm.taskIndex = taskIndex;
        };

        vm.addTask = function(newTask) {
            if (newTask.type == 'EXEC') {
                var task = {
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    type: newTask.type,
                    command: newTask.command,
                    arguments: newTask.arguments,
                    workingDirectory: newTask.workingDirectory,
                    runIfCondition: newTask.runIfCondition,
                    isIgnoringErrors: newTask.isIgnoringErrors
                };
            }
            if (newTask.type == 'FETCH_MATERIAL') {
                var selectedMaterial = JSON.parse(newTask.material);
                var task = {
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    pipelineName: vm.allPipelines[vm.pipelineIndex].name,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    materialDefinitionId: selectedMaterial.id,
                    type: newTask.type,
                    materialType: selectedMaterial.type,
                    materialName: selectedMaterial.name,
                    destination: selectedMaterial.name,
                    runIfCondition: newTask.runIfCondition
                };
            }
            if (newTask.type == 'FETCH_ARTIFACT') {
                var task = {
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    designatedPipelineDefinitionName: JSON.parse(newTask.pipelineObject).name,
                    designatedPipelineDefinitionId: JSON.parse(newTask.pipelineObject).id,
                    type: newTask.type,
                    source: (typeof newTask.source === 'undefined') ? '' : newTask.source,
                    destination: (typeof newTask.destination === 'undefined') ? '' : newTask.destination,
                    runIfCondition: newTask.runIfCondition
                };
                if(newTask.pipelineRun == 'true'){
                    task.shouldUseLatestRun = true;
                } else {
                    task.designatedPipelineExecutionId = newTask.pipelineRun;
                }
            }
            if (newTask.type == 'UPLOAD_ARTIFACT') {
                var task = {
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    type: newTask.type,
                    source: (typeof newTask.source === 'undefined') ? '' : newTask.source,
                    destination: (typeof newTask.destination === 'undefined') ? '' : newTask.destination,
                    runIfCondition: newTask.runIfCondition
                }
            }
            pipeConfigService.addTaskDefinition(task);
            loggerService.log('PipelineConfigController.addTask :');
            loggerService.log(task);
        };

        vm.editTask = function(newTask) {
            var task = angular.copy(vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].taskDefinitions[vm.taskIndex]);
            if (newTask.type == 'EXEC') {
                var updatedTask = {
                    id: task.id,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    type: newTask.type,
                    command: newTask.command,
                    arguments: newTask.arguments,
                    workingDirectory: newTask.workingDirectory,
                    runIfCondition: newTask.runIfCondition,
                    isIgnoringErrors: newTask.isIgnoringErrors
                };
            }
            if (newTask.type == 'FETCH_MATERIAL') {
                var updatedTask = {
                    id: task.id,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    materialDefinitionId: vm.selectedTaskMaterial.id,
                    type: newTask.type,
                    pipelineName: vm.allPipelines[vm.pipelineIndex].name,
                    materialType: vm.selectedTaskMaterial.type,
                    materialName: vm.selectedTaskMaterial.name,
                    destination: vm.selectedTaskMaterial.name,
                    runIfCondition: newTask.runIfCondition
                };
            }
            if (newTask.type == 'FETCH_ARTIFACT') {
                var updatedTask = {
                    id: task.id,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    designatedPipelineDefinitionName: newTask.pipelineObject.name,
                    designatedPipelineDefinitionId: newTask.pipelineObject.id,
                    type: newTask.type,
                    source: (typeof newTask.source === 'undefined') ? '' : newTask.source,
                    destination: (typeof newTask.destination === 'undefined') ? '' : newTask.destination,
                    runIfCondition: newTask.runIfCondition
                };
                if(vm.updatedTask.pipelineRun == -1){
                    updatedTask.shouldUseLatestRun = true;
                } else {
                    updatedTask.designatedPipelineExecutionId = parseInt(newTask.pipelineRun);
                }
            }
            if (newTask.type == 'UPLOAD_ARTIFACT') {
                var updatedTask = {
                    id: task.id,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    type: newTask.type,
                    source: (typeof newTask.source === 'undefined') ? '' : newTask.source,
                    destination: (typeof newTask.destination === 'undefined') ? '' : newTask.destination,
                    runIfCondition: newTask.runIfCondition
                }
            }
            pipeConfigService.updateTaskDefinition(updatedTask);
            loggerService.log('PipelineConfigController.editTask :');
            loggerService.log(updatedTask);
        };

        vm.deleteTask = function(task) {
            pipeConfigService.deleteTaskDefinition(task);
            loggerService.log('PipelineConfigController.deleteTask :');
            loggerService.log(task);
        };

        vm.addResource = function() {
            var jobToUpdate = angular.copy(vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex]);
            jobToUpdate.resources.push(vm.resourceToAdd);
            pipeConfigService.updateJobDefinition(jobToUpdate);
            loggerService.log('PipelineConfigController.addResource :');
            loggerService.log(jobToUpdate);
        };

        vm.getResourceToUpdate = function(resource) {
            vm.oldResource = angular.copy(resource);
            vm.newResource = angular.copy(resource);
        };

        vm.editResource = function() {
            var jobToUpdate = angular.copy(vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex]);
            var resourceIndex = jobToUpdate.resources.indexOf(vm.oldResource);
            jobToUpdate.resources[resourceIndex] = vm.newResource;
            pipeConfigService.updateJobDefinition(jobToUpdate);
            loggerService.log('PipelineConfigController.editResource :');
            loggerService.log(jobToUpdate);
        };

        vm.getResourceToDelete = function(resource) {
            vm.resourceToDelete = angular.copy(resource);
        };

        vm.removeResource = function() {
            var jobToUpdate = angular.copy(vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex]);
            var resourceIndex = jobToUpdate.resources.indexOf(vm.resourceToDelete);
            jobToUpdate.resources.splice(resourceIndex, 1);
            pipeConfigService.updateJobDefinition(jobToUpdate);
            loggerService.log('PipelineConfigController.removeResource :');
            loggerService.log();
        };

        vm.createPipelineDefinition = function(pipeline) {
            pipeConfigService.addPipelineDefinition(pipeline);
            loggerService.log('PipelineConfigController.createPipelineDefinition :');
            loggerService.log(pipeline);
        };

        vm.getStageRunsFromPipeline = function(pipeline) {
            vm.allPipelineRuns.forEach(function(currentPipeline, index, array) {
                if (currentPipeline.pipelineDefinitionId == pipeline.id) {
                    vm.currentStageRuns = [];
                    currentPipeline[index].stages.forEach(function(currentStage, index, array) {
                        vm.currentStageRuns.push(currentStage);
                    });
                }
            });
            return vm.currentStageRuns;
        };

        vm.stageSortableOptions = {
            cancel: ".unsortable",
            items: "tr:not(.unsortable)",
            cursor: "move",
            update: function(e, ui) {

            },
            stop: function() {
                var newPipeline = angular.copy(vm.allPipelines[vm.pipelineIndex]);
                pipeConfigService.updatePipelineDefinition(newPipeline);
            }
        };

        vm.sortableOptions = {
            cancel: ".unsortable",
            items: "tr:not(.unsortable)",
            cursor: "move",
            update: function(e, ui) {

            },
            stop: function() {
                var newJob = angular.copy(vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex]);
                pipeConfigService.updateJobDefinition(newJob);
            }
        };

        vm.environmentVariableUtils = {
            pipelines: {
                addVariable: function(variable) {
                    var variableToAdd = {
                        key: variable.name,
                        value: variable.value,
                        isSecured: variable.isSecured
                    };
                    vm.pipeline.environmentVariables.push(variableToAdd);
                    pipeConfigService.updatePipelineDefinition(vm.pipeline);
                    loggerService.log('PipelineConfigController.environmentVariableUtils.pipelines.addVariable :');
                    loggerService.log(vm.pipeline);
                    vm.close();
                },
                editVariable: function(variable) {
                    vm.pipeline.environmentVariables.forEach(function(current, index, array) {
                        if (current.id == variable.id) {
                            array[index] = variable;
                        }
                    });
                    pipeConfigService.updatePipelineDefinition(vm.pipeline);
                    loggerService.log('PipelineConfigController.environmentVariableUtils.pipelines.editVariable :');
                    loggerService.log(vm.pipeline);
                    vm.close();
                },
                deleteVariable: function(variable) {
                    vm.pipeline.environmentVariables.forEach(function(current, index, array) {
                        if (current.id == variable.id) {
                            array.splice(index, 1);
                        }
                    });
                    pipeConfigService.updatePipelineDefinition(vm.pipeline);
                    loggerService.log('PipelineConfigController.environmentVariableUtils.pipelines.deleteVariable :');
                    loggerService.log(vm.pipeline);
                    vm.close();
                },
                getVariableForEdit: function(variable) {
                    vm.environmentVariableUtils.pipelines.variableToEdit = angular.copy(variable);
                    if(vm.environmentVariableUtils.pipelines.variableToEdit.isDeletable == false){
                        vm.environmentVariableUtils.pipelines.variableToEdit.value = parseInt(vm.environmentVariableUtils.pipelines.variableToEdit.value);
                    }
                },
                variableToEdit: {}
            },
            stages: {
                addVariable: function(variable) {
                    var variableToAdd = {
                        key: variable.name,
                        value: variable.value,
                        isSecured: variable.isSecured
                    };
                    vm.stage.environmentVariables.push(variableToAdd);
                    pipeConfigService.updateStageDefinition(vm.stage);
                    loggerService.log('PipelineConfigController.environmentVariableUtils.stages.addVariable :');
                    loggerService.log(vm.stage);
                    vm.close();
                },
                editVariable: function(variable) {
                    vm.stage.environmentVariables.forEach(function(current, index, array) {
                        if (current.id == variable.id) {
                            array[index] = variable;
                        }
                    });
                    pipeConfigService.updateStageDefinition(vm.stage);
                    loggerService.log('PipelineConfigController.environmentVariableUtils.stages.editVariable :');
                    loggerService.log(vm.stage);
                    vm.close();
                },
                deleteVariable: function(variable) {
                    vm.stage.environmentVariables.forEach(function(current, index, array) {
                        if (current.id == variable.id) {
                            array.splice(index, 1);
                        }
                    });
                    pipeConfigService.updateStageDefinition(vm.stage);
                    loggerService.log('PipelineConfigController.environmentVariableUtils.stages.deleteVariable :');
                    loggerService.log(vm.stage);
                    vm.close();
                },
                getVariableForEdit: function(variable) {
                    vm.environmentVariableUtils.stages.variableToEdit = angular.copy(variable);
                },
                variableToEdit: {}
            },
            jobs: {
                addVariable: function(variable) {
                    var variableToAdd = {
                        key: variable.name,
                        value: variable.value,
                        isSecured: variable.isSecured
                    };
                    vm.job.environmentVariables.push(variableToAdd);
                    pipeConfigService.updateJobDefinition(vm.job);
                    loggerService.log('PipelineConfigController.environmentVariableUtils.jobs.addVariable :');
                    loggerService.log(vm.job);
                    vm.close();
                },
                editVariable: function(variable) {
                    vm.job.environmentVariables.forEach(function(current, index, array) {
                        if (current.id == variable.id) {
                            array[index] = variable;
                        }
                    });
                    pipeConfigService.updateJobDefinition(vm.job);
                    loggerService.log('PipelineConfigController.environmentVariableUtils.jobs.editVariable :');
                    loggerService.log(vm.job);
                    vm.close();
                },
                deleteVariable: function(variable) {
                    vm.job.environmentVariables.forEach(function(current, index, array) {
                        if (current.id == variable.id) {
                            array.splice(index, 1);
                        }
                    });
                    pipeConfigService.updateJobDefinition(vm.job);
                    loggerService.log('PipelineConfigController.environmentVariableUtils.jobs.deleteVariable :');
                    loggerService.log(vm.job);
                    vm.close();
                },
                getVariableForEdit: function(variable) {
                    vm.environmentVariableUtils.jobs.variableToEdit = angular.copy(variable);
                },
                variableToEdit: {}
            }
        };
    });

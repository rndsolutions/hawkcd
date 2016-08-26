'use strict';

angular
    .module('hawk.pipelinesManagement')
    .controller('PipelineConfigController', function($state, $interval, $scope, authDataService, viewModel, pipeConfigService) {
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
        vm.allMaterials = angular.copy(viewModel.allMaterialDefinitions);;
        vm.allPipelineVars = {};
        vm.allStageVars = {};
        vm.allJobVars = {};

        vm.pipelineIndex = {};
        vm.stageIndex = {};
        vm.jobIndex = {};
        vm.materialIndex = {};

        vm.currentStageRuns = [];

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
                trigger: 'Trigger option'
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

        // $scope.$watch(function() { return viewModel.allPipelines.materials }, function(newVal, oldVal) {
        //     vm.allMaterials = viewModel.allPipelines.materials;
        //     console.log(vm.allPipelines.materials);
        // });

        $scope.$watchCollection(function() {
            return viewModel.allMaterialDefinitions;
        }, function(newVal, oldVal) {
            vm.allMaterials = angular.copy(viewModel.allMaterialDefinitions);
        });

        $scope.$watchCollection(function() {
            return viewModel.allPermissions;
        }, function(newVal, oldVal) {
            vm.allPermissions = angular.copy(viewModel.allPermissions);
            console.log(vm.allPermissions);
        });

        $scope.$watchCollection(function() {
            return viewModel.allPipelines;
        }, function(newVal, oldVal) {
            vm.allPipelines = angular.copy(viewModel.allPipelines);
            console.log(vm.allPipelines);
        });

        // $scope.$watchCollection(function () { return viewModel.allMaterialDefinitions }, function (newVal, oldVal) {
        //     vm.allMaterials = viewModel.allMaterialDefinitions;
        //     console.log(vm.allMaterials);
        // });

        // $scope.$watch(function () {return viewModel.allStages}, function (newVal, oldVal) {
        //     vm.allStages = viewModel.allStages;
        //     viewModel.allStages.forEach(function (currentStage, index, array) {
        //         if (currentStage.name == vm.stage.name) {
        //             vm.stage = array[index];
        //         }
        //     });
        //     console.log(vm.allStages);
        // }, true);
        //
        // $scope.$watch(function () {return viewModel.allJobs}, function (newVal, oldVal) {
        //     viewModel.allJobs.forEach(function (currentJob, index, array) {
        //         if (currentJob.name == vm.job.name) {
        //             vm.job = array[index];
        //         }
        //     });
        //     vm.allJobs = viewModel.allJobs;
        //
        //     console.log(vm.allJobs);
        // }, true);

        // $scope.$watch(function () { return viewModel.allPipelineRuns }, function (newVal, oldVal) {
        //     vm.allPipelineRuns = viewModel.allPipelineRuns;
        //     vm.allPipelineRuns.forEach(function (currentPipelineRun, index, array) {
        //         vm.allPipelines.forEach(function (currentPipeline, pipelineIndex, array) {
        //             if(currentPipelineRun.pipelineDefinitionId == currentPipeline.id){
        //                 vm.allPipelines[pipelineIndex].stages = currentPipelineRun.stages;
        //             }
        //         });
        //     });
        //     viewModel.allPipelineGroups.forEach(function (currentPipelineGroup, index, array) {
        //         viewModel.allPipelineGroups.pipelines.forEach(function (currentPipelineFromGroup, pipelineFromGroupIndex, array) {
        //             vm.allPipelines.forEach(function (currentPipeline, pipelineIndex, array) {
        //                 if(currentPipelineFromGroup.id == currentPipeline.id) {
        //                     viewModel.allPipelineGroups[index].pipelines[pipelineFromGroupIndex] = vm.allPipelines[pipelineIndex];
        //                 }
        //             });
        //         });
        //     });
        //     console.log(vm.allPipelineRuns);
        // }, true);

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
            return stageName == vm.currentStage;
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
            vm.updatedTask = {};
            vm.task.Type = '';
            vm.newPipelineVar = {};
            vm.newStageVar = {};
            vm.newJobVar = {};
            vm.materialType = "";
        };

        vm.filteredMaterialDefinitions = [];
        vm.getPipelineForConfig = function(pipeName) {
            vm.allPipelines.forEach(function(currentPipeline, index, array) {
                if (currentPipeline.name == pipeName) {
                    vm.pipeline = array[index];
                    vm.allPipelineVars = vm.pipeline.environmentVariables;
                    vm.pipelineIndex = index;

                    for (var i = 0; i < currentPipeline.materialDefinitionIds.length; i++) {
                        var currentDefinition = currentPipeline.materialDefinitionIds[i];
                        for (var j = 0; j < vm.allMaterials.length; j++) {
                            var currentMaterial = vm.allMaterials[j];
                            if (currentDefinition === currentMaterial.id) {
                                if (vm.filteredMaterialDefinitions.indexOf(currentMaterial) === -1) {
                                    vm.filteredMaterialDefinitions.push(currentMaterial);
                                }
                            }

                        }
                    }
                }
            });




            //vm.pipeline = pipeName;
            vm.newStage = {};
            vm.newMaterials = {};

            vm.updatedPipeline.name = vm.pipeline.name;
            vm.updatedPipeline.labelTemplate = vm.pipeline.labelTemplate;
            vm.updatedPipeline.autoScheduling = vm.pipeline.isAutoSchedulingEnabled;
            vm.currentPipeline = pipeName;
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
            console.log(vm.selectedPipelineStages);
        };

        vm.getPipelineForTaskById = function(id) {
            vm.allPipelines.forEach(function(currentPipeline, pipelineIndex, pipelineArray) {
                if (currentPipeline.id == id) {
                    vm.selectedPipelineStages = angular.copy(currentPipeline.stageDefinitions);
                }
            });
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
            vm.allPipelines[vm.pipelineIndex].stageDefinitions.forEach(function(currentStage, index, array) {
                if (currentStage.name == stage.name) {
                    vm.stage = array[index];
                    vm.allStageVars = vm.stage.environmentVariables;
                    vm.stageIndex = index;
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

        vm.getStageByName = function(stageName) {
            vm.allPipelines[vm.pipelineIndex].stageDefinitions.forEach(function(currentStage, index, array) {
                if (currentStage.name == stageName) {
                    vm.stage = array[index];
                    vm.stageIndex = index;
                }
            });
            vm.stageDeleteButton = false;
            //vm.stage = res;

            vm.newJob = {};
            vm.newStageVariable = {};

            vm.updatedStage.name = vm.stage.name;
            vm.updatedStage.isTriggeredManually = vm.stage.isTriggeredManually;

            vm.currentStage = vm.stage.name;
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
            console.log(vm.selectedStageJobs);
        };

        vm.getStageForTaskById = function(id) {
            vm.selectedPipelineStages.forEach(function(currentStage, stageIndex, stageArray) {
                if (currentStage.id == id) {
                    vm.selectedStageJobs = angular.copy(currentStage.jobDefinitions);
                }
            });
        };

        vm.newStage.selectedMaterialForNewStage = {};
        vm.addStage = function(newStage) {
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
                            ignoreErrors: newStage.jobDefinitions.taskDefinitions.ignoreErrors || false
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
                            stageDefinitionName: newStage.name,
                            jobDefinitionName: newStage.jobDefinitions.name,
                            source: newStage.jobDefinitions.taskDefinitions.source,
                            runIfCondition: newStage.jobDefinitions.taskDefinitions.runIfCondition
                        }]
                    }]
                };
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
                            runIfCondition: newStage.jobDefinitions.taskDefinitions.runIfCondition,
                            ignoreErrors: newStage.jobDefinitions.taskDefinitions.ignoreErrors
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
                            source: newStage.jobDefinitions.taskDefinitions.source
                        }]
                    }]
                };
            }
            debugger;
            pipeConfigService.addStageDefinition(stage);
        };

        vm.editStage = function(stage) {
            var newStage = angular.copy(vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex]);
            newStage.name = stage.name;
            $state.go('index.pipelineConfig.stage.settings', {
                groupName: vm.pipeline.groupName,
                pipelineName: vm.pipeline.name,
                stageName: stage.name
            });
            pipeConfigService.updateStageDefinition(newStage);
        };

        vm.deleteStage = function(stage) {
          debugger;
            pipeConfigService.deleteStageDefinition(stage.id);
        };

        vm.getJob = function(job) {
            if (vm.job != null) {
                vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions.forEach(function(currentJob, index, array) {
                    if (currentJob.name == job.name) {
                        vm.job = array[index];
                        vm.allJobVars = vm.job.environmentVariables;
                        vm.jobIndex = index;
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
            if (vm.job != null) {
                vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions.forEach(function(currentJob, index, array) {
                    if (currentJob.name == jobName) {
                        vm.job = array[index];
                        vm.jobIndex = index;
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
            debugger;
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
                        ignoreErrors: newJob.taskDefinitions.ignoreErrors || false
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
                        pipelineDefinitionName: vm.allPipelines[vm.pipelineIndex].name,
                        stageDefinitionName: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].name,
                        jobDefinitionName: newJob.name,
                        source: newJob.taskDefinitions.source,
                        destination: newJob.taskDefinitions.destination,
                        runIfCondition: newJob.taskDefinitions.runIfCondition
                    }]
                };
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
                        source: newJob.taskDefinitions.source,
                        destination: newJob.taskDefinitions.destination,
                        runIfCondition: newJob.taskDefinitions.runIfCondition
                    }]
                };
            }
            debugger;
            pipeConfigService.addJobDefinition(job);

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
        };

        vm.deleteJob = function(job) {
            pipeConfigService.deleteJobDefinition(job.id);
        };

        vm.assignMaterialToPipeline = function(material) {
            var buffer = JSON.parse(material);

            debugger;
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
                    }
                });
            }

            //vm.materialDeleteButton = false;
            //vm.material = res;

            console.log(vm.material);
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
                //TODO
                // if (nugetMaterial.credentials) {
                //   nuget.MaterialSpecificDetails.username = nugetMaterial.username;
                //   nuget.MaterialSpecificDetails.password = nugetMaterial.password;
                // }
            }
        };

        vm.deleteMaterial = function(material) {
            pipeConfigService.deleteMaterialDefinition(material.id);
        };

        vm.getTask = function(task) {
            if (vm.task != null) {
                vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].taskDefinitions.forEach(function(currentTask, index, array) {
                    if (currentTask.id == task.id) {
                        vm.task = array[index];
                        vm.taskIndex = index;
                    }
                });
            }

            //vm.task = res;
            vm.updatedTask = vm.task;

            //vm.taskIndex = taskIndex;
        };

        vm.getTaskForUpdate = function(task) {
            if (vm.task != null) {
                vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].taskDefinitions.forEach(function(currentTask, index, array) {
                    if (currentTask.id == task.id) {
                        vm.task = array[index];
                        vm.taskIndex = index;
                    }
                });
            }

            //vm.task = res;
            vm.updatedTask = vm.task;
            vm.getPipelineForTaskById(vm.updatedTask.pipelineDefinitionId);
            vm.getStageForTaskById(vm.updatedTask.stageDefinitionId);

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
                    ignoreErrors: newTask.ignoreErrors
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
                    runIfCondition: newTask.runIfCondition,
                    ignoreErrors: newTask.ignoreErrors
                };
            }
            if (newTask.type == 'FETCH_ARTIFACT') {
                var task = {
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    pipelineDefinitionName: vm.allPipelines[vm.pipelineIndex].name,
                    stageDefinitionName: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].name,
                    jobDefinitionName: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].name,
                    type: newTask.type,
                    source: newTask.source,
                    runIfCondition: newTask.runIfCondition,
                    ignoreErrors: newTask.ignoreErrors
                };
            }
            if (newTask.type == 'UPLOAD_ARTIFACT') {
                var task = {
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    type: newTask.type,
                    source: newTask.source,
                    runIfCondition: newTask.runIfCondition,
                    ignoreErrors: newTask.ignoreErrors
                }
            }
            pipeConfigService.addTaskDefinition(task);
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
                    ignoreErrors: newTask.ignoreErrors
                };
            }
            if (newTask.type == 'FETCH_MATERIAL') {
                var updatedTask = {
                    id: task.id,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    materialDefinitionId: JSON.parse(newTask.material).id,
                    type: newTask.type,
                    materialType: JSON.parse(newTask.material).type,
                    materialName: JSON.parse(newTask.material).name,
                    destination: JSON.parse(newTask.material).name,
                    runIfCondition: newTask.runIfCondition,
                    ignoreErrors: newTask.ignoreErrors
                };
            }
            if (newTask.type == 'FETCH_ARTIFACT') {
                var updatedTask = {
                    id: task.id,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    pipelineDefinitionName: vm.allPipelines[vm.pipelineIndex].name,
                    stageDefinitionName: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].name,
                    jobDefinitionName: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].name,
                    type: newTask.type,
                    source: newTask.source,
                    runIfCondition: newTask.runIfCondition,
                    ignoreErrors: newTask.ignoreErrors
                };
            }
            if (newTask.type == 'UPLOAD_ARTIFACT') {
                var updatedTask = {
                    id: task.id,
                    pipelineDefinitionId: vm.allPipelines[vm.pipelineIndex].id,
                    stageDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].id,
                    jobDefinitionId: vm.allPipelines[vm.pipelineIndex].stageDefinitions[vm.stageIndex].jobDefinitions[vm.jobIndex].id,
                    type: newTask.type,
                    source: newTask.source,
                    runIfCondition: newTask.runIfCondition,
                    ignoreErrors: newTask.ignoreErrors
                }
            }
            pipeConfigService.updateTaskDefinition(updatedTask);
        };

        vm.deleteTask = function(task) {
            pipeConfigService.deleteTaskDefinition(task.id);
        };

        vm.createPipelineDefinition = function(pipeline) {
            pipeConfigService.addPipelineDefinition(pipeline);
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
            console.log(vm.currentStageRuns);
            return vm.currentStageRuns;
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
                },
                editVariable: function(variable) {
                    vm.pipeline.environmentVariables.forEach(function(current, index, array) {
                        if (current.id == variable.id) {
                            array[index] = variable;
                        }
                    });
                    pipeConfigService.updatePipelineDefinition(vm.pipeline);
                },
                deleteVariable: function(variable) {
                    vm.pipeline.environmentVariables.forEach(function(current, index, array) {
                        if (current.id == variable.id) {
                            array.splice(index, 1);
                        }
                    });
                    pipeConfigService.updatePipelineDefinition(vm.pipeline);
                },
                getVariableForEdit: function(variable) {
                    vm.environmentVariableUtils.pipelines.variableToEdit = variable;
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
                },
                editVariable: function(variable) {
                    vm.stage.environmentVariables.forEach(function(current, index, array) {
                        if (current.id == variable.id) {
                            array[index] = variable;
                        }
                    });
                    pipeConfigService.updateStageDefinition(vm.stage);
                },
                deleteVariable: function(variable) {
                    vm.stage.environmentVariables.forEach(function(current, index, array) {
                        if (current.id == variable.id) {
                            array.splice(index, 1);
                        }
                    });
                    pipeConfigService.updateStageDefinition(vm.stage);
                },
                getVariableForEdit: function(variable) {
                    vm.environmentVariableUtils.stages.variableToEdit = variable;
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
                },
                editVariable: function(variable) {
                    vm.job.environmentVariables.forEach(function(current, index, array) {
                        if (current.id == variable.id) {
                            array[index] = variable;
                        }
                    });
                    pipeConfigService.updateJobDefinition(vm.job);
                },
                deleteVariable: function(variable) {
                    vm.job.environmentVariables.forEach(function(current, index, array) {
                        if (current.id == variable.id) {
                            array.splice(index, 1);
                        }
                    });
                    pipeConfigService.updateJobDefinition(vm.job);
                },
                getVariableForEdit: function(variable) {
                    vm.environmentVariableUtils.jobs.variableToEdit = variable;
                },
                variableToEdit: {}
            }
        };

        // function getAllPipelines () {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         oldpipeConfig.getAllPipelineDefs(token)
        //             .then(function (res) {
        //                 vm.allPipelines = res;
        //                 console.log(res);
        //             }, function (err) {
        //                 console.log(err);
        //             });
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getAllPipelineDefs(token)
        //                     .then(function (res) {
        //                         vm.allPipelines = res;
        //                         console.log(res);
        //                     }, function (err) {
        //                         console.log(err);
        //                     });
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        // //getAllPipelines();
        //
        // vm.getPipelineForTask = function (pipeName) {
        //     vm.newTask.Stage = '';
        //     vm.selectedPipelineStages = {};
        //     vm.selectedStageJobs = {};
        //     for (var i = 0; i < vm.allPipelines.length; i++) {
        //         if (vm.allPipelines[i].Name == pipeName) {
        //             vm.selectedPipelineStages = vm.allPipelines[i].Stages;
        //             break;
        //         }
        //     }
        //     console.log(vm.selectedPipelineStages);
        // }
        //
        // vm.getStageForTask = function (stageName) {
        //     vm.newTask.Job = '';
        //     vm.selectedStageJobs = {};
        //     for (var i = 0; i < vm.selectedPipelineStages.length; i++) {
        //         if (vm.selectedPipelineStages[i].Name == stageName) {
        //             vm.selectedStageJobs = vm.selectedPipelineStages[i].Jobs;
        //             break;
        //         }
        //     }
        //     console.log(vm.selectedStageJobs);
        // }
        //
        // //region Pipeline functions
        // vm.getPipelineForConfig = function(pipeName) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         oldpipeConfig.getPipelineDef(pipeName, token)
        //             .then(function(res) {
        //                 vm.pipeline = res;
        //
        //                 vm.newStage = {};
        //                 vm.newMaterials = {};
        //
        //                 vm.updatedPipeline.Name = vm.pipeline.Name;
        //                 vm.updatedPipeline.LabelTemplate = vm.pipeline.LabelTemplate;
        //                 vm.updatedPipeline.AutoScheduling = vm.pipeline.AutoScheduling;
        //
        //                 vm.currentPipeline = res.Name;
        //                 getAllStages();
        //                 getAllMaterials();
        //                 getAllPipelineVars();
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getPipelineDef(pipeName, token)
        //                 .then(function(res) {
        //                     vm.pipeline = res;
        //
        //                     vm.newStage = {};
        //                     vm.newMaterials = {};
        //
        //                     vm.updatedPipeline.Name = vm.pipeline.Name;
        //                     vm.updatedPipeline.LabelTemplate = vm.pipeline.LabelTemplate;
        //                     vm.updatedPipeline.AutoScheduling = vm.pipeline.AutoScheduling;
        //
        //                     vm.currentPipeline = res.Name;
        //                     getAllStages();
        //                     getAllMaterials();
        //                     getAllPipelineVars();
        //                 }, function(err) {
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // };
        //
        // vm.editPipeline = function(newPipe) {
        //     if (vm.pipeline != null) {
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //             var token = window.localStorage.getItem("accessToken");
        //             vm.pipeline.Name = newPipe.Name;
        //             vm.pipeline.LabelTemplate = newPipe.LabelTemplate;
        //             vm.pipeline.AutoScheduling = newPipe.AutoScheduling;
        //             oldpipeConfig.updatePipeline(vm.currentPipeline, vm.pipeline, token)
        //                 .then(function(res) {
        //                     vm.pipeline = {};
        //                     vm.currentPipeline = newPipe.Name;
        //                     vm.getPipelineForConfig(vm.currentPipeline);
        //
        //                     var params = {
        //                         groupName: vm.currentGroup,
        //                         pipelineName: newPipe.Name
        //                     };
        //                     $state.go($state.current, params, {
        //                         reload: false
        //                     });
        //
        //                     console.log(res);
        //                 }, function(err) {
        //                     alert(err.Message);
        //                     console.log(err);
        //                 })
        //         } else {
        //             var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.updatePipeline(vm.currentPipeline, vm.pipeline, token)
        //                     .then(function(res) {
        //                         vm.pipeline = {};
        //                         vm.currentPipeline = newPipe.Name;
        //                         vm.getPipelineForConfig(vm.currentPipeline);
        //
        //                         var params = {
        //                             groupName: vm.currentGroup,
        //                             pipelineName: newPipe.Name
        //                         };
        //                         $state.go($state.current, params, {
        //                             reload: false
        //                         });
        //
        //                         console.log(res);
        //                     }, function(err) {
        //                         alert(err.Message);
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    alert(err.Message);
        //                    console.log(err);
        //                })
        //         }
        //     } else {
        //         console.log('Not found');
        //     }
        // };
        //
        // vm.getMaterial = function(materialName) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         oldpipeConfig.getMaterial(vm.currentPipeline, materialName, token)
        //         .then(function(res) {
        //             vm.materialDeleteButton = false;
        //             vm.material = res;
        //
        //             if (vm.material.MaterialSpecificDetails.username &&
        //                 vm.material.MaterialSpecificDetails.password) {
        //                 vm.hasCredentials = true;
        //             } else {
        //                 vm.hasCredentials = false;
        //             }
        //
        //             vm.currentMaterial = res.Name;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err);
        //         })
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getMaterial(vm.currentPipeline, materialName, token)
        //                 .then(function(res) {
        //                     vm.material = res;
        //
        //                     if (vm.material.MaterialSpecificDetails.username &&
        //                         vm.material.MaterialSpecificDetails.password) {
        //                         vm.hasCredentials = true;
        //                     } else {
        //                         vm.hasCredentials = false;
        //                     }
        //
        //                     vm.currentMaterial = res.Name;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // };
        //
        // vm.addMaterial = function(newMaterial) {
        //     var material = {};
        //
        //     if (vm.materialType == 'git') {
        //         material = {
        //             "PipelineName": vm.currentPipeline,
        //             "Name": newMaterial.git.name,
        //             "Type": 'GIT',
        //             "Url": newMaterial.git.url,
        //             "AutoTriggerOnChange": newMaterial.git.poll,
        //             "Destination": newMaterial.git.name,
        //             "MaterialSpecificDetails": {
        //                 "branch": newMaterial.git.branch || 'master'
        //             }
        //         };
        //         if (newMaterial.git.credentials) {
        //             material.MaterialSpecificDetails.username = newMaterial.git.username;
        //             material.MaterialSpecificDetails.password = newMaterial.git.password;
        //         }
        //     }
        //     //TODO
        //     // if (tfsMaterial) {
        //     //   var tfs = {
        //     //     "PipelineName": vm.currentPipeline,
        //     //     "Name": tfsMaterial.name,
        //     //     "Type": 'TFS',
        //     //     "AutoTriggerOnChange": tfsMaterial.poll,
        //     //     "Destination": tfsMaterial.name,
        //     //     "MaterialSpecificDetails": {
        //     //       "domain": tfsMaterial.domain,
        //     //       "projectPath": tfsMaterial.projectPath,
        //     //       "username": tfsMaterial.username,
        //     //       "password": tfsMaterial.password
        //     //     }
        //     //   };
        //     // }
        //     //
        //     if (vm.materialType == 'nuget') {
        //         material = {
        //             "PipelineName": vm.currentPipeline,
        //             "Name": newMaterial.nuget.name,
        //             "Url": newMaterial.nuget.url,
        //             "Type": 'NUGET',
        //             "AutoTriggerOnChange": newMaterial.nuget.poll,
        //             "Destination": newMaterial.nuget.name,
        //             "MaterialSpecificDetails": {
        //                 "packageId": newMaterial.nuget.packageId,
        //                 "includePrerelease": newMaterial.nuget.includePrerelease
        //             }
        //         };
        //         //TODO
        //         // if (nugetMaterial.credentials) {
        //         //   nuget.MaterialSpecificDetails.username = nugetMaterial.username;
        //         //   nuget.MaterialSpecificDetails.password = nugetMaterial.password;
        //         // }
        //     }
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.createMaterial(vm.currentPipeline, material, token)
        //         .then(function(res) {
        //             vm.newMaterial = {};
        //             getAllMaterials();
        //             console.log(res);
        //         }, function(err) {
        //             vm.material = {};
        //             alert(err.Message);
        //             console.log(err);
        //         });
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.createMaterial(vm.currentPipeline, material, token)
        //                 .then(function(res) {
        //                     vm.newMaterial = {};
        //                     getAllMaterials();
        //                     console.log(res);
        //                 }, function(err) {
        //                     vm.material = {};
        //                     alert(err.Message);
        //                     console.log(err);
        //                 });
        //            }, function (err) {
        //                console.log(err);
        //            })
        //        }
        // };
        //
        // vm.editMaterial = function(newMaterial) {
        //     if (vm.material != null) {
        //         if (vm.material.Type == 'Git') {
        //             vm.material.Name = newMaterial.Name;
        //             vm.material.Destination = newMaterial.Name;
        //             vm.material.Url = newMaterial.Url;
        //             vm.material.AutoTriggerOnChange = newMaterial.AutoTriggerOnChange;
        //             vm.material.MaterialSpecificDetails.branch = newMaterial.MaterialSpecificDetails.branch;
        //             if (vm.hasCredentials) {
        //                 vm.material.MaterialSpecificDetails.username = newMaterial.MaterialSpecificDetails.username;
        //                 vm.material.MaterialSpecificDetails.password = newMaterial.MaterialSpecificDetails.password;
        //             } else {
        //                 delete vm.material.MaterialSpecificDetails.username;
        //                 delete vm.material.MaterialSpecificDetails.password;
        //             }
        //         }
        //         if (vm.material.Type == 'Tfs') {
        //             vm.material.Name = newMaterial.Name;
        //             vm.material.Destination = newMaterial.Name;
        //             vm.material.AutoTriggerOnChange = newMaterial.AutoTriggerOnChange;
        //             vm.material.MaterialSpecificDetails.projectPath = newMaterial.MaterialSpecificDetails.projectPath;
        //             vm.material.MaterialSpecificDetails.domain = newMaterial.MaterialSpecificDetails.domain;
        //             vm.material.MaterialSpecificDetails.username = newMaterial.MaterialSpecificDetails.username;
        //             vm.material.MaterialSpecificDetails.password = newMaterial.MaterialSpecificDetails.password;
        //         }
        //         if (vm.material.Type == 'Nuget') {
        //             vm.material.Name = newMaterial.Name;
        //             vm.material.Destination = newMaterial.Name;
        //             vm.material.Url = newMaterial.Url;
        //             vm.material.AutoTriggerOnChange = newMaterial.AutoTriggerOnChange;
        //             vm.material.MaterialSpecificDetails.packageId = newMaterial.MaterialSpecificDetails.packageId;
        //             vm.material.MaterialSpecificDetails.includePrerelease = newMaterial.MaterialSpecificDetails.includePrerelease;
        //             // if (newMaterial.credentials) {
        //             //     vm.material.MaterialSpecificDetails.username = newMaterial.MaterialSpecificDetails.username;
        //             //     vm.material.MaterialSpecificDetails.password = newMaterial.MaterialSpecificDetails.password;
        //             // };
        //         }
        //
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //        if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.updateMaterial(vm.currentPipeline, vm.currentMaterial, vm.material, token)
        //             .then(function(res) {
        //                 vm.material = {};
        //
        //                 getAllMaterials();
        //                 console.log(res);
        //             }, function(err) {
        //                 alert(err.Message);
        //                 console.log(err);
        //             })
        //        } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.updateMaterial(vm.currentPipeline, vm.currentMaterial, vm.material, token)
        //                     .then(function(res) {
        //                         vm.material = {};
        //
        //                         getAllMaterials();
        //                         console.log(res);
        //                     }, function(err) {
        //                         alert(err.Message);
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    console.log(err);
        //                })
        //        }
        //     } else {
        //         console.log('Not found');
        //     }
        // };
        // vm.deleteMaterial = function(materialName) {
        //     if (vm.material != null) {
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //        if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //             oldpipeConfig.deleteMaterial(vm.currentPipeline, materialName, token)
        //                 .then(function(res) {
        //                     getAllMaterials();
        //                     console.log(res);
        //                 }, function(err) {
        //                     alert(err.Message);
        //                     console.log(err);
        //                 })
        //        } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.deleteMaterial(vm.currentPipeline, materialName, token)
        //                     .then(function(res) {
        //                         getAllMaterials();
        //                         console.log(res);
        //                     }, function(err) {
        //                         alert(err.Message);
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    alert(err.Message);
        //                    console.log(err);
        //                })
        //        }
        //     } else {
        //         console.log('Not Found');
        //     }
        // };
        //
        // vm.addStage = function(newStage) {
        //     if (newStage.Jobs.Tasks.Type == 'Exec') {
        //         var stage = {
        //             Name: newStage.Name,
        //             Jobs: [{
        //                 Name: newStage.Jobs.Name,
        //                 Tasks: [{
        //                     Type: newStage.Jobs.Tasks.Type,
        //                     Command: newStage.Jobs.Tasks.Command,
        //                     Arguments: newStage.Jobs.Tasks.Arguments ? newStage.Jobs.Tasks.Arguments.split('\n') : [],
        //                     WorkingDirectory: newStage.Jobs.Tasks.WorkingDirectory,
        //                     RunIfCondition: newStage.Jobs.Tasks.RunIfCondition,
        //                     IgnoreErrors: newStage.Jobs.Tasks.IgnoreErrors || false
        //                 }]
        //             }]
        //         };
        //     }
        //     if (newStage.Jobs.Tasks.Type == 'FetchArtifact') {
        //         var stage = {
        //             Name: newStage.Name,
        //             Jobs: [{
        //                 Name: newStage.Jobs.Name,
        //                 Tasks: [{
        //                     Type: newStage.Jobs.Tasks.Type,
        //                     Pipeline: newStage.Jobs.Tasks.Pipeline,
        //                     Stage: newStage.Jobs.Tasks.Stage,
        //                     Job: newStage.Jobs.Tasks.Job,
        //                     Source: newStage.Jobs.Tasks.Source,
        //                     Destination: newStage.Jobs.Tasks.Destination,
        //                     RunIfCondition: newStage.Jobs.Tasks.RunIfCondition
        //                 }]
        //             }]
        //         };
        //     }
        //     if (newStage.Jobs.Tasks.Type == 'FetchMaterial') {
        //         var stage = {
        //             Name: newStage.Name,
        //             Jobs: [{
        //                 Name: newStage.Jobs.Name,
        //                 Tasks: [{
        //                     Type: newStage.Jobs.Tasks.Type,
        //                     MaterialName: newStage.Jobs.Tasks.MaterialName,
        //                     RunIfCondition: newStage.Jobs.Tasks.RunIfCondition
        //                 }]
        //             }]
        //         };
        //     }
        //     if (newStage.Jobs.Tasks.Type == 'UploadArtifact') {
        //         var stage = {
        //             Name: newStage.Name,
        //             Jobs: [{
        //                 Name: newStage.Jobs.Name,
        //                 Tasks: [{
        //                     Type: newStage.Jobs.Tasks.Type,
        //                     Source: newStage.Jobs.Tasks.Source,
        //                     Destination: newStage.Jobs.Tasks.Destination,
        //                     RunIfCondition: newStage.Jobs.Tasks.RunIfCondition
        //                 }]
        //             }]
        //         };
        //     }
        //
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //    if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.createStage(vm.currentPipeline, stage, token)
        //             .then(function(res) {
        //                 vm.newStage = {};
        //                 getAllStages();
        //                 console.log(res);
        //             }, function(err) {
        //                 vm.newStage = {};
        //                 alert(err.Message);
        //                 console.log(err);
        //             })
        //    } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.createStage(vm.currentPipeline, stage, token)
        //                 .then(function(res) {
        //                     vm.newStage = {};
        //                     getAllStages();
        //                     console.log(res);
        //                 }, function(err) {
        //                     vm.newStage = {};
        //                     alert(err.Message);
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //    }
        // };
        //
        // vm.deleteStage = function(stageName) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.deleteStage(vm.currentPipeline, stageName, token)
        //         .then(function(res) {
        //             vm.getPipelineForConfig(vm.currentPipeline);
        //             vm.stageDeleteButton = false;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err);
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.deleteStage(vm.currentPipeline, stageName, token)
        //                 .then(function(res) {
        //                     vm.getPipelineForConfig(vm.currentPipeline);
        //                     vm.stageDeleteButton = false;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // };
        // //endregion
        //
        // //region Stage functions
        // vm.getStage = function(stageName) {
        //     if (vm.stage != null) {
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.getStage(vm.currentPipeline, stageName, token)
        //             .then(function(res) {
        //                 vm.stageDeleteButton = false;
        //                 vm.stage = res;
        //
        //                 vm.newJob = {};
        //                 vm.newStageVariable = {};
        //
        //                 vm.updatedStage.Name = vm.stage.Name;
        //                 vm.updatedStage.StageType = vm.stage.StageType;
        //
        //                 vm.currentStage = res.Name;
        //                 getAllJobs();
        //                 getAllStageVars();
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.getStage(vm.currentPipeline, stageName, token)
        //                     .then(function(res) {
        //                         vm.stage = res;
        //
        //                         vm.newJob = {};
        //                         vm.newStageVariable = {};
        //
        //                         vm.updatedStage.Name = vm.stage.Name;
        //                         vm.updatedStage.StageType = vm.stage.StageType;
        //
        //                         vm.currentStage = res.Name;
        //                         getAllJobs();
        //                         getAllStageVars();
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    console.log(err);
        //                })
        //         }
        //     } else {
        //         console.log('No changes');
        //     }
        // };
        // vm.editStage = function(newStage) {
        //     if (vm.stage != null) {
        //         vm.stage.Name = newStage.Name;
        //         vm.stage.StageType = newStage.StageType;
        //
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.updateStage(vm.currentPipeline, vm.currentStage, vm.stage, token)
        //             .then(function(res) {
        //                 vm.currentStage = newStage.Name;
        //                 vm.updatedStage = {};
        //
        //                 var params = {
        //                     groupName: vm.currentGroup,
        //                     pipelineName: vm.currentPipeline,
        //                     stageName: newStage.Name
        //                 };
        //                 $state.go($state.current, params, {
        //                     reload: false
        //                 });
        //
        //                 console.log(res);
        //
        //                 getAllStages();
        //                 vm.getStage(vm.currentStage);
        //             }, function(err) {
        //                 alert(err.Message);
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.updateStage(vm.currentPipeline, vm.currentStage, vm.stage, token)
        //                     .then(function(res) {
        //                         vm.currentStage = newStage.Name;
        //                         vm.updatedStage = {};
        //
        //                         var params = {
        //                             groupName: vm.currentGroup,
        //                             pipelineName: vm.currentPipeline,
        //                             stageName: newStage.Name
        //                         };
        //                         $state.go($state.current, params, {
        //                             reload: false
        //                         });
        //
        //                         console.log(res);
        //
        //                         getAllStages();
        //                         vm.getStage(vm.currentStage);
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    alert(err.Message);
        //                    console.log(err);
        //                })
        //         }
        //     } else {
        //         console.log('Not Found');
        //     }
        // };
        //
        // vm.addJob = function(newJob) {
        //     if (newJob.Tasks.Type == 'Exec') {
        //         var job = {
        //             Name: newJob.Name,
        //             Tasks: [{
        //                 Type: newJob.Tasks.Type,
        //                 Command: newJob.Tasks.Command,
        //                 Arguments: newJob.Tasks.Arguments ? newJob.Tasks.Arguments.split('\n') : [],
        //                 WorkingDirectory: newJob.Tasks.WorkingDirectory,
        //                 RunIfCondition: newJob.Tasks.RunIfCondition,
        //                 IgnoreErrors: newJob.Tasks.IgnoreErrors || false
        //             }]
        //         };
        //     }
        //     if (newJob.Tasks.Type == 'FetchArtifact') {
        //         var job = {
        //             Name: newJob.Name,
        //             Tasks: [{
        //                 Type: newJob.Tasks.Type,
        //                 Pipeline: newJob.Tasks.Pipeline,
        //                 Stage: newJob.Tasks.Stage,
        //                 Job: newJob.Tasks.Job,
        //                 Source: newJob.Tasks.Source,
        //                 Destination: newJob.Tasks.Destination,
        //                 RunIfCondition: newJob.Tasks.RunIfCondition
        //             }]
        //         };
        //     }
        //     if (newJob.Tasks.Type == 'FetchMaterial') {
        //         var job = {
        //             Name: newJob.Name,
        //             Tasks: [{
        //                 Type: newJob.Tasks.Type,
        //                 MaterialName: newJob.Tasks.MaterialName
        //             }]
        //         };
        //     }
        //     if (newJob.Tasks.Type == 'UploadArtifact') {
        //         var job = {
        //             Name: newJob.Name,
        //             Tasks: [{
        //                 Type: newJob.Tasks.Type,
        //                 Source: newJob.Tasks.Source,
        //                 Destination: newJob.Tasks.Destination,
        //                 RunIfCondition: newJob.Tasks.RunIfCondition
        //             }]
        //         };
        //     }
        //
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.createJob(vm.currentPipeline, vm.currentStage, job, token)
        //         .then(function(res) {
        //             vm.newJob = {};
        //             getAllStages();
        //             getAllJobs();
        //             console.log(res);
        //         }, function(err) {
        //             vm.newJob = {};
        //             alert(err.Message);
        //             console.log(err);
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.createJob(vm.currentPipeline, vm.currentStage, job, token)
        //                 .then(function(res) {
        //                     vm.newJob = {};
        //                     getAllStages();
        //                     getAllJobs();
        //                     console.log(res);
        //                 }, function(err) {
        //                     vm.newJob = {};
        //                     alert(err.Message);
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // };
        // vm.deleteJob = function(jobName) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.deleteJob(vm.currentPipeline, vm.currentStage, jobName, token)
        //         .then(function(res) {
        //             vm.getPipelineForConfig(vm.currentPipeline);
        //             vm.getStage(vm.currentStage);
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err);
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.deleteJob(vm.currentPipeline, vm.currentStage, jobName, token)
        //                 .then(function(res) {
        //                     vm.getPipelineForConfig(vm.currentPipeline);
        //                     vm.getStage(vm.currentStage);
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // };
        // //endregion
        //
        // //region Job functions
        // vm.getJob = function(jobName) {
        //     if (vm.job != null) {
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.getJob(vm.currentPipeline, vm.currentStage, jobName, token)
        //             .then(function(res) {
        //                 vm.jobDeleteButton = false;
        //                 vm.job = res;
        //
        //                 vm.newTask = {};
        //                 vm.newArtifact = {};
        //                 vm.newJobVariable = {};
        //                 vm.newCustomTab = {};
        //
        //                 vm.updatedJob.Name = vm.job.Name;
        //
        //                 vm.currentJob = res.Name;
        //                 getAllTasks();
        //                 getAllJobVars();
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.getJob(vm.currentPipeline, vm.currentStage, jobName, token)
        //                     .then(function(res) {
        //                         vm.job = res;
        //
        //                         vm.newTask = {};
        //                         vm.newArtifact = {};
        //                         vm.newJobVariable = {};
        //                         vm.newCustomTab = {};
        //
        //                         vm.updatedJob.Name = vm.job.Name;
        //
        //                         vm.currentJob = res.Name;
        //                         getAllTasks();
        //                         getAllJobVars();
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    console.log(err);
        //                })
        //         }
        //     } else {
        //         console.log('Not Found');
        //     }
        // };
        //
        //
        // vm.editJob = function(newJob) {
        //     if (vm.job != null) {
        //         vm.job.Name = newJob.Name;
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.updateJob(vm.currentPipeline, vm.currentStage, vm.currentJob, vm.job, token)
        //             .then(function(res) {
        //                 vm.currentJob = newJob.Name;
        //
        //                 var params = {
        //                     groupName: vm.currentGroup,
        //                     pipelineName: vm.currentPipeline,
        //                     stageName: vm.currentJob,
        //                     jobName: newJob.Name
        //                 };
        //                 $state.go($state.current, params, {
        //                     reload: false
        //                 });
        //
        //                 console.log(res);
        //
        //                 vm.getJob(vm.currentJob);
        //                 getAllJobs();
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.updateJob(vm.currentPipeline, vm.currentStage, vm.currentJob, vm.job, token)
        //                     .then(function(res) {
        //                         vm.currentJob = newJob.Name;
        //
        //                         var params = {
        //                             groupName: vm.currentGroup,
        //                             pipelineName: vm.currentPipeline,
        //                             stageName: vm.currentJob,
        //                             jobName: newJob.Name
        //                         };
        //                         $state.go($state.current, params, {
        //                             reload: false
        //                         });
        //
        //                         console.log(res);
        //
        //                         vm.getJob(vm.currentJob);
        //                         getAllJobs();
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    alert(err.Message);
        //                    console.log(err);
        //                })
        //         }
        //     } else {
        //         console.log('Not Found');
        //     }
        // };
        //
        // vm.getTask = function(taskIndex) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.getTask(vm.currentPipeline, vm.currentStage, vm.currentJob, taskIndex, token)
        //         .then(function(res) {
        //             vm.taskDeleteButton = false;
        //             vm.task = res;
        //
        //             if (res.Arguments != null) {
        //                 vm.task.Arguments = res.Arguments.join('\n');
        //             }
        //
        //             vm.updatedTask = vm.task;
        //
        //             vm.taskIndex = taskIndex;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err);
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                 oldpipeConfig.getTask(vm.currentPipeline, vm.currentStage, vm.currentJob, taskIndex, token)
        //                 .then(function(res) {
        //                     vm.task = res;
        //
        //                     if (res.Arguments != null) {
        //                         vm.task.Arguments = res.Arguments.join('\n');
        //                     }
        //
        //                     vm.updatedTask = vm.task;
        //
        //                     vm.taskIndex = taskIndex;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // };
        // vm.addTask = function(newTask) {
        //     if (newTask.Type == 'Exec') {
        //         var execTask = {
        //             Type: newTask.Type,
        //             Command: newTask.Command,
        //             Arguments: newTask.Arguments ? newTask.Arguments.split('\n') : [],
        //             WorkingDirectory: newTask.WorkingDirectory,
        //             RunIfCondition: newTask.RunIfCondition,
        //             IgnoreErrors: newTask.IgnoreErrors
        //         };
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.createExecTask(vm.currentPipeline, vm.currentStage, vm.currentJob, execTask, token)
        //             .then(function(res) {
        //                 vm.newTask = {};
        //                 getAllTasks();
        //                 console.log(res);
        //             }, function(err) {
        //                 vm.newTask = {};
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.createExecTask(vm.currentPipeline, vm.currentStage, vm.currentJob, execTask, token)
        //                     .then(function(res) {
        //                         vm.newTask = {};
        //                         getAllTasks();
        //                         console.log(res);
        //                     }, function(err) {
        //                         vm.newTask = {};
        //                         alert(err.Message);
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    console.log(err);
        //                })
        //         }
        //     }
        //     if (newTask.Type == 'FetchMaterial') {
        //         var fetchMaterial = {
        //             Type: newTask.Type,
        //             MaterialName: newTask.MaterialName,
        //             RunIfCondition: newTask.RunIfCondition
        //         };
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.createFetchMaterialTask(vm.currentPipeline, vm.currentStage, vm.currentJob, fetchMaterial, token)
        //             .then(function(res) {
        //                 vm.newTask = {};
        //                 getAllTasks();
        //                 console.log(res);
        //             }, function(err) {
        //                 vm.newTask = {};
        //                 alert(err.Message);
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.createFetchMaterialTask(vm.currentPipeline, vm.currentStage, vm.currentJob, fetchMaterial, token)
        //                     .then(function(res) {
        //                         vm.newTask = {};
        //                         getAllTasks();
        //                         console.log(res);
        //                     }, function(err) {
        //                         vm.newTask = {};
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    alert(err.Message);
        //                    console.log(err);
        //                })
        //         }
        //     }
        //     if (newTask.Type == 'FetchArtifact') {
        //         var fetchArtifact = {
        //             Type: newTask.Type,
        //             Pipeline: newTask.Pipeline,
        //             Stage: newTask.Stage,
        //             Job: newTask.Job,
        //             Source: newTask.Source,
        //             Destination: newTask.Destination,
        //             RunIfCondition: newTask.RunIfCondition
        //         };
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.createFetchArtifactTask(vm.currentPipeline, vm.currentStage, vm.currentJob, fetchArtifact, token)
        //             .then(function(res) {
        //                 vm.newTask = {};
        //                 getAllTasks();
        //                 console.log(res);
        //             }, function(err) {
        //                 vm.newTask = {};
        //                 alert(err.Message);
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.createFetchArtifactTask(vm.currentPipeline, vm.currentStage, vm.currentJob, fetchArtifact, token)
        //                     .then(function(res) {
        //                         vm.newTask = {};
        //                         getAllTasks();
        //                         console.log(res);
        //                     }, function(err) {
        //                         vm.newTask = {};
        //                         alert(err.Message);
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    console.log(err);
        //                })
        //         }
        //     }
        //     if (newTask.Type == 'UploadArtifact') {
        //         var uploadArtifact = {
        //             Type: newTask.Type,
        //             Source: newTask.Source,
        //             Destination: newTask.Destination,
        //             RunIfCondition: newTask.RunIfCondition
        //         };
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.createUploadArtifactTask(vm.currentPipeline, vm.currentStage, vm.currentJob, uploadArtifact, token)
        //             .then(function(res) {
        //                 vm.newTask = {};
        //                 getAllTasks();
        //                 console.log(res);
        //             }, function(err) {
        //                 vm.newTask = {};
        //                 alert(err.Message);
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.createUploadArtifactTask(vm.currentPipeline, vm.currentStage, vm.currentJob, uploadArtifact, token)
        //                     .then(function(res) {
        //                         vm.newTask = {};
        //                         getAllTasks();
        //                         console.log(res);
        //                     }, function(err) {
        //                         vm.newTask = {};
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    alert(err.Message);
        //                    console.log(err);
        //                })
        //         }
        //     }
        // };
        //
        // vm.editTask = function(newTask) {
        //     if (vm.task != null) {
        //         if (vm.task.Type == "Exec") {
        //             vm.task.Command = newTask.Command;
        //             vm.task.IgnoreErrors = newTask.IgnoreErrors;
        //             vm.task.RunIfCondition = newTask.RunIfCondition;
        //             vm.task.WorkingDirectory = newTask.WorkingDirectory;
        //             vm.task.Arguments = newTask.Arguments.split('\n');
        //             var tokenIsValid = authDataService.checkTokenExpiration();
        //             if (tokenIsValid) {
        //                var token = window.localStorage.getItem("accessToken");
        //                oldpipeConfig.updateExecTask(vm.currentPipeline, vm.currentStage, vm.currentJob, vm.taskIndex, vm.task, token)
        //                 .then(function(res) {
        //                     getAllTasks();
        //                     getAllPipelines();
        //                     console.log(res);
        //                 }, function(err) {
        //                     alert(err.Message);
        //                     console.log(err);
        //                 })
        //             } else {
        //                var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //                authDataService.getNewToken(currentRefreshToken)
        //                    .then(function (res) {
        //                        var token = res.access_token;
        //                        oldpipeConfig.updateExecTask(vm.currentPipeline, vm.currentStage, vm.currentJob, vm.taskIndex, vm.task, token)
        //                         .then(function(res) {
        //                             getAllTasks();
        //                             getAllPipelines();
        //                             console.log(res);
        //                         }, function(err) {
        //                             console.log(err);
        //                         })
        //                    }, function (err) {
        //                        alert(err.Message);
        //                        console.log(err);
        //                    })
        //             }
        //         }
        //         if (vm.task.Type == "FetchArtifact") {
        //             vm.task.Pipeline = newTask.Pipeline;
        //             vm.task.Stage = newTask.Stage;
        //             vm.task.Job = newTask.Job;
        //             vm.task.Source = newTask.Source;
        //             vm.task.Destination = newTask.Destination;
        //             vm.task.RunIfCondition = newTask.RunIfCondition;
        //             var tokenIsValid = authDataService.checkTokenExpiration();
        //             if (tokenIsValid) {
        //                var token = window.localStorage.getItem("accessToken");
        //                oldpipeConfig.updateFetchArtifactTask(vm.currentPipeline, vm.currentStage, vm.currentJob, vm.taskIndex, newTask, token)
        //                 .then(function(res) {
        //                     vm.newTask = {};
        //                     getAllTasks();
        //                     getAllPipelines();
        //                     console.log(res);
        //                 }, function(err) {
        //                     alert(err.Message);
        //                     console.log(err);
        //                 })
        //             } else {
        //                var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //                authDataService.getNewToken(currentRefreshToken)
        //                    .then(function (res) {
        //                        var token = res.access_token;
        //                        oldpipeConfig.updateFetchArtifactTask(vm.currentPipeline, vm.currentStage, vm.currentJob, vm.taskIndex, newTask, token)
        //                         .then(function(res) {
        //                             vm.newTask = {};
        //                             getAllTasks();
        //                             getAllPipelines();
        //                             console.log(res);
        //                         }, function(err) {
        //                             alert(err.Message);
        //                             console.log(err);
        //                         })
        //                    }, function (err) {
        //                        alert(err.Message);
        //                        console.log(err);
        //                    })
        //             }
        //         }
        //         if (vm.task.Type == "FetchMaterial") {
        //             vm.task.MaterialName = newTask.MaterialName;
        //             vm.task.RunIfCondition = newTask.RunIfCondition;
        //             var tokenIsValid = authDataService.checkTokenExpiration();
        //             if (tokenIsValid) {
        //                var token = window.localStorage.getItem("accessToken");
        //                oldpipeConfig.updateFetchMaterialTask(vm.currentPipeline, vm.currentStage, vm.currentJob, vm.taskIndex, newTask, token)
        //                 .then(function(res) {
        //                     vm.newTask = {};
        //                     getAllTasks();
        //                     getAllPipelines();
        //                     console.log(res);
        //                 }, function(err) {
        //                     alert(err.Message);
        //                     console.log(err);
        //                 })
        //             } else {
        //                var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //                authDataService.getNewToken(currentRefreshToken)
        //                    .then(function (res) {
        //                        var token = res.access_token;
        //                        oldpipeConfig.updateFetchMaterialTask(vm.currentPipeline, vm.currentStage, vm.currentJob, vm.taskIndex, newTask, token)
        //                         .then(function(res) {
        //                             vm.newTask = {};
        //                             getAllTasks();
        //                             getAllPipelines();
        //                             console.log(res);
        //                         }, function(err) {
        //                             console.log(err);
        //                         })
        //                    }, function (err) {
        //                        console.log(err);
        //                    })
        //             }
        //         }
        //         if (vm.task.Type == "UploadArtifact") {
        //             vm.task.Source = newTask.Source;
        //             vm.task.Destination = newTask.Destination;
        //             vm.task.RunIfCondition = newTask.RunIfCondition;
        //             var tokenIsValid = authDataService.checkTokenExpiration();
        //             if (tokenIsValid) {
        //                var token = window.localStorage.getItem("accessToken");
        //                oldpipeConfig.updateUploadArtifactTask(vm.currentPipeline, vm.currentStage, vm.currentJob, vm.taskIndex, newTask, token)
        //                 .then(function(res) {
        //                     vm.newTask = {};
        //                     getAllTasks();
        //                     getAllPipelines();
        //                     console.log(res);
        //                 }, function(err) {
        //                     alert(err.Message);
        //                     console.log(err);
        //                 })
        //             } else {
        //                var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //                authDataService.getNewToken(currentRefreshToken)
        //                    .then(function (res) {
        //                        var token = res.access_token;
        //                        oldpipeConfig.updateUploadArtifactTask(vm.currentPipeline, vm.currentStage, vm.currentJob, vm.taskIndex, newTask, token)
        //                         .then(function(res) {
        //                             vm.newTask = {};
        //                             getAllTasks();
        //                             getAllPipelines();
        //                             console.log(res);
        //                         }, function(err) {
        //                             console.log(err);
        //                         })
        //                    }, function (err) {
        //                        alert(err.Message);
        //                        console.log(err);
        //                    })
        //             }
        //         }
        //     } else {
        //         console.log('Not Found');
        //     }
        // };
        // vm.deleteTask = function(taskIndex) {
        //     if (vm.task != null) {
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.deleteTask(vm.currentPipeline, vm.currentStage, vm.currentJob, taskIndex, token)
        //             .then(function(res) {
        //                 getAllTasks();
        //                 console.log(res);
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.deleteTask(vm.currentPipeline, vm.currentStage, vm.currentJob, taskIndex, token)
        //                     .then(function(res) {
        //                         getAllTasks();
        //                         console.log(res);
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    console.log(err);
        //                })
        //         }
        //     } else {
        //         console.log('Not Found');
        //     }
        // };
        // //endregion
        //
        // function getAllPipelineVars() {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.getAllPipelineVars(vm.currentPipeline, token)
        //         .then(function(res) {
        //             vm.allPipelineVars = res;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err)
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getAllPipelineVars(vm.currentPipeline, token)
        //                 .then(function(res) {
        //                     vm.allPipelineVars = res;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err)
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        //
        // function getAllStageVars() {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.getAllStageVars(vm.currentPipeline, vm.currentStage, token)
        //         .then(function(res) {
        //             vm.allStageVars = res;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err)
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getAllStageVars(vm.currentPipeline, vm.currentStage, token)
        //                 .then(function(res) {
        //                     vm.allStageVars = res;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err)
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        //
        // function getAllJobVars() {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.getAllJobVars(vm.currentPipeline, vm.currentStage, vm.currentJob, token)
        //         .then(function(res) {
        //             vm.allJobVars = res;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err)
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getAllJobVars(vm.currentPipeline, vm.currentStage, vm.currentJob, token)
        //                 .then(function(res) {
        //                     vm.allJobVars = res;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err)
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        //
        // vm.getPipelineVar = function(variableName) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.getPipelineVar(vm.currentPipeline, variableName, token)
        //         .then(function(res) {
        //             vm.variableDeleteButton = false;
        //             vm.pipelineVar = res;
        //             vm.updatedPipelineVar = vm.pipelineVar;
        //             vm.currentPipelineVar = vm.pipelineVar.Name;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err)
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getPipelineVar(vm.currentPipeline, variableName, token)
        //                 .then(function(res) {
        //                     vm.pipelineVar = res;
        //                     vm.updatedPipelineVar = vm.pipelineVar;
        //                     vm.currentPipelineVar = vm.pipelineVar.Name;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err)
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        //
        // vm.getStageVar = function(variableName) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.getStageVar(vm.currentPipeline, vm.currentStage, variableName, token)
        //         .then(function(res) {
        //             vm.variableDeleteButton = false;
        //             vm.stageVar = res;
        //             vm.updatedStageVar = vm.stageVar;
        //             vm.currentStageVar = vm.stageVar.Name;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err)
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getStageVar(vm.currentPipeline, vm.currentStage, variableName, token)
        //                 .then(function(res) {
        //                     vm.stageVar = res;
        //                     vm.updatedStageVar = vm.stageVar;
        //                     vm.currentStageVar = vm.stageVar.Name;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err)
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        // vm.getJobVar = function(variableName) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.getJobVar(vm.currentPipeline, vm.currentStage, vm.currentJob, variableName, token)
        //         .then(function(res) {
        //             vm.variableDeleteButton = false;
        //             vm.jobVar = res;
        //             vm.updatedJobVar = vm.jobVar;
        //             vm.currentJobVar = vm.jobVar.Name;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err)
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getJobVar(vm.currentPipeline, vm.currentStage, vm.currentJob, variableName, token)
        //                 .then(function(res) {
        //                     vm.jobVar = res;
        //                     vm.updatedJobVar = vm.jobVar;
        //                     vm.currentJobVar = vm.jobVar.Name;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err)
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        //
        // vm.addPipelineVar = function(variable) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.createPipelineVar(vm.currentPipeline, variable, token)
        //         .then(function(res) {
        //             vm.newPipelineVar = {};
        //             getAllPipelineVars();
        //             console.log(res);
        //         }, function(err) {
        //             vm.newPipelineVar = {};
        //             console.log(err);
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.createPipelineVar(vm.currentPipeline, variable, token)
        //                 .then(function(res) {
        //                     vm.newPipelineVar = {};
        //                     getAllPipelineVars();
        //                     console.log(res);
        //                 }, function(err) {
        //                     vm.newPipelineVar = {};
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        // vm.addStageVar = function(variable) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.createStageVar(vm.currentPipeline, vm.currentStage, variable, token)
        //         .then(function(res) {
        //             vm.newStageVar = {};
        //             getAllStageVars();
        //             console.log(res);
        //         }, function(err) {
        //             vm.newStageVar = {};
        //             console.log(err);
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.createStageVar(vm.currentPipeline, vm.currentStage, variable, token)
        //                 .then(function(res) {
        //                     vm.newStageVar = {};
        //                     getAllStageVars();
        //                     console.log(res);
        //                 }, function(err) {
        //                     vm.newStageVar = {};
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        // vm.addJobVar = function(variable) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.createJobVar(vm.currentPipeline, vm.currentStage, vm.currentJob, variable, token)
        //         .then(function(res) {
        //             vm.newJobVar = {};
        //             getAllJobVars();
        //             console.log(res);
        //         }, function(err) {
        //             vm.newJobVar = {};
        //             console.log(err);
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.createJobVar(vm.currentPipeline, vm.currentStage, vm.currentJob, variable, token)
        //                 .then(function(res) {
        //                     vm.newJobVar = {};
        //                     getAllJobVars();
        //                     console.log(res);
        //                 }, function(err) {
        //                     vm.newJobVar = {};
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        //
        // vm.deletePipelineVar = function(variableName) {
        //     if (vm.pipelineVar != null) {
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.deletePipelineVar(vm.currentPipeline, variableName, token)
        //             .then(function(res) {
        //                 getAllPipelineVars();
        //                 console.log(res);
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.deletePipelineVar(vm.currentPipeline, variableName, token)
        //                     .then(function(res) {
        //                         getAllPipelineVars();
        //                         console.log(res);
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    console.log(err);
        //                })
        //         }
        //     } else {
        //         console.log('Not found');
        //     }
        // }
        //
        // vm.deleteStageVar = function(variableName) {
        //     if (vm.stageVar != null) {
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.deleteStageVar(vm.currentPipeline, vm.currentStage, variableName, token)
        //             .then(function(res) {
        //                 getAllStageVars();
        //                 console.log(res);
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.deleteStageVar(vm.currentPipeline, vm.currentStage, variableName, token)
        //                     .then(function(res) {
        //                         getAllStageVars();
        //                         console.log(res);
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    console.log(err);
        //                })
        //         }
        //     } else {
        //         console.log('Not found');
        //     }
        // }
        // vm.deleteJobVar = function(variableName) {
        //     if (vm.jobVar != null) {
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.deleteJobVar(vm.currentPipeline, vm.currentStage, vm.currentJob, variableName, token)
        //             .then(function(res) {
        //                 getAllJobVars();
        //                 console.log(res);
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.deleteJobVar(vm.currentPipeline, vm.currentStage, vm.currentJob, variableName, token)
        //                     .then(function(res) {
        //                         getAllJobVars();
        //                         console.log(res);
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    console.log(err);
        //                })
        //         }
        //     } else {
        //         console.log('Not found');
        //     }
        // }
        //
        // vm.updatePipelineVar = function(variable) {
        //     if (vm.pipelineVar != null) {
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.updatePipelineVar(vm.currentPipeline, vm.currentPipelineVar, variable, token)
        //             .then(function(res) {
        //                 getAllPipelineVars();
        //                 console.log(res);
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.updatePipelineVar(vm.currentPipeline, vm.currentPipelineVar, variable, token)
        //                     .then(function(res) {
        //                         getAllPipelineVars();
        //                         console.log(res);
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    console.log(err);
        //                })
        //         }
        //     } else {
        //         console.log('Not found');
        //     }
        // }
        //
        // vm.updateStageVar = function(variable) {
        //     if (vm.stageVar != null) {
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.updateStageVar(vm.currentPipeline, vm.currentStage, vm.currentStageVar, variable, token)
        //             .then(function(res) {
        //                 getAllStageVars();
        //                 console.log(res);
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.updateStageVar(vm.currentPipeline, vm.currentStage, vm.currentStageVar, variable, token)
        //                     .then(function(res) {
        //                         getAllStageVars();
        //                         console.log(res);
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    console.log(err);
        //                })
        //         }
        //     } else {
        //         console.log('Not found');
        //     }
        // }
        // vm.updateJobVar = function(variable) {
        //     if (vm.jobVar != null) {
        //         var tokenIsValid = authDataService.checkTokenExpiration();
        //         if (tokenIsValid) {
        //            var token = window.localStorage.getItem("accessToken");
        //            oldpipeConfig.updateJobVar(vm.currentPipeline, vm.currentStage, vm.currentJob, vm.currentJobVar, variable, token)
        //             .then(function(res) {
        //                 getAllJobVars();
        //                 console.log(res);
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //         } else {
        //            var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //            authDataService.getNewToken(currentRefreshToken)
        //                .then(function (res) {
        //                    var token = res.access_token;
        //                    oldpipeConfig.updateJobVar(vm.currentPipeline, vm.currentStage, vm.currentJob, vm.currentJobVar, variable, token)
        //                     .then(function(res) {
        //                         getAllJobVars();
        //                         console.log(res);
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //                }, function (err) {
        //                    console.log(err);
        //                })
        //         }
        //     } else {
        //         console.log('Not found');
        //     }
        // }
        //
        // //region helpers
        // function getAllStages() {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.getAllStages(vm.currentPipeline, token)
        //         .then(function(res) {
        //             vm.allStages = res;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err);
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getAllStages(vm.currentPipeline, token)
        //                 .then(function(res) {
        //                     vm.allStages = res;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        //
        // function getAllJobs() {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.getAllJobs(vm.currentPipeline, vm.currentStage, token)
        //         .then(function(res) {
        //             vm.allJobs = res;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err);
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getAllJobs(vm.currentPipeline, vm.currentStage, token)
        //                 .then(function(res) {
        //                     vm.allJobs = res;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        //
        // function getAllTasks() {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.getAllTasks(vm.currentPipeline, vm.currentStage, vm.currentJob, token)
        //         .then(function(res) {
        //             vm.allTasks = res;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err);
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getAllTasks(vm.currentPipeline, vm.currentStage, vm.currentJob, token)
        //                 .then(function(res) {
        //                     vm.allTasks = res;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        //
        // function getAllMaterials() {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.getAllMaterials(vm.currentPipeline, token)
        //         .then(function(res) {
        //             vm.allMaterials = res;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err);
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getAllMaterials(vm.currentPipeline, token)
        //                 .then(function(res) {
        //                     vm.allMaterials = res;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        //
        // function getAllArtifacts() {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //        var token = window.localStorage.getItem("accessToken");
        //        oldpipeConfig.getAllArtifacts(vm.currentPipeline, vm.currentStage, vm.currentJob, token)
        //         .then(function(res) {
        //             vm.allArtifacts = res;
        //             console.log(res);
        //         }, function(err) {
        //             console.log(err);
        //         })
        //     } else {
        //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //        authDataService.getNewToken(currentRefreshToken)
        //            .then(function (res) {
        //                var token = res.access_token;
        //                oldpipeConfig.getAllArtifacts(vm.currentPipeline, vm.currentStage, vm.currentJob, token)
        //                 .then(function(res) {
        //                     vm.allArtifacts = res;
        //                     console.log(res);
        //                 }, function(err) {
        //                     console.log(err);
        //                 })
        //            }, function (err) {
        //                console.log(err);
        //            })
        //     }
        // }
        // //endregion
        //
        // vm.gitInputChange = function(gitName) {
        //     if (gitName == undefined || gitName.length == 0) {
        //         $('#logoGit').removeClass('l-active2');
        //     } else {
        //         $('#logoGit').addClass('l-active2');
        //     }
        // };
        // vm.tfsInputChange = function(tfsName) {
        //     if (tfsName == undefined || tfsName.length == 0) {
        //         $('#logoTfs').removeClass('l-active2');
        //     } else {
        //         $('#logoTfs').addClass('l-active2');
        //     }
        // };

        // vm.getPipelineForConfig(vm.currentPipeline);
        // if (vm.currentStage != undefined) {
        //     vm.getStage(vm.currentStage);
        //     if (vm.currentJob != undefined) {
        //         vm.getJob(vm.currentJob);
        //     }
        // }

        //var refresh = $interval(function () {
        //  vm.getPipelineForConfig(vm.currentPipeline);
        //  if (vm.currentStage != undefined) {
        //    vm.getStage(vm.currentStage);
        //    if (vm.currentJob != undefined) {
        //      vm.getJob(vm.currentJob);
        //    }
        //  }
        //}, 3000);
        //
        //$scope.$on('$destroy', function () {
        //  $interval.cancel(refresh);
        //  refresh = undefined;
        //});

    });

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
    .controller('PipelinesController', function($rootScope, $scope, $log, $interval, pipeExecService, pipeService, authDataService, viewModel, pipeConfigService, adminMaterialService, moment) {
        var vm = this;
        vm.toggleLogo = 1;

        vm.defaultPipelineText = {
            empty: "No pipelines in "
        };

        vm.popOverOptions = {
            popOverTitles: {
                pipeline: 'The Pipeline allows crafting the entire application release process from start to finish. A Pipeline consists of Stages, which in turn consist of Jobs, which consist of Tasks.',
                automaticScheduling: 'If selected, the Pipeline will trigger automatically, creating a new run, when its Material is updated.',
                materialPoll: 'If checked, HawkCD will poll your Material for changes automatically.',
                materialCredentials: 'Your GitHub credentials. If the git repository is private, you will need to provide your GitHub credentials, so that HawkCD can access your repository.',
                runPipeline: 'Run the Pipeline',
                pausePipeline: 'Pause the Pipeline',
                stopPipeline: 'Cancel the Pipeline'
            }
        };

        vm.formData = {};

        vm.allPermissions = [];

        vm.allMaterialDefinitions = [];

        vm.allDefinitionsAndRuns = [];

        vm.allPipelineGroups = angular.copy(viewModel.allPipelineGroups);

        vm.currentStageRuns = [];

        vm.allPipelines = angular.copy(viewModel.allPipelines);

        $scope.$watch(function() {
            return viewModel.allPipelineGroups
        }, function(newVal, oldVal) {
            vm.allPipelineGroups = angular.copy(viewModel.allPipelineGroups);

            vm.allPipelineGroups.forEach(function (currentPipelineGroup, groupIndex, groupArray) {
                currentPipelineGroup.pipelines.forEach(function (currentPipeline, pipelineIndex, pipelineArray) {
                    if(currentPipeline.lastRun){
                        currentPipeline.lastRun.stages.forEach(function (currentStage, stageIndex, stageArray) {
                            if(currentStage.endTime) {
                                currentPipeline.lastRun.lastStage = currentStage;
                                currentPipeline.lastRun.lastStage.localEndDate = moment.formatDateUTCToLocal(currentStage.endTime);
                                currentPipeline.lastRun.lastStage.localEndTime = moment.formatTimeInLocal(currentStage.endTime.time);
                            }
                        });
                        if (currentPipeline.lastRun.startTime) {
                            currentPipeline.lastRun.localStartDate = moment.formatDateUTCToLocal(currentPipeline.lastRun.startTime);
                            currentPipeline.lastRun.localStartTime = moment.formatTimeInLocal(currentPipeline.lastRun.startTime.time);
                        }
                        if (currentPipeline.lastRun.endTime) {
                            currentPipeline.lastRun.localEndDate = moment.formatDateUTCToLocal(currentPipeline.lastRun.endTime);
                            currentPipeline.lastRun.localEndTime = moment.formatTimeInLocal(currentPipeline.lastRun.endTime.time);
                        }
                    }
                });
                currentPipelineGroup.pipelines.sort(function(a, b) {
                    return a.name > b.name;
                });
            });
            vm.allPipelineGroups.sort(function(a, b) {
                return a.name > b.name;
            });
            console.log(vm.allPipelineGroups);
        }, true);

        $scope.$watch(function() {
            return viewModel.allMaterialDefinitions
        }, function(newVal, oldVal) {
            vm.allMaterialDefinitions = angular.copy(viewModel.allMaterialDefinitions);
            console.log(vm.allMaterialDefinitions);
        }, true);

        vm.currentMaterials = [];
        vm.selectedMaterial = {};
        vm.materialObject = {};

        vm.getMaterial = function() {
            vm.allMaterialDefinitions.forEach(function(material, index, array) {
                if (vm.currentMaterials.indexOf(material) === -1) {
                    vm.currentMaterials.push(material);
                }
            });
        };

        // vm.getStageRunsFromPipeline = function(pipeline) {
        //     vm.currentStageRuns = [];
        //     // vm.allPipelineRuns.forEach(function(currentPipeline, index, array) {
        //     //     if (currentPipeline.pipelineDefinitionId == pipeline.id) {
        //     //         if (currentPipeline.stageDefinitions.length == 0) {
        //     //             pipeline.stageDefinitions.forEach(function(currentStageDefinition, stageIndex, stageArray) {
        //     //                 vm.currentStageRuns.push(currentStageDefinition);
        //     //             });
        //     //         } else {
        //     //             currentPipeline.stages.forEach(function(currentStage, index, array) {
        //     //                 vm.currentStageRuns.push(currentStage);
        //     //             });
        //     //         }
        //     //     }
        //     // });
        //     console.log(vm.currentStageRuns);
        //     return vm.currentStageRuns;
        // };

        vm.all = [];

        vm.groupId = {};

        vm.pipeId = {};

        vm.all = vm.allPipelines;

        vm.materialType = "hidden";

        vm.deletePipelineDefinition = function(pipeline) {
            pipeConfigService.deletePipelineDefinition(pipeline);
        };

        //region add pipeline modal config
        vm.bar = 1;
        vm.back = function() {
            vm.bar--;
        };
        vm.next = function() {
            vm.bar++;
            if (vm.bar === 3 && vm.materialType === 'existing') {
                vm.materialObject = JSON.parse(vm.selectedMaterial);
            }
        };
        vm.close = function() {
            vm.bar = 1;
            vm.formData = {}
            vm.selectedMaterial = {};
            vm.materialObject = {};
            vm.materialType = "hidden";
            $('#logoTfs').removeClass('l-active2');
            $('#logoNuget').removeClass('l-active2');
            $('#logoGit').removeClass('l-active2');
        };
        //endregion

        //region pipeline controls
        vm.play = function(pipelineDefinition) {
            pipelineDefinition.disabled = true;
            if(pipelineDefinition.lastRun.status == 'PAUSED'){
                pipeExecService.pausePipeline(pipelineDefinition.lastRun);
            } else {
                var pipeline = {
                    "pipelineDefinitionId": pipelineDefinition.id,
                    "pipelineDefinitionName": pipelineDefinition.name,
                    "triggerReason": viewModel.user.username
                };

                pipeExecService.startPipeline(pipeline);
            }
        };

        vm.pause = function (pipelineDefinition) {
            pipelineDefinition.disabled = true;
            pipeExecService.pausePipeline(pipelineDefinition.lastRun);
        };

        //TODO Not implemented on the back-end yet
        vm.stop = function(pipelineDefinition, pipeline) {
            pipelineDefinition.disabled = true;
            pipeExecService.stopPipeline(pipeline);
        };
        //endregion

        vm.getGroupName = function(input) {
            vm.groupName = input.name;
            vm.groupId = input.id;
        };
        vm.getPipeName = function(input) {
            vm.pipeName = input.name;
            vm.pipeId = input.id;
        };

        vm.addPipeline = function(formData) {
            vm.formData = formData;
            var material = {};
            var materialId = {};
            var addPipelineDTO = {};

            addPipelineDTO = {
                pipelineDefinition: {
                    "name": vm.formData.pipeline.name,
                    "pipelineGroupId": vm.groupId,
                    "groupName": vm.groupName,
                    "materialDefinitions": [],
                    "environmentVariables": [],
                    "parameters": [],
                    "environment": {},
                    "stageDefinitions": [{
                        "name": "Default",
                        "jobDefinitions": [{
                            "name": "defaultJob",
                            "taskDefinitions": [{
                                "command": "cmd",
                                "arguments": "/c",
                                "runIfCondition": 'PASSED',
                                "type": 'EXEC'
                            }],
                            "environmentVariables": []
                        }],
                        "environmentVariables": [],
                        "neverCleanArtifacts": false,
                        "cleanWorkingDirectory": false,
                        "stageType": 'false'
                    }],
                    "isAutoSchedulingEnabled": vm.formData.pipeline.autoSchedule
                }
            };

            if (vm.materialType == 'git') {
                var material = {
                    "pipelineDefinitionName": vm.formData.pipeline.name,
                    "name": vm.formData.material.git.name,
                    "type": 'GIT',
                    "repositoryUrl": vm.formData.material.git.url,
                    "isPollingForChanges": vm.formData.material.git.poll,
                    "destination": vm.formData.material.git.name,
                    "branch": vm.formData.material.git.branch || 'master'
                };
                if (formData.material.git.credentials) {
                    material.username = formData.material.git.username;
                    material.password = formData.material.git.password;
                }
                addPipelineDTO.materialDefinition = material;
                pipeConfigService.addPipelineDefinitionWithMaterial(addPipelineDTO.pipelineDefinition, addPipelineDTO.materialDefinition);
            }
            if (vm.materialType == 'existing') {
                addPipelineDTO.materialDefinition = vm.materialObject.id;
                pipeConfigService.addPipelineDefinitionWithExistingMaterial(addPipelineDTO.pipelineDefinition, addPipelineDTO.materialDefinition);
            }

            vm.selectedMaterial = {};
            vm.materialObject = {};
            vm.materialType = 'hidden';
        };

        vm.wizardInfo = {
            steps: {
                first: 'Setup',
                second: 'Materials',
                third: 'Review'
            },
            buttons: {
                back: 'Back',
                continue: 'Continue',
                submit: 'Submit'
            },
            labels: {
                autoSchedule: 'Auto Scheduling',
                name: 'Name',
                pipelineName: 'Pipeline Name',
                gitUrl: 'Git URL',
                branch: 'Branch',
                username: 'Username',
                password: 'Password',
                tfsDomain: 'Domain',
                Url: 'Url',
                projectPath: 'Project Path',
                stageName: 'Stage Name',
                trigger: 'Trigger option',
                nugetUrl: 'URL',
                nugetPackage: 'Package',
                nugetPackageVersion: 'Package Version',
                nugetPrerelease: 'Include Prerelease'
            },
            validationMsg: {
                error: "You have some form errors. Please check below.",
                success: "Your form validation is successful!"
            },
            placeholders: {
                pipelineName: 'Enter your pipeline name',
                gitUrl: 'Enter GIT url',
                username: 'Enter TFS username',
                password: 'Enter TFS password',
                tfsUrl: 'Enter TFS url',
                tfsDomain: 'Enter TFS domain',
                projectPath: 'Enter TFS project path',
                stageName: 'Enter stage name',
                materialName: 'Enter material name',
                gitUsername: 'Enter GIT username',
                gitPassword: 'Enter GIT password',
                gitBranch: 'Enter GIT branch (optional)',
                nugetUrl: 'Enter NuGet url',
                nugetPackage: 'Enter NuGet Package',
                nugetPackageVersion: 'Enter NuGet Package Version'
            }
        };

        //region Changes class of logos if name is entered by user
        vm.gitInputChange = function(gitName) {
            if (gitName == undefined || gitName.length == 0) {
                $('#logoGit').removeClass('l-active2');
            } else {
                $('#logoGit').addClass('l-active2');
            }
        };
        vm.tfsInputChange = function(tfsName) {
            if (tfsName == undefined || tfsName.length == 0) {
                $('#logoTfs').removeClass('l-active2');
            } else {
                $('#logoTfs').addClass('l-active2');
            }
        };
        vm.nugetInputChange = function(nugetName) {
            if (nugetName == undefined || nugetName.length == 0) {
                $('#logoNuget').removeClass('l-active2');
            } else {
                $('#logoNuget').addClass('l-active2');
            }
        };
        //endregion

        //vm.getAll();

        // var interval = $interval(function () {
        //     vm.getAll();
        // }, 3000);

        // $scope.$on('$destroy', function () {
        //     $interval.cancel(interval);
        //     interval = undefined;
        // });

    });

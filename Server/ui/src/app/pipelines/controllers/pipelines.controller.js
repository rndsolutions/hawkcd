'use strict';

angular
    .module('hawk.pipelinesManagement')
    .controller('PipelinesController', function($rootScope, $scope, $log, $interval, pipeStats, pipeConfig, pipeExecService, pipesService, pipeStatsService, authDataService, viewModel, pipeConfigService, adminMaterialService) {
        var vm = this;
        vm.toggleLogo = 1;

        vm.defaultPipelineText = {
            empty: "No pipelines in "
        };

        vm.formData = {};
        vm.allPipelines = [];

        vm.allPermissions = [];

        vm.allPipelineRuns = [];

        vm.allMaterialDefinitions = [];

        vm.allDefinitionsAndRuns = [];

        vm.allPipelineGroups = [];

        vm.currentStageRuns = [];

        // vm.allDefinitionsAndRuns = viewModel.allPipelineDefinitions;

        // $scope.$watch(function() { return viewModel.allPipelineDefinitions }, function(newVal, oldVal) {
        //     vm.allDefinitionsAndRuns = viewModel.allPipelineDefinitions;
        //     console.log(vm.allDefinitionsAndRuns);
        // });

        vm.allPipelines = angular.copy(viewModel.allPipelines);

        $scope.$watch(function() {
            return viewModel.allPipelineGroups
        }, function(newVal, oldVal) {
            vm.allPipelineGroups = angular.copy(viewModel.allPipelineGroups);
            console.log(vm.allPipelineGroups);
        }, true);

        $scope.$watch(function() {
            return viewModel.allPipelines
        }, function(newVal, oldVal) {
            vm.allPipelines = angular.copy(viewModel.allPipelines);
            vm.allPipelines.forEach(function (currentPipeline, pipelineIndex, pipelineArray) {
                currentPipeline.disabled = false;
            });
            vm.allPipelines.sort(function(a, b) {
                return a.name - b.name;
            });
            console.log(vm.allPipelines);
        }, true);

        $scope.$watchCollection(function() {
            return viewModel.allMaterialDefinitions
        }, function(newVal, oldVal) {
            vm.allMaterialDefinitions = angular.copy(viewModel.allMaterialDefinitions);
            console.log(vm.allMaterialDefinitions);
        });

        $scope.$watch(function() {
            return viewModel.allPipelineRuns
        }, function(newVal, oldVal) {
            vm.allPipelineRuns = angular.copy(viewModel.allPipelineRuns);
            vm.allPipelineRuns.sort(function(a, b) {
                return a.executionId - b.executionId;
            });
            vm.allPipelineRuns.forEach(function (currentPipelineRun, index, array) {
                vm.allPipelines.forEach(function (currentPipeline, pipelineIndex, array) {
                    if(currentPipelineRun.pipelineDefinitionId == currentPipeline.id){
                        if(currentPipelineRun.triggerReason == null) {
                            currentPipelineRun.triggerReason = viewModel.user.username;
                        }
                        vm.allPipelines[pipelineIndex].stages = currentPipelineRun.stages;
                        vm.allPipelines[pipelineIndex].lastRun = currentPipelineRun;
                    }
                });
            });
            vm.allPipelineGroups.forEach(function(currentPipelineGroup, index, array) {
                vm.allPipelineGroups[index].pipelines.forEach(function(currentPipelineFromGroup, pipelineFromGroupIndex, array) {
                    vm.allPipelines.forEach(function(currentPipeline, pipelineIndex, array) {
                        // viewModel.user.permissions.forEach(function (currentPermission, permissionIndex, permissionArray) {
                        //     if(currentPipeline.id == currentPermission.permittedEntityId) {
                        //         currentPipeline.role = currentPermission.permissionType;
                        //         viewModel.allPipelines[0].role = 'ADMIN';
                        //         console.log(currentPermission.role);
                        //     }
                        // });
                        currentPipeline.disabled = false;
                        if (currentPipelineFromGroup.id == currentPipeline.id) {
                            vm.allPipelineGroups[index].pipelines[pipelineFromGroupIndex] = vm.allPipelines[pipelineIndex];
                        }
                    });
                });
                vm.allPipelineGroups[index].pipelines.sort(function(a, b) {
                    return a.executionId - b.executionId;
                });
            });

            console.log(vm.allPipelineRuns);
        }, true);

        // $scope.$watchCollection(function() { return viewModel.allMaterialDefinitions }, function(newVal, oldVal) {
        //     vm.allMaterials = viewModel.allMaterialDefinitions;
        //     console.log(vm.allMaterials);
        // });

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

        vm.getStageRunsFromPipeline = function(pipeline) {
            vm.currentStageRuns = [];
            vm.allPipelineRuns.forEach(function(currentPipeline, index, array) {
                if (currentPipeline.pipelineDefinitionId == pipeline.id) {
                    if (currentPipeline.stageDefinitions.length == 0) {
                        pipeline.stageDefinitions.forEach(function(currentStageDefinition, stageIndex, stageArray) {
                            vm.currentStageRuns.push(currentStageDefinition);
                        });
                    } else {
                        currentPipeline.stages.forEach(function(currentStage, index, array) {
                            vm.currentStageRuns.push(currentStage);
                        });
                    }
                }
            });
            console.log(vm.currentStageRuns);
            return vm.currentStageRuns;
        };

        vm.all = [];

        vm.groupId = {};

        vm.pipeId = {};

        vm.all = vm.allPipelines;

        vm.materialType = "hidden";

        vm.deletePipelineDefinition = function(id) {
            pipeConfigService.deletePipelineDefinition(id);
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
            var pipeline = {
                "pipelineDefinitionId": pipelineDefinition.id,
                "pipelineDefinitionName": pipelineDefinition.name
            };
            pipeExecService.startPipeline(pipeline);
        };

        //TODO Not implemented on the back-end yet
        vm.stop = function(pipelineDefinition, pipeline) {
            pipelineDefinition.disabled = true;
            pipeline.shouldBeCanceled = true;
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

        // vm.getAll = function () {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //
        //         pipeConfig.getAllPipelineDefs(token)
        //             .then(function (defs) {
        //                     var all = [];
        //                     vm.allDefinitionsAndRuns = [];
        //                     vm.allDefinitionsAndRuns.push(defs);
        //
        //                     pipeStats.getAllPipelines(token)
        //                         .then(function (runs) {
        //                                 vm.allDefinitionsAndRuns.push(runs);
        //
        //                                 //Combine definitions and runs of pipelines in one array
        //                                 all = pipesService.combineDefinitionAndRuns(vm.allDefinitionsAndRuns);
        //
        //                                 //Orders the data by pipeline name sorted by ExecutionID
        //                                 all = pipesService.arrangePipelinesByNameAndExecution(all);
        //
        //                                 //This method gets last execution for each stage in the last run of each pipeline
        //                                 all = pipesService.getLastStageRunForLastPipeline(all);
        //
        //                                 //Get groups only - needed for empty groups (no pipelines)
        //                                 pipeConfig.getAllGroups(token)
        //                                     .then(function (res) {
        //                                         for (var i = 0; i < res.length; i++) {
        //                                             if (res[i].Pipelines.length == 0) {
        //                                                 var name = res[i].Name;
        //                                                 all[name] = res[i];
        //                                             }
        //                                         }
        //                                         vm.allPipelines = all;
        //                                     }, function (err) {
        //                                         console.log(err);
        //                                     })
        //                             },
        //                             function (err) {
        //                                 console.log(err);
        //                             })
        //                 },
        //                 function (err) {
        //
        //                 });
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //             .then(function (res) {
        //                 var token = res.access_token;
        //                 pipeConfig.getAllPipelineDefs(token)
        //                     .then(function (defs) {
        //                             var all = [];
        //                             vm.allDefinitionsAndRuns = [];
        //                             vm.allDefinitionsAndRuns.push(defs);
        //
        //                             pipeStats.getAllPipelines(token)
        //                                 .then(function (runs) {
        //                                         vm.allDefinitionsAndRuns.push(runs);
        //                                         //Combine definitions and runs of pipelines in one array
        //                                         all = pipesService.combineDefinitionAndRuns(vm.allDefinitionsAndRuns);
        //
        //                                         //Orders the data by pipeline name sorted by ExecutionID
        //                                         all = pipesService.arrangePipelinesByNameAndExecution(all);
        //
        //                                         //This method gets last execution for each stage in the last run of each pipeline
        //                                         all = pipesService.getLastStageRunForLastPipeline(all);
        //
        //                                         //Get groups only - needed for empty groups (no pipelines)
        //                                         pipeConfig.getAllGroups(token)
        //                                             .then(function (res) {
        //                                                 for (var i = 0; i < res.length; i++) {
        //                                                     if (res[i].Pipelines.length == 0) {
        //                                                         var name = res[i].Name;
        //                                                         all[name] = res[i];
        //                                                     }
        //                                                 }
        //                                                 vm.allPipelines = all;
        //                                             }, function (err) {
        //                                                 console.log(err);
        //                                             })
        //                                     },
        //                                     function (err) {
        //                                         console.log(err);
        //                                     })
        //                         },
        //                         function (err) {
        //                             console.log(err);
        //                         });
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     }
        // };

        // vm.getAll = function() {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         pipeConfig.getAllPipelineDefs(token)
        //             .then(function(res) {
        //                 vm.allDefinitionsAndRuns = [];
        //                 vm.allDefinitionsAndRuns.push(res);
        //                 //vm.allDefinitionsAndRuns = pipesService.arrangePipelinesByNameAndExecution(vm.allDefinitionsAndRuns);
        //                 vm.allPipelines = res;
        //                 console.log(vm.allPipelines);
        //                 console.log(res);
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //             .then(function(res) {
        //                 var token = res.access_token;
        //                 pipeConfig.getAllPipelineDefs(token)
        //                     .then(function(res) {
        //                         vm.allDefinitionsAndRuns = [];
        //                         vm.allDefinitionsAndRuns.push(res);
        //                         //vm.allDefinitionsAndRuns = pipesService.arrangePipelinesByNameAndExecution(vm.allDefinitionsAndRuns);
        //                         vm.allPipelines = res;
        //                         console.log(vm.allPipelines);
        //                         console.log(res);
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //             })
        //     }
        // };

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
                debugger;
                addPipelineDTO.materialDefinition = vm.materialObject.id;
                pipeConfigService.addPipelineDefinitionWithExistingMaterial(addPipelineDTO.pipelineDefinition,addPipelineDTO.materialDefinition);
            }

            vm.selectedMaterial = {};
            vm.materialObject = {};
            vm.materialType = 'hidden';

            //TODO
            // if (vm.formData.material.tfs) {
            //     var tfs = {
            //         "PipelineName": vm.formData.pipeline.name,
            //         "Name": vm.formData.materials.tfs.name,
            //         "Type": 'TFS',
            //         "Url": vm.formData.materials.tfs.url,
            //         "AutoTriggerOnChange": vm.formData.materials.tfs.poll,
            //         "Destination": vm.formData.materials.tfs.name,
            //         "MaterialSpecificDetails": {
            //             "domain": vm.formData.materials.tfs.domain,
            //             "projectPath": vm.formData.materials.tfs.projectPath,
            //             "username": vm.formData.materials.tfs.username,
            //             "password": vm.formData.materials.tfs.password
            //         }
            //     };
            //     materials.push(tfs);
            // }
            // //
            // if (vm.materialType == 'nuget') {
            //     material = {
            //         "pipelineName": vm.formData.pipeline.name,
            //         "name": vm.formData.material.nuget.name,
            //         "type": 'NUGET',
            //         "repositoryUrl": vm.formData.material.nuget.url,
            //         "isPollingForChanges": vm.formData.material.nuget.poll,
            //         "destination": vm.formData.material.nuget.name,
            //         "packageId": vm.formData.material.nuget.packageId,
            //         "includePrerelease": vm.formData.material.nuget.includePrerelease
            //     };
            // }



            // var tokenIsValid = authDataService.checkTokenExpiration();
            // if (tokenIsValid) {
            //     var token = window.localStorage.getItem("accessToken");
            //     pipeConfig.createPipeline(pipeline, token)
            //         .then(function (res) {
            //             vm.formData = {};
            //             vm.getAll();
            //             console.log(res);
            //         }, function (err) {
            //             vm.formData = {};
            //             vm.getAll();
            //             console.log(err);
            //         })
            // } else {
            //     var currentRefreshToken = window.localStorage.getItem("refreshToken");
            //     authDataService.getNewToken(currentRefreshToken)
            //         .then(function (res) {
            //             var token = res.access_token;
            //             pipeConfig.createPipeline(pipeline, token)
            //                 .then(function (res) {
            //                     vm.formData = {};
            //                     vm.getAll();
            //                     console.log(res);
            //                 }, function (err) {
            //                     vm.formData = {};
            //                     vm.getAll();
            //                     console.log(err);
            //                 })
            //         }, function (err) {
            //             console.log(err);
            //         })
            // }
        };

        // vm.removePipeline = function(pipeName) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         pipeConfig.deletePipeline(pipeName, token)
        //             .then(function(res) {
        //                 vm.getAll();
        //                 console.log(res);
        //             }, function(err) {
        //                 vm.getAll();
        //                 console.log(err);
        //             })
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //             .then(function(res) {
        //                 var token = res.access_token;
        //                 pipeConfig.deletePipeline(pipeName, token)
        //                     .then(function(res) {
        //                         vm.getAll();
        //                         console.log(res);
        //                     }, function(err) {
        //                         vm.getAll();
        //                         console.log(err);
        //                     })
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //     }
        // };

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

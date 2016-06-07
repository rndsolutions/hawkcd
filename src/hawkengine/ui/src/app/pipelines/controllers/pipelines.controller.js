'use strict';

angular
    .module('hawk.pipelinesManagement')
    .controller('PipelinesController', function ($scope, $log, $interval, pipeStats, pipeConfig, pipeExec, pipesService, pipeStatsService, authDataService) {
        var vm = this;
        vm.toggleLogo = 1;

        vm.defaultPipelineText = {
            empty: "No pipelines in "
        }

        vm.formData = {};
        vm.allPipelines = [];
        vm.allDefinitionsAndRuns = [];

        vm.disabledBtn = false;

        vm.materialType = "git";

        //region add pipeline modal config
        vm.bar = 1;
        vm.back = function () {
            vm.bar--;
        };
        vm.next = function () {
            vm.bar++;
        };
        vm.close = function () {
            vm.bar = 1;
            vm.formData = {}
            $('#logoTfs').removeClass('l-active2');
            $('#logoNuget').removeClass('l-active2');
            $('#logoGit').removeClass('l-active2');
        };
        //endregion

        //region pipeline controls
        vm.play = function (pipeName) {
            var tokenIsValid = authDataService.checkTokenExpiration();
            if (tokenIsValid) {
                var token = window.localStorage.getItem("accessToken");
                pipeExec.scheduleLatestPipeline(pipeName, token)
                    .then(function (res) {
                        vm.disabledBtn = false;
                        console.log(res);
                    }, function (err) {
                        vm.disabledBtn = false;
                        console.log(err);
                    })
            } else {
                var currentRefreshToken = window.localStorage.getItem("refreshToken");
                authDataService.getNewToken(currentRefreshToken)
                    .then(function (res) {
                        var token = res.access_token;
                        pipeExec.scheduleLatestPipeline(pipeName, token)
                            .then(function (res) {
                                vm.disabledBtn = false;
                                console.log(res);
                            }, function (err) {
                                vm.disabledBtn = false;
                                console.log(err);
                            })
                    }, function (err) {
                        vm.disabledBtn = false;
                        console.log(err);
                    })
            }
        };

        //TODO Not implemented on the back-end yet
        vm.stop = function (pipeId) {
            pipeExec.cancelPipeline(pipeId)
                .then(function (res) {
                    console.log(res.Message);
                }, function (err) {
                    console.log(err);
                })
        };
        //endregion

        vm.getGroupName = function (input) {
            vm.groupName = input.Name;
        };
        vm.getPipeName = function (input) {
            vm.pipeName = input.OriginalName;
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

        vm.getAll = function () {
            var tokenIsValid = authDataService.checkTokenExpiration();
                if (tokenIsValid) {
                    var token = window.localStorage.getItem("accessToken");
                    pipeConfig.getAllPipelineDefs(token)
                        .then(function (res) {
                            vm.allDefinitionsAndRuns = [];
                            vm.allDefinitionsAndRuns.push(res);
                            //vm.allDefinitionsAndRuns = pipesService.arrangePipelinesByNameAndExecution(vm.allDefinitionsAndRuns);
                            vm.allPipelines = res;
                            console.log(vm.allPipelines);
                            console.log(res);
                        }, function (err) {
                            console.log(err);
                        })
                } else {
                    var currentRefreshToken = window.localStorage.getItem("refreshToken");
                    authDataService.getNewToken(currentRefreshToken)
                        .then(function (res) {
                            var token = res.access_token;
                            pipeConfig.getAllPipelineDefs(token)
                                .then(function (res) {
                                    vm.allDefinitionsAndRuns = [];
                                    vm.allDefinitionsAndRuns.push(res);
                                    //vm.allDefinitionsAndRuns = pipesService.arrangePipelinesByNameAndExecution(vm.allDefinitionsAndRuns);
                                    vm.allPipelines = res;
                                    console.log(vm.allPipelines);
                                    console.log(res);
                                }, function (err) {
                                    console.log(err);
                                })
                        })
                }
        };

        vm.addPipeline = function (formData) {
            vm.formData = formData;
            var material = {};
            if (vm.materialType == 'git') {
                material = {
                    "PipelineName": vm.formData.pipeline.name,
                    "Name": vm.formData.material.git.name,
                    "Type": 'GIT',
                    "Url": vm.formData.material.git.url,
                    "AutoTriggerOnChange": vm.formData.material.git.poll,
                    "Destination": vm.formData.material.git.name,
                    "MaterialSpecificDetails": {
                        "branch": vm.formData.material.git.branch || 'master'
                    }
                };
                if (formData.material.git.credentials) {
                    material.MaterialSpecificDetails.username = formData.material.git.username;
                    material.MaterialSpecificDetails.password = formData.material.git.password;
                }
            }
            //TODO
            if (vm.formData.material.tfs) {
                var tfs = {
                    "PipelineName": vm.formData.pipeline.name,
                    "Name": vm.formData.materials.tfs.name,
                    "Type": 'TFS',
                    "Url": vm.formData.materials.tfs.url,
                    "AutoTriggerOnChange": vm.formData.materials.tfs.poll,
                    "Destination": vm.formData.materials.tfs.name,
                    "MaterialSpecificDetails": {
                        "domain": vm.formData.materials.tfs.domain,
                        "projectPath": vm.formData.materials.tfs.projectPath,
                        "username": vm.formData.materials.tfs.username,
                        "password": vm.formData.materials.tfs.password
                    }
                };
                materials.push(tfs);
            }
            //
            if (vm.materialType == 'nuget') {
                material = {
                    "PipelineName": vm.formData.pipeline.name,
                    "Name": vm.formData.material.nuget.name,
                    "Type": 'NUGET',
                    "Url": vm.formData.material.nuget.url,
                    "AutoTriggerOnChange": vm.formData.material.nuget.poll,
                    "Destination": vm.formData.material.nuget.name,
                    "MaterialSpecificDetails": {
                        "packageId": vm.formData.material.nuget.packageId,
                        "includePrerelease": vm.formData.material.nuget.includePrerelease
                    }
                };
            }

            var pipeline = {
                "OriginalName": vm.formData.pipeline.name,
                "Name": vm.formData.pipeline.name,
                "GroupName": vm.groupName,
                "Materials": [material],
                "EnvironmentVariables": [],
                "Parameters": [],
                "Environment": {},
                "Stages": [{
                    "Name": vm.formData.stage.name,
                    "Jobs": [{
                        "Name": "defaultJob",
                        "Tasks": [{
                            "Command": "cmd",
                            "Arguments": ["/c"],
                            "RunIfCondition": 'Passed',
                            "Type": 'Exec'
                        }],
                        "EnvironmentVariables": []
                    }],
                    "EnvironmentVariables": [],
                    "NeverCleanArtifacts": false,
                    "CleanWorkingDirectory": false,
                    "StageType": vm.formData.stage.trigger
                }],
                "AutoScheduling": vm.formData.pipeline.autoSchedule
            };

            var tokenIsValid = authDataService.checkTokenExpiration();
            if (tokenIsValid) {
                var token = window.localStorage.getItem("accessToken");
                pipeConfig.createPipeline(pipeline, token)
                    .then(function (res) {
                        vm.formData = {};
                        vm.getAll();
                        console.log(res);
                    }, function (err) {
                        vm.formData = {};
                        vm.getAll();
                        console.log(err);
                    })
            } else {
                var currentRefreshToken = window.localStorage.getItem("refreshToken");
                authDataService.getNewToken(currentRefreshToken)
                    .then(function (res) {
                        var token = res.access_token;
                        pipeConfig.createPipeline(pipeline, token)
                            .then(function (res) {
                                vm.formData = {};
                                vm.getAll();
                                console.log(res);
                            }, function (err) {
                                vm.formData = {};
                                vm.getAll();
                                console.log(err);
                            })
                    }, function (err) {
                        console.log(err);
                    })
            }
        };

        vm.removePipeline = function (pipeName) {
            var tokenIsValid = authDataService.checkTokenExpiration();
            if (tokenIsValid) {
                var token = window.localStorage.getItem("accessToken");
                pipeConfig.deletePipeline(pipeName, token)
                    .then(function (res) {
                        vm.getAll();
                        console.log(res);
                    }, function (err) {
                        vm.getAll();
                        console.log(err);
                    })
            } else {
                var currentRefreshToken = window.localStorage.getItem("refreshToken");
                authDataService.getNewToken(currentRefreshToken)
                    .then(function (res) {
                        var token = res.access_token;
                        pipeConfig.deletePipeline(pipeName, token)
                            .then(function (res) {
                                vm.getAll();
                                console.log(res);
                            }, function (err) {
                                vm.getAll();
                                console.log(err);
                            })
                    }, function (err) {
                        console.log(err);
                    })
            }
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
        vm.gitInputChange = function (gitName) {
            if (gitName == undefined || gitName.length == 0) {
                $('#logoGit').removeClass('l-active2');
            } else {
                $('#logoGit').addClass('l-active2');
            }
        };
        vm.tfsInputChange = function (tfsName) {
            if (tfsName == undefined || tfsName.length == 0) {
                $('#logoTfs').removeClass('l-active2');
            } else {
                $('#logoTfs').addClass('l-active2');
            }
        };
        vm.nugetInputChange = function (nugetName) {
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

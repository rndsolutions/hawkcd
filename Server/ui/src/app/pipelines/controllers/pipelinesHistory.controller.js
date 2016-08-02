'use strict';

angular
    .module('hawk.pipelinesManagement')
    .controller('PipelinesHistoryController', function($state, $scope, $stateParams, $interval, pipeStats, authDataService, viewModel, moment) {
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

        vm.allPipelineRuns = viewModel.allPipelineRuns;

        vm.updateClock = function(pipelineRun) {

        }

        vm.getLastRunAction = function(pipelineRun) {
            if (pipelineRun.endTime == undefined) {
                return;
            }
            var result = {};
            var runDate = pipelineRun.endTime.date;
            var runEndTime = pipelineRun.endTime.time;
            var delta = moment([runDate.year, (runDate.month - 1), runDate.day, runEndTime.hour, runEndTime.minute, runEndTime.second]);
            var now = moment();
            var diff = moment.duration(moment(now).diff(moment(delta))).humanize();
            result.output = diff + " ago";
            return result;
        }

        vm.truncateGitFromUrl = function(repoUrl,commitId) {
            var pattern = '.git';
            var patternLength = pattern.length;
            var buffer = repoUrl.substr(0,repoUrl.indexOf(pattern));
            var result = buffer + '/' + 'commit' + '/' + commitId;
            return result;
        }

        $scope.$watchCollection(function() {
            return viewModel.allPipelineRuns
        }, function(newVal, oldVal) {
            vm.allPipelineRuns = viewModel.allPipelineRuns;
            vm.currentPipelineRuns = [];
            vm.allPipelineRuns.forEach(function(currentPipelineRun, index, array) {
                if (currentPipelineRun.pipelineDefinitionName == $stateParams.pipelineName) {
                    var result = vm.getLastRunAction(currentPipelineRun);
                    currentPipelineRun.lastPipelineAction = result;
                    currentPipelineRun.materials.forEach(function(currentMaterial,index,array){
                      var definition = currentMaterial.materialDefinition;
                        currentMaterial.gitLink = vm.truncateGitFromUrl(definition.repositoryUrl,definition.commitId);
                    });

                    if (currentPipelineRun.triggerReason == null) {
                        currentPipelineRun.triggerReason = "User";
                    }
                    vm.currentPipelineRuns.push(currentPipelineRun);
                }
            });
            vm.currentPipelineRuns.sort(function(a, b) {
                return b.executionId - a.executionId;
            });
            if (vm.currentPipelineRuns.length > 0) {
                vm.currentJob = vm.currentPipelineRuns[0].stages[vm.currentPipelineRuns[0].stages.length - 1].jobs[vm.currentPipelineRuns[0].stages[vm.currentPipelineRuns[0].stages.length - 1].jobs.length - 1];
            }
            console.log(vm.allPipelineRuns);
            console.log(vm.currentPipelineRuns);
        });


        //Gets all executions of a pipeline by given name
        // vm.getAll = function () {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         pipeStats.getAllRunsByName(vm.pipelineName, token)
        //             .then(function (res) {
        //                 // success
        //                 vm.currentPipeline = res;
        //                 vm.currentPipeline = _.sortBy(vm.currentPipeline, 'ExecutionID');
        //                 vm.currentPipeline.reverse();
        //                 for (var i = 0; i < vm.currentPipeline.length; i += 1) {
        //                     vm.currentPipeline[i].Stages = _.groupBy(vm.currentPipeline[i].Stages, 'Name');
        //                 }
        //
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //             .then(function (res) {
        //                 var token = res.access_token;
        //                 pipeStats.getAllRunsByName(vm.pipelineName, token)
        //                     .then(function (res) {
        //                         // success
        //                         vm.currentPipeline = res;
        //                         vm.currentPipeline = _.sortBy(vm.currentPipeline, 'ExecutionID');
        //                         vm.currentPipeline.reverse();
        //                         for (var i = 0; i < vm.currentPipeline.length; i += 1) {
        //                             vm.currentPipeline[i].Stages = _.groupBy(vm.currentPipeline[i].Stages, 'Name');
        //                         }
        //
        //                     }, function (err) {
        //                         console.log(err);
        //                     })
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     }
        // };

        //Init the controller
        //vm.getAll();

        // var intervalHistory = $interval(function () {
        //     vm.getAll();
        // }, 3000);

        // $scope.$on('$destroy', function () {
        //     $interval.cancel(intervalHistory);
        //     intervalHistory = undefined;
        // });

    });

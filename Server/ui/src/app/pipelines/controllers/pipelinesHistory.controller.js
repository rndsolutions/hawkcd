'use strict';

angular
    .module('hawk.pipelinesManagement')
    .controller('PipelinesHistoryController', function ($state, $scope, $stateParams, $interval, pipeStats, authDataService, viewModel) {
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
                start: 'Start',
                trigger: 'Trigger',
                change: 'Change',
                state: 'State',
                outcome: 'Outcome',
                execution: 'Execution'
            }
        };

        vm.currentPipelineRuns = [];

        //Get the current group and pipeline name
        vm.groupName = $stateParams.groupName;
        vm.pipelineName = $stateParams.pipelineName;

        vm.allPipelineRuns = viewModel.allPipelineRuns;

        $scope.$watch(function() { return viewModel.allPipelineRuns }, function(newVal, oldVal) {
            vm.allPipelineRuns = viewModel.allPipelineRuns;
            vm.currentPipelineRuns = [];
            vm.allPipelineRuns.forEach(function (currentPipelineRun, index, array) {
                if (currentPipelineRun.pipelineDefinitionName == $stateParams.pipelineName) {
                    vm.currentPipelineRuns.push(currentPipelineRun);
                }
            });
            vm.allPipelineRuns.sort(function (a, b) {
                return b.executionId-a.executionId;
            });
            console.log(vm.allPipelineRuns);
            console.log(vm.currentPipelineRuns);
        }, true);


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

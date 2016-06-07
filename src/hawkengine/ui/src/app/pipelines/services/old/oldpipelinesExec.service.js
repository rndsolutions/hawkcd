'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('pipeExec', ['$http', '$q', 'CONSTANTS', function ($http, $q, CONSTANTS) {
        var pipeExec = this;

        var pipesEndPoint = CONSTANTS.BASE_URL + CONSTANTS.EXEC + CONSTANTS.PIPELINES + '/';
        var stagesEndPoint = CONSTANTS.BASE_URL + CONSTANTS.EXEC + CONSTANTS.STAGES + '/';
        // var agentsEndPoint = CONSTANTS.BASE_URL + CONSTANTS.EXEC + CONSTANTS.AGENTS + '/';

        //region /pipelines
        pipeExec.scheduleLatestPipeline = function (pipeName, token) {
            var defer = $q.defer();
            var scheduleLatestPipeEndPoint = pipesEndPoint + pipeName + '/schedule-latest'

            $http.post(scheduleLatestPipeEndPoint, {}, {
                    headers: {
                        'Authorization': 'bearer ' + token
                    }
                })
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeExec.schedulePipelineWithRevision = function (pipeName, changes) {
            var defer = $q.defer();

            $http.post(pipesEndPoint + pipeName + '/schedule', changes)
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeExec.pausePipeline = function (pipeId) {
            var defer = $q.defer();

            $http.post(pipesEndPoint + pipeId + '/pause')
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeExec.resumePipeline = function (pipeId) {
            var defer = $q.defer();

            $http.post(pipesEndPoint + pipeId + '/resume')
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeExec.restartPipeline = function (pipeId) {
            var defer = $q.defer();

            $http.post(pipesEndPoint + pipeId + '/restart')
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeExec.cancelPipeline = function (pipeId) {
            var defer = $q.defer();

            $http.post(pipesEndPoint + pipeId + '/cancel')
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        //endregion

        //region /stages
        pipeExec.scheduleStage = function (pipeName, pipeRunId, stageName) {
            var defer = $q.defer();

            $http.post(stagesEndPoint + pipeName + '/' + pipeRunId + '/' + stageName + '/schedule')
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeExec.scheduleStageWithJobs = function (pipeName, pipeRunId, stageName, jobsForExec, token) {
            var defer = $q.defer();

            $http.post(stagesEndPoint + pipeName + '/' + pipeRunId + '/' + stageName + '/schedule-jobs', jobsForExec, {
                    headers: {
                        'Authorization': 'bearer ' + token
                    }
                })
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        //endregion

        //region /agents
        // pipeExec.enableAgent = function (agentId, token) {
        //     var defer = $q.defer();
        //     var apiEndPoint = agentsEndPoint + agentId + '/enable';
        //
        //     $http.post(apiEndPoint, {}, {
        //             headers: {
        //                 'Authorization': 'bearer ' + token
        //             }
        //         })
        //         .success(function (res) {
        //             defer.resolve(res);
        //         })
        //         .error(function (err, status) {
        //             defer.reject(err);
        //         });
        //
        //     return defer.promise;
        // }
        //
        // pipeExec.disableAgent = function (agentId, token) {
        //     var defer = $q.defer();
        //     var apiEndPoint = agentsEndPoint + agentId + '/disable';
        //     $http.post(apiEndPoint, {}, {
        //             headers: {
        //                 'Authorization': 'bearer ' + token
        //             }
        //         })
        //         .success(function (res) {
        //             defer.resolve(res);
        //         })
        //         .error(function (err, status) {
        //             defer.reject(err);
        //         });
        //
        //     return defer.promise;
        // };
        //
        // pipeExec.deleteAgent = function (agentId, token) {
        //     var defer = $q.defer();
        //     var apiEndPoint = agentsEndPoint + agentId;
        //     $http.delete(apiEndPoint, {
        //             headers: {
        //                 'Authorization': 'bearer ' + token
        //             }
        //         })
        //         .success(function (res) {
        //             defer.resolve(res);
        //         })
        //         .error(function (err, status) {
        //             defer.reject(err);
        //         });
        //
        //     return defer.promise;
        // };
        //endregion

        return pipeExec;
                    }]);

'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('pipeStats', ['$http', '$q', 'CONSTANTS', function ($http, $q, CONSTANTS) {
        var pipeStats = this;

        var pipesEndPoint = CONSTANTS.BASE_URL + CONSTANTS.STATS + CONSTANTS.PIPELINES + '/';
        var stagesEndPoint = CONSTANTS.BASE_URL + CONSTANTS.STATS + CONSTANTS.STAGES + '/';
        var materialsEndPoint = CONSTANTS.BASE_URL + CONSTANTS.STATS + '/material-change/';
        // var agentsEndPoint = CONSTANTS.BASE_URL + CONSTANTS.STATS + CONSTANTS.AGENTS + '/';
        // var agentReport = CONSTANTS.BASE_URL + CONSTANTS.STATS + '/report';

        pipeStats.allPipelines = {};

        //region /pipelines
        pipeStats.getAllPipelines = function (token) {
            var defer = $q.defer();

            $http.get(pipesEndPoint, {
                    headers: {
                        'Authorization': 'bearer ' + token
                    }
                })
                .success(function (res) {
                    pipeStats.allPipelines = res;
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeStats.getAllPipelinesByState = function (state) {
            var defer = $q.defer();

            $http.get(pipesEndPoint + state + '/state')
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeStats.getPipelineByNameAndRun = function (pipeName, pipeRunId) {
            var defer = $q.defer();

            $http.get(pipesEndPoint + pipeName + '/' + pipeRunId + '/instance')
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeStats.getAllRunsByName = function (pipeName, token) {
            var defer = $q.defer();
            var allRunsByNameEndPoint = pipesEndPoint + pipeName;

            $http.get(allRunsByNameEndPoint, {
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
        pipeStats.getAllRunsByNameAndState = function (pipeName, state) {
            var defer = $q.defer();

            $http.get(pipesEndPoint + pipeName + '/' + state)
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeStats.getLastRunByName = function (pipeName) {
            var defer = $q.defer();

            $http.get(pipesEndPoint + pipeName + '/last-execution')
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeStats.getLastSuccessRunByName = function (pipeName) {
            var defer = $q.defer();

            $http.get(pipesEndPoint + pipeName + '/last-successful-execution')
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        //returns a double
        pipeStats.getPassRateByName = function (pipeName) {
            var defer = $q.defer();

            $http.get(pipesEndPoint + pipeName + '/pass-rate')
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeStats.getAllStages = function (pipeName, pipeRunId, token) {
            var defer = $q.defer();

            $http.get(stagesEndPoint + pipeName + '/' + pipeRunId, {
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
        pipeStats.getStageByName = function (pipeName, pipeRunId, stageName) {
            var defer = $q.defer();

            $http.get(pipesEndPoint + pipeName + '/' + pipeRunId + '/' + stageName)
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeStats.getStageRunById = function (pipeName, pipeRunId, stageName, stageRunId) {
            var defer = $q.defer();

            $http.get(pipesEndPoint + pipeName + '/' + pipeRunId + '/' + stageName + '/' + stageRunId)
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeStats.getJobByName = function (pipeName, pipeRunId, stageName, stageRunId, jobName) {
            var defer = $q.defer();

            $http.get(pipesEndPoint + pipeName + '/' + pipeRunId + '/' + stageName + '/' + stageRunId + '/' + jobName)
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        //endregion

        //region /materials
        pipeStats.getAllLatestMaterialChanges = function () {
            var defer = $q.defer();

            $http.get(materialsEndPoint)
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeStats.getAllMaterialChangesForPipeline = function (pipeName) {
            var defer = $q.defer();

            $http.get(materialsEndPoint + pipeName)
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        pipeStats.getMaterialChangeByName = function (pipeName, materialName) {
            var defer = $q.defer();

            $http.get(materialsEndPoint + pipeName + '/' + materialName)
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
        pipeStats.getAllAgents = function (token) {
            var defer = $q.defer();
        
            $http.get(agentsEndPoint, {
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
        pipeStats.getAgentById = function (agentId) {
            var defer = $q.defer();
        
            $http.get(agentsEndPoint + agentId)
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });
        
            return defer.promise;
        };
        pipeStats.getAgentStatusReport = function () {
            var defer = $q.defer();
        
            $http.post(agentReport)
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });
        
            return defer.promise;
        };
        //endregion

        return pipeStats;
  }]);

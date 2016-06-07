'use strict';

angular.module('hawk.agentsManagement')
    .factory('oldagentsService', ['$http', '$q', 'CONSTANTS', function ($http, $q, CONSTANTS) {
        var oldagentsService = this;

        var agentsStatsEndPoint = CONSTANTS.BASE_URL + CONSTANTS.STATS + CONSTANTS.AGENTS + '/';
        var agentReport = CONSTANTS.BASE_URL + CONSTANTS.STATS + '/report';
        var agentsExecEndPoint = CONSTANTS.BASE_URL + CONSTANTS.EXEC + CONSTANTS.AGENTS + '/';
        var agentsConfigEndPoint = CONSTANTS.BASE_URL + CONSTANTS.CONFIG + CONSTANTS.AGENTS + '/';

        oldagentsService.checkAgentsForChange = function (serverResponse, currentAgents) {
            var isChanged = false;

            for (var i = 0; i < currentAgents.length; i++) {
                if ((currentAgents[i].ID != serverResponse[i].ID) ||
                    (currentAgents[i].ConfigState != serverResponse[i].ConfigState) ||
                    (currentAgents[i].Resources.length != serverResponse[i].Resources.length ||
                        currentAgents[i].ExecutionState != serverResponse[i].ExecutionState) ||
                    (currentAgents[i].IsConnected != serverResponse[i].IsConnected)) {
                    isChanged = true;
                    break;
                }

                //Number of Resources is the same, but check if value is different
                if (currentAgents[i].Resources.length == serverResponse[i].Resources.length) {
                    for (var q = 0; q < currentAgents[i].Resources.length; q++) {
                        if (currentAgents[i].Resources[q] != serverResponse[i].Resources[q]) {
                            isChanged = true;
                            break;
                        }
                    }
                }
            }
            return isChanged;
        };

        //region Agent Stats
        oldagentsService.getAllAgents = function (token) {
            var defer = $q.defer();

            $http.get(agentsStatsEndPoint, {
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
        oldagentsService.getAgentById = function (agentId) {
            var defer = $q.defer();

            $http.get(agentsStatsEndPoint + agentId)
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        oldagentsService.getAgentStatusReport = function () {
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

        //region Agent Exec
        oldagentsService.enableAgent = function (agentId, token) {
            var defer = $q.defer();
            var apiEndPoint = agentsExecEndPoint + agentId + '/enable';

            $http.post(apiEndPoint, {}, {
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

        oldagentsService.disableAgent = function (agentId, token) {
            var defer = $q.defer();
            var apiEndPoint = agentsExecEndPoint + agentId + '/disable';
            $http.post(apiEndPoint, {}, {
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

        oldagentsService.deleteAgent = function (agentId, token) {
            var defer = $q.defer();
            var apiEndPoint = agentsExecEndPoint + agentId;
            $http.delete(apiEndPoint, {
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

        //region Agent Config
        oldagentsService.getAllAgents = function (token) {
            var defer = $q.defer();

            $http.get(agentsConfigEndPoint, {
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
        oldagentsService.createAgent = function (agent, token) {
            var defer = $q.defer();

            $http.post(agentsConfigEndPoint, agent, {
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
        oldagentsService.deleteAgent = function (agentId, token) {
            var defer = $q.defer();
            var deleteAgentEndPoint = agentsConfigEndPoint + agentId;

            $http.delete(deleteAgentEndPoint, {
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
        oldagentsService.getAgent = function (agentId, token) {
            var defer = $q.defer();

            $http.get(agentsConfigEndPoint + agentId, {
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
        oldagentsService.updateAgent = function (agentId, agent, token) {
            var defer = $q.defer();
            var updateAgentEndPoint = agentsConfigEndPoint + agentId;
            $http.put(updateAgentEndPoint, agent, {
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


        return oldagentsService;
    }]);

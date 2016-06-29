'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('pipeConfig', ['$http', '$q', 'CONSTANTS', function ($http, $q, CONSTANTS) {
        var pipeConfig = this;

        var pipesEndPoint = CONSTANTS.WS_URL + CONSTANTS.CONFIG + CONSTANTS.PIPELINES + '/';
        var groupsEndPoint = CONSTANTS.BASE_URL + CONSTANTS.CONFIG + CONSTANTS.PIPELINE_GROUPS + '/';
        var stagesEndPoint = CONSTANTS.BASE_URL + CONSTANTS.CONFIG + CONSTANTS.STAGES + '/';
        var jobsEndPoint = CONSTANTS.BASE_URL + CONSTANTS.CONFIG + CONSTANTS.JOBS + '/';
        var tasksEndPoint = CONSTANTS.BASE_URL + CONSTANTS.CONFIG + CONSTANTS.TASKS + '/';
        var artifactsEndPoint = CONSTANTS.BASE_URL + CONSTANTS.CONFIG + CONSTANTS.ARTIFACTS + '/';
        var materialsEndPoint = CONSTANTS.BASE_URL + CONSTANTS.CONFIG + CONSTANTS.MATERIALS + '/';
        // var agentsEndPoint = CONSTANTS.BASE_URL + CONSTANTS.CONFIG + CONSTANTS.AGENTS + '/';
        var environmentsEndPoint = CONSTANTS.BASE_URL + CONSTANTS.CONFIG + CONSTANTS.ENVIRONMENTS + '/';
        var environmentVariablesEndPoint = CONSTANTS.BASE_URL + CONSTANTS.CONFIG + CONSTANTS.ENVIRONMENTVARS + '/';
        var devEndPoint = CONSTANTS.BASE_URL + CONSTANTS.CONFIG + CONSTANTS.DEV + '/';

        //region /pipelines
        pipeConfig.getAllPipelineDefs = function (token) {
            var defer = $q.defer();

            var classname = "hawkengine.net.services.AgentService";
            var controller = "Config";
            var methodname = "GetAllAgents";
            var args = token;
            var res = "";



            // $http.get(pipesEndPoint, {
            //         headers: {
            //             'Authorization': 'bearer ' + token
            //         }
            //     })
            //     .success(function (res) {
            //         defer.resolve(res);
            //     })
            //     .error(function (err, status) {
            //         defer.reject(err);
            //     });

            return defer.promise;
        };
        pipeConfig.createPipeline = function (pipeline, token) {
            var defer = $q.defer();

            $http.post(pipesEndPoint, pipeline, {
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
        pipeConfig.deletePipeline = function (pipeName, token) {
            var defer = $q.defer();
            var deletePipelineEndPoint = pipesEndPoint + pipeName;
            $http.delete(deletePipelineEndPoint, {
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
        pipeConfig.getPipelineDef = function (pipeName, token) {
            var defer = $q.defer();

            $http.get(pipesEndPoint + pipeName, {
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
        pipeConfig.updatePipeline = function (pipeName, pipeline, token) {
            var defer = $q.defer();

            $http.put(pipesEndPoint + pipeName, pipeline, {
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

        //region /pipelines_groups
        pipeConfig.getAllGroups = function (token) {
            var defer = $q.defer();

            $http.get(groupsEndPoint, {
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
        pipeConfig.createGroup = function (pipeGroup, token) {
            var defer = $q.defer();

            $http.post(groupsEndPoint, pipeGroup, {
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
        pipeConfig.deleteGroup = function (pipeGroupName, token) {
            var defer = $q.defer();

            $http.delete(groupsEndPoint + pipeGroupName, {
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
        pipeConfig.getGroup = function (pipeGroupName, token) {
            var defer = $q.defer();

            $http.get(groupsEndPoint + pipeGroupName, {
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
        pipeConfig.updateGroup = function (pipeGroupName, pipeGroup, token) {
            var defer = $q.defer();

            $http.put(groupsEndPoint + pipeGroupName, pipeGroup, {
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

        //region /stages
        pipeConfig.getAllStages = function (pipeName, token) {
            var defer = $q.defer();

            $http.get(stagesEndPoint + pipeName, {
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
        pipeConfig.createStage = function (pipeName, stage, token) {
            var defer = $q.defer();

            $http.post(stagesEndPoint + pipeName, stage, {
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
        pipeConfig.deleteStage = function (pipeName, stageName, token) {
            var defer = $q.defer();

            $http.delete(stagesEndPoint + pipeName + '/' + stageName, {
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
        // pipeConfig.getStage = function (stage) {
        //     pipeConfigService.getStageDefinitionById(stage.id);
        // };
        pipeConfig.updateStage = function (pipeName, stageName, stage, token) {
            var defer = $q.defer();

            $http.put(stagesEndPoint + pipeName + '/' + stageName, stage, {
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

        //region /jobs
        pipeConfig.getAllJobs = function (pipeName, stageName, token) {
            var defer = $q.defer();

            $http.get(jobsEndPoint + pipeName + '/' + stageName, {
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
        pipeConfig.createJob = function (pipeName, stageName, job, token) {
            var defer = $q.defer();

            $http.post(jobsEndPoint + pipeName + '/' + stageName, job, {
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
        pipeConfig.deleteJob = function (pipeName, stageName, jobName, token) {
            var defer = $q.defer();

            $http.delete(jobsEndPoint + pipeName + '/' + stageName + '/' + jobName, {
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
        // pipeConfig.getJob = function (pipeName, stageName, jobName, token) {
        //     var defer = $q.defer();
        //
        //     $http.get(jobsEndPoint + pipeName + '/' + stageName + '/' + jobName, {
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
        pipeConfig.updateJob = function (pipeName, stageName, jobName, job, token) {
            var defer = $q.defer();

            $http.put(jobsEndPoint + pipeName + '/' + stageName + '/' + jobName, job, {
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

        //region /tasks
        pipeConfig.getAllTasks = function (pipeName, stageName, jobName, token) {
            var defer = $q.defer();

            $http.get(tasksEndPoint + pipeName + '/' + stageName + '/' + jobName, {
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
        pipeConfig.deleteTask = function (pipeName, stageName, jobName, taskIndex, token) {
            var defer = $q.defer();

            $http.delete(tasksEndPoint + pipeName + '/' + stageName + '/' + jobName + '/' + taskIndex, {
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
        pipeConfig.getTask = function (pipeName, stageName, jobName, taskIndex, token) {
            var defer = $q.defer();

            $http.get(tasksEndPoint + pipeName + '/' + stageName + '/' + jobName + '/' + taskIndex, {
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

        pipeConfig.createExecTask = function (pipeName, stageName, jobName, task, token) {
            var defer = $q.defer();

            $http.post(tasksEndPoint + pipeName + '/' + stageName + '/' + jobName + '/exec', task, {
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
        pipeConfig.createFetchArtifactTask = function (pipeName, stageName, jobName, task, token) {
            var defer = $q.defer();

            $http.post(tasksEndPoint + pipeName + '/' + stageName + '/' + jobName + '/fetchArtifact', task, {
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
        pipeConfig.createFetchMaterialTask = function (pipeName, stageName, jobName, task, token) {
            var defer = $q.defer();

            $http.post(tasksEndPoint + pipeName + '/' + stageName + '/' + jobName + '/fetchMaterial', task, {
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
        pipeConfig.createUploadArtifactTask = function (pipeName, stageName, jobName, task, token) {
            var defer = $q.defer();

            $http.post(tasksEndPoint + pipeName + '/' + stageName + '/' + jobName + '/uploadArtifact', task, {
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

        pipeConfig.updateExecTask = function (pipeName, stageName, jobName, taskIndex, task, token) {
            var defer = $q.defer();

            $http.put(tasksEndPoint + pipeName + '/' + stageName + '/' + jobName + '/' + taskIndex + '/exec', task, {
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
        pipeConfig.updateFetchArtifactTask = function (pipeName, stageName, jobName, taskIndex, task, token) {
            var defer = $q.defer();

            $http.put(tasksEndPoint + pipeName + '/' + stageName + '/' + jobName + '/' + taskIndex + '/fetchArtifact', task, {
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
        pipeConfig.updateFetchMaterialTask = function (pipeName, stageName, jobName, taskIndex, task, token) {
            var defer = $q.defer();

            $http.put(tasksEndPoint + pipeName + '/' + stageName + '/' + jobName + '/' + taskIndex + '/fetchMaterial', task, {
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
        pipeConfig.updateUploadArtifactTask = function (pipeName, stageName, jobName, taskIndex, task, token) {
            var defer = $q.defer();

            $http.put(tasksEndPoint + pipeName + '/' + stageName + '/' + jobName + '/' + taskIndex + '/uploadArtifact', task, {
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

        //region /artifacts
        pipeConfig.getAllArtifacts = function (pipeName, stageName, jobName, token) {
            var defer = $q.defer();

            $http.get(artifactsEndPoint + pipeName + '/' + stageName + '/' + jobName, {
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
        pipeConfig.createArtifact = function (pipeName, stageName, jobName, artifactIndex, token) {
            var defer = $q.defer();

            $http.post(artifactsEndPoint + pipeName + '/' + stageName + '/' + jobName + '/' + artifactIndex, {
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
        pipeConfig.deleteArtifact = function (pipeName, stageName, jobName, artifactIndex, token) {
            var defer = $q.defer();

            $http.delete(artifactsEndPoint + pipeName + '/' + stageName + '/' + jobName + '/' + artifactIndex, {
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
        pipeConfig.getArtifact = function (pipeName, stageName, jobName, artifactIndex, token) {
            var defer = $q.defer();

            $http.get(artifactsEndPoint + pipeName + '/' + stageName + '/' + jobName + '/' + artifactIndex, {
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
        pipeConfig.updateArtifact = function (pipeName, stageName, jobName, artifactIndex, artifact, token) {
            var defer = $q.defer();

            $http.put(artifactsEndPoint + pipeName + '/' + stageName + '/' + jobName + '/' + artifactIndex, artifact, {
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

        //region /materials
        pipeConfig.getAllMaterials = function (pipeName, token) {
            var defer = $q.defer();

            $http.get(materialsEndPoint + pipeName, {
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
        pipeConfig.getMaterial = function (pipeName, materialName, token) {
            var defer = $q.defer();

            $http.get(materialsEndPoint + pipeName + '/' + materialName, {
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
        pipeConfig.createMaterial = function (pipeName, material, token) {
            var defer = $q.defer();

            $http.post(materialsEndPoint + pipeName, material, {
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
        pipeConfig.deleteMaterial = function (pipeName, materialName, token) {
            var defer = $q.defer();

            $http.delete(materialsEndPoint + pipeName + '/' + materialName, {
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
        pipeConfig.updateMaterial = function (pipeName, materialName, material, token) {
            var defer = $q.defer();

            $http.put(materialsEndPoint + pipeName + '/' + materialName, material, {
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
        // pipeConfig.getAllAgents = function (token) {
        //     var defer = $q.defer();
        //
        //     $http.get(agentsEndPoint, {
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
        // pipeConfig.createAgent = function (agent, token) {
        //     var defer = $q.defer();
        //
        //     $http.post(agentsEndPoint, agent, {
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
        // pipeConfig.deleteAgent = function (agentId, token) {
        //     var defer = $q.defer();
        //     var deleteAgentEndPoint = agentsEndPoint + agentId;
        //
        //     $http.delete(deleteAgentEndPoint, {
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
        // pipeConfig.getAgent = function (agentId, token) {
        //     var defer = $q.defer();
        //
        //     $http.get(agentsEndPoint + agentId, {
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
        // pipeConfig.updateAgent = function (agentId, agent, token) {
        //     var defer = $q.defer();
        //     var updateAgentEndPoint = agentsEndPoint + agentId;
        //     $http.put(updateAgentEndPoint, agent, {
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

        //region /environments
        pipeConfig.getAllEnvironments = function (token) {
            var defer = $q.defer();

            $http.get(environmentsEndPoint, {
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
        pipeConfig.createEnvironment = function (environment, token) {
            var defer = $q.defer();

            $http.post(environmentsEndPoint, environment, {
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
        pipeConfig.deleteEnvironment = function (environmentName, token) {
            var defer = $q.defer();

            $http.delete(environmentsEndPoint + '/' + environmentName, {
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
        pipeConfig.getEnvironment = function (environmentName, token) {
            var defer = $q.defer();

            $http.get(environmentsEndPoint + '/' + environmentName, {
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
        pipeConfig.updateEnvironment = function (environmentName, environment, token) {
            var defer = $q.defer();

            $http.put(environmentsEndPoint + '/' + environmentName, environment, {
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

        pipeConfig.getAllPipelineVars = function (pipelineName, token) {
            var defer = $q.defer();

            $http.get(environmentVariablesEndPoint + pipelineName + '/list', {
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
        pipeConfig.getAllStageVars = function (pipelineName, stageName, token) {
            var defer = $q.defer();

            $http.get(environmentVariablesEndPoint + pipelineName + '/' + stageName + '/list', {
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
        pipeConfig.getAllJobVars = function (pipelineName, stageName, jobName, token) {
            var defer = $q.defer();

            $http.get(environmentVariablesEndPoint + pipelineName + '/' + stageName + '/' + jobName + '/list', {
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

        pipeConfig.getPipelineVar = function (pipelineName, variable, token) {
            var defer = $q.defer();

            $http.get(environmentVariablesEndPoint + pipelineName + '/' + variable, {
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
        pipeConfig.getStageVar = function (pipelineName, stageName, variable, token) {
            var defer = $q.defer();

            $http.get(environmentVariablesEndPoint + pipelineName + '/' + stageName + '/' + variable, {
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
        pipeConfig.getJobVar = function (pipelineName, stageName, jobName, variable, token) {
            var defer = $q.defer();

            $http.get(environmentVariablesEndPoint + pipelineName + '/' + stageName + '/' + jobName + '/' + variable, {
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

        pipeConfig.createPipelineVar = function (pipelineName, variable, token) {
            var defer = $q.defer();

            $http.post(environmentVariablesEndPoint + pipelineName, variable, {
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
        pipeConfig.createStageVar = function (pipelineName, stageName, variable, token) {
            var defer = $q.defer();

            $http.post(environmentVariablesEndPoint + pipelineName + '/' + stageName, variable, {
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
        pipeConfig.createJobVar = function (pipelineName, stageName, jobName, variable, token) {
            var defer = $q.defer();

            $http.post(environmentVariablesEndPoint + pipelineName + '/' + stageName + '/' + jobName, variable, {
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

        pipeConfig.deletePipelineVar = function (pipelineName, variableName, token) {
            var defer = $q.defer();

            $http.delete(environmentVariablesEndPoint + pipelineName + '/' + variableName, {
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
        pipeConfig.deleteStageVar = function (pipelineName, stageName, variableName, token) {
            var defer = $q.defer();

            $http.delete(environmentVariablesEndPoint + pipelineName + '/' + stageName + '/' + variableName, {
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
        pipeConfig.deleteJobVar = function (pipelineName, stageName, jobName, variableName, token) {
            var defer = $q.defer();

            $http.delete(environmentVariablesEndPoint + pipelineName + '/' + stageName + '/' + jobName + '/' + variableName, {
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

        pipeConfig.updatePipelineVar = function (pipelineName, variableName, variable, token) {
            var defer = $q.defer();

            $http.put(environmentVariablesEndPoint + pipelineName + '/' + variableName, variable, {
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
        pipeConfig.updateStageVar = function (pipelineName, stageName, variableName, variable, token) {
            var defer = $q.defer();

            $http.put(environmentVariablesEndPoint + pipelineName + '/' + stageName + '/' + variableName, variable, {
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
        pipeConfig.updateJobVar = function (pipelineName, stageName, jobName, variableName, variable, token) {
            var defer = $q.defer();

            $http.put(environmentVariablesEndPoint + pipelineName + '/' + stageName + '/' + jobName + '/' + variableName, variable, {
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
        pipeConfig.getLatestCommit = function (token) {
            var defer = $q.defer();

            $http.get(devEndPoint + 'get_latest_commit', {
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

        return pipeConfig;
    }]);

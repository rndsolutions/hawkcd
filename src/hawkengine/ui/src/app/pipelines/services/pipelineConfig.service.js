'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('pipeConfigService', [function () {
        var pipeConfigService = this;
        
        //region Senders
        
        //region /pipeline
        
        pipeConfigService.getAllPipelineDefs = function (token) {

        };
        pipeConfigService.createPipeline = function (pipeline, token) {

        };
        pipeConfigService.deletePipeline = function (pipeName, token) {

        };
        pipeConfigService.getPipelineDef = function (pipeName, token) {

        };
        pipeConfigService.updatePipeline = function (pipeName, pipeline, token) {

        };
        //endregion

        //region /pipelines_groups
        pipeConfigService.getAllGroups = function (token) {

        };
        pipeConfigService.createGroup = function (pipeGroup, token) {

        };
        pipeConfigService.deleteGroup = function (pipeGroupName, token) {

        };
        pipeConfigService.getGroup = function (pipeGroupName, token) {

        };
        pipeConfigService.updateGroup = function (pipeGroupName, pipeGroup, token) {

        };
        //endregion

        //region /stages
        pipeConfigService.getAllStages = function (pipeName, token) {

        };
        pipeConfigService.createStage = function (pipeName, stage, token) {
            if(token == isValid){
                if(error == "") {
                    var methodName = "AddStage";
                    var className = "PipelineDefinitionService";
                    var packageName = "hawkengine.net.services.PipelineDefinitionService";
                    var result = "";
                    var args = ["Pipelinebro", "Stagebro"];
                    var error = "";
                    var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
                    websocketSenderService.call(json);
                }
            }
        };
        pipeConfigService.deleteStage = function (pipeName, stageName, token) {

        };
        pipeConfigService.getStage = function (pipeName, stageName, token) {

        };
        pipeConfigService.updateStage = function (pipeName, stageName, stage, token) {

        };
        //endregion

        //region /jobs
        pipeConfigService.getAllJobs = function (pipeName, stageName, token) {

        };
        pipeConfigService.createJob = function (pipeName, stageName, job, token) {

        };
        pipeConfigService.deleteJob = function (pipeName, stageName, jobName, token) {

        };
        pipeConfigService.getJob = function (pipeName, stageName, jobName, token) {

        };
        pipeConfigService.updateJob = function (pipeName, stageName, jobName, job, token) {

        };
        //endregion

        //region /tasks
        pipeConfigService.getAllTasks = function (pipeName, stageName, jobName, token) {

        };
        pipeConfigService.deleteTask = function (pipeName, stageName, jobName, taskIndex, token) {

        };
        pipeConfigService.getTask = function (pipeName, stageName, jobName, taskIndex, token) {

        };

        pipeConfigService.createExecTask = function (pipeName, stageName, jobName, task, token) {

        };
        pipeConfigService.createFetchArtifactTask = function (pipeName, stageName, jobName, task, token) {

        };
        pipeConfigService.createFetchMaterialTask = function (pipeName, stageName, jobName, task, token) {

        };
        pipeConfigService.createUploadArtifactTask = function (pipeName, stageName, jobName, task, token) {

        };

        pipeConfigService.updateExecTask = function (pipeName, stageName, jobName, taskIndex, task, token) {

        };
        pipeConfigService.updateFetchArtifactTask = function (pipeName, stageName, jobName, taskIndex, task, token) {

        };
        pipeConfigService.updateFetchMaterialTask = function (pipeName, stageName, jobName, taskIndex, task, token) {

        };
        pipeConfigService.updateUploadArtifactTask = function (pipeName, stageName, jobName, taskIndex, task, token) {

        };
        //endregion

        //region /artifacts
        pipeConfigService.getAllArtifacts = function (pipeName, stageName, jobName, token) {

        };
        pipeConfigService.createArtifact = function (pipeName, stageName, jobName, artifactIndex, token) {

        };
        pipeConfigService.deleteArtifact = function (pipeName, stageName, jobName, artifactIndex, token) {

        };
        pipeConfigService.getArtifact = function (pipeName, stageName, jobName, artifactIndex, token) {

        };
        pipeConfigService.updateArtifact = function (pipeName, stageName, jobName, artifactIndex, artifact, token) {

        };
        //endregion

        //region /materials
        pipeConfigService.getAllMaterials = function (pipeName, token) {

        };
        pipeConfigService.getMaterial = function (pipeName, materialName, token) {

        };
        pipeConfigService.createMaterial = function (pipeName, material, token) {

        };
        pipeConfigService.deleteMaterial = function (pipeName, materialName, token) {

        };
        pipeConfigService.updateMaterial = function (pipeName, materialName, material, token) {

        };

        //endregion

        //region /environments
        pipeConfigService.getAllEnvironments = function (token) {

        };
        pipeConfigService.createEnvironment = function (environment, token) {

        };
        pipeConfigService.deleteEnvironment = function (environmentName, token) {

        };
        pipeConfigService.getEnvironment = function (environmentName, token) {

        };
        pipeConfigService.updateEnvironment = function (environmentName, environment, token) {

        };
        //endregion

        pipeConfigService.getAllPipelineVars = function (pipelineName, token) {

        };
        pipeConfigService.getAllStageVars = function (pipelineName, stageName, token) {

        };
        pipeConfigService.getAllJobVars = function (pipelineName, stageName, jobName, token) {

        };

        pipeConfigService.getPipelineVar = function (pipelineName, variable, token) {

        };
        pipeConfigService.getStageVar = function (pipelineName, stageName, variable, token) {

        };
        pipeConfigService.getJobVar = function (pipelineName, stageName, jobName, variable, token) {

        };

        pipeConfigService.createPipelineVar = function (pipelineName, variable, token) {

        };
        pipeConfigService.createStageVar = function (pipelineName, stageName, variable, token) {

        };
        pipeConfigService.createJobVar = function (pipelineName, stageName, jobName, variable, token) {

        };

        pipeConfigService.deletePipelineVar = function (pipelineName, variableName, token) {

        };
        pipeConfigService.deleteStageVar = function (pipelineName, stageName, variableName, token) {

        };
        pipeConfigService.deleteJobVar = function (pipelineName, stageName, jobName, variableName, token) {

        };

        pipeConfigService.updatePipelineVar = function (pipelineName, variableName, variable, token) {

        };
        pipeConfigService.updateStageVar = function (pipelineName, stageName, variableName, variable, token) {

        };
        pipeConfigService.updateJobVar = function (pipelineName, stageName, jobName, variableName, variable, token) {

        };
        pipeConfigService.getLatestCommit = function (token) {

        };
        
        //endregion

        return pipeConfigService;
    }]);
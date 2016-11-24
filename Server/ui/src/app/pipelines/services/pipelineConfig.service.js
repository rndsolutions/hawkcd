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
    .factory('pipeConfigService', ['jsonHandlerService', 'websocketSenderService', function (jsonHandlerService, websocketSenderService) {
        var pipeConfigService = this;

        //region Senders

        //region /pipeline

        pipeConfigService.getAllPipelineDefinitions = function () {
            var methodName = "getAll";
            var className = "PipelineDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.getPipelineDefinitionById = function (id) {
            var methodName = "getById";
            var className = "PipelineDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.getAllPipelineGroupDTOs = function () {
            var methodName = "getAllPipelineGroupDTOs";
            var className = "PipelineGroupService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.addPipelineDefinition = function (pipelineDefinition) {
            var methodName = "add";
            var className = "PipelineDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.PipelineDefinition\", \"object\": " + JSON.stringify(pipelineDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.addPipelineDefinitionWithMaterial = function (pipelineDefinition,materialDefinition) {
            var methodName = "addWithMaterialDefinition";
            var className = "PipelineDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.PipelineDefinition\", \"object\": " + JSON.stringify(pipelineDefinition) + "}," + "{\"packageName\": \"io.hawkcd.model.MaterialDefinition\", \"object\": " + JSON.stringify(materialDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.addPipelineDefinitionWithExistingMaterial = function (pipelineDefinition,materialDefinition) {
            var methodName = "addWithMaterialDefinition";
            var className = "PipelineDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.PipelineDefinition\", \"object\": " + JSON.stringify(pipelineDefinition) + "}," + "{\"packageName\": \"java.lang.String\", \"object\": \"" + materialDefinition + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.updatePipelineDefinition = function (pipelineDefinition) {
            var methodName = "update";
            var className = "PipelineDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.PipelineDefinition\", \"object\": " + JSON.stringify(pipelineDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        //TODO: Send Pipeline Definition and Pipeline Group to assign to (and possibly Pipeline Group that is assigned from)
        pipeConfigService.assignPipelineDefinition = function (pipelineDefinitionId, pipelineGroupId, pipelineGroupName) {
            var methodName = "assignPipelineToGroup";
            var className = "PipelineDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + pipelineDefinitionId + "\"}, " +
            "{\"packageName\": \"java.lang.String\", \"object\": \"" + pipelineGroupId + "\"}, " +
            "{\"packageName\": \"java.lang.String\", \"object\": \"" + pipelineGroupName + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        //TODO: Send Pipeline Definition to be unassigned
        pipeConfigService.unassignPipelineDefinition = function (pipelineDefinitionId) {
            var methodName = "unassignPipelineFromGroup";
            var className = "PipelineDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + pipelineDefinitionId + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        //TODO: Send Pipeline Definition to be deleted
        pipeConfigService.deletePipelineDefinition = function (pipelineDefinition) {
            var methodName = "delete";
            var className = "PipelineDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.PipelineDefinition\", \"object\": " + JSON.stringify(pipelineDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
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
        pipeConfigService.getAllStageDefinitions = function () {
            var methodName = "getAll";
            var className = "StageDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        pipeConfigService.getStageDefinitionById = function (id) {
            var methodName = "getById";
            var className = "StageDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        pipeConfigService.addStageDefinition = function (stageDefinition) {
            var methodName = "add";
            var className = "StageDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.StageDefinition\", \"object\": " + JSON.stringify(stageDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        pipeConfigService.updateStageDefinition = function (stageDefinition) {
            var methodName = "update";
            var className = "StageDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.StageDefinition\", \"object\": " + JSON.stringify(stageDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        //TODO: Send Stage Definition to be deleted
        pipeConfigService.deleteStageDefinition = function (stageDefinition) {
            var methodName = "delete";
            var className = "StageDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.StageDefinition\", \"object\": " + JSON.stringify(stageDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        //endregion

        //region /jobs
        pipeConfigService.getAllJobDefinitions = function () {
            var methodName = "getAll";
            var className = "JobDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        pipeConfigService.getJobDefinitionById = function (id) {
            var methodName = "getById";
            var className = "JobDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        pipeConfigService.addJobDefinition = function (jobDefinition) {
            var methodName = "add";
            var className = "JobDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.JobDefinition\", \"object\": " + JSON.stringify(jobDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        pipeConfigService.updateJobDefinition = function (jobDefinition) {
            var methodName = "update";
            var className = "JobDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.JobDefinition\", \"object\": " + JSON.stringify(jobDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        //TODO: Send Job Definition to be deleted
        pipeConfigService.deleteJobDefinition = function (jobDefinition) {
            var methodName = "delete";
            var className = "JobDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.JobDefinition\", \"object\": " + JSON.stringify(jobDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        //endregion

        //region /tasks

        pipeConfigService.addTaskDefinition = function (taskDefinition){
            var methodName = "add";
            var className = "TaskDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.TaskDefinition\", \"object\": " + JSON.stringify(taskDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.updateTaskDefinition = function (taskDefinition) {
            var methodName = "update";
            var className = "TaskDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.TaskDefinition\", \"object\": " + JSON.stringify(taskDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        //TODO: Send Task Definition to be deleted
        pipeConfigService.deleteTaskDefinition = function (taskDefinition) {
            var methodName = "delete";
            var className = "TaskDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.TaskDefinition\", \"object\": " + JSON.stringify(taskDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.getAllMaterialDefinitions = function () {
            var methodName = "getAll";
            var className = "MaterialDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.getMaterialDefinitionById = function (id) {
            var methodName = "getById";
            var className = "MaterialDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.addGitMaterialDefinition = function (materialDefinition) {
            var methodName = "add";
            var className = "MaterialDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.GitMaterial\", \"object\": " + JSON.stringify(materialDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.addNugetMaterialDefinition = function (materialDefinition) {
            var methodName = "add";
            var className = "MaterialDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.NugetMaterial\", \"object\": " + JSON.stringify(materialDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.updateGitMaterialDefinition = function (materialDefinition) {
            var methodName = "update";
            var className = "MaterialDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.GitMaterial\", \"object\": " + JSON.stringify(materialDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.updateNugetMaterialDefinition = function (materialDefinition) {
            var methodName = "update";
            var className = "MaterialDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.NugetMaterial\", \"object\": " + JSON.stringify(materialDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        pipeConfigService.deleteMaterialDefinition = function (materialDefinition) {
            var methodName = "delete";
            var className = "MaterialDefinitionService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.MaterialDefinition\", \"object\": " + JSON.stringify(materialDefinition) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

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

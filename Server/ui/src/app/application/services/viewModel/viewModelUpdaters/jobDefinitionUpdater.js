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
    .module('hawk')
    .factory('jobDefinitionUpdater', ['viewModel', function (viewModel) {
        var jobDefinitionUpdater = this;

        jobDefinitionUpdater.getAllJobDefinitions = function (jobDefinitions) {
            viewModel.allJobs = jobDefinitions;
        };

        jobDefinitionUpdater.addJobDefinition = function (jobDefinition) {
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if (currentPipeline.id == jobDefinition.pipelineDefinitionId) {
                    viewModel.allPipelines[index].stageDefinitions.forEach(function (currentStage, stageIndex, stageArray) {
                        if(currentStage.id == jobDefinition.stageDefinitionId){
                            viewModel.allPipelines[index].stageDefinitions[stageIndex].jobDefinitions.push(jobDefinition);
                        }
                    });
                }
            });

        };

        jobDefinitionUpdater.updateJobDefinition = function (jobDefinition) {
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if(currentPipeline.id == jobDefinition.pipelineDefinitionId) {
                    viewModel.allPipelines[index].stageDefinitions.forEach(function (currentStage, stageIndex, stageArray) {
                        if(currentStage.id == jobDefinition.stageDefinitionId) {
                            viewModel.allPipelines[index].stageDefinitions[stageIndex].jobDefinitions.forEach(function (currentJob, jobIndex, jobArray) {
                                if(currentJob.id == jobDefinition.id) {
                                    viewModel.allPipelines[index].stageDefinitions[stageIndex].jobDefinitions[jobIndex] = jobDefinition;
                                }
                            });
                        }
                    });
                }
            });
        };

        return jobDefinitionUpdater;
    }]);

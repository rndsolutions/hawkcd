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
    .factory('pipelineDefinitionUpdater', ['viewModel', function (viewModel) {
        var pipelineDefinitionUpdater = this;

        pipelineDefinitionUpdater.getAllPipelineDefinitions = function (pipelineDefinitions){
            viewModel.allPipelines = pipelineDefinitions;
            var isFound = false;
            viewModel.allPipelines.forEach(function (currentPipeline, pipelineIndex, pipelineArray) {
                if(currentPipeline.pipelineGroupId == ''){
                    viewModel.unassignedPipelines.forEach(function (currentUnassignedPipeline, unassignedPipelineIndex, unassignedPipelineArray) {
                        if(currentPipeline.id == currentUnassignedPipeline.id) {
                            viewModel.unassignedPipelines[unassignedPipelineIndex] = currentPipeline;
                            isFound = true;
                        }
                    });
                    if(!isFound) {
                        viewModel.unassignedPipelines.push(currentPipeline);
                        isFound = false;
                    }
                    viewModel.assignedPipelines.forEach(function (currentAssignedPipeline, assignedPipelineIndex, assignedPipelineArray) {
                        if(currentPipeline.id == currentAssignedPipeline.id) {
                            viewModel.assignedPipelines.splice(assignedPipelineIndex);
                        }
                    });
                    isFound = false;
                } else {
                    viewModel.assignedPipelines.forEach(function (currentAssignedPipeline, assignedPipelineIndex, assignedPipelineArray) {
                        if(currentPipeline.id == currentAssignedPipeline.id) {
                            viewModel.assignedPipelines[assignedPipelineIndex] = currentPipeline;
                            isFound = true;
                        }
                    });
                    if(!isFound){
                        viewModel.assignedPipelines.push(currentPipeline);
                        isFound = false;
                    }
                    viewModel.unassignedPipelines.forEach(function (currentUnassignedPipeline, unAssignedPipelineIndex, unAssignedPipelineArray) {
                        if(currentPipeline.id == currentUnassignedPipeline.id) {
                            viewModel.unassignedPipelines.splice(unAssignedPipelineIndex);
                        }
                    });
                    isFound = false;
                }
            });
            isFound = false;
        };

        pipelineDefinitionUpdater.addPipelineDefinition = function (pipelineDefinition) {
            viewModel.allPipelineGroups.forEach(function (currentPipelineGroupDTO, index, array) {
                if(currentPipelineGroupDTO.id == pipelineDefinition.pipelineGroupId){
                    viewModel.assignedPipelines.push(pipelineDefinition);
                } else if (pipelineDefinition.pipelineGroupId == '') {
                    viewModel.unassignedPipelines.push(pipelineDefinition);
                }
            });

            viewModel.allPipelines.push(pipelineDefinition);
        };

        pipelineDefinitionUpdater.updatePipelineDefinition = function (pipelineDefinition) {
            viewModel.allPipelines.forEach(function (currentPipeline, index, array) {
                if(currentPipeline.id == pipelineDefinition.id){
                    viewModel.allPipelines[index] = pipelineDefinition;
                }
            });

            viewModel.allPipelineGroups.forEach(function (currentPipelineGroupDTO, index, array) {
                if(currentPipelineGroupDTO.id == pipelineDefinition.pipelineGroupId) {
                    viewModel.allPipelineGroups[index].pipelines.forEach(function (currentPipeline, pipelineIndex, array) {
                        if(currentPipeline.id == pipelineDefinition.id) {
                            viewModel.allPipelineGroups[index].pipelines[pipelineIndex] = pipelineDefinition;
                        }
                    });
                }

            });
        };

        return pipelineDefinitionUpdater;
    }]);

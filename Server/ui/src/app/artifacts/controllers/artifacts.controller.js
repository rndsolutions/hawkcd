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
    .module('hawk.artifactManagement')
    .controller('ArtifactController', function($rootScope, $scope, $log, $interval, viewModel, pipelineUpdater, moment, commonUtitlites, filterRuns, artifactService) {
        var vm = this;

        vm.allPipelines = [];

        vm.isDefinitionGroupOpen = [];

        vm.isRunSelected = [];

        vm.query = '';

        vm.popOverOptions = {
            popOverTitles: {
                artifact: 'Artifacts are files and folders that are stored on the Server, inside their respective Pipelines.'
            }
        };

        vm.scrollDisable = false;

        vm.isFirstLoad = true;

        artifactService.getAllArtifactPipelines('', 10, '');

        vm.truncateGitFromUrl = function(repoUrl, commitId) {
            return commonUtitlites.truncateGitFromUrl(repoUrl,commitId);
        };

        vm.goToGitLink = function(link) {
            location.href = link;
        };

        vm.search = function() {
            vm.isFirstLoad = false;
            // vm.filteredItems = filterRuns.search(vm.allPipelineRuns, vm.query);
            if(viewModel.artifactPipelines[0] && viewModel.artifactPipelines[0].searchCriteria){
                viewModel.artifactPipelines[0].searchCriteria = angular.copy(vm.query);
            }
            vm.searchCriteria = angular.copy(vm.query);
            artifactService.getAllArtifactPipelines(vm.query, 10, '');
        };

        // $scope.$watch(function() {
        //     return viewModel.artifactPipelines
        // }, function(newVal, oldVal) {
        //     vm.allPipelineRuns = angular.copy(viewModel.artifactPipelines);
        //
        //     vm.allPipelineRuns.forEach(function (currentPipelineRun, runIndex, runArray) {
        //         currentPipelineRun.materials.forEach(function(currentMaterial, index, array) {
        //             var definition = currentMaterial.materialDefinition;
        //             currentMaterial.gitLink = vm.truncateGitFromUrl(definition.repositoryUrl, definition.commitId);
        //         });
        //
        //         currentPipelineRun.stages.forEach(function (currentStage, stageIndex, stageArray) {
        //             if(currentStage.endTime) {
        //                 currentPipelineRun.lastStage = currentStage;
        //                 currentPipelineRun.lastStage.localEndDate = moment.formatDateUTCToLocal(currentStage.endTime);
        //                 currentPipelineRun.lastStage.localEndTime = moment.formatTimeInLocal(currentStage.endTime.time);
        //             }
        //         });
        //     });
        //
        //     vm.allPipelineRuns.sort(function(a, b) {
        //         return moment.utc(b.startTime.date).add(b.startTime.time.hour, 'hours').add(b.startTime.time.minute, 'minutes').add(b.startTime.time.second, 'seconds').diff(moment.utc(a.startTime.date).add(a.startTime.time.hour, 'hours').add(a.startTime.time.minute, 'minutes').add(a.startTime.time.second, 'seconds'));
        //     });
        //
        //     vm.search();
        //
        // }, true);

        vm.allPipelineRuns = function () {
            return viewModel.artifactPipelines;
        };

        vm.scrollCall = function() {
            if(vm.allPipelineRuns()[0]){
                if(vm.allPipelineRuns()[0].disabled == false){
                    artifactService.getAllArtifactPipelines(vm.query, 10, vm.allPipelineRuns()[vm.allPipelineRuns().length - 1].id);
                }
                vm.isFirstLoad = false;
                vm.allPipelineRuns()[0].disabled = true;
            }
        };

        vm.loadJsTree = function (index, event) {
            if(event.currentTarget.attributes['aria-expanded'].nodeValue == 'false'){
                if(vm.allPipelineRuns()[index].artifactsFileStructure[0]){
                    $('#jstree' + index).jstree(true).settings.core.data = angular.copy(vm.allPipelineRuns()[index].artifactsFileStructure[0].children);
                    $('#jstree' + index).jstree(true).refresh();
                }
            }
        };

        $scope.treeEventsObj = function (e, data) {
            var selectedNode = data.node.original;
            if(selectedNode.type != 'folder'){
                artifactService.getFile(selectedNode.path + '/' + selectedNode.text);
            }
        };

        $scope.treeConfig = {
            core : {
                error : function(error) {
                    $log.error('treeCtrl: error from js tree - ' + angular.toJson(error));
                },
                check_callback : true
            },
            types : {
                folder : {
                    icon : "fa fa-folder-o"
                },
                file : {
                    icon : "fa fa-file-text-o"
                }
            },
            plugins : ['types']
        };

        // $(document).ready(function(){
        //     $('#jstree').jstree();
        //     // $(document).on("select_node.jstree", function (e, data) {
        //     //     // vm.selectedData = angular.copy($scope.treeData);
        //     //     var node = data.node;
        //     //     debugger;
        //     // });
        //     // $(document).on("click", ".jstree-anchor", function (e) {
        //     //     var node = e;
        //     //     debugger;
        //     // });
        // });

        // $('#jstree').on("changed.jstree", function (e, data) {
        //     // vm.selectedData = angular.copy($scope.treeData);
        // });



        // $('#jstree').bind("select_node.jstree", function (e, data) {
        //     var object = data.instance.get_json()[0];
        //     debugger;
        // });

        // var selected_nodes = $scope.treeInstance.jstree(true).get_selected();

        vm.openAccordion = function(array, index) {
            if (array[index] != true && array[index] != false) {
                array[index] = true;
            } else {
                array[index] = !array[index];
            }
        };

        vm.selectRun = function(index){
            vm.isRunSelected.forEach(function (currentRun, runIndex, runArray) {
                runArray[runIndex] = false;
            });
            vm.isRunSelected[index] = true;
            // console.log($scope.treeData);
        };

        $scope.$on("$destroy", function() {
            pipelineUpdater.flushAllArtifactPipelines();
        });

    });

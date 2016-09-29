'use strict';

angular
    .module('hawk.artifactManagement')
    .controller('ArtifactController', function($rootScope, $scope, $log, $interval, viewModel, pipelineUpdater, moment, commonUtitlites, filterRuns, artifactService, pipeExecService) {
        var vm = this;

        vm.allPipelines = [];

        vm.isDefinitionGroupOpen = [];

        vm.isRunSelected = [];

        vm.query = '';

        vm.scrollDisable = false;

        pipeExecService.getAllArtifactPipelines('', 10, '');

        vm.truncateGitFromUrl = function(repoUrl, commitId) {
            return commonUtitlites.truncateGitFromUrl(repoUrl,commitId);
        };

        vm.goToGitLink = function(link) {
            location.href = link;
        };

        vm.search = function() {
            // vm.filteredItems = filterRuns.search(vm.allPipelineRuns, vm.query);
            if(viewModel.artifactPipelines[0]){
                viewModel.artifactPipelines[0].searchCriteria = angular.copy(vm.query);
            }
            vm.searchCriteria = angular.copy(vm.query);
            pipeExecService.getAllArtifactPipelines(vm.query, 10, '');
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
                    pipeExecService.getAllArtifactPipelines(vm.query, 10, vm.allPipelineRuns()[vm.allPipelineRuns().length - 1].id);
                }
                vm.allPipelineRuns()[0].disabled = true;
            }
        };

        vm.loadJsTree = function (index, event) {
            if(event.currentTarget.attributes['aria-expanded'].nodeValue == 'false'){
                $('#jstree' + index).jstree(true).settings.core.data = angular.copy(vm.allPipelineRuns()[index].artifactsFileStructure[0].children);
                $('#jstree' + index).jstree(true).refresh();
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

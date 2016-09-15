'use strict';

angular
    .module('hawk.artifactManagement')
    .controller('ArtifactController', function($rootScope, $scope, $log, $interval, viewModel, moment, commonUtitlites, filterRuns) {
        var vm = this;

        vm.allPipelines = [];

        vm.allPipelineRuns = [];

        vm.isDefinitionGroupOpen = [];

        vm.isRunSelected = [];

        vm.query = "";

        vm.truncateGitFromUrl = function(repoUrl, commitId) {
            return commonUtitlites.truncateGitFromUrl(repoUrl,commitId);
        };

        vm.goToGitLink = function(link) {
            location.href = link;
        };

        vm.search = function() {
            vm.filteredItems = filterRuns.search(vm.allPipelineRuns, vm.query);
        };

        $scope.$watch(function() {
            return viewModel.allPipelineRuns
        }, function(newVal, oldVal) {
            vm.allPipelineRuns = angular.copy(viewModel.allPipelineRuns);

            vm.allPipelineRuns.forEach(function (currentPipelineRun, runIndex, runArray) {
                currentPipelineRun.materials.forEach(function(currentMaterial, index, array) {
                    var definition = currentMaterial.materialDefinition;
                    currentMaterial.gitLink = vm.truncateGitFromUrl(definition.repositoryUrl, definition.commitId);
                });

                currentPipelineRun.stages.forEach(function (currentStage, stageIndex, stageArray) {
                    if(currentStage.endTime) {
                        currentPipelineRun.lastStage = currentStage;
                        currentPipelineRun.lastStage.localEndDate = moment.formatDateUTCToLocal(currentStage.endTime);
                        currentPipelineRun.lastStage.localEndTime = moment.formatTimeInLocal(currentStage.endTime.time);
                    }
                });
            });

            vm.allPipelineRuns.sort(function(a, b) {
                return moment.utc(b.startTime.date).add(b.startTime.time.hour, 'hours').add(b.startTime.time.minute, 'minutes').add(b.startTime.time.second, 'seconds').diff(moment.utc(a.startTime.date).add(a.startTime.time.hour, 'hours').add(a.startTime.time.minute, 'minutes').add(a.startTime.time.second, 'seconds'));
            });

            vm.search();

        }, true);

        $scope.treeInstance = {};

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

        $(document).ready(function(){
            $('#jstree').jstree();
        });

        $('#jstree').on("changed.jstree", function (e, data) {
            // vm.selectedData = angular.copy($scope.treeData);
        });

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
            console.log($scope.treeData);
        };

    });

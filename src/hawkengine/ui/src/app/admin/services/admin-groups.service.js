'use strict';

angular
    .module('hawk')
    .factory('adminGroupService', [function () {
        var adminGroupService = this;

        adminGroupService.checkIfDataIsDifferent = function (currentGroupAndPipelines, newGroupAndPipelines) {
            var isTheSame = true;

            for (var i = 0; i < newGroupAndPipelines.length; i++) {          
                if (newGroupAndPipelines[i].Name != currentGroupAndPipelines[i].Name ||
                    newGroupAndPipelines[i].Pipelines.length != currentGroupAndPipelines[i].Pipelines.length) {
                    isTheSame = false;
                    break;
                }
            }

            return isTheSame;
        }

        return adminGroupService;
    }]);

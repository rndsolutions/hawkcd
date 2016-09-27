'use strict';

angular
    .module('hawk')
    .factory('userGroupUpdater', ['viewModel', function (viewModel) {
        var userGroupUpdater = this;

        userGroupUpdater.getUserGroups = function (userGroups) {
            viewModel.userGroups = userGroups;
        };

        userGroupUpdater.getUserGroupDTOs = function (userGroups) {
            viewModel.userGroups = userGroups;
        };

        userGroupUpdater.updateUserGroup = function (userGroup) {
            viewModel.userGroups.forEach(function (currentUserGroup, userGroupIndex, userGroupArray) {
                if(currentUserGroup.id == userGroup.id){
                    viewModel.userGroups[userGroupIndex] = userGroup;
                }
            });
        };

        userGroupUpdater.addUserGroup = function (userGroup) {
            viewModel.userGroups.push(userGroup);
        };

        return userGroupUpdater;
    }]);

'use strict';

angular
    .module('hawk')
    .factory('userUpdater', ['viewModel', function (viewModel) {
        var userUpdater = this;

        userUpdater.getUsers = function (users) {
            viewModel.users = users;
        };

        userUpdater.addUser = function (user) {
            viewModel.users.push(user);
        };

        userUpdater.updateUser = function (user) {
            viewModel.users.forEach(function (currentUser, userIndex, userArray) {
                if(currentUser.id == user.id){
                    viewModel.users[userIndex] = user;
                }
            });
        };

        userUpdater.updateUsers = function (users) {
            viewModel.users.forEach(function (currentUser, userIndex, userArray) {
                users.forEach(function (currentUpdatedUser, updatedUserIndex, updatedUserArray) {
                    if(currentUser.id == currentUpdatedUser.id) {
                        viewModel.users[userIndex] = users[updatedUserIndex];
                    }
                });
            });
        };

        return userUpdater;
    }]);

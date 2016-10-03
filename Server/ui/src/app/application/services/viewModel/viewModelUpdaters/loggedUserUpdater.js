'use strict';

angular
    .module('hawk')
    .factory('loggedUserUpdater', ['viewModel', function (viewModel) {
        var loggedUserUpdater = this;

        loggedUserUpdater.getUser = function (user) {
            viewModel.user = user;
            console.log(user);
        };

        return loggedUserUpdater;
    }]);

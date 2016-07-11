'use strict';

angular
  .module('hawk.userManagement', ["ui.router"])
  .config(['$stateProvider', function ($stateProvider) {

    //Pipelines
    $stateProvider
    // User Profile
      .state("index.profile", {
        url: "profile",
        templateUrl: "app/profile/views/index.html",
        data: { pageTitle: 'User Profile' },
        controller: "UserController",
        controllerAs:'user'
      })
  }]);



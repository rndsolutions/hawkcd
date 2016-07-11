'use strict';

angular
  .module('hawk.environmentsManagement', ["ui.router"])
  .config(['$stateProvider', StateProvider]);

function StateProvider($stateProvider) {
  // Environments
  $stateProvider
    .state('environments', {
      url: "/environments",
      templateUrl: "app/environments/views/environment.html",
      data: {pageTitle: 'Environments'},
      controller: "EnvironmentsController"
    })
}


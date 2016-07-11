'use strict';


//add ['datatables'] dependency
angular
  .module('hawk.agentsManagement', ['ui.router'])
  .config(['$stateProvider', function ($stateProvider) {
  // Agents
  $stateProvider
    .state('index.agents', {
      url: "agents",
      templateUrl: "app/agents/views/index.html",
      data: {pageTitle: 'Agents'},
      controller: "AgentsController",
      controllerAs:"agents"
    })
}]);


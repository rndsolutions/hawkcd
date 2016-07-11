'use strict';

angular
  .module('hawk')
  /* Setup Layout Part - Quick Sidebar */
  .controller('QuickSidebarController', ['$scope', function ($scope) {
    $scope.$on('$includeContentLoaded', function () {
      setTimeout(function () {
        QuickSidebar.init(); // init quick sidebar
      }, 2000)
    });
  }]);

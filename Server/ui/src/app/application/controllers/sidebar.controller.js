'use strict';

angular
    .module('hawk')
    /* Setup Layout Part - Sidebar */
    .controller('SidebarController', ['$scope', function ($scope) {
        $scope.$on('$includeContentLoaded', function () {
            Layout.initSidebar(); // init sidebar
        });

       
  }]);

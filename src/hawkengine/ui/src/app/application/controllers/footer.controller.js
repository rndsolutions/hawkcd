'use strict';

angular
  .module('hawk')
  /* Setup Layout Part - Footer */
  .controller('FooterController', ['$scope', function ($scope) {
    $scope.$on('$includeContentLoaded', function () {
      Layout.initFooter(); // init footer
    });
  }]);

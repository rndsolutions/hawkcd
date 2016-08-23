'use strict';

angular
    .module('hawk')
    /* Setup Layout Part - Header */
    .controller('HeaderController', ['$scope', '$rootScope', '$state', 'profileService', 'loginService', function ($scope, $rootScope, $state, profileService, loginService) {
        $scope.$on('$includeContentLoaded', function () {
            Layout.initHeader(); // init header
        });

        $rootScope.logout = function () {
            loginService.logout();
        };
        
        $scope.myProfile = 'My profile';
  }]);

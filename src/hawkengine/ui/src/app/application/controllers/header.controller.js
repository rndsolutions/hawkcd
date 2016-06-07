'use strict';

angular
    .module('hawk')
    /* Setup Layout Part - Header */
    .controller('HeaderController', ['$scope', '$rootScope', '$state', 'profileService', function ($scope, $rootScope, $state, profileService) {
        $scope.$on('$includeContentLoaded', function () {
            Layout.initHeader(); // init header
        });

        $rootScope.logout = function () {
            $rootScope.isLogged = false;
        };
        
        $scope.myProfile = 'My profile';
  }]);

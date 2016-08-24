'use strict';

angular.module('ansi_up',[])
    .factory('ansi_up',['$window',function($window){
        var ansi_up = $window.ansi_up;
        return ansi_up;
    }]);

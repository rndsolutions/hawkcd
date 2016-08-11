'use strict';

angular.module('momentjs',[])
  .factory('moment',['$window',function($window){
      var moment = $window.moment;
      return moment;
  }]);

'use strict';

angular.module('momentjs',[])
  .factory('moment',['$window',function($window){
      var moment = $window.moment;
      moment.getLastRunAction = function(pipelineRun) {
          if (pipelineRun.endTime == undefined) {
              return;
          }
          var result = {};
          var runEndTime = pipelineRun.endTime;
          var delta = moment(runEndTime);
          var now = moment();
          var diff = moment.duration(moment(now).diff(moment(delta))).humanize();
          if(diff == 'a few seconds'){
              diff = 'few seconds ago';
              result.output = diff;
          } else {
              result.output = diff + " ago";
          }
          return result;
      };

      return moment;
  }]);

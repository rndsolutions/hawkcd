'use strict';

angular.module('momentjs', [])
    .factory('moment', ['$window', function($window) {
        var moment = $window.moment;
        moment.getLastRunAction = function(pipelineRun) {
            if (pipelineRun.endTime == undefined) {
                return;
            }
            var result = {};
            var endDay = pipelineRun.endTime.date;
            var endTime = pipelineRun.endTime.time;
            var runEndTime = pipelineRun.endTime;
            debugger;
            var time = moment.utc({
                year: endDay.year,
                month: endDay.month,
                day: endDay.day,
                hour: endTime.hour,
                minute: endTime.minute,
                second: endTime.second
            });
            //date is in UTC. 3 hours back. To be in local we add 3 hours. We also add 1 month, because JS months in date are -1.[arrays count]
            // time.add(3, 'h');
            time.local();
            time.subtract(1, 'M');
            var delta = moment(time);
            var now = moment();
            var diff = moment.duration(moment(now).diff(moment(delta))).humanize();
            var rawDiff = moment.duration(moment(now).diff(moment(delta)));
            if (rawDiff.hours() < 1) {
                diff = rawDiff.minutes() + ' minutes ago';
                result.output = diff;
            } else {
                result.output = diff + " ago";
            }
            return result;
        };

        moment.formatTimeInLocal = function(time) {
            var bufferTime = moment.utc(time);
            bufferTime.local();
            var result = {
              hour: bufferTime.hours(),
              minute:bufferTime.minutes(),
              second:bufferTime.seconds()
            };
            return result;
        };
      
        return moment;
    }]);

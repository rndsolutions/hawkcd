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
            var time = moment.utc({
                year: endDay.year,
                month: endDay.month,
                day: endDay.day,
                hour: endTime.hour,
                minute: endTime.minute,
                second: endTime.second
            });
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
                minute: bufferTime.minutes(),
                second: bufferTime.seconds()
            };
            return result;
        };

        moment.formatDateUTCToLocal = function(input) {
            if (input === undefined) {
                return;
            }
            var buffer = moment.utc({
                year: input.date.year,
                month: input.date.month,
                day: input.date.day,
                hour: input.time.hour,
                minute: input.time.minute,
                second: input.time.second
            });
            buffer.local();
            var result = buffer.toObject();
            return result;
        }

        return moment;
    }]);

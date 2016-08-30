'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('commonUtitlites', [function () {
        var commonUtitlites = this;

        commonUtitlites.truncateGitFromUrl = function(repoUrl, commitId) {
            var pattern = '.git';
            var patternLength = pattern.length;
            var buffer = repoUrl.substr(0, repoUrl.indexOf(pattern));
            var result = buffer + '/' + 'commit' + '/' + commitId;
            return result;
        };

        return commonUtitlites;

    }]);

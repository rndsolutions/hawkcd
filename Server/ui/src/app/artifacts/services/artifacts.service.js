'use strict';

angular
    .module('hawk.artifactManagement')
    .factory('artifactService', ['CONSTANTS', function(CONSTANTS) {
        var artifactService = this;

        var fileEndpoint = CONSTANTS.SERVER_URL + '/';

        artifactService.getFile = function (filePath) {
            window.location.replace(fileEndpoint + filePath);
        };

        return artifactService;
    }]);

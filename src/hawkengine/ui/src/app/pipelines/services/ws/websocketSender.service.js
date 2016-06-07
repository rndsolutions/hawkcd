'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketSenderService', ['$rootScope', function ($rootScope) {
        var websocketSenderService = this;

        websocketSenderService.call = function (json) {
            $rootScope.socket.send(json);
        };

        return websocketSenderService;
    }]);
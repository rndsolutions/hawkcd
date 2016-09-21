'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('websocketSenderService', ['$rootScope', function ($rootScope) {
        var websocketSenderService = this;

        websocketSenderService.call = function (json) {
            if($rootScope.socket.readyState === 1) {
                $rootScope.socket.send(json);

            } else {
                setTimeout(function() {
                    websocketSenderService.call(json);
                }, 500);
            }
        };

        return websocketSenderService;
    }]);
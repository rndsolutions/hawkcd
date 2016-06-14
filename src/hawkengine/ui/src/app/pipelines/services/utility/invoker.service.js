'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('invokerService', [function () {
        
        var invoker = function (obj, dispatcher) {
            dispatcher = dispatcher || {};
            obj = obj || {};
            var className = obj['className'];
            var methodName = obj['methodName'];
            dispatcher[className][methodName](obj.result);
        };

        return invoker;
    }]);

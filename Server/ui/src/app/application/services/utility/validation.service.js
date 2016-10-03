'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('validationService', ['toaster', 'notificationService', 'CONSTANTS', function(toaster, notificationService, CONSTANTS) {
        var validationService = this;

        validationService.isValid = function(obj) {
            var isValid = true;
            for (var prop in obj) {
                if ((prop === 'className' || prop === 'methodName') && ((obj[prop] === null || obj[prop] === 'undefined') || isEmpty(obj[prop]))) {
                    isValid = false;
                }
            }

            return isValid;
        }

        function isEmpty(str) {
            var result = (!str || str.length === 0);
            return result;
        }

        validationService.dispatcherFlow = function(object, solveFunctions, shouldDisplay) {
            if (object.notificationType == CONSTANTS.TOAST_SUCCESS) {
                solveFunctions.forEach(function(currentFunction,index,array){
                    currentFunction(object.result);
                });

                if (shouldDisplay) {
                    notificationService.notificationDispatcher[object.notificationType](object.errorMessage);
                }
            } else {
                notificationService.notificationDispatcher[object.notificationType](object.errorMessage);
            }
        }

        // validationService.flowNoParameters = function(object, solveFunction, shouldDisplay = false) {
        //     if (object.notificationType == CONSTANTS.TOAST_SUCCESS) {
        //         solveFunction();
        //         if (shouldDisplay) {
        //             notificationService.notificationDispatcher[object.notificationType](object.errorMessage);
        //         }
        //     } else {
        //         notificationService.notificationDispatcher[object.notificationType](object.errorMessage);
        //     }
        // }

        return validationService;

    }]);

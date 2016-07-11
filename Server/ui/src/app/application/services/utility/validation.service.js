'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('validationService', [function () {
        var validationService = this;

        validationService.isValid = function (obj) {
            var isValid = true;
            for (var prop in obj) {
                if ((prop === 'className' || prop === 'methodName') && ((obj[prop] === null || obj[prop] === 'undefined') || isEmpty(obj[prop]))) {
                    isValid = false;
                }
            }

            return isValid;
        }

        function isEmpty(str){
            var result = (!str || str.length === 0);
            return result;
        }

        return validationService;

    }]);
'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('validationService', ['toaster',function (toaster) {
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

        validationService.dispatcherFlow = function(object,solveFunction){
          if(object.error == false) {
              solveFunction(object.result);
          }
          else{
              toaster.pop('error', "Notification", object.errorMessage);
          }
        }

        validationService.flowNoParameters = function(object,solveFunction){
          if(object.error == false) {
              solveFunction();
          }
          else{
              toaster.pop('error', "Notification", object.errorMessage);
          }
        }

        return validationService;

    }]);

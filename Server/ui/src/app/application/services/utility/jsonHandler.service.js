'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('jsonHandlerService', [function () {
        var jsonHandlerService = this;

        jsonHandlerService
        .createJson = function(className, packageName, methodName, result, error, args){
            var json =
            "{ \
                \"className\": \"" + className + "\", \
                \"packageName\": \"" + packageName + "\",\
                \"methodName\": \"" + methodName + "\",\
                \"result\": \"" + result + "\", \
                \"notificationType\": \"" + error + "\", \
                \"errorMessage\": \"\", \
                \"args\": [" + args + "] \
            }";
            return json;
        };

        return jsonHandlerService;

    }]);

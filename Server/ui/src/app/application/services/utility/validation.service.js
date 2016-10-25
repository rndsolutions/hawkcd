/* Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

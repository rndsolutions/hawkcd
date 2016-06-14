'use strict';

angular
    .module('hawk.pipelinesManagement')
    .factory('validationService', ['toaster', function (toaster) {
        var validationService = this;

        this.validateNull = function (obj) {
            for (var prop in obj) {
                if (typeof obj[prop] != 'function' && obj[prop] === null) {
                    toaster.error('Missing Agent data ');
                }
            }
        }

        this.validateResult = function (obj) {
            for (var prop in obj) {
                if (prop == 'result' && (obj[prop] === null || obj[prop] === 'undefined')) {
                    toaster.error('Missing Agent details data');
                }
            }
        }

        this.validateProperties = function(obj){
            for (var prop in obj) {
                if (obj[prop] === null || obj[prop] === 'undefined') {
                    toaster.error('Missing Agent details data');
                }
            }
        }

        return this;

    }]);
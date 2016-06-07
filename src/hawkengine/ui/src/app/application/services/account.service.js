'use strict';

angular
    .module('hawk')
    .factory('accountService', ['$http', '$q', 'CONSTANTS', function ($http, $q, CONSTANTS) {
        var accountService = this;
        var registerEndPoint = CONSTANTS.BASE_URL + '/Account' + '/Register';

        //register
        accountService.registerUser = function (user) {
            var defer = $q.defer();

            $http.post(registerEndPoint, user)
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };


        return accountService;
    }]);

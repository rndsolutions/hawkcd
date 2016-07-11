'use strict';

angular
    .module('hawk.adminManagement')
    .factory('adminService', ['$http', '$q', 'CONSTANTS', function($http, $q, CONSTANTS) {
        var adminService = this;

        var usersEndPoint = CONSTANTS.BASE_URL + CONSTANTS.ACCOUNT + CONSTANTS.USERS + '/';
        var registerEndPoint = CONSTANTS.BASE_URL + CONSTANTS.ACCOUNT + '/Register';

        var token = window.localStorage['accessToken'];

        adminService.registerUser = function (user, token) {
            var defer = $q.defer();

            $http.post(registerEndPoint, user, {
                    headers: {
                        'Authorization': 'bearer ' + token
                    }
                })
                .success(function (res) {
                    defer.resolve(res);
                })
                .error(function (err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };

        adminService.getAllUsers = function(token) {
            var defer = $q.defer();

            $http.get(usersEndPoint, {
                    headers: {
                        'Authorization': 'bearer ' + token
                    }
                })
                .success(function(res) {
                    defer.resolve(res);
                })
                .error(function(err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        adminService.getUser = function(id, token) {
            var defer = $q.defer();

            $http.get(usersEndPoint + id, {
                    headers: {
                        'Authorization': 'bearer ' + token
                    }
                })
                .success(function(res) {
                    defer.resolve(res);
                })
                .error(function(err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };
        adminService.deleteUser = function(id, token) {
            var defer = $q.defer();

            $http.delete(usersEndPoint + id, {
                    headers: {
                        'Authorization': 'bearer ' + token
                    }
                })
                .success(function(res) {
                    defer.resolve(res);
                })
                .error(function(err, status) {
                    defer.reject(err);
                });

            return defer.promise;
        };

        return adminService;
    }]);

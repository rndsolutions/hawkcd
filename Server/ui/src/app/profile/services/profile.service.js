'use strict';

angular
    .module('hawk.profileManagement', [])
    .factory('profileService', ['$http', '$q', 'CONSTANTS', function ($http, $q, CONSTANTS) {
        var profileService = this;

        var accountEndPoint = CONSTANTS.BASE_URL + CONSTANTS.ACCOUNT;
        var changePasswordEndPoint = accountEndPoint + '/ChangePassword';
        var userInfoEndPoint = accountEndPoint + '/Users/UserInfo';
        var updateUserEndPoint = accountEndPoint + '/Users';

        // 

        profileService.getUserInfo = function (token) {
            var defer = $q.defer();

            $http.get(userInfoEndPoint, {
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
        }

        profileService.changePassword = function (newPassword, token) {
            var defer = $q.defer();

            $http.post(changePasswordEndPoint, newPassword, {
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

        profileService.updateUserInfo = function (user, token) {
            var defer = $q.defer();
            
            $http.put(updateUserEndPoint, user, {
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
        }

        return profileService;
    }]);

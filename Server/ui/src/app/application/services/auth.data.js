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
    .module('hawk')
    .factory('authDataService', ['$q', '$http', '$state', 'authenticationService', function ($q, $http, $state, authenticationService) {
        var authDataService = this;
        var _authentication = {
            IsAuthenticated: false,
            userName: ""
        };
        var userInfo;

        var tokenEndPoint = 'http://hawkserver:8080/Token';

        authDataService.authenticationData = _authentication;

        // authDataService.getNewToken = function (refreshToken) {
        //     var deferred = $q.defer();
        //
        //     var data = "grant_type=refresh_token&refresh_token=" + refreshToken;
        //     $http.post(tokenEndPoint, data, {
        //             headers: {
        //                 'Content-Type': 'application/x-www-form-urlencoded'
        //             }
        //         }).success(function (response) {
        //             userInfo = {
        //                 accessToken: response.access_token,
        //                 refreshToken: response.refresh_token,
        //                 email: response.userName,
        //                 username: response.userName,
        //                 issued: response['.issued'],
        //                 expires: response['.expires']
        //             };
        //             authenticationService.setTokenInfo(userInfo);
        //             deferred.resolve(response);
        //         })
        //         .error(function (err, status) {
        //             localStorage.clear();
        //             $state.go('auth');
        //             deferred.reject(err);
        //         });
        //
        //     return deferred.promise;
        // }

        // authDataService.checkTokenExpiration = function () {
        //     var currentDate = new Date();
        //     currentDate.setSeconds(currentDate.getSeconds() + 10);
        //     currentDate = Date.parse(currentDate);
        //
        //     var tokenDate = Date.parse(window.localStorage.expires);
        //
        //     if (isNaN(tokenDate)) {
        //         return false;
        //     }
        //     if (currentDate > tokenDate) {
        //         return false;
        //     } else {
        //         return true;
        //     }
        // }

        // if (!authDataService.authenticationData.IsAuthenticated) {
        //     authDataService.authenticationData.IsAuthenticated = localStorage.IsAuthenticated;
        //     authDataService.authenticationData.userName = localStorage.userName;
        //     authDataService.checkTokenExpiration();
        // } else {
        //     authDataService.checkTokenExpiration();
        // }



        return authDataService;
    }]);

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
    .factory('authenticationService', ['jsonHandlerService', 'websocketSenderService', function(jsonHandlerService, websocketSenderService) {
        var authenticationService = this;

        authenticationService.getAllPermissions = function () {
            var methodName = "getAll";
            var className = "AuthorizationService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };
        // var tokenInfo = {};
        //
        // authenticationService.setTokenInfo = function(data) {
        //     tokenInfo = data;
        //     window.localStorage["refreshToken"] = tokenInfo.refreshToken;
        //     window.localStorage["accessToken"] = tokenInfo.accessToken;
        //     window.localStorage["expires"] = tokenInfo.expires;
        //     window.localStorage["issued"] = tokenInfo.issued;
        //     window.localStorage["email"] = tokenInfo.username;
        //     window.localStorage["username"] = tokenInfo.username;
        //     window.localStorage["IsAuthenticated"] = true;
        // }

        // authenticationService.getTokenInfo = function() {
        //     return tokenInfo;
        // }
        //
        // authenticationService.removeToken = function() {
        //     localStorage.clear();
        // }
        //
        // authenticationService.init = function() {
        //     if (window.localStorage["TokenInfo"]) {
        //         tokenInfo = JSON.parse(window.localStorage["TokenInfo"]);
        //     }
        // }
        //
        // authenticationService.setHeader = function(http) {
        //     delete http.defaults.headers.common['X-Requested-With'];
        //     if ((tokenInfo != undefined) && (tokenInfo.accessToken != undefined) && (tokenInfo.accessToken != null) && (tokenInfo.accessToken != "")) {
        //         http.defaults.headers.common['Authorization'] = 'Bearer ' + tokenInfo.accessToken;
        //         http.defaults.headers.common['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
        //     }
        // }
        // authenticationService.validateRequest = function() {
        //     //  var url = serviceBase + 'api/home';
        //     var deferred = $q.defer();
        //     $http.get(url).then(function() {
        //         deferred.resolve(null);
        //     }, function(error) {
        //         deferred.reject(error);
        //     });
        //     return deferred.promise;
        // }


        return authenticationService;
    }]);

'use strict';

angular
    .module('hawk')
    .factory('authenticationService', ['$http', '$q', 'CONSTANTS', function($http, $q, CONSTANTS) {
        var authenticationService = this;
        var tokenInfo = {};

        authenticationService.setTokenInfo = function(data) {
            tokenInfo = data;
            window.localStorage["refreshToken"] = tokenInfo.refreshToken;
            window.localStorage["accessToken"] = tokenInfo.accessToken;
            window.localStorage["expires"] = tokenInfo.expires;
            window.localStorage["issued"] = tokenInfo.issued;
            window.localStorage["email"] = tokenInfo.username;
            window.localStorage["username"] = tokenInfo.username;
            window.localStorage["IsAuthenticated"] = true;
        }

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

'use strict';

angular
    .module('hawk')
    .factory('loginService', ['$http', '$q', 'CONSTANTS', 'authenticationService', 'authDataService', '$state', function ($http, $q, CONSTANTS, authenticationService, authDataService, $state) {
        var loginService = this;
        var tokenEndPoint = '/Token';

        var userInfo;
        var deferred;

        this.login = function (userName, password) {
            deferred = $q.defer();
            var data = "grant_type=password&username=" + userName + "&password=" + password;
            $http.post(tokenEndPoint, data, {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }).success(function (response) {
                    var o = response;
                    console.log(response.userName);
                    userInfo = {
                        accessToken: response.access_token,
                        refreshToken: response.refresh_token,
                        username: response.userName,
                        email: response.userName,
                        issued: response['.issued'],
                        expires: response['.expires']
                    };
                    authenticationService.setTokenInfo(userInfo);
                    authDataService.authenticationData.IsAuthenticated = true;
                    authDataService.authenticationData.userName = response.userName;


                    $state.go('index.pipelines');
                    deferred.resolve(null);

                })
                .error(function (err, status) {
                    authDataService.authenticationData.IsAuthenticated = false;
                    authDataService.authenticationData.userName = "";
                    deferred.reject(err);
                });
            return deferred.promise;
        }
        this.logOut = function () {
            authenticationService.removeToken();
            authDataService.authenticationData.IsAuthenticated = false;
            authDataService.authenticationData.userName = "";
            $state.go('auth');

            //Api for logout?
        }




        return loginService;
    }]);

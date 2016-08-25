'use strict';

angular
    .module('hawk')
    .factory('loginService', ['$http', '$q', 'CONSTANTS', 'authenticationService', 'authDataService', 'viewModel', 'viewModelUpdater', '$location', '$state', '$auth', function ($http, $q, CONSTANTS, authenticationService, authDataService, viewModel, viewModelUpdater, $location, $state, $auth) {
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
        };


//         this.logout = function () {
//
// //           $http.post('http://localhost:8080/auth/logout', viewModel.user.username, {
// //               headers: {
// //                   'Content-Type': 'application/json'
// //               }
// //           }).then(function (res){
// //            console.log(res);
// //            }).resolve(function (err){
// //            });
//
//                 $http({
//                 method: 'POST',
//                 url: 'http://localhost:8080/auth/logout',
//                 data: viewModel.user.username
//               }).then(function successCallback(response) {
//                   console.log(response);
//                 }, function errorCallback(response) {
//                   console.log(response);
//                });
//
//
//            // //Api for logout?
//        };

        this.logout = function () {
            $http({
                method: 'POST',
                url: 'http://localhost:8080/auth/logout',
                data: viewModel.user.username
            })
                .then(function(res) {
                    $auth.logout();
                    localStorage.clear();
                    $location.path("/authenticate");
                    console.log(res);
                }, function(err) {
                    console.log(err);
                });
        };

       this.logoutUser = function (username) {
debugger;
           $auth.removeToken();
           viewModelUpdater.flushViewModel();
       };




        return loginService;
    }]);

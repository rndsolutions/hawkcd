'use strict';

angular
    .module('hawk')
    /* Setup App Main Controller */
    .controller('AppController', ['$scope', 'loginService', 'accountService', 'profileService', 'authDataService', 'pipeConfig','$auth',"$location",
    function ($scope, loginService, accountService, profileService, authDataService, pipeConfig, $auth, $location) {
        $scope.$on('$viewContentLoaded', function () {
            //App.initComponents(); // init core components
            Layout.init(); //  Init entire layout(header, footer, sidebar, etc) on page load if the partials included in server side instead of loading with ng-include directive
        });

        $scope.userEmail = localStorage['email'];

        // console.log(localStorage);
        $scope.latestCommit = "";

        $scope.pageSidebarClosed = true;

        $scope.openSidebar = function () {
            //getLatestCommit();
            $scope.pageSidebarClosed = !$scope.pageSidebarClosed;
        };

        $scope.userLoginForm = {};

        $scope.registerUser = function () {
            accountService.registerUser($scope.userLoginForm)
                .then(function (res) {
                    console.log('User registered');
                }, function (err) {
                    console.log(err);
                })
        };

        //Login user
        $scope.username = '';
        $scope.password = '';

        $scope.loginUser = function () {
            loginService.login($scope.username, $scope.password)
                .then(function (res) {
                    console.log(res);
                    $scope.me.UserName = $scope.username;
                    $scope.updatedUser.Email = $scope.username;
                }, function (err) {
                    console.log(err);
                });
        }

        $scope.logoutUser = function () {
            $auth.logout();
            $location.path("/authenticate");
        }

        $scope.me = {};
        $scope.me.UserName = localStorage.username;
        console.log($scope.me.UserName);

        $scope.updatedUser = {};
        $scope.updatedUser.Email = localStorage.email;
        //        function getMe () {
        //          profileService.getUserInfo()
        //            .then(function (res) {
        //              $scope.me = res;
        //              console.log(res);
        //              $scope.updatedUser = res;
        //              console.log(res);
        //            }, function (err) {
        //              console.log(err);
        //            }) 
        //        }
        //        getMe();

        $scope.updateUser = function (userName) {
            var user = {
                "Email": userName
            }

            profileService.updateUserInfo(user)
                .then(function (res) {
                    $scope.updatedUser.Email = userName;
                    $scope.me.UserName = userName;

                    localStorage.username = userName;
                    localStorage.email = userName;

                    // window.localStorage['username'] = userName;
                    console.log(res);
                }, function (err) {
                    console.log(err);
                })
        };

        $scope.authenticate = function(provider){
            $auth.authenticate(provider)
            .then(function (response){
                $auth.setToken(response.data.id);
                $location.path("/pipelines");
            })
            .catch(function(response){
                console.log(response);
            });
            //console.log(provider);
        }

    }]);

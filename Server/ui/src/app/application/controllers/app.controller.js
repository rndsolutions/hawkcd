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
    /* Setup App Main Controller */
    .controller('AppController', ['$scope', '$rootScope', 'CONSTANTS', 'loggerService', 'loginService', 'viewModel', 'accountService', 'profileService', 'authDataService', '$auth', "$location", "$http",
        function($scope, $rootScope, CONSTANTS, loggerService, loginService, viewModel, accountService, profileService, authDataService, $auth, $location, $http) {
            $scope.$on('$viewContentLoaded', function() {
                //App.initComponents(); // init core components
                Layout.init(); //  Init entire layout(header, footer, sidebar, etc) on page load if the partials included in server side instead of loading with ng-include directive
            });

            $scope.userEmail = localStorage['email'];

            $scope.user = {};

            $scope.$watch(function() {
                return viewModel.user;
            }, function(newVal, oldVal) {
                $scope.user = viewModel.user;
            }, true);

            // console.log(localStorage);
            $scope.latestCommit = "";

            $scope.pageSidebarClosed = true;

            $scope.openSidebar = function() {
                //getLatestCommit();
                //$scope.pageSidebarClosed = !$scope.pageSidebarClosed;
            };

            $scope.userLoginForm = {};
            $scope.forgetForm = {};

            $scope.registerUser = function() {
                accountService.registerUser($scope.userLoginForm)
                    .then(function(res) {
                        loggerService.log('User registered');
                    }, function(err) {
                        loggerService.log(err);
                    })
            };

            //Login user
            $scope.username = '';
            $scope.password = '';

            $scope.loginUser = function() {
                loginService.login($scope.username, $scope.password)
                    .then(function(res) {
                        loggerService.log(res);
                        $rootScope.me.UserName = $scope.username;
                        $rootScope.updatedUser.Email = $scope.username;
                    }, function(err) {
                        loggerService.log(err);
                    });
            }

            $scope.logoutUser = function() {
                loginService.logout();

            };

            $scope.me = {};
            $scope.me.UserName = localStorage.username;

            $scope.updatedUser = {};
            $scope.updatedUser.Email = localStorage.email;

            $scope.updateUser = function(userName) {
                var user = {
                    "Email": userName
                }

                profileService.updateUserInfo(user)
                    .then(function(res) {
                        $scope.updatedUser.Email = userName;
                        $scope.me.UserName = userName;

                        localStorage.username = userName;
                        localStorage.email = userName;

                        // window.localStorage['username'] = userName;
                        loggerService.log(res);
                    }, function(err) {
                        loggerService.log(err);
                    })
            };

            $scope.showLoginForm = true;
            $scope.showRegisterForm = false;
            $scope.showForgotPasswordForm = false;

            $scope.showRegistration = function() {
                $scope.showLoginForm = false;
                $scope.showRegisterForm = true;
                $scope.showForgotPasswordForm = false;

            }

            $scope.showLogin = function() {
                $scope.showLoginForm = true;
                $scope.showRegisterForm = false;
                $scope.showForgotPasswordForm = false;
            }

            $scope.cancelRegister = function(){
              $scope.register_email = '';
              $scope.register_password = '';
              $scope.confirm_password = '';
              $scope.registerForm.$setPristine();
              $scope.registerForm.$setUntouched();
            }

            $scope.forgotPassword = {};
            $scope.cancelForgotPassword = function(){
              $scope.forgetForm.$setPristine();
              $scope.forgetForm.$setUntouched();
              $scope.forgotPassword.email = '';
            }

            $scope.showForgotPassword = function() {
                $scope.showLoginForm = false;
                $scope.showRegisterForm = false;
                $scope.showForgotPasswordForm = true;
            }

            $scope.authenticate = function(provider) {
                $auth.authenticate(provider)
                    .then(function(response) {
                        $auth.setToken(response.data.id);
                        $location.path("/pipelines");
                    })
                    .catch(function(response) {
                        loggerService.log(response);
                    });
            }

            // $('#log-form').keydown(function(e) {
            //     var key = e.which;
            //     if (key == 13) {
            //         // As ASCII code for ENTER key is "13"
            //         $scope.login(); // Submit form code
            //     }
            // });

            $scope.login = function() {
              $scope.mismatchCredentials = {};
                var user = {
                    email: $scope.email,
                    password: $scope.password
                }

                $auth.login(user)
                    .then(function(response) {
                        loggerService.log(response.data);
                        $auth.setToken(response.data);
                        $location.path("/pipelines");
                        $rootScope.startWebsocket('ws://' + CONSTANTS.HOST + '/ws/v1');
                    })
                    .catch(function(response) {
                      $scope.mismatchCredentials.errorMessage = response.data.message;
                        loggerService.log(response);
                            // Handle errors here, such as displaying a notification
                            // for invalid email and/or password.
                    });
            };

            $scope.register = function(user) {

                var user = {
                    email: $scope.register_email,
                    password: $scope.register_password,
                    confirmPassword: $scope.confirm_password
                }

                // Simple GET request example:
                $http({
                    method: 'POST',
                    url: CONSTANTS.SERVER_URL + '/auth/register',
                    data: user
                }).then(function successCallback(response) {
                    $scope.showLogin();
                    loggerService.log(response);
                }, function errorCallback(response) {
                    loggerService.log(response);
                });

                $scope.register_email = undefined;
                $scope.register_password = undefined;
                $scope.confirm_password = undefined;
                $scope.registerForm.$setPristine();
                $scope.registerForm.$setUntouched();
            }

        }
    ]);

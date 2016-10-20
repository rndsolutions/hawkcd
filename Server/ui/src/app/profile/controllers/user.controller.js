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
    .module('hawk.profileManagement')
    .controller('UserController', function($stateParams, $scope, profileService, $rootScope, authDataService, viewModel, adminService) {
        var vm = this;

        vm.defaultText = {
            pageHeader: "Profile Information",
            breadCrumb: "Profile",
            overview: "Overview",
            account: "Account"
        };

        vm.accountTabText = {
            headers: {
                personalInfo: 'Personal info',
                changeAvatar: 'Change Avatar',
                changePassword: 'Change Password',
                privacy: 'Privacity Settings'
            }
        };

        //Initialize the overview screen as default
        vm.currentActiveScreen = 'Option 1';

        //Initilize menus sub menus

        vm.subMenuActiveScreen = 1;
        vm.newPasswordObject = {};
        vm.accountActiveMenu = 'Personal info';
        vm.currentlyLoggedUser = {};
        $scope.$watch(function() {
            return viewModel.user;
        }, function(newVal, oldVal) {
            vm.currentlyLoggedUser = viewModel.user;
        }, true);

        vm.updateUserPassword = function(passwordData) {
            if (passwordData.newPassword == passwordData.confirmNewPassword) {
                adminService.updateUserPassword(vm.currentlyLoggedUser, passwordData.newPassword,passwordData.oldPassword);
            }
            vm.newPasswordObject = {};
        };

        // vm.me = {};
        // vm.updatedUser = {};
        // function getMe () {
        //   profileService.getUserInfo()
        //     .then(function (res) {
        //       vm.me = res;
        //       vm.updatedUser = res;
        //       console.log(res);
        //       console.log(vm.me);
        //       console.log(vm.updatedUser);
        //     }, function (err) {
        //       console.log(err);
        //     })
        // }
        // getMe();

        vm.newPassword = {};
        // vm.changePassword = function() {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         profileService.changePassword(vm.newPassword, token)
        //             .then(function(res) {
        //                 console.log(res);
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //             .then(function(res) {
        //                 var token = res.access_token;
        //                 profileService.changePassword(vm.newPassword, token)
        //                     .then(function(res) {
        //                         console.log(res);
        //                     }, function(err) {
        //                         console.log(err);
        //                     })
        //             }, function(err) {
        //                 console.log(err);
        //             })
        //     }
        // }
    });

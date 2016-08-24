'use strict';

angular
    .module('hawk.profileManagement')
    .controller('UserController', function($stateParams, $scope, profileService, $rootScope, authDataService, viewModel, adminService) {
        var vm = this;

        vm.defaultText = {
            pageHeader: "Your profile",
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
        $scope.$watchCollection(function() {
            return viewModel.user;
        }, function(newVal, oldVal) {
            vm.currentlyLoggedUser = viewModel.user;
        })

        vm.updateUserPassword = function(passwordData) {
            if (passwordData.newPassword == passwordData.confirmNewPassword) {
                adminService.updateUserPassword(vm.currentlyLoggedUser, passwordData.newPassword,passwordData.oldPassword);
            }
        }

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

'use strict';

angular
    .module('hawk.adminManagement')
    .factory('adminService', ['$http', '$q', 'CONSTANTS', 'websocketSenderService', 'jsonHandlerService', function($http, $q, CONSTANTS, websocketSenderService, jsonHandlerService) {
        var adminService = this;

        adminService.getAllUserGroups = function() {
            var methodName = "getAll";
            var className = "UserGroupService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.getAllUserGroupDTOs = function() {
            var methodName = "getAllUserGroups";
            var className = "UserGroupService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.getAllUsers = function() {
            var methodName = "getAll";
            var className = "UserService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"\", \"object\": \"\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.addUserGroup = function (userGroup) {
            var methodName = "add";
            var className = "UserGroupService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.UserGroup\", \"object\": " + JSON.stringify(userGroup) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        adminService.updateUser = function (user) {
            var methodName = "update";
            var className = "UserService";
            var packageName = "net.hawkengine.services";
            var result = "";
            var args = ["{\"packageName\": \"net.hawkengine.model.User\", \"object\": " + JSON.stringify(user) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
            console.log(json);
        };

        // var usersEndPoint = CONSTANTS.BASE_URL + CONSTANTS.ACCOUNT + CONSTANTS.USERS + '/';
        // var registerEndPoint = CONSTANTS.BASE_URL + CONSTANTS.ACCOUNT + '/Register';
        //
        // var token = window.localStorage['accessToken'];
        //
        // adminService.registerUser = function (user, token) {
        //     var defer = $q.defer();
        //
        //     $http.post(registerEndPoint, user, {
        //             headers: {
        //                 'Authorization': 'bearer ' + token
        //             }
        //         })
        //         .success(function (res) {
        //             defer.resolve(res);
        //         })
        //         .error(function (err, status) {
        //             defer.reject(err);
        //         });
        //
        //     return defer.promise;
        // };
        //
        // adminService.getAllUsers = function(token) {
        //     var defer = $q.defer();
        //
        //     $http.get(usersEndPoint, {
        //             headers: {
        //                 'Authorization': 'bearer ' + token
        //             }
        //         })
        //         .success(function(res) {
        //             defer.resolve(res);
        //         })
        //         .error(function(err, status) {
        //             defer.reject(err);
        //         });
        //
        //     return defer.promise;
        // };
        // adminService.getUser = function(id, token) {
        //     var defer = $q.defer();
        //
        //     $http.get(usersEndPoint + id, {
        //             headers: {
        //                 'Authorization': 'bearer ' + token
        //             }
        //         })
        //         .success(function(res) {
        //             defer.resolve(res);
        //         })
        //         .error(function(err, status) {
        //             defer.reject(err);
        //         });
        //
        //     return defer.promise;
        // };
        // adminService.deleteUser = function(id, token) {
        //     var defer = $q.defer();
        //
        //     $http.delete(usersEndPoint + id, {
        //             headers: {
        //                 'Authorization': 'bearer ' + token
        //             }
        //         })
        //         .success(function(res) {
        //             defer.resolve(res);
        //         })
        //         .error(function(err, status) {
        //             defer.reject(err);
        //         });
        //
        //     return defer.promise;
        // };

        return adminService;
    }]);

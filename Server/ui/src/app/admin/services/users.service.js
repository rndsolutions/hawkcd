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
    .module('hawk.adminManagement')
    .factory('adminService', ['$http', '$q', 'CONSTANTS', 'websocketSenderService', 'jsonHandlerService', function($http, $q, CONSTANTS, websocketSenderService, jsonHandlerService) {
        var adminService = this;

        adminService.getAllUserGroups = function() {
            var methodName = "getAll";
            var className = "UserUpdaterService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = [];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        adminService.getAllUserGroupDTOs = function() {
            var methodName = "getAllUserGroups";
            var className = "UserGroupService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = [];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        adminService.updateUserGroupDTO = function(userGroup) {
            var methodName = "updateUserGroupDto";
            var className = "UserGroupService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.dto.UserGroupDto\", \"object\": " + JSON.stringify(userGroup) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        adminService.deleteUserGroup = function(userGroup) {
            var methodName = "delete";
            var className = "UserGroupService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.UserGroup\", \"object\": " + JSON.stringify(userGroup) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        adminService.getAllUsers = function() {
            var methodName = "getAll";
            var className = "UserService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = [];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        adminService.addUser = function(user) {
            var methodName = "addUserWithoutProvider";
            var className = "UserService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.User\", \"object\": " + JSON.stringify(user) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        adminService.addUserGroup = function(userGroup) {
            var methodName = "addUserGroupDto";
            var className = "UserGroupService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.dto.UserGroupDto\", \"object\": " + JSON.stringify(userGroup) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        adminService.updateUserPermissions = function(userId, grants) {
            var methodName = "updateUserPermissions";
            var className = "UserUpdaterService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + userId + "\"}, " +
            "{\"packageName\": \"io.hawkcd.core.security.AuthorizationGrant\", \"object\": " + JSON.stringify(grants) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        adminService.resetUserPassword = function(user) {
            var methodName = "resetUserPassword";
            var className = "UserService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.User\", \"object\": " + JSON.stringify(user) + "}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        adminService.updateUserPassword = function(user, newUserPassword,oldPassword) {
            var methodName = "changeUserPassword";
            var className = "UserService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"io.hawkcd.model.dto.UserDto\", \"object\": " + JSON.stringify(user) + "}, " +
                "{\"packageName\": \"java.lang.String\", \"object\": \"" + newUserPassword + "\"}, " +
                "{\"packageName\": \"java.lang.String\", \"object\": \"" + oldPassword + "\"}"
            ];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        adminService.deleteUser = function(id) {
            var methodName = "delete";
            var className = "UserService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + id + "\"}"];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        adminService.updateUserGroupPermissions = function(userGroupId, permissions) {
            var methodName = "updateUserGroupPermissions";
            var className = "UserUpdaterService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \"" + userGroupId + "\"}, " +
            "{\"packageName\": \"io.hawkcd.core.security.AuthorizationGrant\", \"object\": " + JSON.stringify(permissions) + "}"
            ];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        adminService.assignUsers = function(userGroupId, users) {
            var methodName = "assignUsers";
            var className = "UserUpdaterService";
            var packageName = "io.hawkcd.services";
            var result = "";
            var args = ["{\"packageName\": \"java.lang.String\", \"object\": \""+ userGroupId +"\"}, " +
            "{\"packageName\": \"java.lang.String\", \"object\": " + JSON.stringify(users) + "}"
            ];
            var error = "";
            var json = jsonHandlerService.createJson(className, packageName, methodName, result, error, args);
            websocketSenderService.call(json);
        };

        return adminService;
    }]);

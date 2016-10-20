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
    .factory('userUpdater', ['viewModel', function (viewModel) {
        var userUpdater = this;

        userUpdater.getUsers = function (users) {
            viewModel.users = users;
        };

        userUpdater.addUser = function (user) {
            viewModel.users.push(user);
        };

        userUpdater.updateUser = function (user) {
            viewModel.users.forEach(function (currentUser, userIndex, userArray) {
                if(currentUser.id == user.id){
                    viewModel.users[userIndex] = user;
                }
            });
        };

        userUpdater.updateUsers = function (users) {
            viewModel.users.forEach(function (currentUser, userIndex, userArray) {
                users.forEach(function (currentUpdatedUser, updatedUserIndex, updatedUserArray) {
                    if(currentUser.id == currentUpdatedUser.id) {
                        viewModel.users[userIndex] = users[updatedUserIndex];
                    }
                });
            });
        };

        return userUpdater;
    }]);

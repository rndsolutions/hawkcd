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
    .module("hawk")
    .service('sessionService', function () {
        //Get the current user from localStorage        
        var _user = JSON.parse(localStorage.getItem('hawk.user'));
        var _accessToken = JSON.parse(localStorage.getItem('hawk.accessToken'));      
        
        //Get and set user
        function getUser() {
            return _user;
        }

        function setUser(user) {
            _user = user;
            localStorage.setItem('hawk.user', JSON.stringify(user));
        }
        
        //Get and set token
        function getAccessToken() {
            return _accessToken;
        }

        function setAccessToken(token) {
            _accessToken = token;
            localStorage.setItem('hawk.accessToken', token);
        }

        //Logout
        function destroyUser() {
            this.setUser(null);
            this.setAccessToken(null);
        }

        return {
            user: {
                get: function () {
                    return getUser();
                },
                set: function (user) {
                    return setUser(user);
                }
            },
            token: {
                get: function () {
                    return getAccessToken();
                },
                set: function (token) {
                    return setAccessToken(token);
                }
            },
            logout: function () {
                destroyUser();
            }
        }
    });


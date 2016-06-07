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


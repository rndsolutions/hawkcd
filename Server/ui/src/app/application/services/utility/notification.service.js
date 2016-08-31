'use strict';

angular
    .module('hawk.adminManagement')
    .factory('notificationService', ['toaster', function(toaster) {
        var notificationService = this;
        var TOAST_SUCCESS = 'SUCCESS';

        notificationService.notificationDispatcher = {
            WARNING: function(message) {
                toaster.pop('warning', 'Notification', message, 0);
            },
            SUCCESS: function(message) {
                toaster.pop('success', 'Notification', message);
            },
            ERROR: function(message) {
                toaster.pop('error', 'Notification', message, 0);
            }
        };

        return notificationService;

    }]);

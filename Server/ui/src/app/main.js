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

/***
 Hawk Main Script
 ***/
'use strict';

/* Hawk Main Module */
angular
    .module("hawk", [
        "hawk.environmentsManagement",
        "hawk.agentsManagement",
        "hawk.pipelinesManagement",
        "hawk.userManagement",
        "hawk.adminManagement",
        "hawk.profileManagement",
        "hawk.artifactManagement",
        "ui.router",
        'ui.sortable',
        "ui.bootstrap",
        //"oc.lazyLoad",
        //"ngSanitize",
        'luegg.directives',
        'toaster',
        'momentjs',
        'satellizer',
        'ansi_up',
        'ngJsTree',
        'infinite-scroll'
        //'ngAnimate'
        //'flow'
    ])

    .constant({
        CONSTANTS: {
            'BASE_URL': '/api',
            'SERVER_URL': window.location.origin,
            'HOST': window.location.host,
            'CONFIG': '/config',
            'EXEC': '/exec',
            'STATS': '/stats',
            'PIPELINES': '/pipelines',
            'PIPELINE_GROUPS': "/pipeline_groups",
            'AGENTS': '/agents',
            'STAGES': '/stages',
            'JOBS': '/jobs',
            'TASKS': '/tasks',
            'ARTIFACTS': '/artifacts',
            'MATERIALS': '/materials',
            'SERVER': '/server',
            'ENVIRONMENTS': '/environments',
            'ENVIRONMENTVARS': '/environmentvariables',
            'ACCOUNT': '/Account',
            'USERS': '/Users',
            'DEV': '/dev',

        'MODEL': 'io.hawkcd.model',
        'SERVICES': 'io.hawkcd.services',

        //region agent methods
        'AGENT_GET_BY_ID': 'getAgentById',
        'AGENT_GET_ALL': 'getAllAgents',
        'AGENT_ADD': 'addAgent',
        'AGENT_UPDATE': 'updateAgent',
        'AGENT_DELETE': 'deleteAgent',
        'AGENT_SET_CONFIGSTATE': 'setAgentConfigState',
        //endregion

        //region agent packages
        'AGENT_SERVICE': 'AgentService',
        'AGENT_MODEL': 'Agent',
        //endregion

        //region toaster types
        'TOAST_WARNING': 'WARNING',
        'TOAST_SUCCESS': 'SUCCESS',
        'TOAST_ERROR': 'ERROR'
            //end region toaster types
    }
})



/* Setup global settings */
.factory('settings', ['$rootScope', function($rootScope) {
    // supported languages
    var settings = {
        layout: {

            pageContentWhite: true, // set page content layout
            pageBodySolid: false, // solid body color state
            pageAutoScrollOnLoad: 0 // auto scroll to top on page load
        },
        assetsPath: '../assets',
        globalPath: '../assets/global',
        layoutPath: '../assets/layouts/layout'
    };

    $rootScope.settings = settings;

    return settings;
}])

/* Setup Routing For All Pages */
.config(['$stateProvider', '$urlRouterProvider', '$animateProvider', '$authProvider', 'CONSTANTS',
    function($stateProvider, $urlRouterProvider, $animateProvider, $authProvider, CONSTANTS) {

        // used for debugging
        $authProvider.baseUrl = CONSTANTS.SERVER_URL;
        $authProvider.github({
            clientId: '2d3dbbf586d2260cbd68',
            scope: ['user:email', 'repo']
        });


        // Redirect any unmatched url
        $urlRouterProvider.when('', '/pipelines').when('/', '/pipelines').otherwise("/pipelines");
        //$animateProvider.classNameFilter(/angular-animate/);
        $stateProvider
            .state('auth', {
                url: "/authenticate",
                templateUrl: "app/auth.html",
                data: {
                    pageTitle: 'Authenticate'
                },
                resolve: {
                    auth: function(authDataService, agentService, $location) {
                        if (authDataService.authenticationData.IsAuthenticated) {
                            //pipeStatsService.getAgentById();
                            // $location.path('/pipelines');
                        }
                    }
                }
            })

        .state('index', {
            url: "/",
            templateUrl: "app/main.html",
            resolve: {
                auth: function(authDataService, agentService, $location,
                    $auth, $rootScope, $timeout, loggerService) {

                    if($auth.isAuthenticated){
                        loggerService.log('User authentication successful');
                    } else {
                        loggerService.log('User authentication failed');

                    }

                    if (!$auth.isAuthenticated()) {
                        $timeout(function() {
                            $location.path('/authenticate');
                            $rootScope.$apply();
                        }, 100);
                    }

                    //$auth.authenticate('github');

                    if (!authDataService.authenticationData.IsAuthenticated) {
                        //pipeStatsService.getAgentById();
                        //$location.path('/authenticate');
                    }
                }
            }
        })

    }
])

/* Init global settings and run the app */
.run(["$rootScope", "settings", "$state", "websocketReceiverService", "agentService", "loggerService", "adminGroupService", "adminService", "adminMaterialService", "pipeConfigService", "pipeExecService", "authenticationService", "toaster", "$auth", "$location", "CONSTANTS", "notificationService", function($rootScope, settings, $state, websocketReceiverService, agentService, loggerService, adminGroupService, adminService, adminMaterialService, pipeConfigService, pipeExecService, authenticationService, toaster, $auth, $location, CONSTANTS, notificationService) {
    $rootScope.$state = $state; // state to be accessed from view
    $rootScope.$settings = settings; // state to be accessed from view
    $rootScope.$on('$stateChange');

    $rootScope.$on("$locationChangeSuccess", function(event, newUrl, oldUrl) {
        $rootScope.localStorageDebug = localStorage.getItem('hawkDebug');

        var debugMode = newUrl.split('debug=')[1];
        if(!debugMode && !$rootScope.localStorageDebug || $rootScope.localStorageDebug == 'undefined'){
            debugMode = "false";
        } else if(!debugMode){
            debugMode = $rootScope.localStorageDebug;
        }

        localStorage.setItem('hawkDebug', debugMode);
        $rootScope.localStorageDebug = localStorage.getItem('hawkDebug');



    });

    var wsServerLocation = 'ws://' + CONSTANTS.HOST + '/ws/v1';

    var timerID = 0;


    //TODO: Replace localStorage with $auth.isAuthenitcated()
    $rootScope.startWebsocket = function start(wsServerLocation) {
        $rootScope.socket = new WebSocket(wsServerLocation.concat('?token=' + $auth.getToken()));

        $rootScope.socket.onmessage = function(event) {
            // console.log(event.data);
            websocketReceiverService.processEvent(JSON.parse(event.data));
        };

        $rootScope.socket.onopen = function(event) {
            toaster.clear();
            notificationService.notificationDispatcher[CONSTANTS.TOAST_SUCCESS]("Connection to server successful!");
            if (window.timerID) {
                window.clearInterval(window.timerID);
                window.timerID = 0;
            }
        };

        $rootScope.socket.onclose = function(event) {
            if (!$auth.isAuthenticated()) {
                $auth.logout();
                $location.path('/authenticate');
                loggerService.log('Connection to Server closed:');
                loggerService.log(event);
                toaster.clear();
                notificationService.notificationDispatcher[CONSTANTS.TOAST_WARNING](event.reason);
                $rootScope.$apply();
                return;
            }

//            if (!window.timerID) {
//                window.timerID = setInterval(function() {
//                    start(wsServerLocation)
//                }, 5000);
//            }
//            toaster.clear();
//            notificationService.notificationDispatcher[CONSTANTS.TOAST_ERROR]("Connection lost. Reconnecting...");
//            $rootScope.$apply();
//            console.log(CONSTANTS.WS_URL);
//            console.log(CONSTANTS.SERVER_URL);
        }
    };
    //debugger;
    if ($auth.isAuthenticated()) {
        $rootScope.startWebsocket(wsServerLocation);
        if(localStorage.hawkDebug == "true"){
            console.log('Connected to Server, your token is:');
            console.log($auth.getToken());

        }
    }
}]);

/* Fix for Bootstrap modal behavior */
window.onhashchange = function() {
    $('.modal-backdrop').remove();
};

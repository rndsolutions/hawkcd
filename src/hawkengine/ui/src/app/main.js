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
        "ui.router",
        'ui.sortable',
        //"ui.bootstrap",
        //"oc.lazyLoad",
        //"ngSanitize",
        'luegg.directives',
        'toaster'
        //'ngAnimate'
        //'flow'
    ])

    .constant({
        CONSTANTS: {
            'BASE_URL': '/api',
            'WS_URL': 'ws://hawkserver:8080/ws/v1',
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

            'MODEL': 'net.hawkengine.model',
            'SERVICES': 'net.hawkengine.services',

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
            'AGENT_MODEL': 'Agent'
            //endregion
        }
    })



    /* Setup global settings */
    .factory('settings', ['$rootScope', function ($rootScope) {
        // supported languages
        var settings = {
            layout: {

                pageContentWhite: true, // set page content layout
                pageBodySolid: false, // solid body color state
                pageAutoScrollOnLoad: 1000 // auto scroll to top on page load
            },
            assetsPath: '../assets',
            globalPath: '../assets/global',
            layoutPath: '../assets/layouts/layout'
        };

        $rootScope.settings = settings;

        return settings;
    }])

    /* Setup Routing For All Pages */
    .config(['$stateProvider', '$urlRouterProvider', '$animateProvider', function ($stateProvider, $urlRouterProvider, $animateProvider) {
        // Redirect any unmatched url
        $urlRouterProvider.otherwise("/");

        //$animateProvider.classNameFilter(/angular-animate/);

        $stateProvider

            .state('auth', {
                url: "/authenticate",
                templateUrl: "app/auth.html",
                data: {
                    pageTitle: 'Authenticate'
                },
                resolve: {
                    auth: function (authDataService, pipeStatsService, agentService, $location) {
                        if (authDataService.authenticationData.IsAuthenticated) {
                            //pipeStatsService.getAgentById();
                            //$location.path('/pipelines');
                        }
                    }
                }
            })

            .state('index', {
                url: "/",
                templateUrl: "app/main.html",
                resolve: {
                    auth: function (authDataService, pipeStatsService, agentService, $location) {
                        if (!authDataService.authenticationData.IsAuthenticated) {
                            //pipeStatsService.getAgentById();
                            //$location.path('/authenticate');
                        }
                    }
                }
            })

    }])

    /* Init global settings and run the app */
    .run(["$rootScope", "settings", "$state", "websocketReceiverService", "agentService", "adminGroupService", "pipeConfigService", "toaster", function ($rootScope, settings, $state, websocketReceiverService, agentService, adminGroupService, pipeConfigService, toaster) {
        $rootScope.$state = $state; // state to be accessed from view
        $rootScope.$settings = settings; // state to be accessed from view
        $rootScope.$on('$stateChange');

        var wsServerLocation = "ws://hawkserver:8080/ws/v1";

        var toasterPopped = false;

        var timerID=0;

        function start(wsServerLocation){
            $rootScope.socket = new WebSocket(wsServerLocation);
            $rootScope.socket.onmessage = function (event) {
                console.log(event.data);
                websocketReceiverService.processEvent(JSON.parse(event.data));
            };

            $rootScope.socket.onopen = function (event) {
                toaster.clear();
                toaster.pop('success', "Notification", "Connection to server successful!");
                toasterPopped = true;
                if(window.timerID){
                    window.clearInterval(window.timerID);
                    window.timerID=0;
                }
                //pipeStatsService.getAgentById();
                pipeConfigService.getAllPipelineGroupDTOs();
                agentService.getAllAgents();
                adminGroupService.getAllPipelineGroups();
                //pipeStatsService.getAllPipelineGroups();
            };

            $rootScope.socket.onclose = function (event) {
                toasterPopped = false;
                if(!window.timerID){
                    window.timerID=setInterval(function(){start(wsServerLocation)}, 5000);
                }
                if(toasterPopped == false){
                    toaster.clear();
                    toaster.pop('error', "Notification", "Connection lost. Reconnecting...", 0);
                }
                $rootScope.$apply();
            }
        }
        start(wsServerLocation);

    }]);

/* Fix for Bootstrap modal behavior */
window.onhashchange = function () {
    $('.modal-backdrop').remove();
};

/* Check for dev mode */
window.onpopstate = function () {
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }

    if (vars.dev == "true") {
        console.log("Dev Mode enabled.");
    }
};

//var socket = new WebSocket("ws://hawkserver:8080/ws/v1");

// socket.onopen = function (event){
//    
// };
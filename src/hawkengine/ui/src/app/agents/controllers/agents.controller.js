'use strict';

angular
    .module('hawk.agentsManagement')
    // add DTOptionsBuilder, DTColumnDefBuilder to func
    .controller('AgentsController', function ($rootScope, $scope, $interval, pipeStats, pipeExec, pipeConfig, agentService, websocketReceiverService, authDataService, viewModel) {
        var vm = this;
        vm.agentToDelete = {};

        vm.defaultText = {
            agents: "Agents",
            tableHeaders: {
                action: "Actions",
                agentName: "Agent Name",
                path: "Path",
                os: "OS",
                ip: "IP Address",
                status: "Status",
                freeSpace: "Free Space",
                resources: "Resources",
                environment: "Environments"
            },
            breadCrumb: "Agents",
            deleteModalHeader: "Delete Agent",
            deleteModalConfirm: "Are you sure you want to delete Agent: ",
            addResourceModalHeader: "Add resources to agent: ",
            resourcePlaceHolder: "Resource",
            delete: "DELETE",
            cancel: "CANCEL",
            add: "Add all"
        };

        vm.status = {
            enable: "enable",
            disable: "disable",
            delete: "delete",
            resource: "Edit Resources"
        };

        vm.currentAgents = {};

        vm.currentAgents = viewModel.allAgents;

        $scope.$watch(function() { return viewModel.allAgents }, function(newVal, oldVal) {
            vm.currentAgents = viewModel.allAgents;
            console.log(vm.currentAgents);
        });

        vm.setAgentToDelete = function (agent) {
            vm.agentToDelete = agent;
        };

        vm.setAgentToAddResource = function (agent) {
            vm.resourcesToAdd = ['Resource 1'];
            vm.currentAgentResources = ['Resource 1'];

            vm.agentToAddResource = angular.copy(agent);
        };

        vm.addInputResource = function () {
            vm.resourcesToAdd.push('Resource' + vm.resourcesToAdd.length);
            vm.currentAgentResources.push('Resource' + vm.resourcesToAdd.length);
        };

        vm.removeLastResource = function () {
            vm.resourcesToAdd.pop();
            vm.currentAgentResources.pop();

        };
        
        vm.delete = function (id) {
            agentService.delete(id);
        };

        vm.changeAgentStatus = function (index, currentStatus) {
            if (!currentStatus) {
                var newAgent = vm.currentAgents[index];
                newAgent.isEnabled = true;
                agentService.update(newAgent);
            }

            if (currentStatus) {
                var newAgent = vm.currentAgents[index];
                newAgent.isEnabled = false;
                agentService.update(newAgent);
            }
        };

        // vm.getAllAgents = function () {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         agentsService.getAllAgents(token)
        //             .then(function (res) {
        //                 // success
        //                 if (vm.currentAgents.length == res.length) {
        //                     //Check agents for change
        //                     var responseIsChanged = agentsService.checkAgentsForChange(vm.currentAgents, res);
        //                 } else {
        //                     vm.currentAgents = res;
        //                 }
        //                 if (responseIsChanged) {
        //                     vm.currentAgents = res;
        //                 }
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //             .then(function (res) {
        //                 var token = res.access_token;
        //                 agentsService.getAllAgents(token)
        //                     .then(function (res) {
        //                         // success
        //                         if (vm.currentAgents.length == res.length) {
        //                             //Check agents for change
        //                             var responseIsChanged = agentsService.checkAgentsForChange(vm.currentAgents, res);
        //                         } else {
        //                             vm.currentAgents = res;
        //                         }
        //                         if (responseIsChanged) {
        //                             vm.currentAgents = res;
        //                         }
        //                     }, function (err) {
        //                         console.log(err);
        //                     })
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     }
        // };
        //
        // vm.enableAgent = function (agentID) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         agentsService.enableAgent(agentID, token)
        //             .then(function (res) {
        //                 console.log(res);
        //                 vm.getAllAgents();
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //             .then(function (res) {
        //                 var token = res.access_token;
        //                 agentsService.enableAgent(agentID, token)
        //                     .then(function (res) {
        //                         console.log(res);
        //                         vm.getAllAgents();
        //                     }, function (err) {
        //                         console.log(err);
        //                     })
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     }
        // };
        //
        // vm.disableAgent = function (agentID) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         agentsService.disableAgent(agentID, token)
        //             .then(function (res) {
        //                 console.log(res);
        //                 vm.getAllAgents();
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //             .then(function (res) {
        //                 var token = res.access_token;
        //                 agentsService.disableAgent(agentID, token)
        //                     .then(function (res) {
        //                         console.log(res);
        //                         vm.getAllAgents();
        //                     }, function (err) {
        //                         console.log(err);
        //                     })
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     }
        // };
        //
        // vm.updateAgent = function () {
        //     vm.agentToAddResource.Resources = vm.currentAgentResources;
        //
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         agentsService.updateAgent(vm.agentToAddResource.ID, vm.agentToAddResource, token)
        //             .then(function (res) {
        //                 console.log(res);
        //                 vm.getAllAgents();
        //
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //             .then(function (res) {
        //                 var token = res.access_token;
        //                 agentsService.updateAgent(vm.agentToAddResource.ID, vm.agentToAddResource, token)
        //                     .then(function (res) {
        //                         console.log(res);
        //                         vm.getAllAgents();
        //                     }, function (err) {
        //                         console.log(err);
        //                     })
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     }
        // };
        //
        // vm.deleteAgent = function (agentID) {
        //     var tokenIsValid = authDataService.checkTokenExpiration();
        //     if (tokenIsValid) {
        //         var token = window.localStorage.getItem("accessToken");
        //         agentsService.deleteAgent(agentID, token)
        //             .then(function (res) {
        //                 console.log(res);
        //                 vm.getAllAgents();
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     } else {
        //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
        //         authDataService.getNewToken(currentRefreshToken)
        //             .then(function (res) {
        //                 var token = res.access_token;
        //                 agentsService.deleteAgent(agentID, token)
        //                     .then(function (res) {
        //                         console.log(res);
        //                         vm.getAllAgents();
        //                     }, function (err) {
        //                         console.log(err);
        //                     })
        //             }, function (err) {
        //                 console.log(err);
        //             })
        //     }
        // };
        //
        // vm.changeAgentStatus = function (index, currentStatus) {
        //     var currentAgentId = vm.currentAgents[index].id;
        //
        //     if (currentStatus == 'Disabled') {
        //         //vm.currentAgents[index].configState = 'Enabled';
        //         agentService.changeAgentStatus(currentAgentId, 'Enabled');
        //         // toaster.pop('success', "Notification", "Agent " + vm.currentAgents[index].hostName + "-" + vm.currentAgents[index].id.substr(0,8) + " enabled!");
        //     }
        //
        //     if (currentStatus == 'Enabled') {
        //         //vm.currentAgents[index].configState = 'Disabled';
        //         agentService.changeAgentStatus(currentAgentId, 'Disabled');
        //         // toaster.pop('success', "Notification", "Agent " + vm.currentAgents[index].hostName + "-" + vm.currentAgents[index].id.substr(0,8) + " disabled!");
        //     }
        // };

        // $scope.$on('updatewsAgent', function(event, args) {
        //     var data = args.object;
        //     console.log(data);
        // });
        //
        // $scope.$on('updateAgents', function(event, args) {
        //     var data = args.object;
        //     vm.currentAgents = data;
        //     console.log(data);
        // });

        //Reloads the data at a given interval. Parameter is false, because it is not the initial loading
        var intervalAgents = $interval(function () {
            //vm.getAllAgents();
        }, 4000);

        $scope.$on('$destroy', function () {
            $interval.cancel(intervalAgents);
            intervalAgents = undefined;
        });
        //vm.getAllAgents();
    });

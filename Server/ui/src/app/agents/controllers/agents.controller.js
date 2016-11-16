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
    .module('hawk.agentsManagement')
    // add DTOptionsBuilder, DTColumnDefBuilder to func
    .controller('AgentsController', function($rootScope, $scope, $interval, $window, $timeout, agentService, websocketReceiverService, authDataService, viewModel) {
        var vm = this;
        vm.agentToDelete = {};

        vm.currentAgentResources = [];

        vm.defaultText = {
            agents: "Agents",
            tableHeaders: {
                action: "Actions",
                agentName: "Name",
                path: "Sandbox",
                os: "OS",
                ip: "IP Address",
                status: "Status",
                freeSpace: "Free Space",
                resources: "Resources",
                environment: "Environments"
            },
            submit: "Submit",
            breadCrumb: "Agents",
            deleteModalHeader: "Delete Agent",
            deleteModalConfirm: "Are you sure you want to delete Agent: ",
            addResourceModalHeader: "Add resources to agent: ",
            resourcePlaceHolder: "Resource",
            delete: "DELETE",
            cancel: "CANCEL",
            add: "Add all"
        };

        vm.popOverOptions = {
            popOverTitles: {
                agentDesc: 'Agents are the workers that execute Jobs and their Tasks',
                agent: 'This is the host\'s hostname + the first few numbers of its ID',
                status: 'Idle - the Agent is ready to accept Jobs. \n\n Running - the Agent is currently executing a Job. Further Jobs cannot be assigned to it until it completes the Job.',
                sandbox: 'The install location of the Agent on the server',
                os: 'The Operating System the Agent is running on',
                ipAddress: 'The Agent\'s internal IP address',
                resources: 'Resources are used to assign Jobs to Agents',
                actions: 'Delete - deletes the Agent. \n\n Edit Resources - allows you to add or remove Resources to that Agent. \n\n Disable - disables the Agent, meaning it will no longer receive Jobs'
            },
            placements: {
                top: 'top'
            },
            triggers: {
                click: 'click'
            }
        };

        vm.status = {
            enable: "enable",
            disable: "disable",
            delete: "delete",
            resource: "Edit Resources"
        };

        vm.currentAgents = [];

        vm.windowWidth = $window.innerWidth;

        vm.currentAgents = angular.copy(viewModel.allAgents);

        $window.onresize = function(event) {
            $timeout(function() {
                vm.windowWidth = $window.innerWidth;
                $scope.$apply();
            });
        };

        $scope.$watch(function() {
            return viewModel.allAgents
        }, function(newVal, oldVal) {
            vm.currentAgents = angular.copy(viewModel.allAgents);
            vm.currentAgents.forEach(function(currentAgent, agentIndex, agentArray) {
                if (currentAgent.isRunning == false) {
                    vm.currentAgents[agentIndex].status = "Idle";
                } else if (currentAgent.isRunning == true) {
                    vm.currentAgents[agentIndex].status = "Running";
                }
            });
            console.log(vm.currentAgents);
        }, true);

        vm.setAgentToDelete = function(agent) {
            vm.agentToDelete = agent;
        };

        vm.setAgentToDisplayDetails = function(agent) {
            vm.agentToDisplayDetails = agent;
        };

        vm.setAgentToAddResource = function(agent) {
            vm.currentAgentResources = [];
            agent.resources.forEach(function(currentResource, index, array) {
                vm.currentAgentResources.push(currentResource);
            });

            vm.agentToAddResource = angular.copy(agent);
        };

        vm.setAgentToEdit = function(index) {
            vm.currentAgentResources = [];
            vm.agentToEdit = vm.currentAgents[index];
            vm.agentIndex = index;
            vm.agentToAddResource = angular.copy(vm.currentAgents[index]);

            vm.agentToEdit.resources.forEach(function(currentResource, index, array) {
                vm.currentAgentResources.push(currentResource);
            });
        };

        vm.addResource = function() {
            vm.currentAgentResources.push('');
        };

        vm.removeResource = function(index) {
            vm.currentAgentResources.splice(index, 1);
        };

        vm.close = function() {
            vm.currentAgentResources = [];
            vm.agentToEdit = {};
            vm.agentIndex = 0;
            vm.agentToDelete = {};
            vm.agentToAddResource = {};
            vm.agentToDisplayDetails = {};
        };

        vm.growTextArea = function(element) {
            element.style.height = "5px";
            element.style.height = (element.scrollHeight)+"px";
        };

        vm.addInputResource = function() {
            vm.agentToAddResource.resources = vm.currentAgentResources;
            agentService.update(vm.agentToAddResource)
        };

        vm.submitAgent = function() {
            vm.agentToAddResource = vm.currentAgents[vm.agentIndex];
            vm.agentToAddResource.resources = vm.currentAgentResources;
            agentService.update(vm.agentToAddResource)
        };

        vm.removeLastResource = function() {
            vm.currentAgentResources.pop();
        };

        vm.delete = function(id) {
            agentService.deleteAgent(id);
        };

        vm.addResourceInput = function() {
            vm.currentAgentResources.push("Resource " + (vm.currentAgentResources.length + 1));
        };

        vm.discardResources = function() {
            vm.currentAgentResources = vm.agentToAddResource.resources;
        };

        vm.changeAgentStatus = function(agent) {
            var newAgent;
            if (agent.isEnabled == false) {
                newAgent = JSON.parse(JSON.stringify(agent));
                newAgent.isEnabled = true;
                agentService.update(newAgent);
            } else {
                newAgent = JSON.parse(JSON.stringify(agent));
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

        //vm.getAllAgents();
    });

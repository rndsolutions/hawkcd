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
    .controller('AgentsController', function($rootScope, $scope, $interval, $window, $timeout, agentService, loggerService, websocketReceiverService, authDataService, viewModel) {
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
                status: "State",
                availability: "Availability",
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
                availability: 'Enabled - the Agent is enabled and ready to accept Jobs. \n\n Disabled - the Agent is disabled and it will not accept any Jobs.',
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
                // if (currentAgent.isEnabled == false) {
                //     vm.currentAgents[agentIndex].status = "Disabled";
                // } else 
                if (currentAgent.isRunning == false) {
                    vm.currentAgents[agentIndex].status = "Idle";
                } else if (currentAgent.isRunning == true) {
                    vm.currentAgents[agentIndex].status = "Running";
                }

                if (currentAgent.isEnabled == false) {
                    vm.currentAgents[agentIndex].availability = 'Disabled';
                } else if(currentAgent.isEnabled == true){
                    vm.currentAgents[agentIndex].availability = 'Enabled';
                }
            });
            loggerService.log('Agents watcher: ');
            loggerService.log(vm.currentAgents);
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

        vm.delete = function(agent) {
            agentService.deleteAgent(agent);
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
    });

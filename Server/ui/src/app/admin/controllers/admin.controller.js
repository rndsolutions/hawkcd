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
    .controller('AdminController',
        function($state, $interval, $scope, $filter, $window, $timeout, DTOptionsBuilder, DTColumnDefBuilder, accountService, adminService, pipeConfigService, profileService, adminGroupService, filterUsers, authDataService, viewModel, $rootScope, adminMaterialService) {
            var vm = this;

            vm.breadCrumb = {
                admin: "Admin"
            };

            vm.defaultText = {
                pageHeader: 'Administration',
                breadCrumb: 'Admin',
                userGroup: 'User Groups',
                user: 'Users',
                group: 'Pipeline Groups',
                server: 'Environments',
                materials: 'Materials',
                tableHeaders: {
                    action: 'Actions',
                    user: 'Username',
                    IDP: 'IDP',
                    groups: 'Groups',
                    type: 'Type',
                    email: 'Email',
                    permissions: 'Permissions'
                },
                status: {
                    enable: 'ENABLE',
                    disable: 'DISABLE',
                    delete: 'DELETE'
                },
                addNewPipelineGroup: {
                    newGroup: 'Add New Pipeline Group',
                    groupName: 'Group Name',
                    input: 'Name of the new group',
                    submit: 'Submit',
                    cancel: 'Cancel'
                },
                addNewMaterial: {
                    newMaterial: 'Add New Material',
                    input: 'Name of the new group',
                    submit: 'Add Material',
                    cancel: 'Cancel',
                    gitUserName: 'Enter Git username',
                    gitPassword: 'Enter Git password'
                },
                editMaterial: {
                    header: 'Edit Material',
                    submit: 'Edit Material',
                    cancel: 'Cancel',
                    gitUserName: 'Enter Git username',
                    gitPassword: 'Enter Git password'
                },
                deleteMaterial: {
                    header: 'Delete Material',
                    confirm: 'Are you sure you want to delete Material: ',
                    delete: 'Confirm',
                    cancel: 'Cancel'
                },
                addNewUserGroup: {
                    newGroup: 'Add New User Group',
                    groupName: 'Group Name',
                    input: 'Name of the new group',
                    submit: 'Submit',
                    cancel: 'Cancel'
                },
                deletePipelineModal: {
                    header: 'Delete Pipeline',
                    confirm: 'Are you sure you want to delete Pipeline: ',
                    delete: 'Confirm',
                    cancel: 'Cancel'
                },
                deleteUserModal: {
                    header: 'Delete User',
                    confirm: 'Are you sure you want to delete User: ',
                    delete: 'Confirm',
                    cancel: 'Cancel'
                },
                editUserInfo: {
                    header: 'Delete User',
                    changeButton: 'Change Email',
                    resetPassword: 'Reset Password',
                    save: 'Save',
                    delete: 'Confirm',
                    cancel: 'Cancel'
                },
                deleteGroupModal: {
                    header: 'Delete Pipeline Group',
                    confirm: 'Are you sure you want to delete Group: '
                },
                unassignPipelineModal: {
                    header: 'Unassign Pipeline',
                    confirm: 'Are you sure you want to unassign Pipeline: '
                },
                buttonTitles:{
                  ok:'Ok',
                  cancel:'Cancel'
                }
            };

            vm.popOverOptions = {
                userTitles: {
                    username: 'Username',
                    dateRegistered: 'Registered On',
                    permissions: 'Permissions',
                    action: 'Action',
                    controls: 'Controls',
                    edit: 'Edit'
                },
                userInfo: {
                    username: 'Name of the user.',
                    dateRegistered: 'Date registered on.',
                    permissions: 'Permissions of the user.',
                    action: 'Enable/Disable Users. Disabled users cannot login and use HawkCD.',
                    edit: 'Reset user password. Change user email.'
                },
                tableTitles: {
                    name: 'Name',
                    type: 'Type',
                    url: 'URL',
                    branch: 'Branch',
                    actions: 'Actions'
                },
                popOverTitles: {
                    materialName: 'The name of the Material. Once the Material is created the name cannot be changed.',
                    materialType: 'Type of the material',
                    materialUrl: 'URL to your GitHub project repository (the one you would use to clone a repository). This option requires that the URL is valid and ends with ".git"',
                    materialBranch: 'The git branch to track from (e.g. master). If nothing is entered it will default to "master".',
                    materialCredentials: 'Your GitHub credentials. If the git repository is private, you will need to provide your GitHub credentials, so that HawkCD can access your repository.',
                    materialPoll: 'If checked, HawkCD will poll your Material for changes automatically.',
                    actions: 'Actions that can be performed on the material',
                    pipelineGroup: 'Pipeline Group can be thought as a container for Pipelines. Each Pipeline belongs to a group (unless it\'s unassigned).',
                    material: 'A Material represents a branch of a Git repository. It is used to define which part of your project(s) the Pipeline will work with. For every Pipeline there must be at least one Material defined.',
                    userGroup: 'User Groups aim to make managing the Permissions of multiple Users at the same time easier, because both Users and User Groups have Permissions.',
                    user: 'Users are entities that can have individual Permissions.',
                    permissionScope: 'Represents the object level at which specific permissions can be applied.',
                    permissionType: 'Define permissions - what a user can do in specific Permission Scope.',
                    permissionEntity: 'The specific object(s) for which the Permissions are applied.'
                },
                placements: {
                    top: 'top'
                },
                triggers: {
                    click: 'click'
                }
            }

            vm.newPipelineGroup = {
                name: '',
                pipelines: []
            };

            vm.defalultGroupsText = {
                pipeline: 'Pipeline groups'
            };

            vm.currentActiveScreen = '';

            vm.state = $state;
            console.log($state.current);

            vm.currentPipelineGroups = [];

            vm.currentMaterials = [];

            vm.toggleAssignedPipeline = null;

            vm.togglePipeline = null;

            vm.pipelineGroupToAssign = {};

            vm.pipelineToAssign = null;

            vm.pipelineToUnassign = null;

            vm.unassignedPipelines = [];

            vm.userGroupToManage = {};

            vm.truefalse = {};

            vm.newUserGroup = {};

            vm.selectedUserGroup = null;

            vm.selectedUser = null;

            vm.userGroups = [];

            vm.users = angular.copy(viewModel.users);

            vm.newUser = {};

            vm.allPipelines = [];

            vm.data = {
                model: null,
                scopeOptions: [{
                    value: 'PIPELINE',
                    name: 'Pipeline'
                }, {
                    value: 'PIPELINE_GROUP',
                    name: 'Pipeline Group'
                }, {
                    value: 'SERVER',
                    name: 'Server'
                }],
                typeOptions: [{
                    value: 'NONE',
                    name: 'None'
                }, {
                    value: 'ADMIN',
                    name: 'Admin'
                }, {
                    value: 'OPERATOR',
                    name: 'Operator'
                }, {
                    value: 'VIEWER',
                    name: 'Viewer'
                }]
            };

            vm.materialType = 'git';
            vm.formData = {};

            vm.windowWidth = $window.innerWidth;

            $window.onresize = function(event) {
                $timeout(function() {
                    vm.windowWidth = $window.innerWidth;
                    $scope.$apply();
                    // debugger;
                });
            };

            vm.addMaterial = function(formData) {
                var material = {};
                vm.formData = formData;
                material = {
                    "name": vm.formData.material.git.name,
                    "type": 'GIT',
                    "repositoryUrl": vm.formData.material.git.url,
                    "isPollingForChanges": vm.formData.material.git.poll,
                    "destination": vm.formData.material.git.name,
                    "branch": vm.formData.material.git.branch || 'master'
                };
                if (formData.material.git.credentials) {
                    material.username = formData.material.git.username;
                    material.password = formData.material.git.password;
                }
                adminMaterialService.addGitMaterialDefinition(material);

                vm.materialType = 'hidden';
                vm.formData = {};
                vm.closeModal();
            };

            vm.editMaterial = function(formData) {
                if (!formData.material.credentials) {
                    formData.material.username = undefined;
                    formData.material.password = undefined;
                }
                formData.material.destination = formData.material.destination !== formData.material.name ? formData.material.name : formData.material.destination;
                adminMaterialService.updateGitMaterialDefinition(formData.material);
                vm.closeEditModal();
            };

            vm.closeEditModal = function() {
                vm.formData = {};
                vm.materialType = 'git';
            }

            $scope.$watch(function() {
                return viewModel.userGroups
            }, function(newVal, oldVal) {
                vm.userGroups = angular.copy(viewModel.userGroups);
            }, true);

            $scope.$watch(function() {
                return viewModel.allPipelines
            }, function(newVal, oldVal) {
                vm.allPipelines = angular.copy(viewModel.allPipelines);
            }, true);

            $scope.$watch(function() {
                return viewModel.users
            }, function(newVal, oldVal) {
                vm.users = angular.copy(viewModel.users);
                if (vm.users != null) {
                    vm.users.forEach(function(currentUser, userIndex, userArray) {
                        if (currentUser.permissions != null) {
                            currentUser.permissions.forEach(function(currentPermission, userIndex, userArray) {
                                if (currentPermission.permissionScope == 'PIPELINE') {
                                    vm.allPipelines.forEach(function(currentPipeline, pipelineIndex, pipelineArray) {
                                        if (currentPipeline.id == currentPermission.permittedEntityId) {
                                            currentPermission.permittedEntityName = currentPipeline.name;
                                        }
                                    });
                                } else if (currentPermission.permissionScope == 'PIPELINE_GROUP') {
                                    vm.currentPipelineGroups.forEach(function(currentPipelineGroup, pipelineGroupIndex, pipelineGroupArray) {
                                        if (currentPipelineGroup.id == currentPermission.permittedEntityId) {
                                            currentPermission.permittedEntityName = currentPipelineGroup.name;
                                        }
                                    });
                                } else if (currentPermission.permissionScope == 'SERVER') {
                                    currentPermission.permittedEntityName = 'SERVER';
                                }
                            });
                        }
                    });
                }
            }, true);

            vm.isUserGroupOpen = [];

            vm.isPipeGroupOpen = [];

            vm.openAccordion = function(array, index) {
                if (array[index] != true && array[index] != false) {
                    array[index] = true;
                } else {
                    array[index] = !array[index];
                }
            };

            vm.selectUserGroup = function(index) {
                var userToAdd = null;
                var isFound = false;
                vm.selectedUserGroup = angular.copy(vm.userGroups[index]);
                vm.selectedUserGroup.newUsers = [];
                // vm.selectedUserGroup.users.forEach(function (currentUser, userIndex, userArray) {
                //     userToAdd = angular.copy(currentUser);
                //     userToAdd.isAssigned = true;
                //     vm.selectedUserGroup.newUsers.push(userToAdd);
                //     userToAdd = null;
                // });
                vm.users.forEach(function(currentUser, userIndex, userArray) {
                    isFound = false;
                    vm.selectedUserGroup.users.forEach(function(currentUserFromGroup, userFromGroupIndex, userFromGroupArray) {
                        if (currentUserFromGroup.id == currentUser.id) {
                            isFound = true;
                            userToAdd = angular.copy(currentUser);
                            userToAdd.isAssigned = true;
                            vm.selectedUserGroup.newUsers.push(userToAdd);
                            userToAdd = null;
                        }
                    });
                    if (!isFound) {
                        userToAdd = angular.copy(currentUser);
                        vm.selectedUserGroup.newUsers.push(userToAdd);
                        userToAdd = null;
                    }
                });
                vm.search();

            };

            vm.toggleClicked = function(user) {
                user.isClicked = true;
            };

            vm.toggleUser = function(user) {
                var userToUpdate = angular.copy(user);
                userToUpdate.isEnabled = !userToUpdate.isEnabled;
                adminService.updateUser(userToUpdate);
            };

            vm.selectUser = function(index) {
                vm.selectedUser = angular.copy(vm.users[index]);
                vm.userDTO = angular.copy(vm.users[index]);
            };

            vm.selectPipeline = function(pipeline, index) {
                vm.togglePipeline = index;
            };

            vm.addUserGroup = function() {
                adminService.addUserGroup(vm.newUserGroup);
            };

            vm.selectAssignedPipelineToAssign = function(pipeline, index) {
                vm.toggleAssignedPipeline = index;
                vm.toggleUnassignedPipeline = null;
                vm.pipelineToAssign = pipeline;
            };

            vm.selectUnassignedPipelineToAssign = function(pipeline, index) {
                vm.toggleUnassignedPipeline = index;
                vm.toggleAssignedPipeline = null;
                vm.pipelineToAssign = pipeline;
            };

            vm.selectAssignedPipelineToUnassign = function(pipeline) {
                vm.pipelineToUnassign = angular.copy(pipeline);
            };

            vm.addUser = function() {
                adminService.addUser(vm.newUser);
            };

            vm.userDTO = {};
            vm.submitUserSettingsForm = function(updatedUser, userDTO, form) {
                var changeEmailFlag = ((form.userEmail.$viewValue !== updatedUser.email) &&
                    (form.userNewPassword.$viewValue === undefined || form.userNewPassword.$viewValue.length === 0)) ? true : false;
                var changePasswordFlag = ((form.userEmail.$viewValue === updatedUser.email) &&
                    (userDTO.newPassword !== undefined && userDTO.newPassword.length !== 0)) ? true : false;
                var changeBothFlag = ((form.userEmail.$viewValue !== updatedUser.email) &&
                    (userDTO.newPassword !== undefined && userDTO.newPassword.length !== 0)) ? true : false;
                if (changeEmailFlag) {
                    vm.changeUserEmail(updatedUser, userDTO, form);
                } else if (changePasswordFlag) {
                    vm.changeUserPassword(updatedUser, userDTO, form);
                } else if (changeBothFlag) {
                    var innerForm = angular.copy(form);
                    var innerDTO = angular.copy(userDTO);
                    var innerUserDTO = angular.copy(updatedUser);
                    vm.changeUserEmail(innerUserDTO, innerDTO, innerForm);
                    vm.changeUserPassword(innerUserDTO, innerDTO, innerForm);
                }
            }

            vm.changeUserEmail = function(updatedUser, userDTO, form) {
                updatedUser.email = userDTO.email;
                adminService.updateUser(updatedUser);
                vm.closeUserSettingsModal(form);
            }

            vm.changeUserPassword = function(updatedUser, userDTO, form) {
                updatedUser.password = userDTO.newPassword;
                adminService.resetUserPassword(updatedUser);
                vm.closeUserSettingsModal(form);
            }

            vm.closeUserSettingsModal = function(form) {
                form.userEmail.$viewValue = undefined;
                form.userNewPassword.$viewValue = undefined;
                form.confirmNewPassword.$viewValue = undefined;
                form.$setPristine();
                form.$setUntouched();
                form.userEmail.$render();
                form.userNewPassword.$render();
                form.confirmNewPassword.$render();

                vm.userDTO = {};
            }

            vm.removeUser = function() {
                adminService.deleteUser(vm.selectedUser.id);
            };

            vm.assignPipeline = function(pipeline) {
                var pipelineGroup = vm.pipelineGroupToAssign;
                debugger;
                pipeConfigService.assignPipelineDefinition(pipeline.id, pipelineGroup.id, pipelineGroup.name);
            };

            vm.unassignPipeline = function() {
                pipeConfigService.unassignPipelineDefinition(vm.pipelineToUnassign.id);
            };

            vm.assignUsers = function() {
                vm.selectedUserGroup.users = angular.copy(vm.selectedUserGroup.newUsers);
                // vm.selectedUserGroup.users.forEach(function (currentUser, userIndex, userArray) {
                //     if(currentUser.isAssigned){
                //         adminService.assignUser(angular.copy(currentUser), vm.selectedUserGroup);
                //     }
                // });
                vm.selectedUserGroup.users.forEach(function(currentUser, userIndex, userArray) {
                    var isFound = false;
                    if (currentUser.isClicked) {
                        if (currentUser.isAssigned) {
                            currentUser.userGroupIds.forEach(function(currentUserGroupId, userGroupIdIndex, userGroupIdArray) {
                                if (currentUserGroupId == vm.selectedUserGroup.id) {
                                    isFound = true;
                                }
                            });
                            if (!isFound) {
                                var updatedUser = angular.copy(currentUser);
                                updatedUser.userGroupIds.push(vm.selectedUserGroup.id);
                                adminService.assignUser(updatedUser, vm.selectedUserGroup);
                            }
                            isFound = false;
                        } else {
                            currentUser.userGroupIds.forEach(function(currentUserGroupId, userGroupIdIndex, userGroupIdArray) {
                                if (currentUserGroupId == vm.selectedUserGroup.id) {
                                    isFound = true;
                                }
                            });
                            if (isFound) {
                                var index = currentUser.userGroupIds.indexOf(vm.selectedUserGroup.id);
                                var updatedUser = angular.copy(currentUser);
                                updatedUser.userGroupIds.splice(index, 1);
                                adminService.unassignUser(updatedUser, vm.selectedUserGroup);
                            }
                            isFound = false;
                        }
                    }
                });
                vm.close();
            };

            vm.setPipelineGroupToAssign = function(pipelineGroup) {
                vm.pipelineGroupToAssign = pipelineGroup;
            };

            vm.clearSelection = function() {
                vm.togglePipeline = null;
                vm.toggleAssignedPipeline = null;
                vm.toggleUnassignedPipeline = null;
            };

            vm.close = function() {
                vm.pipelineGroupToAssign = {};
                vm.pipelineToAssign = null;
                vm.pipelineToUnassign = null;
                vm.toggleAssignedPipeline = null;
                vm.toggleUnassignedPipeline = null;
                vm.selectedUserGroup = null;
                vm.selectedUser = null;
                vm.newUser.email = '';
                vm.newUser.password = '';
                vm.newUser.confirmPassword = '';
                vm.newPipelineGroup = null;
                vm.newUserGroup = {};
                vm.clearSelection();
            };

            vm.closeModal = function() {
                vm.formData = {};
                vm.materialType = 'git';
            }

            vm.closeModalMaterials = function(internalViewModel, materialForm) {
                var reSetter = '';

                if (!materialForm.$pristine || materialForm.$invalid) {
                    materialForm.gitUrl.$setViewValue(reSetter);
                    materialForm.gitUrl.$render();
                    materialForm.materialName.$setViewValue(reSetter);
                    materialForm.materialName.$render();
                    materialForm.$setPristine();
                    materialForm.$setUntouched();
                    internalViewModel.formData = {};
                }

                internalViewModel.materialType = 'git';
            };


            vm.currentPipelineGroups = angular.copy(viewModel.allPipelineGroups);

            vm.sortingOrder = 'email';
            vm.pageSizes = [5, 10, 25, 50];
            vm.reverse = false;
            vm.filteredItems = [];
            vm.groupedItems = [];
            vm.itemsPerPage = 10;
            vm.pagedItems = [];
            vm.currentPage = 0;
            vm.query = "";
            vm.items = {};

            $(document).ready(function() {
                $('#userGroups').DataTable();
            });

            vm.getEntities = function() {

            };

            vm.addPermission = function() {
                vm.newPermission = {
                    "permissionScope": "SERVER",
                    "permittedEntityId": "SERVER",
                    "permissionType": "ADMIN"
                };
                vm.selectedUser.permissions.push(vm.newPermission);
            };

            vm.addGroupPermission = function() {
                vm.newPermission = {
                    "permissionScope": "SERVER",
                    "permittedEntityId": "SERVER",
                    "permissionType": "ADMIN"
                };
                vm.selectedUserGroup.permissions.push(vm.newPermission);
            };

            vm.removePermission = function(index) {
                vm.selectedUser.permissions.splice(index, 1);
            };

            vm.removeGroupPermission = function(index) {
                vm.selectedUserGroup.permissions.splice(index, 1);
            };

            vm.updateUserPermission = function() {
                adminService.updateUser(vm.selectedUser);
                vm.closePermissionModal();
            };

            vm.updateUserGroupPermission = function() {
                adminService.updateUserGroupDTO(vm.selectedUserGroup);
                vm.closeGroupPermissionModal();
            };

            vm.closePermissionModal = function() {
                var table = $('#userPermissionTable');
                // for(var row in table[0].rows) {
                //     debugger;
                //     if(row[0].id == "extraRow"){
                //         row[0].remove();
                //     }
                // }

                $('.extraRow').each(function() {
                    $(this).remove();
                });
                vm.selectedUser = null;
            };

            vm.closeGroupPermissionModal = function() {
                var table = $('#userGroupPermissionTable');
                // for(var row in table[0].rows) {
                //     debugger;
                //     if(row[0].id == "extraRow"){
                //         row[0].remove();
                //     }
                // }

                $('.extraRow').each(function() {
                    $(this).remove();
                });
                vm.selectedUserGroup = null;
            };

            $(window).load(function() {
                $('#user-checkbox').bootstrapSwitch();
            });

            $('#user-checkbox').on('switchChange.bootstrapSwitch', function(event, state) {
                console.log(state);
            });

            vm.range = function(start, end) {
                var ret = [];
                if (!end) {
                    end = start;
                    start = 0;
                }
                for (var i = start; i < end; i++) {
                    ret.push(i);
                }
                return ret;
            };

            vm.search = function() {
                vm.items = vm.selectedUserGroup.newUsers;
                vm.filteredItems = filterUsers.search(vm.items, vm.query);
                vm.currentPage = 0;
                vm.pagedItems = filterUsers.groupToPages(vm.filteredItems, vm.itemsPerPage);
            };

            vm.prevPage = function() {
                if (vm.currentPage > 0) {
                    vm.currentPage--;
                }
            };

            vm.nextPage = function() {
                if (vm.currentPage < vm.pagedItems.length - 1) {
                    vm.currentPage++;
                }
            };

            vm.setPage = function(index) {
                vm.currentPage = index;
            };

            // vm.deleteItem = function (idx) {
            //     var itemToDelete = vm.pagedItems[vm.currentPage][idx];
            //     var idxInItems = vm.items.indexOf(itemToDelete);
            //     vm.items.splice(idxInItems,1);
            //     vm.search();
            //
            //     return false;
            // };

            $scope.$watch(function() {
                return viewModel.unassignedPipelines
            }, function(newVal, oldVal) {
                vm.unassignedPipelines = angular.copy(viewModel.unassignedPipelines);
            }, true);

            $scope.$watch(function() {
                return viewModel.allPipelineGroups
            }, function(newVal, oldVal) {
                vm.currentPipelineGroups = angular.copy(viewModel.allPipelineGroups);
                vm.currentPipelineGroups.sort(function(a, b) {
                    return a.name - b.name;
                });
                console.log(vm.currentPipelineGroups);
            }, true);

            $scope.$watch(function() {
                return viewModel.allMaterialDefinitions
            }, function(newVal, oldVal) {
                vm.currentMaterials = angular.copy(viewModel.allMaterialDefinitions);
                console.log(vm.currentMaterials);
            }, true);

            vm.addNewPipelineGroup = function() {
                adminGroupService.addNewPipelineGroup(vm.newPipelineGroup);
            };

            vm.setPipelineForDelete = function(pipelineName) {
                vm.pipelineToDeleteName = pipelineName;
            };

            vm.setMaterialForDelete = function(materialName, materialId) {
                vm.materialToDeleteName = materialName;
                vm.materialToDeleteId = materialId;
            };

            vm.setUserGroupToDelete = function(userGroup) {
                vm.userGroupToDelete = userGroup;
            };

            vm.setPipelineGroupToDelete = function(pipelineGroup) {
                vm.pipelineGroupToDelete = pipelineGroup;
            };

            vm.deleteUserGroup = function() {
                adminService.deleteUserGroup(vm.userGroupToDelete.id);
            };

            vm.deletePipelineGroup = function() {
                adminGroupService.deletePipelineGroup(vm.pipelineGroupToDelete.id);
            };

            vm.deleteMaterial = function(materialId) {
                adminMaterialService.deleteMaterialDefinition(materialId);
            };

            vm.setMaterialForEdit = function(material) {
                vm.materialType = 'hidden';
                vm.formData.material = angular.copy(material);
                if (material.username) {
                    vm.formData.material.credentials = true;
                }
            };

            vm.setUserGroupToManage = function(userGroup) {
                vm.userGroupToManage = userGroup;
            };

            // function getAllUsers() {
            //     var tokenIsValid = authDataService.checkTokenExpiration();
            //     if (tokenIsValid) {
            //        var token = window.localStorage.getItem("accessToken");
            //        adminService.getAllUsers(token)
            //         .then(function (res) {
            //             vm.allUsers = res;
            //             console.log(res);
            //         }, function (err) {
            //             console.log(err);
            //         })
            //     } else {
            //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
            //        authDataService.getNewToken(currentRefreshToken)
            //            .then(function (res) {
            //                var token = res.access_token;
            //                adminService.getAllUsers(token)
            //                 .then(function (res) {
            //                     vm.allUsers = res;
            //                     console.log(res);
            //                 }, function (err) {
            //                     console.log(err);
            //                 })
            //            }, function (err) {
            //                console.log(err);
            //            })
            //     }
            // }
            //
            // vm.addUser = function () {
            //     var tokenIsValid = authDataService.checkTokenExpiration();
            //     if (tokenIsValid) {
            //        var token = window.localStorage.getItem("accessToken");
            //        adminService.registerUser(vm.newUser, token)
            //         .then(function (res) {
            //             console.log('User with email ' + vm.newUser.Email + ' successfully created.')
            //             getAllUsers();
            //             vm.newUser = {};
            //             console.log(res);
            //         }, function (err) {
            //             vm.newUser = {};
            //             console.log(err);
            //         })
            //     } else {
            //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
            //        authDataService.getNewToken(currentRefreshToken)
            //            .then(function (res) {
            //                var token = res.access_token;
            //                adminService.registerUser(vm.newUser, token)
            //                 .then(function (res) {
            //                     console.log('User with email ' + vm.newUser.Email + ' successfully created.')
            //                     getAllUsers();
            //                     vm.newUser = {};
            //                     console.log(res);
            //                 }, function (err) {
            //                     vm.newUser = {};
            //                     console.log(err);
            //                 })
            //            }, function (err) {
            //                console.log(err);
            //            })
            //     }
            // }
            //
            // vm.currentUserId = {};
            // vm.user = {};
            //
            // vm.getCurrentUser = function () {
            //     var tokenIsValid = authDataService.checkTokenExpiration();
            //     if (tokenIsValid) {
            //        var token = window.localStorage.getItem("accessToken");
            //        profileService.getUserInfo(token)
            //         .then(function (res) {
            //             vm.currentUserId = res.Id;
            //             console.log(res);
            //         }, function (err) {
            //             console.log(err);
            //         })
            //     } else {
            //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
            //        authDataService.getNewToken(currentRefreshToken)
            //            .then(function (res) {
            //                var token = res.access_token;
            //                profileService.getUserInfo(token)
            //                 .then(function (res) {
            //                     vm.currentUserId = res.Id;
            //                     console.log(res);
            //                 }, function (err) {
            //                     console.log(err);
            //                 })
            //            }, function (err) {
            //                console.log(err);
            //            })
            //     }
            // }
            //
            // vm.getCurrentUser();
            //
            // vm.getUser = function (id) {
            //     var tokenIsValid = authDataService.checkTokenExpiration();
            //     if (tokenIsValid) {
            //        var token = window.localStorage.getItem("accessToken");
            //        adminService.getUser(id, token)
            //         .then(function (res) {
            //             vm.user = res;
            //             console.log(res);
            //         }, function (err) {
            //             console.log(err);
            //         })
            //     } else {
            //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
            //        authDataService.getNewToken(currentRefreshToken)
            //            .then(function (res) {
            //                var token = res.access_token;
            //                adminService.getUser(id, token)
            //                 .then(function (res) {
            //                     vm.user = res;
            //                     console.log(res);
            //                 }, function (err) {
            //                     console.log(err);
            //                 })
            //            }, function (err) {
            //                console.log(err);
            //            })
            //     }
            // }
            //
            // vm.deleteUser = function (id) {
            //     var tokenIsValid = authDataService.checkTokenExpiration();
            //     if (tokenIsValid) {
            //        var token = window.localStorage.getItem("accessToken");
            //        adminService.deleteUser(id, token)
            //         .then(function (res) {
            //             getAllUsers();
            //             console.log(res);
            //         }, function (err) {
            //             console.log(err);
            //         })
            //     } else {
            //        var currentRefreshToken = window.localStorage.getItem("refreshToken");
            //        authDataService.getNewToken(currentRefreshToken)
            //            .then(function (res) {
            //                var token = res.access_token;
            //                adminService.deleteUser(id, token)
            //                 .then(function (res) {
            //                     getAllUsers();
            //                     console.log(res);
            //                 }, function (err) {
            //                     console.log(err);
            //                 })
            //            }, function (err) {
            //                console.log(err);
            //            })
            //     }
            // }
            //
            // getAllUsers();
            //
            //
            // //region Group Management
            //
            // vm.allGroupDefs = [];
            //
            // vm.getAllGroupDefs = function () {
            //     var tokenIsValid = authDataService.checkTokenExpiration();
            //     if (tokenIsValid) {
            //         var token = window.localStorage.getItem("accessToken");
            //         pipeConfig.getAllGroups(token)
            //             .then(function (res) {
            //
            //                 //Result and current are different
            //                 if (res.length != vm.allGroupDefs.length) {
            //                     vm.allGroupDefs = res;
            //                 }
            //                 //Check again for difference
            //                 else {
            //                     //Check if there is a new pipeline group or a new pipeline in existing group
            //                     var areEqual = adminGroupService.checkIfDataIsDifferent(vm.allGroupDefs, res);
            //
            //                     if (!areEqual) {
            //                         vm.allGroupDefs = res;
            //                     }
            //                 }
            //             }, function (err) {
            //                 console.log(err);
            //             })
            //     } else {
            //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
            //         authDataService.getNewToken(currentRefreshToken)
            //             .then(function (res) {
            //                 var token = res.access_token;
            //                 pipeConfig.getAllGroups(token)
            //                     .then(function (res) {
            //
            //                         //Result and current are different
            //                         if (res.length != vm.allGroupDefs.length) {
            //                             vm.allGroupDefs = res;
            //                         }
            //                         //Check again for difference
            //                         else {
            //                             //Check if there is a new pipeline group or a new pipeline in existing group
            //                             var areEqual = adminGroupService.checkIfDataIsDifferent(vm.allGroupDefs, res);
            //
            //                             if (!areEqual) {
            //                                 vm.allGroupDefs = res;
            //                             }
            //                         }
            //                     }, function (err) {
            //                         console.log(err);
            //                     })
            //             }, function (err) {
            //
            //             })
            //
            //     }
            // };
            //
            // vm.addNewPipelineGroup = function () {
            //     var tokenIsValid = authDataService.checkTokenExpiration();
            //     if (tokenIsValid) {
            //         var token = window.localStorage.getItem("accessToken");
            //         pipeConfig.createGroup(vm.newPipelineGroup, token)
            //             .then(function (res) {
            //                 vm.newPipelineGroup.Name = '';
            //                 vm.getAllGroupDefs();
            //             }, function (err) {})
            //     } else {
            //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
            //         authDataService.getNewToken(currentRefreshToken)
            //             .then(function (res) {
            //                 var token = res.access_token;
            //                 pipeConfig.createGroup(vm.newPipelineGroup, token)
            //                     .then(function (res) {
            //                         vm.newPipelineGroup.Name = '';
            //                         vm.getAllGroupDefs();
            //                     }, function (err) {
            //
            //                     })
            //             }, function (err) {
            //                 console.log(err);
            //             })
            //     }
            // };
            //

            //
            // vm.deleteGroup = function () {
            //     var tokenIsValid = authDataService.checkTokenExpiration();
            //     if (tokenIsValid) {
            //         var token = window.localStorage.getItem("accessToken");
            //         pipeConfig.deleteGroup(vm.groupForDeleteName, token)
            //             .then(function (res) {
            //                 vm.getAllGroupDefs();
            //                 console.log(res);
            //             }, function (err) {
            //                 console.log(err);
            //             })
            //     } else {
            //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
            //         authDataService.getNewToken(currentRefreshToken)
            //             .then(function (res) {
            //                 var token = res.access_token;
            //                 pipeConfig.deleteGroup(vm.groupForDeleteName, token)
            //                     .then(function (res) {
            //                         vm.getAllGroupDefs();
            //                         console.log(res);
            //                     }, function (err) {
            //                         console.log(err);
            //                     })
            //             }, function (err) {
            //                 console.log(err);
            //             })
            //     }
            // };
            //
            // vm.deletePipeline = function () {
            //     var tokenIsValid = authDataService.checkTokenExpiration();
            //     if (tokenIsValid) {
            //         var token = window.localStorage.getItem("accessToken");
            //         pipeConfig.deletePipeline(vm.pipelineToDeleteName, token)
            //             .then(function (res) {
            //                 vm.getAllGroupDefs();
            //                 console.log(res);
            //             }, function (err) {
            //                 console.log(err);
            //             })
            //     } else {
            //         var currentRefreshToken = window.localStorage.getItem("refreshToken");
            //         authDataService.getNewToken(currentRefreshToken)
            //             .then(function (res) {
            //                 var token = res.access_token;
            //                 pipeConfig.deletePipeline(vm.pipelineToDeleteName,token)
            //                     .then(function (res) {
            //                         vm.getAllGroupDefs();
            //                         console.log(res);
            //                     }, function (err) {
            //                         console.log(err);
            //                     })
            //             }, function (err) {
            //                 console.log(err);
            //             })
            //     }
            // };
            //
            //
            // vm.getAllGroupDefs();
            //endregion

            //region User Management
            //DATATABLE options
            vm.dtOptions = DTOptionsBuilder.newOptions().withPaginationType('full_numbers').withDisplayLength(6);

            vm.dtColumnDefs = [
                DTColumnDefBuilder.newColumnDef(0).notSortable(),
                DTColumnDefBuilder.newColumnDef(1),
                DTColumnDefBuilder.newColumnDef(2),
                DTColumnDefBuilder.newColumnDef(3),
                DTColumnDefBuilder.newColumnDef(4),
                DTColumnDefBuilder.newColumnDef(5),
                DTColumnDefBuilder.newColumnDef(6)
            ];

            function deleteRow(id) {
                vm.message = 'You are trying to remove the row with ID: ' + id;
                // Delete some data and call server to make changes...

                // Then reload the data so that DT is refreshed
                vm.dtInstance.reloadData();
            }

            //endregion

            //region Server Management
            vm.serverTab = {
                header: 'LDAP Settings',
                uri: 'URI',
                managerDN: 'Manager DN',
                password: 'Password',
                searchBase: 'Search base',
                searchFilter: 'Search filter',
                submit: 'Submit',
                cancel: 'Cancel'
            };


            //endregion

            // vm.allGroupDefs = [];

            // vm.allGroupDefs = viewModel.allPipelineGroups;

            // $scope.$watch(function () { return viewModel.allPipelineGroups }, function (newVal, oldVal) {
            //     vm.allGroupDefs = viewModel.allPipelineGroups;
            //     console.log(vm.allGroupDefs);
            // });

            //region Packages
            vm.packageTab = {
                header: 'NuGet Repository'
            };

            //endregion

            // var intervallGroups = $interval(function () {
            //     vm.getAllGroupDefs();
            // }, 4000);

            $scope.$on('$destroy', function() {});



        });

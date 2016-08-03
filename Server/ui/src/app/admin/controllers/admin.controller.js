'use strict';

angular
    .module('hawk.adminManagement')
    .controller('AdminController',
        function($state, $interval, $scope, $filter, DTOptionsBuilder, DTColumnDefBuilder, pipeConfig, accountService, adminService, pipeConfigService, profileService, adminGroupService, authDataService, viewModel, $rootScope) {
            var vm = this;


            vm.breadCrumb = {
                admin: "Admin"
            };

            vm.defaultText = {
                pageHeader: 'Administration',
                breadCrumb: 'Admin',
                userGroup: 'User Groups',
                user: 'Users',
                group: 'Groups',
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
                deleteUserModal:{
                    header: 'Delete User',
                    confirm: 'Are you sure you want to delete User: ',
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
                }
            };

            vm.popOverOptions = {
                tableTitles: {
                    name: 'Name',
                    type: 'Type',
                    url: 'URL',
                    branch: 'Branch',
                    actions: 'Actions'
                },
                popOverTitles:{
                  name:'Name of the material',
                  type:'Type of the material',
                  url:'URL location of the material',
                  branch:'Current branch of the material',
                  actions:'Actions that can be performed on the material'
                },
                placements:{
                  top:'top'
                },
                triggers:{
                  click:'click'
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

            vm.users = [];

            vm.newUser = {};

            vm.allPipelines = [];

            vm.data = {
                model: null,
                scopeOptions: [
                    {value: 'PIPELINE', name: 'Pipeline'},
                    {value: 'PIPELINE_GROUP', name: 'Pipeline Group'},
                    {value: 'SERVER', name: 'Server'}
                ],
                typeOptions: [
                    {value: 'NONE', name: 'None'},
                    {value: 'ADMIN', name: 'Admin'},
                    {value: 'OPERATOR', name: 'Operator'},
                    {value: 'VIEWER', name: 'Viewer'}
                ]
            };

            $scope.$watchCollection(function () { return viewModel.userGroups }, function (newVal, oldVal) {
                vm.userGroups = viewModel.userGroups;
            });

            $scope.$watchCollection(function () { return viewModel.allPipelines }, function (newVal, oldVal) {
                vm.allPipelines = viewModel.allPipelines;
            });

            $scope.$watchCollection(function () { return viewModel.users }, function (newVal, oldVal) {
                vm.users = viewModel.users;
                if(vm.users != null){
                    vm.users.forEach(function (currentUser, userIndex, userArray) {
                        if(currentUser.permissions != null){
                            currentUser.permissions.forEach(function (currentPermission, userIndex, userArray) {
                                if(currentPermission.permissionScope == 'PIPELINE'){
                                    vm.allPipelines.forEach(function (currentPipeline, pipelineIndex, pipelineArray) {
                                        if(currentPipeline.id == currentPermission.permittedEntityId){
                                            currentPermission.permittedEntityName = currentPipeline.name;
                                        }
                                    });
                                }
                                else if(currentPermission.permissionScope == 'PIPELINE_GROUP') {
                                    vm.currentPipelineGroups.forEach(function (currentPipelineGroup, pipelineGroupIndex, pipelineGroupArray) {
                                        if(currentPipelineGroup.id == currentPermission.permittedEntityId) {
                                            currentPermission.permittedEntityName = currentPipelineGroup.name;
                                        }
                                    });
                                }
                                else if(currentPermission.permissionScope == 'SERVER') {
                                    currentPermission.permittedEntityName = 'SERVER';
                                }
                            });
                        }
                    });
                }
            });

            vm.selectUserGroup = function (index) {
                vm.selectedUserGroup = vm.userGroups[index];
            };

            vm.selectUser = function (index) {
                vm.selectedUser = angular.copy(vm.users[index]);
            };

            vm.selectPipeline = function(pipeline, index) {
                vm.togglePipeline = index;
            };

            vm.addUserGroup = function () {
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

            vm.removeUser = function() {
                adminService.deleteUser(vm.selectedUser.id);
            };

            vm.assignPipeline = function(pipeline) {
                var updatedPipeline = angular.copy(pipeline);
                var pipelineGroupId = vm.pipelineGroupToAssign.id;
                pipeConfigService.assignPipelineDefinition(updatedPipeline, pipelineGroupId);
            };

            vm.unassignPipeline = function() {
                var updatedPipeline = angular.copy(vm.pipelineToUnassign);
                pipeConfigService.unassignPipelineDefinition(updatedPipeline);
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
                vm.newUser = {};
                vm.clearSelection();
            };

            vm.currentPipelineGroups = viewModel.allPipelineGroups;

            $scope.sortingOrder = 'name';
            $scope.pageSizes = [5,10,25,50];
            $scope.reverse = false;
            $scope.filteredItems = [];
            $scope.groupedItems = [];
            $scope.itemsPerPage = 10;
            $scope.pagedItems = [];
            $scope.currentPage = 0;
            vm.query = "";
            $scope.items = vm.currentPipelineGroups;


            $(document).ready(function() {
                $('#userGroups').DataTable();
            } );

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

            vm.removePermission = function(index) {
                vm.selectedUser.permissions.splice(index, 1);
            };

            vm.updateUserPermission = function() {
                adminService.updateUser(vm.selectedUser);
                vm.closePermissionModal();
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

            var searchMatch = function (haystack, needle) {
                if (!needle) {
                    return true;
                }
                var result = haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
                return result;
            };

            $scope.search = function () {
                $scope.filteredItems = $filter('filter')($scope.items, function (item) {
                    if (searchMatch(item["name"], vm.query))
                    {
                        return true;
                    }
                    return false;
                });
                // take care of the sorting order
                if ($scope.sortingOrder !== '') {
                    $scope.filteredItems = $filter('orderBy')($scope.filteredItems, $scope.sortingOrder, $scope.reverse);
                }
                $scope.currentPage = 0;
                // now group by pages
                $scope.groupToPages();
            };

            // show items per page
            $scope.perPage = function () {
                $scope.groupToPages();
            };

            // calculate page in place
            $scope.groupToPages = function () {
                $scope.pagedItems = [];

                for (var i = 0; i < $scope.filteredItems.length; i++) {
                    if (i % $scope.itemsPerPage === 0) {
                        $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
                    } else {
                        $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
                    }
                }
            };

            $scope.deleteItem = function (idx) {
                var itemToDelete = $scope.pagedItems[$scope.currentPage][idx];
                var idxInItems = $scope.items.indexOf(itemToDelete);
                $scope.items.splice(idxInItems,1);
                $scope.search();

                return false;
            };

            $scope.range = function (start, end) {
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

            $scope.prevPage = function () {
                if ($scope.currentPage > 0) {
                    $scope.currentPage--;
                }
            };

            $scope.nextPage = function () {
                if ($scope.currentPage < $scope.pagedItems.length - 1) {
                    $scope.currentPage++;
                }
            };

            $scope.setPage = function () {
                $scope.currentPage = this.n;
            };

            $scope.search();


            // change sorting order
            $scope.sort_by = function(newSortingOrder) {
                if ($scope.sortingOrder == newSortingOrder)
                    $scope.reverse = !$scope.reverse;

                $scope.sortingOrder = newSortingOrder;
            };

            $scope.$watchCollection(function () { return viewModel.unassignedPipelines }, function(newVal, oldVal) {
                vm.unassignedPipelines = viewModel.unassignedPipelines;
            });

            $scope.$watchCollection(function() {
                return viewModel.allPipelineGroups
            }, function(newVal, oldVal) {
                vm.currentPipelineGroups = viewModel.allPipelineGroups;
                console.log(vm.currentPipelineGroups);
            });

            $scope.$watchCollection(function() {
                return viewModel.allMaterialDefinitions
            }, function(newVal, oldVal) {
                vm.currentMaterials = viewModel.allMaterialDefinitions;
                console.log(vm.currentMaterials);
            });

            vm.addNewPipelineGroup = function() {
                adminGroupService.addNewPipelineGroup(vm.newPipelineGroup);
            };

            vm.setPipelineForDelete = function(pipelineName) {
                vm.pipelineToDeleteName = pipelineName;
            };

            vm.pipelineGroupToDelete;

            vm.setPipelineGroupToDelete = function(pipelineGroup) {
                vm.pipelineGroupToDelete = pipelineGroup;
            };

            vm.deletePipelineGroup = function() {
                adminGroupService.deletePipelineGroup(vm.pipelineGroupToDelete.id);
            };

            vm.setUserGroupToManage = function (userGroup) {
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

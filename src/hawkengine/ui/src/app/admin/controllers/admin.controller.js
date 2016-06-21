'use strict';

angular
    .module('hawk.adminManagement')
    .controller('AdminController',
    function ($state, $interval, $scope, DTOptionsBuilder, DTColumnDefBuilder, pipeConfig, accountService, adminService, profileService, adminGroupService, authDataService, viewModel, $rootScope) {
        var vm = this;


        vm.breadCrumb = {
            admin: "Admin"
        };

        vm.defaultText = {
            pageHeader: 'Administration',
            breadCrumb: 'Admin',
            user: 'User Management',
            group: 'Group Management',
            server: 'Server Management',
            packages: 'Package Repositories',
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
            deletePipelineModal: {
                header: 'Delete Pipeline',
                confirm: 'Are you sure you want to delete Pipeline: ',
                delete: 'Confirm',
                cancel: 'Cancel'
            },
            deleteGroupModal: {
                header: 'Delete Pipeline Group',
                confirm: 'Are you sure you want to delete Group: '
            }
        };

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

        vm.newUser = {};

        vm.close = function () {
            vm.newUser = {};
        };

        vm.currentPipelineGroups = {};

        vm.currentPipelineGroups = viewModel.allPipelineGroups;

        $scope.$watch(function () { return viewModel.allPipelines }, function (newVal, oldVal) {
            vm.currentPipelineGroups = viewModel.allPipelines;
            console.log(vm.currentPipelineGroups);
        });

        vm.addNewPipelineGroup = function () {
            adminGroupService.addNewPipelineGroup(vm.newPipelineGroup);
        };

        vm.setPipelineForDelete = function (pipelineName) {
            vm.pipelineToDeleteName = pipelineName;
        };
        
        vm.pipelineGroupToDelete;

        vm.setPipelineGroupToDelete = function (pipelineGroup) {
            vm.pipelineGroupToDelete = pipelineGroup;
        };

        vm.deletePipelineGroup = function(){
            adminGroupService.deletePipelineGroup(vm.pipelineGroupToDelete.id);
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

        vm.users = {
            user1: 'neshto',
            user2: 'nestho1'
        };
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

        $scope.$on('$destroy', function () {
        });



    });

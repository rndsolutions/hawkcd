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
    .module('hawk.adminManagement', ['ui.router', 'datatables'])
    .config(['$stateProvider', function ($stateProvider, $state) {

        $stateProvider
            .state('index.admin', {
                url: "admin",
                abstract: true,
                templateUrl: "app/admin/views/index.html",
                data: {
                    pageTitle: 'Admin'
                },
                controller: "AdminController",
                controllerAs: "admin"
            })

        .state('index.admin.groups', {
            url: "/groups",
            data: {
                pageTitle: 'Group Management'
            },
            templateUrl: "app/admin/views/partials/_groups.html"
        })

        .state('index.admin.servers', {
            url: "/servers",
            data: {
                pageTitle: 'Server Management'
            },
            templateUrl: "app/admin/views/partials/_servers.html"

        })


        .state('index.admin.userGroups', {
            url: "/userGroups",
            data: {
                pageTitle: 'User Group Management'
            },
            templateUrl: "app/admin/views/partials/_userGroups.html"
        })

            .state('index.admin.users', {
                url: "/users",
                data: {
                    pageTitle: 'User Management'
                },
                templateUrl: "app/admin/views/partials/_users.html"
            })

        .state('index.admin.materials', {
            url: "/materials",
            data: {
                pageTitle: 'Material Repositories'
            },
            templateUrl: "app/admin/views/partials/_materials.html"
        })
                }]);

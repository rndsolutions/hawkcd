'use strict';

angular
    .module('hawk.adminManagement')
    .factory('filterUsers', ['$filter', function($filter) {
        var filterUsers = this;

            // show items per page
            // vm.perPage = function () {
            //     vm.groupToPages();
            // };

            // calculate page in place
            filterUsers.groupToPages = function (filteredItems, itemsPerPage) {
                var pagedItems = [];

                for (var i = 0; i < filteredItems.length; i++) {
                    if (i % itemsPerPage === 0) {
                        pagedItems[Math.floor(i / itemsPerPage)] = [ filteredItems[i] ];
                    } else {
                        pagedItems[Math.floor(i / itemsPerPage)].push(filteredItems[i]);
                    }
                }
                return pagedItems;
            };

            // vm.search();
            //
            // // change sorting order
            // vm.sort_by = function(newSortingOrder) {
            //     if (vm.sortingOrder == newSortingOrder)
            //         vm.reverse = !vm.reverse;
            //
            //     vm.sortingOrder = newSortingOrder;
            // };

        filterUsers.searchMatch = function (haystack, needle) {
            if (!needle) {
                return true;
            }
            var result = haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
            return result;
        };

        filterUsers.search = function (items, query) {
            var filteredItems = $filter('filter')(items, function (item) {
                if (filterUsers.searchMatch(item["email"], query))
                {
                    return true;
                }
                return false;
            });
            // take care of the sorting order
            // if (vm.sortingOrder !== '') {
            //     filteredItems = $filter('orderBy')(filteredItems, sortingOrder, reverse);
            // }

            // vm.currentPage = 0;
            // now group by pages
            // call this through controller vm.groupToPages();
            return filteredItems;
        };

        return filterUsers;
    }]);

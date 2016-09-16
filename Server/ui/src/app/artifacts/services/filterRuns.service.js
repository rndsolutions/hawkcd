'use strict';

angular
    .module('hawk.artifactManagement')
    .factory('filterRuns', ['$filter', function($filter) {
        var filterRuns = this;

        filterRuns.searchMatch = function (haystack, needle) {
            if (!needle) {
                return true;
            }
            var result = haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
            return result;
        };

        filterRuns.search = function (items, query) {
            var filteredItems = $filter('filter')(items, function (item) {
                if (filterRuns.searchMatch(item["pipelineDefinitionName"], query))
                {
                    return true;
                }
                return false;
            });
            return filteredItems;
        };

        return filterRuns;
    }]);

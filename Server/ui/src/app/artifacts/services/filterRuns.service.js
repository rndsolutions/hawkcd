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

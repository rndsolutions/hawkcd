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
  .module('hawk')
  /* Setup Layout Part - Footer */
  .controller('FooterController', ['$scope','footerService','viewModel' function ($scope, footerService, viewModel) {

    $scope.$on('$includeContentLoaded', function () {
      Layout.initFooter(); // init footer
    });

     var vm=this;
     vm.serverVersion='';

     $scope.$watch(function(){
        return viewModel.serverVersion;
     },
        function(newVal, oldVal){
            vm.serverVersion = angular.copy(viewModel.serverVersion);
        }
     );

     vm.getVersion =  function(){
        footerService.getVersion();
     };

     vm.getVersion()

     $scope.version = '0.0.5-alpha.34 ';

  }]);

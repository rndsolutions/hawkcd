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

/***
 Global Directives
 ***/

// Route State Load Spinner(used on page or content load)
angular
    .module('hawk')
    .directive('ngSpinnerBar', ['$rootScope',
        function ($rootScope) {
            return {
                link: function (scope, element, attrs) {
                    // by defult hide the spinner bar
                    element.addClass('hide'); // hide spinner bar by default

                    // display the spinner bar whenever the route changes(the content part started loading)
                    $rootScope.$on('$stateChangeStart', function () {
                        element.removeClass('hide'); // show spinner bar
                    });

                    // hide the spinner bar on rounte change success(after the content loaded)
                    $rootScope.$on('$stateChangeSuccess', function () {
                        element.addClass('hide'); // hide spinner bar
                        $('body').removeClass('page-on-load'); // remove page loading indicator
                        Layout.setSidebarMenuActiveLink('match'); // activate selected link in the sidebar menu

                        // auto scorll to page top
                        // setTimeout(function() {
                        //     App.scrollTop(); // scroll to the top on content load
                        // }, $rootScope.settings.layout.pageAutoScrollOnLoad);
                    });

                    // handle errors
                    $rootScope.$on('$stateNotFound', function () {
                        element.addClass('hide'); // hide spinner bar
                    });

                    // handle errors
                    $rootScope.$on('$stateChangeError', function () {
                        element.addClass('hide'); // hide spinner bar
                    });
                }
            };
        }
    ])

    // Handle global LINK click
    .directive('a', function () {
        return {
            restrict: 'E',
            link: function (scope, elem, attrs) {
                if (attrs.ngClick || attrs.href === '' || attrs.href === '#') {
                    elem.on('click', function (e) {
                        e.preventDefault(); // prevent link click for above criteria
                    });
                }
            }
        };
    })

    // Handle Dropdown Hover Plugin Integration
    .directive('dropdownMenuHover', function () {
        return {
            link: function (scope, elem) {
                elem.dropdownHover();
            }
        };
    })

    //Bootstrap switch button
    .directive('bootstrapSwitch', ['adminService', function (adminService) {
        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, element, attrs, ngModel) {
                element.bootstrapSwitch();

                element.on('switchChange.bootstrapSwitch', function (event, state) {
                    if (ngModel) {
                        scope.$apply(function () {
                            ngModel.$setViewValue(state);
                            var buffer = scope.user;
                            adminService.updateUser(buffer);
                        });
                    }
                });

                scope.$watch(attrs.ngModel, function (newValue, oldValue) {
                    if (newValue) {
                        element.bootstrapSwitch('state', true, true);
                    } else {
                        element.bootstrapSwitch('state', false, true);
                    }
                });

            }
        };
    }
    ])

    //Convert to Number
    .directive('convertToNumber', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attrs, ngModel) {
                ngModel.$parsers.push(function (val) {
                    return parseInt(val, 10);
                });
                ngModel.$formatters.push(function (val) {
                    return '' + val;
                });
            }
        };
    })

    .directive('elastic', [
        '$timeout',
        function ($timeout) {
            return {
                restrict: 'A',
                link: function ($scope, element) {
                    if (!$scope.initialHeight) {
                        element[0].style.height = "32px";
                    }
                    $scope.initialHeight = $scope.initialHeight || element[0].style.height || "32px";
                    var resize = function () {
                        element[0].style.height = $scope.initialHeight;
                        if (element[0].scrollHeight > 0) {
                            element[0].style.height = "" + element[0].scrollHeight + "px";
                        }
                    };
                    element.on("input change", resize);
                    $timeout(resize, 0);
                }
            };
        }
    ])

    .directive('submitButton', function () {
        return {
            require: '^form',
            restrict: 'A',
            link: function (scope, element, attributes, formCtrl) {
                element.bind("keypress", function (event) {
                    if (event.which === 13 && formCtrl.$invalid) {
                        event.preventDefault();
                    }
                });
            }
        }
    })

    .directive('submitButtonPipeline', function () {
        return {
            require: '^form',
            restrict: 'A',
            link: function (scope, element, attributes, formCtrl) {
                element.bind("keypress", function (event) {
                    if (event.which === 13 && formCtrl.$invalid || (scope.$parent.pipes.bar == 2 && scope.$parent.pipes.materialType === 'hidden') || (scope.$parent.pipes.bar == 2 && scope.$parent.pipes.materialType == 'existing' && scope.$parent.pipes.selectedMaterial.$$hashKey != null)) {
                        event.preventDefault();
                    }
                });
            }
        }
    })

    .directive('submitButtonUser', function () {
        return {
            require: '^form',
            restrict: 'A',
            link: function (scope, element, attributes, formCtrl) {
                element.bind("keypress", function (event) {
                    debugger;
                    if (event.which === 13 && formCtrl.$invalid ||
                        ((formCtrl.confirmNewPassword.$modelValue && !formCtrl.userNewPassword.$modelValue) || (!formCtrl.confirmNewPassword.$modelValue && formCtrl.userNewPassword.$modelValue))) {
                        event.preventDefault();
                    }
                });
            }
        }
    })

    .directive('setScrollPosition', function ($timeout) {
        return {
            restrict: 'A',
            scope: {
                scrollPosition: '@'
            },
            link: function (scope, element, attributes, formCtrl) {
                var innerElement = element.find('div');
                innerElement.bind('DOMSubtreeModified',elemenHasChanged);

                function elemenHasChanged(e){
                    if(scope.scrollPosition === 'bottom' || !scope.scrollPosition){
                        //The timout is needed to prevent 'the jumping' of the screen
                        $timeout(function(){
                            element.scrollTop(innerElement.height());
                        }, 10);
                    } else if(scope.scrollPosition === 'top'){
                        //The timout is needed to prevent 'the jumping' of the screen
                        $timeout(function(){
                            element.scrollTop(0);
                        }, 10);
                    }
                }

            }
        };
    });
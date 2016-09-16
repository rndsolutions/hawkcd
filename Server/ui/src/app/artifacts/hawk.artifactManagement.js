'use strict';

angular
    .module('hawk.artifactManagement', ['ui.router', 'datatables'])
    .config(['$stateProvider', function ($stateProvider, $state) {

        $stateProvider
            .state('index.artifact', {
                url: "artifacts",
                templateUrl: "app/artifacts/views/index.html",
                data: {
                    pageTitle: 'Artifacts'
                },
                controller: "ArtifactController",
                controllerAs: "artifact"
            })
    }]);

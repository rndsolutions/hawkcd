'use strict';

angular
    .module('hawk.pipelinesManagement', ["ui.router"])
    .config(['$stateProvider', function ($stateProvider) {

        //Pipelines
        $stateProvider
            .state('index.pipelines', {
                url: "pipelines",
                templateUrl: "app/pipelines/views/index.html",
                data: {
                    pageTitle: 'Pipelines'
                },
                controller: "PipelinesController",
                controllerAs: 'pipes'
            })

            .state('index.pipelineHistory', {
                abstract: true,
                url: 'pipelines/{groupName}/{pipelineName}',
                templateUrl: "app/pipelines/views/history.html",
                data: {
                    pageTitle: 'Pipeline History'
                },
                controller: "PipelinesHistoryController",
                controllerAs: 'pipesHistory'
            })

            .state('index.pipelineHistory.current', {
                url: '/current',
                templateUrl: "app/pipelines/views/partials/pipeline-history/_pipeline-current.html"
            })

            .state('index.pipelineHistory.history', {
                url: '/history',
                templateUrl: "app/pipelines/views/partials/pipeline-history/_pipeline-history.html"
            })

            .state('index.pipelineRunManagement', {
                url: 'pipelines/{pipelineName}/{executionID}/RunManagement',
                templateUrl: "app/pipelines/views/runs-management.html",
                data: {
                    pageTitle: 'Pipeline Run Management'
                },
                controller: "PipelinesRunManagement",
                controllerAs: 'pipesRunManagement'
            })

            //region PIPELINE CONFIG SCREEN
            .state('index.pipelineConfig', {
                abstract: true,
                url: "pipelines/",
                templateUrl: "app/pipelines/views/config.html",
                data: {
                    pageTitle: 'Pipeline Config'
                },
                controller: "PipelineConfigController",
                controllerAs: 'pipeConfig',
                cache: false
            })
            .state('index.pipelineConfig.pipeline', {
                abstract: true,
                url: "{groupName}/{pipelineName}/config",
                templateUrl: "app/pipelines/views/partials/config/_pipeline-config.html"
            })
            .state('index.pipelineConfig.pipeline.general', {
                url: '/general',
                templateUrl: "app/pipelines/views/partials/config/pipeline-options/_general-options.html"
            })
            .state('index.pipelineConfig.pipeline.materials', {
                url: '/materials',
                templateUrl: "app/pipelines/views/partials/config/pipeline-options/_materials.html"
            })
            .state('index.pipelineConfig.pipeline.stages', {
                url: '/stages',
                templateUrl: "app/pipelines/views/partials/config/pipeline-options/_stages.html"
            })
            .state('index.pipelineConfig.pipeline.variables', {
                url: '/variables',
                templateUrl: "app/pipelines/views/partials/config/pipeline-options/_variables.html"
            })


            .state('index.pipelineConfig.stage', {
                abstract: true,
                url: "{groupName}/{pipelineName}/{stageName}/config",
                templateUrl: "app/pipelines/views/partials/config/_stage-config.html"
            })
            .state('index.pipelineConfig.stage.settings', {
                url: "/settings",
                templateUrl: "app/pipelines/views/partials/config/stage-options/_settings.html"
            })
            .state('index.pipelineConfig.stage.jobs', {
                url: "/jobs",
                templateUrl: "app/pipelines/views/partials/config/stage-options/_jobs.html"
            })
            .state('index.pipelineConfig.stage.variables', {
                url: "/variables",
                templateUrl: "app/pipelines/views/partials/config/stage-options/_variables.html"
            })


            .state('index.pipelineConfig.job', {
                abstract: true,
                url: "{groupName}/{pipelineName}/{stageName}/{jobName}/config",
                templateUrl: "app/pipelines/views/partials/config/_job-config.html"
            })
            .state('index.pipelineConfig.job.settings', {
                url: "/settings",
                templateUrl: "app/pipelines/views/partials/config/job-options/_settings.html"
            })
            .state('index.pipelineConfig.job.tasks', {
                url: "/tasks",
                templateUrl: "app/pipelines/views/partials/config/job-options/_tasks.html"
            })
            .state('index.pipelineConfig.job.variables', {
                url: "/variables",
                templateUrl: "app/pipelines/views/partials/config/job-options/_variables.html"
            })
            .state('index.pipelineConfig.job.tabs', {
                url: "/tabs",
                templateUrl: "app/pipelines/views/partials/config/job-options/_tabs.html"
            });
        //endregion

    }]);
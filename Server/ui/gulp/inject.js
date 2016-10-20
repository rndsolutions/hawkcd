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

var path = require('path');
var gulp = require('gulp');
var conf = require('./conf');

var $ = require('gulp-load-plugins')();

var wiredep = require('wiredep').stream;
var _ = require('lodash');

var browserSync = require('browser-sync');

gulp.task('inject-reload', ['inject'], function () {
  browserSync.reload();
});

gulp.task('inject', ['scripts'], function () {
  var injectStyles = gulp.src([
    path.join(conf.paths.src, '/app/**/*.css')
  ], {read: false});

  var injectScripts = gulp.src([
      path.join(conf.paths.src, '/app/**/*.main.js'),
      path.join(conf.paths.src, '/app/**/*.js'),
      path.join('!' + conf.paths.src, '/app/dataTables/*.js'),
      path.join('!' + conf.paths.src, '/app/**/bootstrap.js'),
      path.join('!' + conf.paths.src, '/app/**/quick-sidebar.js'),
      path.join('!' + conf.paths.src, '/app/**/app.js'),
      path.join('!' + conf.paths.src, '/app/**/layout.js'),
      path.join('!' + conf.paths.src, '/app/**/*.spec.js'),
      path.join('!' + conf.paths.src, '/app/**/*.mock.js'),
      path.join('!' + conf.paths.src, '/app/**/jstree.min.js'),
      path.join('!' + conf.paths.src, '/app/**/ngJsTree.min.js'),
      path.join('!' + conf.paths.src, '/app/**/ng-infinite-scroll.min.js'),
      path.join('!' + conf.paths.src, '/app/**/bootstrap-switch.js')
    ])
    .pipe($.angularFilesort()).on('error', conf.errorHandler('AngularFilesort'));

  // var injectCustomScripts = gulp.src([
  //   path.join(conf.paths.src, '/app/js/app.js'),
  //   path.join(conf.paths.src, '/app/js/layout.js'),
  //  path.join(conf.paths.src, '/app/js/quick-sidebar.js')
//  ]).pipe($.angularFilesort()).on('error', conf.errorHandler('AngularFilesort'));

  var injectOptions = {
    ignorePath: [conf.paths.src, path.join(conf.paths.tmp, '/serve')],
    addRootSlash: false
  };

  return gulp.src(path.join(conf.paths.src, '/*.html'))
    .pipe($.inject(injectStyles, injectOptions))
    .pipe($.inject(injectScripts, injectOptions))
    .pipe(wiredep(_.extend({}, conf.wiredep)))
    .pipe(gulp.dest(path.join(conf.paths.tmp, '/serve')));
});

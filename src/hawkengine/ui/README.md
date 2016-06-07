# Hawk UI

### Main Technologies

Hawk uses a number of open source projects to work properly:

* [AngularJS] - HTML enhanced for web apps!
* [Twitter Bootstrap] - great UI boilerplate for modern web apps
* [node.js] - evented I/O for the backend
* [Gulp] - the streaming build system
* [Metronic](http://www.keenthemes.com/preview/metronic/) - "THE MOST COMPLETE & TRUSTED ADMIN THEME"

And of course Hawk itself is NOT YET open source with a [private repository](https://github.com/rndsolutions/hawk-ui)
 on GitHub. ! Only RnD Solutions Members allowed.

### Installation
Run these commands in console

```sh
$ git clone https://github.com/rndsolutions/hawk-ui.git
```

```sh
$ npm install
```

### Development
Terminal/Console Commands:

Build an optimized version of your application in folder build -
```sh
$ gulp build
```
Start BrowserSync server on your source files with live reload -
```sh
$ gulp serve-dev
```
Start BrowserSync server on your optimized application without live reload -
```sh
$ gulp serve-build
```
Run your unit tests with Karma
```sh
$ gulp test
```
Run your unit tests with Karma in watch mode
```sh
$ gulp test:auto
```
Launch your e2e tests with Protractor
```sh
$ gulp protractor
```
Launch your e2e tests with Protractor on the dist files
```sh
$ gulp protractor:dist
```

   [node.js]: <http://nodejs.org>
   [Twitter Bootstrap]: <http://twitter.github.com/bootstrap/>
   [jQuery]: <http://jquery.com>
   [AngularJS]: <http://angularjs.org>
   [Gulp]: <http://gulpjs.com>

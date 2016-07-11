module.exports = function() {
  var client = './src/client/';
  var clientApp = client + 'app/';
  var report = './report/';
  var root = './';
  var server = './src/server/';
  var specRunnerFile = 'specs.html';
  var temp = './.tmp/';
  var wiredep = require('wiredep');
  var bowerFiles = wiredep({devDependencies: true})['js'];

  var config = {
    /**
     * Files paths
     */
    alljs: [
      './src/**/*.js',
      './*.js'
    ],
    build: './build/',
    client: client,
    css: client + 'assets/**/*.css',
    fonts: [
      './bower_components/font-awesome/fonts/**/*.*',
      './bower_components/simple-line-icons/fonts/**/*.*',
      './bower_components/bootstrap/dist/fonts/**/*.*'
      ],
    html: clientApp + '**/views/*.html',
    htmltemplates: clientApp + '**/views/*.html',
    images: client + 'assets/images/*.*',
    index: client + 'index.html',
    js: [
      client + '/assets/**/*.js',
      clientApp + 'main.js',
      clientApp + '**/hawk.*.js',
      clientApp + '**/*.js',
      '!' + clientApp + '**/*.spec.js'
    ],
    report: report,
    root: root,
    server: server,
    temp: temp,

    /**
     * optimized files
     */
    optimized: {
      app: 'app.js',
      lib: 'lib.js'
    },

    /**
     * template cache
     */
    templateCache: {
      file: 'templates.js',
      options: {
        module: 'hawk',
        standAlone: false,
        root: 'app/'
      }
    },

    /**
     * browser sync
     */
    browserReloadDelay: 1000,

    /**
     * Bower and NPM locations
     */
    bower: {
      json: require('./bower.json'),
      directory: './bower_components/',
      ignorePath: '../..'
    },
    packages : [
      './package.json',
      './bower.json'
    ],

    /**
     * specs.html, our HTML spec runner
     */
    specRunner: client + specRunnerFile,
    specRunnerFile: specRunnerFile,
    testlibraries: [
      'node_modules/mocha/mocha.js',
      'node_modules/chai/chai.js',
      'node_modules/mocha-clean/index.js',
      'node_modules/sinon-chai/lib/sinon-chai.js'
    ],
    specs: [clientApp + '**/*.spec.js'],

    /**
     * Karma and testing settings
     */
    specHelpers: [client + 'test-helpers/*.js'],
    serverIntegrationSpecs: [client + 'tests/server-integration/**/*.spec.js'],

    /**
     * Server settings
     */
    defaultPort: 3000

  };

  config.getWiredepDefaultOptions = function() {
    var options = {
      bowerJson: config.bower.json,
      directory: config.bower.directory,
      ignorePath: config.bower.ignorePath
    };
    return options;
  };

  config.karma = getKarmaOptions();

  return config;

  ////////////////

  function getKarmaOptions() {
    var options = {
      files: [].concat(
        bowerFiles,
        config.specHelpers,
        client + '**/main.js',
        client + '**/*.js',
        temp + config.templateCache.file,
        config.serverIntegrationSpecs
      ),
      exclude: [],
      coverage: {
        dir: report + 'coverage',
        reporters: [
          {type: 'html', subdir: 'report-html'},
          {type: 'lcov', subdir: 'report-lcov'},
          {type: 'text-summary'}
        ]
      },
      preprocessors: {}
    };
    options.preprocessors[clientApp + '**/!(*.spec)+(.js)'] = ['coverage'];
    return options;
  }
};

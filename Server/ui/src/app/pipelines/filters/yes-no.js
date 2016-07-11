angular
  .module('hawk')
  .filter('yesNo', function() {
    return function(input) {
      return input ? 'Yes' : 'No';
    }
  });



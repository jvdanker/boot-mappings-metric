angular.module('app', [])

	.config(function () {
	})

	.controller('testController', ['$scope', 'metricsService', function($scope, metricsService) {
		
		metricsService.onUpdate(function() {
			$scope.metrics = metricsService.getCurrent();
		});
	}])
	
	.factory('metricsService', ['$http', '$interval', '$timeout', function($http, $interval, $timeout) {
		var current = null;
		var callback = null;
		
		function poll() {
			$http.get('/plugin/metrics/mappings/metrics').then(function(data) {
				current = data.data;
				
				if (callback) {
					callback();
				}
			});
		}
		
		function start() {
			$timeout(function() {
				poll();
			});
			
			$interval(function() {
				poll();
			}, 3000, false)
		}
		
		function get(key) {
			return current.metrics[key];
		}
		
		function getCurrent() {
			return current;
		}
		
		function onUpdate(fn) {
			callback = fn;
		}
		
		return {
			start: start,
			get: get,
			getCurrent: getCurrent,
			onUpdate: onUpdate
		}
	}])
	
	.run(['metricsService', function(metricsService) {
		metricsService.start();
	}]);
;

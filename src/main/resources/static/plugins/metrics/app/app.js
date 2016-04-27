angular.module('app', ['ngRoute'])

	.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
		
		$routeProvider
	      .when('/', {
	        templateUrl: 'metrics.html',
	        controller: 'MetricsCtrl'
	      })
	      .when('/mappings', {
	        templateUrl: 'mappings.html',
	        controller: 'MappingsCtrl'
	      })
	      .otherwise({
	        templateUrl: 'metrics.html',
	        controller: 'MetricsCtrl'
	      });

	    $locationProvider.html5Mode(true);
	    
	}])

	.controller('MetricsCtrl', ['$scope', 'metricsService', function($scope, metricsService) {
		metricsService.onUpdate(function(metrics) {
			$scope.metrics = metrics;
		});

		metricsService.poll();
	}])
	
	.controller('MappingsCtrl', ['$scope', 'metricsService', 'configurationService', function($scope, metricsService, configurationService) {
		$scope.metrics = {
				metrics: {},
				configuration: {}
		};
		
		metricsService.onUpdate(function(metrics) {
			$scope.metrics = metrics;
		});
		
		metricsService.poll();
		
		$scope.toggle = function(key) {
			metricsService.stop()
				.then(function() {
					var config = $scope.metrics.configuration[key];
					var action = config.enabled ? 'enable' : 'disable';
					return configurationService.changePattern(key, action);
				}).then(function() {
					metricsService.start();
				})
		}; 
	}])
	
	.factory('metricsService', ['$http', '$interval', '$timeout', '$q', function($http, $interval, $timeout, $q) {
		var callback = null;
		var interval = null;
		
		function poll() {
			return $http.get('/plugin/metrics/mappings/metrics').then(function(data) {
				if (callback) {
					callback(data.data);
				}
			});
		}
		
		function start() {
			interval = $interval(function() {
				return poll();
			}, 3000, false)

			return poll();
		}
		
		function stop() {
			var defer = $q.defer();
			
			$interval.cancel(interval);
			
			defer.resolve();
			return defer.promise;
		}
		
		function onUpdate(fn) {
			callback = fn;
		}
		
		return {
			start: start,
			stop: stop,
			poll: poll,
			onUpdate: onUpdate
		}
	}])
	
	.factory('configurationService', ['$http', function($http) {
		var callback = null;
		
		function changePattern(pattern, action) {
			return $http.post('/plugin/metrics/mappings/config', {
				pattern: pattern,
				action: action
			});
		}
		
		return {
			changePattern: changePattern
		}
	}])
	
	.directive('bootstrapSwitch', ['$parse',
        function($parse) {
            return {
                restrict: 'A',
                require: '?ngModel',
                link: function(scope, element, attrs, ngModel) {
                	var changeHandler = $parse(attrs.onChange);
                    element.bootstrapSwitch();

                    element.on('switchChange.bootstrapSwitch', function(event, state) {
                        if (ngModel) {
                            scope.$apply(function() {
                                ngModel.$setViewValue(state);
                            });
                        }
                        
                        changeHandler(scope);
                    });
                    
                    scope.$watch(attrs.ngModel, function(newValue, oldValue) {
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
	
	.run(['metricsService', function(metricsService) {
		metricsService.start();
	}]);
;

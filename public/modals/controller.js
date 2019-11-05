angular.module('qaaf')
.controller('NodeModalCtrl', function($scope, $uibModalInstance, config) {
	
	$scope.data = {};
	$scope.config = config;
	
	$scope.save = function(){
		$uibModalInstance.close({
			data: $scope.data,
			depot: $scope.depot
		});
	}
	
	$scope.cancel = function(){
		$uibModalInstance.dismiss();
	}
	
}).controller('ConfigModalCtrl', function($scope, $uibModalInstance, config) {
	
	$scope.config = config;
	
	$scope.save = function(){
		$uibModalInstance.close($scope.config);
	}
	
});

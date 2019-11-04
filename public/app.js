angular.module('qaaf', ['ui.bootstrap'])
.controller('Greeting', function($scope, $http, $uibModal) {
    
	$http.get('/greeting').
    then(function(response) {
        $scope.greeting = response.data;
    });
	
	$scope.data = {
		points: []
	};
	
	$scope.plan = function() {
		console.log($scope.data);
	}
	
    mapboxgl.accessToken = 'pk.eyJ1IjoiYWxpeWRnciIsImEiOiJjazJpcjZjMnExZXRpM3BvMjFqeGwzMWZvIn0.-dW4TEm1efXqzQfEYeu_Iw';
    mapboxgl.setRTLTextPlugin('https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-rtl-text/v0.2.3/mapbox-gl-rtl-text.js');
    var map = new mapboxgl.Map({
	    container: 'map',
	    style: 'mapbox://styles/aliydgr/ck2irkysx262n1cs2appnuoty',
	    center: [51.384823, 35.742647],
	    zoom: 13
    });
    
    map.on('click', function (e) {
    	
    	var features = map.queryRenderedFeatures(e.point);
    	if(features.length == 0)
    		return;
    	
    	 var modalInstance = $uibModal.open({
             templateUrl: "modal.html",
             controller: "Modal",
             size: "sm"
         });
    	 
	     modalInstance.result.then(function (result) {
	    	 
	    	 var popup = new mapboxgl.Popup({className: 'popup'})
	    	 	.setHTML("<div><span>takeCapacity:</span><span>" + result.data.takeCapacity + "</span></div>" + 
	    	 			"<div><span>deliverCapacity:</span><span>" + result.data.deliverCapacity + "</span></div>" +
	    	 			"<div><span>start:</span><span>" + result.data.start + "</span></div>" +
	    	 			"<div><span>end:</span><span>" + result.data.end + "</span></div>");
	    	 
	    	 var color = (result.depot) ? "#c2185b" : "#0097a7";
	    	 var marker = new mapboxgl.Marker({color:color})
		    	 .setLngLat(e.lngLat)
		    	 .setPopup(popup)
		    	 .addTo(map);
	    	 
	    	 result.data.coord = marker.getLngLat();
	    	 $scope.data.points.push(result.data);
	    	 if(result.depot)
	    		 $scope.data.depot = $scope.data.points.length - 1; 
	     });

    });
    
}).controller('Modal', function($scope, $uibModalInstance) {
	
	$scope.data = {};
	
	$scope.save = function(){
		$uibModalInstance.close({
			data: $scope.data,
			depot: $scope.depot
		});
	}
	
	$scope.cancel = function(){
		$uibModalInstance.dismiss();
	}
	
});

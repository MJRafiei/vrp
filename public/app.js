angular.module('qaaf', [])
.controller('Greeting', function($scope, $http) {
    
	$http.get('/greeting').
    then(function(response) {
        $scope.greeting = response.data;
    });
	
	$scope.points = [];
	$scope.plan = function() {
		console.log($scope.points);
		console.log($scope.driverCount);
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
    	var marker = new mapboxgl.Marker()
    	  .setLngLat(e.lngLat)
    	  .addTo(map);
    	
    	$scope.points.push(marker.getLngLat());
    });
    
});
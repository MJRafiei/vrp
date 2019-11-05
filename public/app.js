angular.module('qaaf', ['ui.bootstrap'])
.controller('Greeting', function($scope, $http, $uibModal) {
    
	$scope.data = {
		nodes: []
	}
	
	$scope.config = {
		deliverCapacity: true
	}
	
	var colors = ['#d32f2f', '#c2185b', '#ffa000', '#7b1fa2', '#689f38'];
	$scope.plan = function() {
		
		$http.post('/optimize', $scope.data).then(function(response) {
			var i = 0;
			response.travels.forEach(function (travel) {
				var coords = [];
				travel.nodes.forEach(function (node) {
					coords.push([node.coordinate.lng, node.coord.lat]);
				});
				
				var geojson = {
					type: "FeatureCollection",
					features: [{
						type: "Feature",
						geometry: {
							type: "LineString",
							coordinates: coords
						}
					}]
				};
				
				map.addLayer({
					'id': i,
					'type': 'line',
					'source': {
						'type': 'geojson',
						'data': geojson
					},
					'paint': {
						'line-color': colors[i++],
						'line-width': 3,
						'line-opacity': .8
					}
				});
			});
		});
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
             size: "sm",
             resolve: {
            	 config: function() {
            		 return $scope.config;
            	 }
             }
         });
    	 
	     modalInstance.result.then(function (result) {
	    	 
	    	 var popup = new mapboxgl.Popup({className: 'popup'})
	    	 	.setHTML("<div><label>takeCapacity:</label><span>" + result.data.takeCapacity + "</span></div>" + 
	    	 			"<div><label>deliverCapacity:</label><span>" + result.data.deliverCapacity + "</span></div>" +
	    	 			"<div><label>start:</label><span>" + result.data.start + "</span></div>" +
	    	 			"<div><label>end:</label><span>" + result.data.end + "</span></div>");
	    	 
	    	 var color = (result.depot) ? "#c2185b" : "#0097a7";
	    	 var marker = new mapboxgl.Marker({color:color})
		    	 .setLngLat(e.lngLat)
		    	 .setPopup(popup)
		    	 .addTo(map);
	    	 
	    	 result.data.coordinate = marker.getLngLat();
	    	 $scope.data.nodes.push(result.data);
	    	 if(result.depot)
	    		 $scope.data.depot = $scope.data.nodes.length - 1; 
	     });

    });
    
}).controller('Modal', function($scope, $uibModalInstance, config) {
	
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
	
});

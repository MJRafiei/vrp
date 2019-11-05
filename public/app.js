angular.module('qaaf', ['ui.bootstrap'])
.controller('Greeting', function($scope, $http, $uibModal) {
    
	mapboxgl.accessToken = 'pk.eyJ1IjoiYWxpeWRnciIsImEiOiJjazJpcjZjMnExZXRpM3BvMjFqeGwzMWZvIn0.-dW4TEm1efXqzQfEYeu_Iw';
    mapboxgl.setRTLTextPlugin('https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-rtl-text/v0.2.3/mapbox-gl-rtl-text.js');
    var map = new mapboxgl.Map({
	    container: 'map',
	    style: 'mapbox://styles/aliydgr/ck2irkysx262n1cs2appnuoty',
	    center: [51.384823, 35.742647],
	    zoom: 13,
	    doubleClickZoom: false
    });
    
    map.on('dblclick', function (e) {
    	$scope.addNode(e.lngLat);
    });
    
	
	$scope.data = {
		nodes: []
	}
	
	$scope.config = {
		deliverCapacity: true,
		takeCapacity: false,
		duration: false,
		distance: false,
		timeWindows: false
	}
	
	$scope.setting = function() {
		var modalInstance = $uibModal.open({
            templateUrl: "modals/config-modal.html",
            controller: "ConfigModalCtrl",
            size: "sm",
            resolve: {
            	config: function() {
            		return Object.assign({}, $scope.config);
            	}
            }
        });
		
		modalInstance.result.then(function (result) {
			$scope.config = result;
		});
	}
	
	$scope.plan = function() {
		$http.post('/optimize?dist=' + $scope.config.distance + "&time=" + $scope.config.timeWindows, $scope.data).then(function(response) {
			var i = 0;
			response.data.travels.forEach(function (travel) {
				var coords = [];
				travel.nodes.forEach(function (node) {
					coords.push([node.coordinate.lng, node.coordinate.lat]);
				});
				$scope.addLine(coords, i++);
			});
		});
	}
	
    $scope.depot = function() {
		//TODO
	}
    
    $scope.addNode = function(lngLat) {
    	var modalInstance = $uibModal.open({
    		templateUrl: "modals/node-modal.html",
    		controller: "NodeModalCtrl",
    		size: "sm",
    		resolve: {
    			config: function() {
    				return $scope.config;
    			}
    		}
    	});
    	
    	modalInstance.result.then(function (result) {
    		var html = "";
    		html += ($scope.config.takeCapacity) ? "<div><label>takeCapacity:</label><span>" + result.data.takeCapacity + "</span></div>" : ""; 
    		html += ($scope.config.deliverCapacity) ? "<div><label>deliverCapacity:</label><span>" + result.data.deliverCapacity + "</span></div>" : "";
    		html += ($scope.config.timeWindows) ? "<div><label>start:</label><span>" + result.data.start + "</span></div>" : "";
    		html += ($scope.config.timeWindows) ? "<div><label>end:</label><span>" + result.data.end + "</span></div>" : "";
    	
    		var popup = new mapboxgl.Popup({className: 'popup'}).setHTML(html)
    		var color = (result.depot) ? "#c2185b" : "#0097a7";
    		var marker = new mapboxgl.Marker({color:color})
	    		.setLngLat(lngLat)
	    		.setPopup(popup)
	    		.addTo(map);
    		
    		result.data.coordinate = marker.getLngLat();
    		$scope.data.nodes.push(result.data);
    		if(result.depot)
    			$scope.data.depot = $scope.data.nodes.length - 1; 
    	});
    }
    
    var colors = ['#ffa000', '#7b1fa2', '#689f38', '#c2185b', '#d32f2f'];
    $scope.addLine = function(coords, i) {
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
			'id': 'id' + i,
			'type': 'line',
			'source': {
				'type': 'geojson',
				'data': geojson
			},
			'paint': {
				'line-color': colors[i],
				'line-width': 3,
				'line-opacity': .8
			}
		});
    }
    
});

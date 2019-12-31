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

        map.on('dblclick', function(e) {
            console.log(e.lngLat);
            $scope.addNode(e.lngLat);
        });

        $scope.submitData = function() {
            var f = document.getElementById('input').files[0];
            var fileContent = Papa.parse(f, {
                dynamicTyping: true,
                header: true,
                complete: function(fileContent) {
                    console.log($scope.data.nodes)
                    var color = (fileContent.data.length > 80) ? "#e71837" : "#ffff00";
                    // var color = "#e71837";
                    fileContent.data.forEach(function(node, i) {
                        var processedNode = { "id": i };
                        processedNode.coordinate = { "lng": node.lng, "lat": node.lat };
                        for (var config in $scope.config) {
                            if ($scope.config[config]) processedNode[config] = node[config];
                        }
                        $scope.data.nodes.push(processedNode);
                        console.log();

                        if (processedNode['deliverCapacity'] === null)
                            $scope.data.depot = $scope.data.nodes.length - 1;
                        $scope.$apply();
                        $scope.renderNode(processedNode, processedNode['deliverCapacity'] === null, color);
                    })
                }
            });

        }

        $scope.renderNode = function(node, depot, col) {
            var html = "";
            html += "<div><label>" + node.id + "</label></div>";
            html += ($scope.config.takeCapacity) ? "<div><label>takeCapacity: </label><span>" + node.takeCapacity + "</span></div>" : "";
            html += ($scope.config.deliverCapacity) ? "<div><label>deliverCapacity: </label><span>" + node.deliverCapacity + "</span></div>" : "";
            html += ($scope.config.timeWindows) ? "<div><label>start: </label><span>" + node.start + "</span></div>" : "";
            html += ($scope.config.timeWindows) ? "<div><label>end: </label><span>" + node.end + "</span></div>" : "";

            console.log(node);

            var popup = new mapboxgl.Popup({ className: 'popup' }).setHTML(html);
            // var color = (depot) ? "#c2185b" : "#0097a7";
            var color = (depot) ? "#ffffff" : col;
            var el = document.createElement('div');
            if (!depot) {
                el.innerText = node.loyalty;
                // el.style.backgroundColor = color;
                el.style.color = color;
                el.style.width = '15px';
                el.style.height = '15px';
                el.style.fontSize = '24px';
            } else {
                el.style.backgroundColor = color;
                el.style.width = '14px';
                el.style.height = '14px';
            }

            var marker = new mapboxgl.Marker(el)
                .setLngLat(node.coordinate)
                .setPopup(popup)
                .addTo(map);
            return marker;
        }


        $scope.data = {
            nodes: [],
            plan: []
        }

        $scope.config = {
            deliverCapacity: true,
            loyalty: true,
            takeCapacity: false,
            duration: false,
            distance: false,
            timeWindows: false,
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

            modalInstance.result.then(function(result) {
                $scope.config = result;
            });
        }

        $scope.plan = function() {
            $scope.cleanMap()
            console.log($scope.data);
            $http.post('/optimize?dist=' + $scope.config.distance + "&time=" + $scope.config.timeWindows, $scope.data).then(function(response) {
                var i = 0;
                $scope.data.plan = response.data.travels;
                response.data.travels.forEach(function(travel) {
                    console.log(travel);
                    var travlesToShow = [];
                    //TODO	It should pass better object to drawTravel function
                    if (travel.transitions != null && travel.transitions[0] != null) {
                        for (j = 0; j < travel.transitions.length; j++) {
                            travlesToShow.push({
                                coords: [
                                    [travel.nodes[j].coordinate.lng, travel.nodes[j].coordinate.lat],
                                    [travel.nodes[j + 1].coordinate.lng, travel.nodes[j + 1].coordinate.lat]
                                ],
                                info: {
                                    distance: travel.transitions[j].distance,
                                    duration: travel.transitions[j].duration,
                                    durationInTraffic: travel.transitions[j].durationInTraffic,
                                },
                                geometry: travel.transitions[j].geometry
                            });
                        };
                    }

                    console.log(travlesToShow);

                    $scope.drawTravel(travlesToShow, i++);
                });
            });
        }

        $scope.report = function() {
            var modalInstance = $uibModal.open({
                templateUrl: "modals/report-modal.html",
                controller: "ReportModalCtrl",
                size: "lg",
                resolve: {
                    config: function() {
                        return $scope.config;
                    },
                    data: function() {
                        return $scope.data;
                    }
                }
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

            modalInstance.result.then(function(result) {
                result.data.coordinate = lngLat;
                $scope.renderNode(result.data, result.depot);
                $scope.data.nodes.push(result.data);
                if (result.depot)
                    $scope.data.depot = $scope.data.nodes.length - 1;
            });
        }

        var colors = ['#ffa000', '#7b1fa2', '#689f38', '#c2185b', '#d32f2f'];
        $scope.drawTravel = function(travel, i) {
            var geojson = {
                type: "FeatureCollection",
                features: []
            };
            for (j = 0; j < travel.length; j++) {
                coords = polyline.decode(travel[j].geometry);
                coords.forEach(function(coord) {
                    coord.reverse();
                })
                geojson.features.push({
                    type: "Feature",
                    geometry: {
                        type: "LineString",
                        coordinates: coords
                    },
                    properties: {
                        description: `<strong style="font-size: larger">Route Info</strong>
								  <ul>
										<li>Distance: ${(travel[j].info.distance / 1000).toFixed(2)} km </li>
										<li>Duration: ${(travel[j].info.duration / 60).toFixed(1)} min </li>
										<li>DurationInTraffic: ${(travel[j].info.durationInTraffic / 60).toFixed(1)} min </li>
								  </ul>`,
                    }
                })
            };

            map.addLayer({
                'id': 'planned-routes ' + i,
                'type': 'line',
                'source': {
                    'type': 'geojson',
                    'data': geojson
                },
                'paint': {
                    'line-color': colors[i],
                    'line-width': 5,
                    'line-opacity': .8
                }
            });

            var popup = new mapboxgl.Popup({
                closeButton: false,
                closeOnClick: false
            });

            map.on('mouseenter', 'planned-routes ' + i, function(e) {
                map.getCanvas().style.cursor = 'pointer';
                popup
                    .setLngLat(e.lngLat)
                    .setHTML(e.features[0].properties.description)
                    .addTo(map);
            });

            map.on('mouseleave', 'planned-routes ' + i, function() {
                map.getCanvas().style.cursor = '';
                popup.remove();
            });


        }

        $scope.cleanMap = function() {
            map.getStyle().layers.forEach(function(layer) {
                if (layer.id.startsWith("planned-routes")) {
                    map.removeLayer(layer.id);
                    map.removeSource(layer.id);
                }
            });
        }

    });

function setLoyalty(value) {

}
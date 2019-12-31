angular.module('qaaf')
    .controller('NodeModalCtrl', function($scope, $uibModalInstance, config) {

        $scope.data = {};
        $scope.config = config;

        $scope.save = function() {
            $uibModalInstance.close({
                data: $scope.data,
                depot: $scope.depot
            });
        }

        $scope.cancel = function() {
            $uibModalInstance.dismiss();
        }

    }).controller('ConfigModalCtrl', function($scope, $uibModalInstance, config) {

        $scope.config = config;

        $scope.save = function() {
            $uibModalInstance.close($scope.config);
        }

    }).controller('ReportModalCtrl', function($scope, $uibModalInstance, config, data) {

        $scope.config = config;
        $scope.data = [];

        data.plan.forEach(function(travel) {
            var steps = [];
            var cumulDuration = 0,
                weightedDuration = 0,
                cumulDistance = 0;
            for (i = 0; i < travel.transitions.length; i++) {
                cumulDuration += travel.transitions[i].duration;
                cumulDistance += travel.transitions[i].distance;
                weightedDuration += travel.nodes[i + 1].loyalty * cumulDuration;
                steps.push({
                    from: travel.nodes[i],
                    to: travel.nodes[i + 1],
                    loyalty: travel.nodes[i + 1].loyalty,
                    duration: travel.transitions[i].duration,
                    cumulDuration: cumulDuration,
                    cumulDistance: cumulDistance,
                    weightedDuration: weightedDuration
                })
            }
            $scope.data.push({
                size: steps.length,
                steps: steps,
            })
        })

        $scope.save = function() {
            $uibModalInstance.close({
                data: $scope.data,
                depot: $scope.depot
            });
        }

    });
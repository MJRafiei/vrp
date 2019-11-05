package ir.viratech.qaaf.controller;

import ir.viratech.qaaf.domain.Node;
import ir.viratech.qaaf.domain.OptimizationResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ir.viratech.qaaf.core.Dispatcher;
import ir.viratech.qaaf.domain.ETAResponse;
import ir.viratech.qaaf.domain.OptimizationRequest;
import ir.viratech.qaaf.model.Schedule;
import ir.viratech.qaaf.service.OptimizerService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OptimizerController {

	private final OptimizerService service;

	@PostMapping("/optimize")
	public OptimizationResponse optimize(@RequestBody OptimizationRequest request) {

		ETAResponse[][] matrix = service.createMatrix(request.getNodes());
		int[][] distances = new int[matrix.length][matrix.length];
		int[][] durations = new int[matrix.length][matrix.length];
		int[][] demands = new int[2][matrix.length];
		int[][] timeWindows = new int[matrix.length][2];
		int[] capacities = new int[]{request.getMaxDeliverCapacity(), request.getMaxTakeCapacity()};

		int minTimeWindow = request.getNodes().stream().mapToInt(Node::getStart).min().getAsInt();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				if (i == j)
					continue;
				distances[i][j] = matrix[i][j].getDistance();
				durations[i][j] = matrix[i][j].getDuration();
			}
			demands[0][i] = request.getNodes().get(i).getDeliverCapacity();
			demands[1][i] = request.getNodes().get(i).getTakeCapacity();
			timeWindows[i][0] = (request.getNodes().get(i).getStart() - minTimeWindow) * 3600;
			timeWindows[i][1] = (request.getNodes().get(i).getEnd() - minTimeWindow) * 3600;
		}

		Schedule schedule = new Dispatcher(request.getVehicleCount(), request.getDepot(), durations, request.getMaxDuration())
				.distances(distances, request.getMaxDistance())
				.demands(demands, capacities)
				.timeWindows(timeWindows)
				.solve();

		return new OptimizationResponse(schedule, request);
	}

}

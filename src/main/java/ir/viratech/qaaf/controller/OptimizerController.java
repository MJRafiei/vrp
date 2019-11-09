package ir.viratech.qaaf.controller;

import ir.viratech.qaaf.domain.Node;
import ir.viratech.qaaf.domain.OptimizationResponse;
import ir.viratech.qaaf.model.TimeWindow;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
	public OptimizationResponse optimize(@RequestBody OptimizationRequest request,
			@RequestParam("dist") boolean dist, @RequestParam(name="tcap", required=false) boolean tcap,
			@RequestParam("time") boolean time, @RequestParam(name="dcap", required=false) boolean dcap) {

		ETAResponse[][] matrix = service.createMatrix(request.getNodes());
		int[][] distances = new int[matrix.length][matrix.length];
		int[][] durations = new int[matrix.length][matrix.length];
		int[][] demands = new int[2][matrix.length];
		TimeWindow[] timeWindows = new TimeWindow[matrix.length];
		int[] capacities = new int[]{request.getMaxDeliverCapacity(), request.getMaxTakeCapacity()};

		long minStart = request.getNodes().stream().mapToLong(Node::getStart).min().getAsLong();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				if (i == j)
					continue;
				distances[i][j] = matrix[i][j].getDistance();
				durations[i][j] = matrix[i][j].getDuration();
			}
			demands[0][i] = request.getNodes().get(i).getDeliverCapacity();
			demands[1][i] = request.getNodes().get(i).getTakeCapacity();
			timeWindows[i] = new TimeWindow((request.getNodes().get(i).getStart() - minStart) * 3600,
					(request.getNodes().get(i).getEnd() - minStart) * 3600);
		}

		if(request.getMaxDuration() == 0)
			request.setMaxDuration(24*3600);
		Dispatcher dispatcher = new Dispatcher(request.getVehicleCount(), request.getDepot(), durations, request.getMaxDuration());
		dispatcher = dispatcher.demands(demands, capacities);
		dispatcher = (dist) ? dispatcher.distances(distances, request.getMaxDistance()) : dispatcher;
		dispatcher = (time) ? dispatcher.timeWindows(timeWindows) : dispatcher;

		Schedule schedule = dispatcher.solve();
		return new OptimizationResponse(schedule, request, matrix);
	}

}

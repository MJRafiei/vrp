package ir.viratech.qaaf.domain;

import ir.viratech.qaaf.model.Schedule;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OptimizationResponse {

	private Travel[] travels;

	public OptimizationResponse(Schedule schedule, OptimizationRequest request, ETAResponse[][] matrix) {
		travels = new Travel[schedule.getVehicleDestinations().length];

		for (int i = 0; i < travels.length; i++) {
			int transitionSize = schedule.getVehicleDestinations()[i].length - 1;
			travels[i] = new Travel(transitionSize);
			for (int j = 0; j < transitionSize; j++) {
				int st = schedule.getVehicleDestinations()[i][j], en = schedule.getVehicleDestinations()[i][j + 1];
				travels[i].transitions[j] = matrix[st][en];
				travels[i].nodes[j] = new Node(request.getNodes().get(st).getCoordinate(),
						schedule.getVehicleTimeWindows()[i][j].getStart(),
						schedule.getVehicleTimeWindows()[i][j].getEnd());
				travels[i].nodes[j].setLoyalty(request.getNodes().get(st).getLoyalty());
			}
			travels[i].nodes[transitionSize] = new Node(request.getNodes().get(request.getDepot()).getCoordinate(),
					schedule.getVehicleTimeWindows()[i][transitionSize].getStart(),
					schedule.getVehicleTimeWindows()[i][transitionSize].getEnd());
		}

	}

	@Data
	@NoArgsConstructor
	public static class Travel {
		private ETAResponse[] transitions;
		private Node[] nodes;

		Travel(int transitionSize) {
			transitions = new ETAResponse[transitionSize];
			nodes = new Node[transitionSize + 1];
		}
	}
}

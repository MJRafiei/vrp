package ir.viratech.qaaf.domain;

import ir.viratech.qaaf.model.Schedule;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OptimizationResponse {

	private Travel[] travels;

	public OptimizationResponse(Schedule schedule, OptimizationRequest request) {
		travels = new Travel[schedule.vehicleTravel.length];

		for (int i = 0; i < travels.length; i++) {
			int travelSize = schedule.vehicleTravel[i].length;
			travels[i] = new Travel(travelSize);
			for (int j = 0; j < travelSize; j++) {
				int idx = schedule.vehicleTravel[i][j];
				travels[i].nodes[j] = new Node(request.getNodes().get(idx).getCoordinate(),
						schedule.getNodeMeetTimeWindow()[idx][0],
						schedule.getNodeMeetTimeWindow()[idx][1]);
			}
			travels[i].nodes[travelSize] = new Node(request.getNodes().get(request.getDepot()).getCoordinate(),
					0, 0);
			//FIXME: time window for going back to depot
			//FIXME: convert time window values of schedule to good format for response


		}

	}

	@Data
	@NoArgsConstructor
	public static class Travel {
		private String[] geometries;
		private Node[] nodes;

		Travel(int travelSize) {
			geometries = new String[travelSize];
			nodes = new Node[travelSize + 1];
		}

		public Travel(String[] geometries, Node[] nodes) {
			this.geometries = geometries;
			this.nodes = nodes;
		}
	}
}

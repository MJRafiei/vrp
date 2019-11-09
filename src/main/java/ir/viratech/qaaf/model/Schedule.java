package ir.viratech.qaaf.model;

import com.google.ortools.constraintsolver.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Schedule {


	// [i][j]: i'th vehicles j'th destination
	private int[][] vehicleDestinations;

	// [i][j]: i'th vehicle j'th timeWindow
	private TimeWindow[][] vehicleTimeWindows;

	public Schedule(int vehicleCount, RoutingModel routing, RoutingIndexManager manager, Assignment solution) {
		vehicleDestinations = new int[vehicleCount][];
		vehicleTimeWindows = new TimeWindow[vehicleCount][];
		RoutingDimension durationDimension = routing.getMutableDimension("Duration");
		for (int i = 0; i < vehicleCount; i++) {
			List<Integer> destinations = new ArrayList<>();
			List<TimeWindow> timeWindows = new ArrayList<>();
			long index = routing.start(i);
			while (true) {
				int j = manager.indexToNode(index);
				destinations.add(j);
				IntVar durationVar = durationDimension.cumulVar(index);
				timeWindows.add(new TimeWindow(solution.min(durationVar), solution.max(durationVar)));
				if (routing.isEnd(index))
					break;
				index = solution.value(routing.nextVar(index));
			}
			
			vehicleDestinations[i] = destinations.stream().mapToInt(k->k).toArray();;
			vehicleTimeWindows[i] = timeWindows.toArray(new TimeWindow[0]);
		}
	}
}

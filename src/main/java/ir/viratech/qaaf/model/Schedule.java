package ir.viratech.qaaf.model;

import com.google.ortools.constraintsolver.*;

import java.util.ArrayList;
import java.util.List;

public class Schedule {

	public int[][] nodeMeetTimeWindow;
	public int[][] vehicleTravel;

	public Schedule(int vehicleCount, int nodeCount, RoutingModel routing, RoutingIndexManager manager, Assignment solution) {
		nodeMeetTimeWindow = new int[nodeCount][2];
		vehicleTravel = new int[vehicleCount][];
		RoutingDimension durationDimension = routing.getMutableDimension("Duration");
		for (int i = 0; i < vehicleCount; i++) {
			List<Integer> travel = new ArrayList<>();
			long index = routing.start(i);
			while (!routing.isEnd(index)) {
				int j = manager.indexToNode(index);
				travel.add(j);
				IntVar durationVar = durationDimension.cumulVar(index);
				nodeMeetTimeWindow[j][0] = (int) solution.min(durationVar);
				nodeMeetTimeWindow[j][1] = (int) solution.max(durationVar);
			}
			
			vehicleTravel[i] = travel.stream().mapToInt(k->k).toArray();;
		}
	}
}

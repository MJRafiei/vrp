package ir.viratech.qaaf.core;

import com.google.ortools.constraintsolver.*;
import ir.viratech.qaaf.model.Schedule;

public class Dispatcher {

	public Schedule schedule(int vehicleCount, int depot,
	                         int maxDistance, int maxDuration, int[][] distances, int[][] durations,
	                         int[] capacities, int[][] demands, int[][] timeWindows) {
		RoutingIndexManager manager = new RoutingIndexManager(distances.length, vehicleCount, depot);
		RoutingModel routing = new RoutingModel(manager);

		int durationCallbackIndex = callbackIndex(routing, manager, durations);
		routing.addDimension(durationCallbackIndex, maxDuration, maxDuration, false, "Duration");

		routing.addDimension(callbackIndex(routing, manager, distances), 0, maxDistance, true, "Distance");

		for (int i = 0; i < capacities.length; i++)
			routing.addDimension(callbackIndex(routing, manager, demands[i]), 0, capacities[i],
					true, "Capacity (" + i + ")");

		RoutingDimension durationDimension = routing.getMutableDimension("Duration");
		for (int i = 0; i < timeWindows.length; i++)
			if (i != depot)
				durationDimension.cumulVar(manager.nodeToIndex(i)).setRange(timeWindows[i][0], timeWindows[i][1]);
		for (int i = 0; i < vehicleCount; i++) {
			long startIndex = routing.start(i), endIndex = routing.end(i);
			durationDimension.cumulVar(startIndex).setRange(timeWindows[depot][0], timeWindows[depot][1]);
			routing.addVariableMinimizedByFinalizer(durationDimension.cumulVar(startIndex));
			routing.addVariableMinimizedByFinalizer(durationDimension.cumulVar(endIndex));
		}

		Assignment solution = routing.solve();
		return new Schedule(vehicleCount, distances.length, routing, manager, solution);
	}

	private int callbackIndex(RoutingModel routing, RoutingIndexManager manager, int[][] matrix) {
		return routing.registerTransitCallback((long fromIndex, long toIndex) -> {
			int fromNode = manager.indexToNode(fromIndex);
			int toNode = manager.indexToNode(toIndex);
			return matrix[fromNode][toNode];
		});
	}

	private int callbackIndex(RoutingModel routing, RoutingIndexManager manager, int[] array) {
		return routing.registerUnaryTransitCallback(
				(long index) -> array[manager.indexToNode(index)]);
	}

}
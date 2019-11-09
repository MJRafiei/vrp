package ir.viratech.qaaf.core;

import com.google.ortools.constraintsolver.*;
import ir.viratech.qaaf.model.Schedule;
import ir.viratech.qaaf.model.TimeWindow;

public class Dispatcher {

	static {
	    System.loadLibrary("jniortools");
	}

	private final int vehicleCount;
	private final int depot;
	private final int maxDuration;
	private final int durationCallbackIndex;

	private RoutingIndexManager manager;
	private RoutingModel routing;

	public Dispatcher(int vehicleCount, int depot, int[][] durations, int maxDuration) {
		this.vehicleCount = vehicleCount;
		this.depot = depot;
		this.maxDuration = maxDuration;

		manager = new RoutingIndexManager(durations.length, vehicleCount, depot);
		routing = new RoutingModel(manager);

		durationCallbackIndex = callbackIndex(routing, manager, durations);
		routing.setArcCostEvaluatorOfAllVehicles(durationCallbackIndex);
	}

	public Dispatcher distances(int[][] distances, int maxDistance) {
		routing.addDimension(callbackIndex(routing, manager, distances), 0, maxDistance, true, "Distance");
		return this;
	}

	public Dispatcher demands(int[][] demands, int[] capacities) {
		for (int i = 0; i < capacities.length; i++)
			routing.addDimension(callbackIndex(routing, manager, demands[i]), 0, capacities[i],
					true, "Capacity (" + i + ")");
		return this;
	}

	public Dispatcher timeWindows(TimeWindow[] timeWindows) {
		routing.addDimension(durationCallbackIndex, maxDuration, maxDuration, false, "Duration");
		RoutingDimension durationDimension = routing.getMutableDimension("Duration");
		for (int i = 0; i < timeWindows.length; i++)
			if (i != depot)
				durationDimension.cumulVar(manager.nodeToIndex(i)).setRange(timeWindows[i].getStart(), timeWindows[i].getEnd());
		for (int i = 0; i < vehicleCount; i++) {
			long startIndex = routing.start(i), endIndex = routing.end(i);
			durationDimension.cumulVar(startIndex).setRange(timeWindows[depot].getStart(), timeWindows[depot].getEnd());
			routing.addVariableMinimizedByFinalizer(durationDimension.cumulVar(startIndex));
			routing.addVariableMinimizedByFinalizer(durationDimension.cumulVar(endIndex));
		}

		return this;
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

	public Schedule solve() {
		RoutingDimension durationDimension = routing.getMutableDimension("Duration");
		if (durationDimension == null)
			routing.addDimension(durationCallbackIndex, 0, maxDuration, true, "Duration");
		Assignment solution = routing.solve();
		return new Schedule(vehicleCount, routing, manager, solution);
	}

}
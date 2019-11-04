package ir.viratech.qaaf.domain;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OptimizationRequest {

	List<Point> points;
	int depot;
    int maxDeliverCapacity;
    int maxTakeCapacity;
    int maxDistance;
    int maxDuration;
    int vehicleCount;
	
	@Data
	@NoArgsConstructor
	public static class Point {
		
		Coordinate coord;
		int deliverCapacity;
		int takeCapacity;
		int start;
        int end;
		
		@Data
		@NoArgsConstructor
		public static class Coordinate {
			double lng;
			double lat;
		}
	}
	
}

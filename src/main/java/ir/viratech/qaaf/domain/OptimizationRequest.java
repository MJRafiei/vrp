package ir.viratech.qaaf.domain;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OptimizationRequest {

	List<Point> points;
	
	@Data
	@NoArgsConstructor
	public static class Point {
		
		Coordinate coord;
		
		@Data
		@NoArgsConstructor
		public static class Coordinate {
			double lng;
			double lat;
		}
	}
	
}

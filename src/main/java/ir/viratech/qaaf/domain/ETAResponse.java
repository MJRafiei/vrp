package ir.viratech.qaaf.domain;

import java.util.List;

import ir.viratech.qaaf.domain.OptimizationRequest.Point.Coordinate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ETAResponse {

	private Point start;
    private Point end;
    private int distance;
    private int duration;
    private int durationInTraffic;
    private String geometry;
	
    @Data
    @NoArgsConstructor
    public static class Point {
    	String type;
    	List<Double> coordinates;
    	
    	public Coordinate getCoordinate() {
    		Coordinate c = new Coordinate();
    		c.setLat(coordinates.get(0));
    		c.setLng(coordinates.get(1));
    		return c;
    	}
    }
}

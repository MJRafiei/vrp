package ir.viratech.qaaf.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Node {

	private Coordinate coordinate;
	private int deliverCapacity;
	private int takeCapacity;
	private int start;
	private int end;

	public Node(Coordinate coordinate, int start, int end) {
		this.coordinate = coordinate;
		this.start = start;
		this.end = end;
	}

	@Data
	@NoArgsConstructor
	public static class Coordinate {
		private double lng;
		private double lat;
	}
}

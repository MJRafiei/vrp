package ir.viratech.qaaf.domain;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Node implements Serializable {

	private Coordinate coordinate;
	private int deliverCapacity;
	private int loyalty;
	private int takeCapacity;
	private long start;
	private long end;

	public Node(Coordinate coordinate, long start, long end) {
		this.coordinate = coordinate;
		this.start = start;
		this.end = end;
	}

	@Override public boolean equals (Object o) {
		if (!(o instanceof Node)) return false;
		Node node = (Node) o;
		return (this.coordinate.equals(node.coordinate));
	}

	@Override public String toString () {
		return this.coordinate.toString();
	}

	@Data
	@NoArgsConstructor
	public static class Coordinate implements Serializable {
		private double lng;
		private double lat;

		@Override public boolean equals (Object o) {
			if (!(o instanceof Coordinate)) return false;
			Coordinate c = (Coordinate) o;
			return (Math.abs(this.lat - c.lat) < 0.00001) 
				&& (Math.abs(this.lng - c.lng) < 0.00001);
		}

		@Override public String toString() {
			return this.lng + "," + this.lat;
		}
	}

}

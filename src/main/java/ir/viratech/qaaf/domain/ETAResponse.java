package ir.viratech.qaaf.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ETAResponse {

	private int distance;
	private int duration;
	private int durationInTraffic;
	private String geometry;

}

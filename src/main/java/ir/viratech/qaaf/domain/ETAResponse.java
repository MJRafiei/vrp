package ir.viratech.qaaf.domain;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ETAResponse implements Serializable{

	private int distance;
	private int duration;
	private int durationInTraffic;
	private String geometry;

}

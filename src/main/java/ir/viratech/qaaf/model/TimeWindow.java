package ir.viratech.qaaf.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeWindow {
	private long start;
	private long end;
}

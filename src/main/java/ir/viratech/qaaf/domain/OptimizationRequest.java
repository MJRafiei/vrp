package ir.viratech.qaaf.domain;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OptimizationRequest {

	private List<Node> nodes;
	private int depot;
    private int maxDeliverCapacity;
    private int maxTakeCapacity;
    private int maxDistance;
    private int maxDuration;
    private int vehicleCount;

}

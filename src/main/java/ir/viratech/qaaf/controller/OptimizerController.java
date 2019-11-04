package ir.viratech.qaaf.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ir.viratech.qaaf.domain.ETAResponse;
import ir.viratech.qaaf.domain.OptimizationRequest;
import ir.viratech.qaaf.service.OptimizerService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OptimizerController {

	private final OptimizerService service;
	
	@PostMapping("/optimize")
	public void optimize(@RequestBody OptimizationRequest request) {
		
		ETAResponse[][] matrix = service.createMatrix(request.getPoints());
		//TODO
	}
	

}

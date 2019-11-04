package ir.viratech.qaaf.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import ir.viratech.qaaf.domain.ETAResponse;
import ir.viratech.qaaf.domain.OptimizationRequest.Point;

@Service
public class OptimizerService {

	public List<ETAResponse> createMatrix(List<Point> points) {
		
		RestTemplate template = new RestTemplate();
		String baseUrl = "http://213.232.124.172:8080/GIS/api/eta/route?";
		List<ETAResponse> result = new ArrayList<ETAResponse>();
		
		for (Point p1 : points) {
			for (Point p2 : points) {
				if(p1 == p2)
					continue;
				
				String url = baseUrl + "src_lat=" + p1.getCoord().getLat() + "&src_lon=" + p1.getCoord().getLng()
						+ "&dst_lat=" + p1.getCoord().getLat() + "&dst_lon=" + p1.getCoord().getLng();
				
				try {
					ETAResponse response = template.getForObject(url, ETAResponse.class);
					System.out.println(response);
					result.add(response);
				} catch (HttpClientErrorException e) {
					//TODO: in case of HTTP status 4xx
					System.out.println(400);
				} catch (HttpServerErrorException e) {
					// TODO: in case of HTTP status 5xx
					System.out.println(400);
				} catch (UnknownHttpStatusCodeException e) {
					// TODO: in case of an unknown HTTP status
					System.out.println("?");
				}
				
			}
		}
		
		return result;
	}
	
}

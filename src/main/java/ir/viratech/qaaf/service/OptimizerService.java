package ir.viratech.qaaf.service;

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

	public ETAResponse[][] createMatrix(List<Point> points) {
		
		RestTemplate template = new RestTemplate();
		String baseUrl = "http://213.232.124.172:8080/GIS/api/eta/route?";
		ETAResponse[][] result = new ETAResponse[points.size()][points.size()];
		
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[i].length; j++) {
				if (i == j)
					continue;
				
				Point pi = points.get(i);
				Point pj = points.get(j);
				String url = baseUrl + "src_lat=" + pi.getCoord().getLat() + "&src_lon=" + pi.getCoord().getLng()
						+ "&dst_lat=" + pj.getCoord().getLat() + "&dst_lon=" + pj.getCoord().getLng();
				
				try {
					ETAResponse response = template.getForObject(url, ETAResponse.class);
					System.out.println(response);
					result[i][j] = response;
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

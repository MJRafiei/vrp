package ir.viratech.qaaf.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import ir.viratech.qaaf.domain.ETAResponse;
import ir.viratech.qaaf.domain.Node;

@Service
public class OptimizerService {

	public ETAResponse[][] createMatrix(List<Node> nodes) {
		
		RestTemplate template = new RestTemplate();
		String baseUrl = "http://213.232.124.172:8080/GIS/api/eta/route?";
		ETAResponse[][] result = new ETAResponse[nodes.size()][nodes.size()];
		
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[i].length; j++) {
				if (i == j)
					continue;
				
				Node pi = nodes.get(i);
				Node pj = nodes.get(j);
				String url = baseUrl + "src_lat=" + pi.getCoordinate().getLat() + "&src_lon=" + pi.getCoordinate().getLng()
						+ "&dst_lat=" + pj.getCoordinate().getLat() + "&dst_lon=" + pj.getCoordinate().getLng();
				
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

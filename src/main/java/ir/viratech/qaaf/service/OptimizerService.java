package ir.viratech.qaaf.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import ir.viratech.qaaf.domain.ETAResponse;
import ir.viratech.qaaf.domain.ETAResponseCacheInterface;
import ir.viratech.qaaf.domain.MapBasedETAResponseCache;
import ir.viratech.qaaf.domain.Node;

@Service
public class OptimizerService {

	private String cachePath = "KryoCachedETAResponses";
	ETAResponseCacheInterface cache = new MapBasedETAResponseCache();
	boolean dirty = true;

	public ETAResponse[][] createMatrix(List<Node> nodes) {

		RestTemplate template = new RestTemplate();
		String baseUrl = "http://213.232.124.172:8080/eta/route?";
		if (dirty) {
			cache.loadFrom(cachePath);
			dirty = false;
		}
		ETAResponse[][] result = new ETAResponse[nodes.size()][nodes.size()];

		for (int i = 0; i < result.length; i++) {
			boolean flag = false;
			for (int j = 0; j < result[i].length; j++) {
				if (i == j)
					continue;
				System.out.println("(" + i + ", " + j + ")");

				Node pi = nodes.get(i);
				Node pj = nodes.get(j);
				String url = baseUrl + "src_lat=" + pi.getCoordinate().getLat() + "&src_lon="
						+ pi.getCoordinate().getLng() + "&dst_lat=" + pj.getCoordinate().getLat() + "&dst_lon="
						+ pj.getCoordinate().getLng();

				ETAResponse response = cache.getETA(pi, pj);

				int counter = 0;
				while (response == null && counter < 10) {
					try {
						flag = true;
						dirty = true;
						counter++;
						System.out.println(url);
						response = template.getForObject(url, ETAResponse.class);
						cache.addETA(pi, pj, response);
					} catch (HttpClientErrorException e) {
						// TODO: in case of HTTP status 4xx
						System.out.println(400);
					} catch (HttpServerErrorException e) {
						// TODO: in case of HTTP status 5xx
						System.out.println(500);
					} catch (UnknownHttpStatusCodeException e) {
						// TODO: in case of an unknown HTTP status
						System.out.println("?");
					}
				}
				result[i][j] = response;
			}
			if (flag)
				cache.saveTo(cachePath);
		}

		cache.saveTo(cachePath);

		return result;
	}

}

package net.vdanker.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsApi {

	@Autowired
	private MetricsCollection metrics;
	
	@RequestMapping("/plugin/metrics/mappings/metrics")
	public ResponseEntity<MetricsCollection> getMetrics() {
		return ResponseEntity.ok(metrics);
	}
}

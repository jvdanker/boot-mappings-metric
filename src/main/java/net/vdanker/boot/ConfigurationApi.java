package net.vdanker.boot;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.vdanker.boot.MetricsCollection.MethodConfiguration;

@RestController
@RequestMapping("/plugin/metrics")
public class ConfigurationApi {

	@Autowired
	private MetricsCollection metrics;
	
	@RequestMapping({"/","mappings"})
	public String getIndex() {
		return "redirect:index.html";
	}
	
	@RequestMapping(value="mappings/config", method=RequestMethod.POST)
	public ResponseEntity<?> setConfig(@RequestBody ConfigRequest request) {
		if ("disable".equals(request.action)) {
			MethodConfiguration config = metrics.getConfiguration().get(request.pattern);
			config.enabled = false;
			config.response = HttpStatus.SERVICE_UNAVAILABLE;
			metrics.setConfiguration(request.pattern, config);

			return ResponseEntity.ok().build();
		} else if ("enable".equals(request.action)) {
			MethodConfiguration config = metrics.getConfiguration().get(request.pattern);
			config.enabled = true;
			config.response = HttpStatus.OK;
			metrics.setConfiguration(request.pattern, config);

			return ResponseEntity.ok().build();
		}
		
		return ResponseEntity.badRequest().build();
	}
	
	public static class ConfigRequest {
		private String pattern;
		private String action;
		public String getPattern() {
			return pattern;
		}
		public void setPattern(String pattern) {
			this.pattern = pattern;
		}
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
	}
}

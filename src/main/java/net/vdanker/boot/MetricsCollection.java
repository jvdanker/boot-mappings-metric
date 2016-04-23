package net.vdanker.boot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class MetricsCollection {

	@Autowired
	private RequestMappingHandlerMapping handlerMapping;
	
	private Map<String, MethodMetric> metrics;
	
	public MetricsCollection() {
		this.metrics = new ConcurrentHashMap<>();
	}

	public synchronized void put(String name, long duration) {
		MethodMetric metric = metrics.get(name);
		if (metric == null) {
			metric = new MethodMetric();
			this.metrics.put(name, metric);
		} 
		
		metric.count++;
		metric.min = (metric.min == -1) ? duration : (duration < metric.min) ? duration : metric.min;
		metric.max = (duration > metric.max) ? duration : metric.max;
		metric.sum += duration;
		metric.avg = metric.sum / metric.count;
		metric.last = duration;
	}
	
	public Map<String, MethodMetric> getMetrics() {
		Map<String, MethodMetric> result = new HashMap<>();
		
		Set<String> handlerMappingPatterns = getHandlerMappingPatterns();
		handlerMappingPatterns.forEach(m -> {
			MethodMetric methodMetric = this.metrics.get(m);
			if (methodMetric == null) {
				this.metrics.put(m, new MethodMetric());
			}
			
			result.put(m, methodMetric);
		});
		
		return result;
	}
	
	private Set<String> getHandlerMappingPatterns() {
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = this.handlerMapping.getHandlerMethods();
		
		Set<String> result = new HashSet<>();
		handlerMethods.forEach((k, v) -> {
			result.addAll(k.getPatternsCondition().getPatterns());
		});
		
		return result;
	}

	public class MethodMetric {
		public int count;
		public long min = -1;
		public long avg;
		public long max;
		public long sum;
		public long last;
	}
}

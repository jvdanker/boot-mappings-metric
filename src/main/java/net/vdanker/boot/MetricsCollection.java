package net.vdanker.boot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsCollection {

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
		return metrics;
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

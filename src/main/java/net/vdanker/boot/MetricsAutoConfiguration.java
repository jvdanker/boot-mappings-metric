package net.vdanker.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ConditionalOnWebApplication
@ComponentScan
public class MetricsAutoConfiguration {

	@Bean
	public MetricsCollection metricsCollection() {
		 return new MetricsCollection();
	}
	
}

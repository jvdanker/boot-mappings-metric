package net.vdanker.boot;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.vdanker.boot.MetricsCollection.MethodConfiguration;

@Component
@Aspect
public class MetricsAspect {

	@Autowired
	private MetricsCollection metrics;
	
	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void anyRequestMapping() {
	}

	@Around("net.vdanker.boot.MetricsAspect.anyRequestMapping()")
	public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
		long startTimeMillis = System.currentTimeMillis();

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
		MethodConfiguration methodConfiguration = this.metrics.getConfiguration().get(request.getRequestURI());
		if (methodConfiguration != null && !methodConfiguration.enabled) {
			return ResponseEntity.status(methodConfiguration.response).build();
		}
		
        try {
            return pjp.proceed();
        } finally {
        	long lastTime = System.currentTimeMillis() - startTimeMillis;
            this.metrics.put(request.getRequestURI(), lastTime);
        }
	}
}

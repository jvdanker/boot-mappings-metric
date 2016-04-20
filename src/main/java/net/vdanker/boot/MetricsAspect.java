package net.vdanker.boot;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

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

		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();
		RequestMapping declaredAnnotation = method.getDeclaredAnnotation(RequestMapping.class);
		
        try {
            return pjp.proceed();
        } finally {
        	long lastTime = System.currentTimeMillis() - startTimeMillis;
        	
        	String[] value = declaredAnnotation.value();
        	if (value.length == 0) {
        		value = new String[] {signature.getName()};
        	}
            this.metrics.put(value[0], lastTime);
        }

	}
}

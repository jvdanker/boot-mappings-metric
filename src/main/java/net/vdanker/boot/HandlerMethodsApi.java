package net.vdanker.boot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RestController
@RequestMapping("/plugin/metrics")
public class HandlerMethodsApi {
	
	@Autowired
	private RequestMappingHandlerMapping handlerMapping;

	@RequestMapping("handlermethods")
	public List<PatternsRequestCondition> get() {
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = this.handlerMapping.getHandlerMethods();
		
		List<PatternsRequestCondition> result = new ArrayList<>();
		handlerMethods.forEach((k, v) -> {
			result.add(k.getPatternsCondition());
		});
		
		return result;
	}
}

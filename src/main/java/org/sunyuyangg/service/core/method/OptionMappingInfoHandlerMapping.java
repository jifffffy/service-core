package org.sunyuyangg.service.core.method;

import com.ibm.staf.service.STAFServiceInterfaceLevel30;
import org.sunyuyangg.service.core.FrameworkService;
import org.sunyuyangg.service.core.handler.AbstractHandlerMethodMapping;
import org.sunyuyangg.service.core.handler.HandlerMethod;

import java.lang.reflect.Method;


public abstract class OptionMappingInfoHandlerMapping extends AbstractHandlerMethodMapping<OptionMappingInfo> {

	protected OptionMappingInfoHandlerMapping() {
		setHandlerMethodMappingNamingStrategy(new OptionMappingInfoHandlerMethodMappingNamingStrategy());
	}

	@Override
	protected OptionMappingInfo getMatchingMapping(OptionMappingInfo mapping, STAFServiceInterfaceLevel30.RequestInfo request) throws Exception {
		return mapping.getMatching(request);
	}

	@Override
	protected String getMappingPath(OptionMappingInfo mapping) {
		return mapping.getMappingPath();
	}


	protected HandlerMethod createHandlerMethod(OptionMappingInfo mapping, Object handler, Method method) {
		HandlerMethod handlerMethod;

		if (handler instanceof String) {
			String beanName = (String) handler;
			handlerMethod = new HandlerMethod(beanName, obtainApplicationContext().getAutowireCapableBeanFactory(), method);
		} else {
			handlerMethod = new HandlerMethod(handler, method);
		}
		return handlerMethod;
	}

	@Override
	protected void registerHelp(OptionMappingInfo mapping) {
		FrameworkService.addHelpMessage(new HelpMessage(mapping));
	}
}

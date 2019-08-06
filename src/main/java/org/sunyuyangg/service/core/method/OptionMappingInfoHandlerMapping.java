package org.sunyuyangg.service.core.method;

import com.ibm.staf.service.STAFServiceInterfaceLevel30;
import org.sunyuyangg.service.core.FrameworkService;
import org.sunyuyangg.service.core.handler.AbstractHandlerMethodMapping;


public abstract class OptionMappingInfoHandlerMapping extends AbstractHandlerMethodMapping<OptionMappingInfo> {

	protected OptionMappingInfoHandlerMapping() {
		setHandlerMethodMappingNamingStrategy(new OptionMappingInfoHandlerMethodMappingNamingStrategy());
	}

	@Override
	protected OptionMappingInfo getMatchingMapping(OptionMappingInfo mapping, STAFServiceInterfaceLevel30.RequestInfo request) {
		return mapping.getMatching(request);
	}

	@Override
	protected String getMappingPath(OptionMappingInfo mapping) {
		return mapping.getMappingPath();
	}

	@Override
	protected void processHandlerMethod(AbstractHandlerMethodMapping<OptionMappingInfo>.Match match) {
		match.handlerMethod.setParseResult(match.mapping.getParseResult());
	}

	@Override
	protected void registerHelp(OptionMappingInfo mapping) {
		FrameworkService.addHelpMessage(new HelpMessage(mapping));
	}
}

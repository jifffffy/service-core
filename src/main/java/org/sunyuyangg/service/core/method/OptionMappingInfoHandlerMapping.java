package org.sunyuyangg.service.core.method;


import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30;
import com.sun.org.apache.regexp.internal.RE;
import org.pmw.tinylog.Logger;
import org.sunyuyangg.service.core.FrameworkService;
import org.sunyuyangg.service.core.annotation.Option;
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
		StringBuffer stringBuffer = new StringBuffer();
		mapping.getOptions().stream().forEach(option -> stringBuffer.append(createHelp(option)));
		FrameworkService.addHelpMsg(stringBuffer.toString());
	}

	private String createHelp(OptionMappingInfo.Option option) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(option.name.toUpperCase() );
		stringBuffer.append(" ");
		if(option.valueRequirement == STAFCommandParser.VALUEREQUIRED) {
			stringBuffer.append("<" + option.name.toLowerCase() + ">");
		}

		if(option.valueRequirement == STAFCommandParser.VALUEALLOWED) {
			stringBuffer.append("[" + option.name.toLowerCase() + "]");
		}
		return stringBuffer.toString();
	}
}

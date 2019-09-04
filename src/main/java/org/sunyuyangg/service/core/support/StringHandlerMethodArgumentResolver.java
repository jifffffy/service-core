package org.sunyuyangg.service.core.support;

import com.ibm.staf.service.STAFCommandParseResult;
import org.springframework.core.MethodParameter;

public class StringHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter, STAFCommandParseResult parseResult) {
        return parameter.getParameterType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, STAFCommandParseResult parseResult) throws Exception {
        return parseResult.optionValue(parameter.getParameterName());
    }
}

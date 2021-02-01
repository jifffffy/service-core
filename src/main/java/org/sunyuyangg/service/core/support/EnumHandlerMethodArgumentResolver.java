package org.sunyuyangg.service.core.support;

import com.ibm.staf.service.STAFCommandParseResult;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.core.MethodParameter;


public class EnumHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter, STAFCommandParseResult parseResult) {
        return parameter.getParameterType().isEnum();
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, STAFCommandParseResult parseResult) throws Exception {
        return EnumUtils.getEnum((Class<Enum>)parameter.getParameterType(), parseResult.optionValue(parameter.getParameterName()));
    }
}

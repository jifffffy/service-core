package org.sunyuyangg.service.core.support;

import com.ibm.staf.service.STAFCommandParseResult;
import org.pmw.tinylog.Logger;
import org.springframework.core.MethodParameter;
import org.sunyuyangg.service.core.Util;

import java.io.IOException;

public class JsonHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private Object result;


    public JsonHandlerMethodArgumentResolver() {

    }

    @Override
    public boolean supportsParameter(MethodParameter parameter, STAFCommandParseResult parseResult) {
        try {
            result = Util.objectMapper().readValue(parseResult.optionValue(parameter.getParameterName()), parameter.getParameterType());
            return true;
        } catch (IOException e) {
            Logger.error(e);
            return false;
        }
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, STAFCommandParseResult parseResult) throws Exception {
         return result;
    }

}

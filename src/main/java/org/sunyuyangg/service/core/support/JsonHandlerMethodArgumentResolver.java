package org.sunyuyangg.service.core.support;

import com.ibm.staf.service.STAFCommandParseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.sunyuyangg.service.core.Util;

import java.io.IOException;

public class JsonHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    protected final Log logger = LogFactory.getLog(getClass());

    public JsonHandlerMethodArgumentResolver() {

    }

    @Override
    public boolean supportsParameter(MethodParameter parameter, STAFCommandParseResult parseResult) {
        return isJSONValid(parseResult.optionValue(parameter.getParameterName()));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, STAFCommandParseResult parseResult) throws Exception {
         return Util.objectMapper().readValue(parseResult.optionValue(parameter.getParameterName()), parameter.getParameterType());
    }

    private boolean isJSONValid(String jsonInString ) {
        try {
            Util.objectMapper().readTree(jsonInString);
            return true;
        } catch (IOException e) {
            logger.error(e);
            return false;
        }
    }

}

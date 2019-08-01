package org.sunyuyangg.service.core.support;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.staf.service.STAFCommandParseResult;
import org.pmw.tinylog.Logger;
import org.springframework.core.MethodParameter;

import java.io.IOException;

public class SampleHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private ObjectMapper objectMapper ;
    private Object result;


    public SampleHandlerMethodArgumentResolver() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter, STAFCommandParseResult parseResult) {
        try {
            result = objectMapper.readValue(parseResult.optionValue(parameter.getParameterName()), parameter.getParameterType());
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

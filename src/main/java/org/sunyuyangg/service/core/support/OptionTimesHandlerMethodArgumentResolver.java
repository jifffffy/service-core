package org.sunyuyangg.service.core.support;

import com.ibm.staf.service.STAFCommandParseResult;
import org.springframework.core.MethodParameter;

import java.util.ArrayList;
import java.util.List;

public class OptionTimesHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter, STAFCommandParseResult parseResult) {
        return parseResult.optionTimes(parameter.getParameterName()) > 1;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, STAFCommandParseResult parseResult) throws Exception {
        assert parameter.getParameterType().getSimpleName().equalsIgnoreCase(List.class.getSimpleName());
        int times = parseResult.optionTimes(parameter.getParameterName());
        List<String> list = new ArrayList<>();
        for(int i =1; i <= times; i++) {
            list.add(parseResult.optionValue(parameter.getParameterName(), i));
        }
        return list;
    }
}

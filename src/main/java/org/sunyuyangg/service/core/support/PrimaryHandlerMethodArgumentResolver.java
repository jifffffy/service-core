package org.sunyuyangg.service.core.support;

import com.ibm.staf.service.STAFCommandParseResult;
import org.springframework.core.MethodParameter;
import org.springframework.util.ClassUtils;

public class PrimaryHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter, STAFCommandParseResult parseResult) {
        return ClassUtils.isPrimitiveOrWrapper(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, STAFCommandParseResult parseResult) throws Exception {
        return parse(parameter.getParameterType(), parseResult.optionValue(parameter.getParameterName()));
    }

    private Object parse(Class clazz, String value) throws Exception {
        String type = clazz.getSimpleName().toLowerCase();
        if (type.equalsIgnoreCase(Boolean.class.getSimpleName())) {
            return Boolean.valueOf(value);
        } else if (type.equalsIgnoreCase(Integer.class.getSimpleName())) {
            return Integer.valueOf(value);
        } else if (type.equalsIgnoreCase(Long.class.getSimpleName())) {
            return Long.valueOf(value);
        } else if (type.equalsIgnoreCase(Double.class.getSimpleName())) {
            return Double.valueOf(value);
        } else if (type.equalsIgnoreCase(Float.class.getSimpleName())) {
            return Float.valueOf(value);
        } else {
            throw new Exception("can not convert the primary parameter");
        }
    }
}

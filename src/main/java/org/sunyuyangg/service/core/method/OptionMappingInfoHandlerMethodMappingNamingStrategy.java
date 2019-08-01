package org.sunyuyangg.service.core.method;


import org.pmw.tinylog.Logger;
import org.sunyuyangg.service.core.handler.HandlerMethod;
import org.sunyuyangg.service.core.handler.HandlerMethodMappingNamingStrategy;

public class OptionMappingInfoHandlerMethodMappingNamingStrategy implements HandlerMethodMappingNamingStrategy<OptionMappingInfo> {

    /** Separator between the type and method-level parts of a HandlerMethod mapping name */
    public static final String SEPARATOR = "#";
    public static final String SUFFIX = "CONTROLLER";

    @Override
    public String getName(HandlerMethod handlerMethod, OptionMappingInfo mapping) {
        if (mapping.getName() != null) {
            return mapping.getName();
        }
        StringBuilder sb = new StringBuilder();
        String simpleTypeName = handlerMethod.getBeanType().getSimpleName().toUpperCase();
        if(simpleTypeName.contains(SUFFIX)) {
            simpleTypeName = simpleTypeName.substring(0, simpleTypeName.indexOf(SUFFIX));
        }
        sb.append(simpleTypeName);
        sb.append(SEPARATOR).append(handlerMethod.getMethod().getName().toUpperCase());
        Logger.info("register mapping name : {}", sb.toString());
        return sb.toString();
    }
}

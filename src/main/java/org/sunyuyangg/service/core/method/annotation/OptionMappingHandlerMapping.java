package org.sunyuyangg.service.core.method.annotation;


import com.ibm.staf.service.STAFCommandParser;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.sunyuyangg.service.core.FrameworkService;
import org.sunyuyangg.service.core.annotation.OptionMapping;
import org.sunyuyangg.service.core.method.OptionMappingInfo;
import org.sunyuyangg.service.core.method.OptionMappingInfoHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;

public class OptionMappingHandlerMapping extends OptionMappingInfoHandlerMapping {


    @Override
    protected OptionMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        return createOptionMappingInfo(method, handlerType);
    }

    private OptionMappingInfo createOptionMappingInfo(AnnotatedElement element, Class<?> handlerType) {
        OptionMapping optionMapping = AnnotatedElementUtils.findMergedAnnotation(element, OptionMapping.class);
        if (optionMapping == null) {
            return null;
        }
        OptionMappingInfo.Builder builder = OptionMappingInfo.builder(optionMapping.maxArgs(), optionMapping.caseSensitive());
        builder.name(optionMapping.name());
        String name = handlerType.getSimpleName().toUpperCase();
        if(name.contains("CONTROLLER")) {
            name = name.substring(0, name.indexOf("CONTROLLER"));
        }
        builder.option(name, 1, STAFCommandParser.VALUENOTALLOWED);
        builder.desc(optionMapping.desc());
        Arrays.asList(optionMapping.options()).forEach(option -> builder.option(option.name(), option.maxAllowed(), option.valueRequirement()));
        Arrays.asList(optionMapping.optionGroup()).forEach(optionGroup -> builder.optionGroup(optionGroup.names(), optionGroup.min(), optionGroup.max()));
        Arrays.asList(optionMapping.optionNeeds()).forEach(optionNeeds -> builder.optionNeed(optionNeeds.needers(), optionNeeds.needees()));
        return builder.build();
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return (AnnotatedElementUtils.hasAnnotation(beanType, Controller.class));
    }

}

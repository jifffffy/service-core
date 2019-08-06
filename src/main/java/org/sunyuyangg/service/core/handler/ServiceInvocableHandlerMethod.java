package org.sunyuyangg.service.core.handler;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParseResult;
import org.pmw.tinylog.Logger;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.sunyuyangg.service.core.adapter.ModelAndView;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ServiceInvocableHandlerMethod extends InvocableHandlerMethod {

    public ServiceInvocableHandlerMethod(Object bean, Method method) {
        super(bean, method);
    }

    public ServiceInvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    private String formatErrorForReturnValue(String message, @Nullable Object returnValue) {
        StringBuilder sb = new StringBuilder(message);
        if (returnValue != null) {
            sb.append(" [type=").append(returnValue.getClass().getName()).append("]");
        }
        sb.append(" [value=").append(returnValue).append("]");
        return getDetailedErrorMessage(sb.toString());
    }

    public void invokeAndHandle(STAFCommandParseResult request, ModelAndView modelAndView) throws Exception{
        Object returnValue = invokeForRequest(request);
        modelAndView.setModel(returnValue);
        modelAndView.setRc(STAFResult.Ok);
    }

    private Object invokeForRequest(STAFCommandParseResult parseResult) throws Exception{
        Object[] args = getMethodArgumentValues(parseResult);
        Logger.info("Invoking {} with arguments {}", ClassUtils.getQualifiedMethodName(getMethod(), getBeanType()), Arrays.toString(args));
        Object returnValue = doInvoke(args);
        Logger.info("Method [{}] returned [{}]", ClassUtils.getQualifiedMethodName(getMethod(), getBeanType()), returnValue);
        return returnValue;
    }
}

package org.sunyuyangg.service.core.handler;

import com.ibm.staf.service.STAFCommandParseResult;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.sunyuyangg.service.core.method.MappingInfo;
import org.sunyuyangg.service.core.method.OptionMappingInfo;
import org.sunyuyangg.service.core.support.HandlerMethodArgumentResolverComposite;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class InvocableHandlerMethod extends HandlerMethod {

    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private HandlerMethodArgumentResolverComposite argumentResolvers = new HandlerMethodArgumentResolverComposite();


    public InvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);

    }

    public InvocableHandlerMethod(ServiceRequest serviceRequest, Object bean, Method method) {
        super(serviceRequest, bean, method);
    }

    public void setHandlerMethodArgumentResolvers(HandlerMethodArgumentResolverComposite argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }


    protected Object[] getMethodArgumentValues(ServiceRequest serviceRequest) throws Exception {
        MethodParameter[] parameters = getMethodParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);

            if (this.argumentResolvers.supportsParameter(parameter, serviceRequest.getParseResult())) {
                try {
                    args[i] = this.argumentResolvers.resolveArgument(
                            parameter, serviceRequest.getParseResult());
                    continue;
                } catch (Exception ex) {
                    // Leave stack trace for later, e.g. AbstractHandlerExceptionResolver
                    String message = ex.getMessage();
                    if (!message.contains(parameter.getExecutable().toGenericString())) {
                        logger.debug(formatArgumentError(parameter, message));
                    }
                    throw ex;
                }
            }

            if (isNullable(parameter, serviceRequest.getMapping())) {
                continue;
            }

            if (args[i] == null) {
                throw new IllegalStateException(formatArgumentError(parameter, "No suitable resolver"));
            }
        }
        return args;
    }

    private boolean isNullable(MethodParameter parameter, MappingInfo mapping) {

        return mapping.isNullable(parameter.getParameterName());
    }

    private static String formatArgumentError(MethodParameter param, String message) {
        return "Could not resolve parameter [" + param.getParameterIndex() + "] in " +
                param.getExecutable().toGenericString() + (StringUtils.hasText(message) ? ": " + message : "");
    }


    /**
     * Invoke the handler method with the given argument values.
     */
    protected Object doInvoke(Object... args) throws Exception {
        ReflectionUtils.makeAccessible(getBridgedMethod());
        try {
            return getBridgedMethod().invoke(getBean(), args);
        } catch (IllegalArgumentException ex) {
            assertTargetBean(getBridgedMethod(), getBean(), args);
            String text = (ex.getMessage() != null ? ex.getMessage() : "Illegal argument");
            throw new IllegalStateException(getInvocationErrorMessage(text, args), ex);
        } catch (InvocationTargetException ex) {
            // Unwrap for HandlerExceptionResolvers ...
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException) targetException;
            } else if (targetException instanceof Error) {
                throw (Error) targetException;
            } else if (targetException instanceof Exception) {
                throw (Exception) targetException;
            } else {
                String text = getInvocationErrorMessage("Failed to invoke handler method", args);
                throw new IllegalStateException(text, targetException);
            }
        }
    }

    /**
     * Assert that the target bean class is an instance of the class where the given
     * method is declared. In some cases the actual controller instance at request-
     * processing time may be a JDK dynamic proxy (lazy initialization, prototype
     * beans, and others). {@code @Controller}'s that require proxying should prefer
     * class-based proxy mechanisms.
     */
    private void assertTargetBean(Method method, Object targetBean, Object[] args) {
        Class<?> methodDeclaringClass = method.getDeclaringClass();
        Class<?> targetBeanClass = targetBean.getClass();
        if (!methodDeclaringClass.isAssignableFrom(targetBeanClass)) {
            String text = "The mapped handler method class '" + methodDeclaringClass.getName() +
                    "' is not an instance of the actual controller bean class '" +
                    targetBeanClass.getName() + "'. If the controller requires proxying " +
                    "(e.g. due to @Transactional), please use class-based proxying.";
            throw new IllegalStateException(getInvocationErrorMessage(text, args));
        }
    }

    private String getInvocationErrorMessage(String text, Object[] resolvedArgs) {
        StringBuilder sb = new StringBuilder(getDetailedErrorMessage(text));
        sb.append("Resolved arguments: \n");
        for (int i = 0; i < resolvedArgs.length; i++) {
            sb.append("[").append(i).append("] ");
            if (resolvedArgs[i] == null) {
                sb.append("[null] \n");
            } else {
                sb.append("[type=").append(resolvedArgs[i].getClass().getName()).append("] ");
                sb.append("[value=").append(resolvedArgs[i]).append("]\n");
            }
        }
        return sb.toString();
    }

    /**
     * Adds HandlerMethod details such as the bean type and method signature to the message.
     *
     * @param text error message to append the HandlerMethod details to
     */
    protected String getDetailedErrorMessage(String text) {
        StringBuilder sb = new StringBuilder(text).append("\n");
        sb.append("HandlerMethod details: \n");
        sb.append("Controller [").append(getBeanType().getName()).append("]\n");
        sb.append("Method [").append(getBridgedMethod().toGenericString()).append("]\n");
        return sb.toString();
    }
}

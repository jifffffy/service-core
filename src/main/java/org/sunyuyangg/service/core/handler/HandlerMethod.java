package org.sunyuyangg.service.core.handler;


import com.ibm.staf.service.STAFCommandParseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class HandlerMethod {

    /**
     * Logger that is available to subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    private final Object bean;

    @Nullable
    private final BeanFactory beanFactory;

    private final Class<?> beanType;

    private final Method method;

    private final ServiceRequest serviceRequest;

    private final Method bridgedMethod;

    private final MethodParameter[] parameters;

    @Nullable
    private int responseStatus;

    @Nullable
    private String responseStatusReason;

    @Nullable
    private HandlerMethod resolvedFromHandlerMethod;



    /**
     * Create an instance from a bean instance and a method.
     */
    public HandlerMethod(ServiceRequest serviceRequest, Object bean, Method method) {
        Assert.notNull(bean, "Bean is required");
        Assert.notNull(method, "Method is required");
        this.bean = bean;
        this.beanFactory = null;
        this.serviceRequest = serviceRequest;
        this.beanType = ClassUtils.getUserClass(bean);
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = initMethodParameters();
        evaluateResponseStatus();
    }

    /**
     * Create an instance from a bean instance, method name, and parameter types.
     *
     * @throws NoSuchMethodException when the method cannot be found
     */
    public HandlerMethod(ServiceRequest serviceRequest, Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Assert.notNull(bean, "Bean is required");
        Assert.notNull(methodName, "Method name is required");
        this.bean = bean;
        this.beanFactory = null;
        this.serviceRequest = serviceRequest;
        this.beanType = ClassUtils.getUserClass(bean);
        this.method = bean.getClass().getMethod(methodName, parameterTypes);
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(this.method);
        this.parameters = initMethodParameters();
        evaluateResponseStatus();
    }

    /**
     * Create an instance from a bean name, a method, and a {@code BeanFactory}.
     * The method {@link #createWithResolvedBean()} may be used later to
     * re-create the {@code HandlerMethod} with an initialized bean.
     */
    public HandlerMethod(ServiceRequest serviceRequest, String beanName, BeanFactory beanFactory, Method method) {
        Assert.hasText(beanName, "Bean name is required");
        Assert.notNull(beanFactory, "BeanFactory is required");
        Assert.notNull(method, "Method is required");
        this.bean = beanName;
        this.beanFactory = beanFactory;
        this.serviceRequest = serviceRequest;
        Class<?> beanType = beanFactory.getType(beanName);
        if (beanType == null) {
            throw new IllegalStateException("Cannot resolve bean type for bean with name '" + beanName + "'");
        }
        this.beanType = ClassUtils.getUserClass(beanType);
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = initMethodParameters();
        evaluateResponseStatus();
    }

    /**
     * Copy constructor for use in subclasses.
     */
    protected HandlerMethod(HandlerMethod handlerMethod) {
        Assert.notNull(handlerMethod, "HandlerMethod is required");
        this.bean = handlerMethod.bean;
        this.serviceRequest = handlerMethod.serviceRequest;
        this.beanFactory = handlerMethod.beanFactory;
        this.beanType = handlerMethod.beanType;
        this.method = handlerMethod.method;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.parameters = handlerMethod.parameters;
        this.responseStatus = handlerMethod.responseStatus;
        this.responseStatusReason = handlerMethod.responseStatusReason;
        this.resolvedFromHandlerMethod = handlerMethod.resolvedFromHandlerMethod;
    }

    /**
     * Re-create HandlerMethod with the resolved handler.
     */
    private HandlerMethod(HandlerMethod handlerMethod, Object handler) {
        Assert.notNull(handlerMethod, "HandlerMethod is required");
        Assert.notNull(handler, "Handler object is required");
        this.bean = handler;
        this.serviceRequest = handlerMethod.serviceRequest;
        this.beanFactory = handlerMethod.beanFactory;
        this.beanType = handlerMethod.beanType;
        this.method = handlerMethod.method;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.parameters = handlerMethod.parameters;
        this.responseStatus = handlerMethod.responseStatus;
        this.responseStatusReason = handlerMethod.responseStatusReason;
        this.resolvedFromHandlerMethod = handlerMethod;
    }


    private MethodParameter[] initMethodParameters() {
        int count = this.bridgedMethod.getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            HandlerMethodParameter parameter = new HandlerMethodParameter(i);
            GenericTypeResolver.resolveParameterType(parameter, this.beanType);
            result[i] = parameter;
        }
        return result;
    }

    private void evaluateResponseStatus() {
    }

    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }

    /**
     * Return the bean for this handler method.
     */
    public Object getBean() {
        return this.bean;
    }

    /**
     * Return the method for this handler method.
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * This method returns the type of the handler for this handler method.
     * <p>Note that if the bean type is a CGLIB-generated class, the original
     * user-defined class is returned.
     */
    public Class<?> getBeanType() {
        return this.beanType;
    }

    /**
     * If the bean method is a bridge method, this method returns the bridged
     * (user-defined) method. Otherwise it returns the same method as {@link #getMethod()}.
     */
    protected Method getBridgedMethod() {
        return this.bridgedMethod;
    }

    /**
     * Return the method parameters for this handler method.
     */
    public MethodParameter[] getMethodParameters() {
        return this.parameters;
    }

    @Nullable
    protected int getResponseStatus() {
        return this.responseStatus;
    }


    @Nullable
    protected String getResponseStatusReason() {
        return this.responseStatusReason;
    }

    /**
     * Return the HandlerMethod return type.
     */
    public MethodParameter getReturnType() {
        return new HandlerMethodParameter(-1);
    }

    /**
     * Return the actual return value type.
     */
    public MethodParameter getReturnValueType(@Nullable Object returnValue) {
        return new ReturnValueMethodParameter(returnValue);
    }

    /**
     * Return {@code true} if the method return type is void, {@code false} otherwise.
     */
    public boolean isVoid() {
        return Void.TYPE.equals(getReturnType().getParameterType());
    }

    /**
     * Return a single annotation on the underlying method traversing its super methods
     * if no annotation can be found on the given method itself.
     * <p>Also supports <em>merged</em> composed annotations with attribute
     * overrides as of Spring Framework 4.2.2.
     *
     * @param annotationType the type of annotation to introspect the method for
     * @return the annotation, or {@code null} if none found
     * @see AnnotatedElementUtils#findMergedAnnotation
     */
    @Nullable
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.findMergedAnnotation(this.method, annotationType);
    }

    /**
     * Return whether the parameter is declared with the given annotation type.
     *
     * @param annotationType the annotation type to look for
     * @see AnnotatedElementUtils#hasAnnotation
     * @since 4.3
     */
    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.hasAnnotation(this.method, annotationType);
    }

    /**
     * Return the HandlerMethod from which this HandlerMethod instance was
     * resolved via {@link #createWithResolvedBean()}.
     */
    @Nullable
    public HandlerMethod getResolvedFromHandlerMethod() {
        return this.resolvedFromHandlerMethod;
    }

    /**
     * If the provided instance contains a bean name rather than an object instance,
     * the bean name is resolved before a {@link HandlerMethod} is created and returned.
     */
    public HandlerMethod createWithResolvedBean() {
        Object handler = this.bean;
        if (this.bean instanceof String) {
            Assert.state(this.beanFactory != null, "Cannot resolve bean name without BeanFactory");
            String beanName = (String) this.bean;
            handler = this.beanFactory.getBean(beanName);
        }
        return new HandlerMethod(this, handler);
    }

    /**
     * Return a short representation of this handler method for log message purposes.
     *
     * @since 4.3
     */
    public String getShortLogMessage() {
        int args = this.method.getParameterCount();
        return getBeanType().getName() + "#" + this.method.getName() + "[" + args + " args]";
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HandlerMethod)) {
            return false;
        }
        HandlerMethod otherMethod = (HandlerMethod) other;
        return (this.bean.equals(otherMethod.bean) && this.method.equals(otherMethod.method));
    }

    @Override
    public int hashCode() {
        return (this.bean.hashCode() * 31 + this.method.hashCode());
    }

    @Override
    public String toString() {
        return this.method.toGenericString();
    }


    /**
     * A MethodParameter with HandlerMethod-specific behavior.
     */
    protected class HandlerMethodParameter extends SynthesizingMethodParameter {

        public HandlerMethodParameter(int index) {
            super(HandlerMethod.this.bridgedMethod, index);
        }

        protected HandlerMethodParameter(HandlerMethodParameter original) {
            super(original);
        }

        @Override
        public Class<?> getContainingClass() {
            return HandlerMethod.this.getBeanType();
        }

        @Override
        public <T extends Annotation> T getMethodAnnotation(Class<T> annotationType) {
            return HandlerMethod.this.getMethodAnnotation(annotationType);
        }

        @Override
        public <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationType) {
            return HandlerMethod.this.hasMethodAnnotation(annotationType);
        }

        @Override
        public HandlerMethodParameter clone() {
            return new HandlerMethodParameter(this);
        }
    }


    /**
     * A MethodParameter for a HandlerMethod return type based on an actual return value.
     */
    private class ReturnValueMethodParameter extends HandlerMethodParameter {

        @Nullable
        private final Object returnValue;

        public ReturnValueMethodParameter(@Nullable Object returnValue) {
            super(-1);
            this.returnValue = returnValue;
        }

        protected ReturnValueMethodParameter(ReturnValueMethodParameter original) {
            super(original);
            this.returnValue = original.returnValue;
        }

        @Override
        public Class<?> getParameterType() {
            return (this.returnValue != null ? this.returnValue.getClass() : super.getParameterType());
        }

        @Override
        public ReturnValueMethodParameter clone() {
            return new ReturnValueMethodParameter(this);
        }
    }

}


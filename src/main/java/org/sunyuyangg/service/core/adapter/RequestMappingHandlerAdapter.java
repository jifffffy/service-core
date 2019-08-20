package org.sunyuyangg.service.core.adapter;


import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParseResult;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.sunyuyangg.service.core.handler.HandlerMethod;
import org.sunyuyangg.service.core.handler.ServiceInvocableHandlerMethod;
import org.sunyuyangg.service.core.handler.ServiceRequest;
import org.sunyuyangg.service.core.support.*;

import java.util.ArrayList;
import java.util.List;


public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter
        implements BeanFactoryAware, InitializingBean {

    @Nullable
    private ConfigurableBeanFactory beanFactory;
    private HandlerMethodArgumentResolverComposite argumentResolvers;
    private List<HandlerMethodArgumentResolver> customArgumentResolvers;

    @Override
    protected boolean supportsInternal(HandlerMethod handlerMethod) {
        return true;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.beanFactory = (ConfigurableBeanFactory) beanFactory;
        }
    }

    @Nullable
    protected ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.argumentResolvers == null) {
            List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
    }

    /**
     * Return the list of argument resolvers to use including built-in resolvers
     * and custom resolvers provided via {@link #setCustomArgumentResolvers}.
     */
    private List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        resolvers.add(new PrimaryHandlerMethodArgumentResolver());
        resolvers.add(new StringHandlerMethodArgumentResolver());
        resolvers.add(new OptionTimesHandlerMethodArgumentResolver());
        resolvers.add(new JsonHandlerMethodArgumentResolver());
        // Custom arguments
        if (getCustomArgumentResolvers() != null) {
            resolvers.addAll(getCustomArgumentResolvers());
        }
        return resolvers;
    }

    public void setCustomArgumentResolvers(@Nullable List<HandlerMethodArgumentResolver> argumentResolvers) {
        this.customArgumentResolvers = argumentResolvers;
    }

    /**
     * Return the custom argument resolvers, or {@code null}.
     */
    @Nullable
    public List<HandlerMethodArgumentResolver> getCustomArgumentResolvers() {
        return this.customArgumentResolvers;
    }


    @Override
    protected ModelAndView handleInternal(ServiceRequest request, STAFResult response, HandlerMethod handlerMethod) throws Exception {
        ModelAndView mav;
        mav = invokeHandlerMethod(request, response, handlerMethod);
        return mav;
    }

    private ModelAndView invokeHandlerMethod(ServiceRequest request, STAFResult response, HandlerMethod handlerMethod) throws Exception {

        ServiceInvocableHandlerMethod invocableMethod = createInvocableHandlerMethod(handlerMethod);
        if (this.argumentResolvers != null) {
            invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        }
        ModelAndView modelAndView = new ModelAndView();
        invocableMethod.invokeAndHandle(request, modelAndView);
        return modelAndView;
    }


    private ServiceInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
        return new ServiceInvocableHandlerMethod(handlerMethod);
    }

}

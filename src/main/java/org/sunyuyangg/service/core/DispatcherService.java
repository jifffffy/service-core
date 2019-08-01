package org.sunyuyangg.service.core;


import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import org.pmw.tinylog.Logger;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.sunyuyangg.service.core.adapter.ModelAndView;
import org.sunyuyangg.service.core.exception.NoHandlerFoundException;
import org.sunyuyangg.service.core.handler.HandlerMethod;

import java.io.IOException;
import java.util.*;

public class DispatcherService extends FrameworkService {

    /**
     * List of HandlerMappings used by this servlet
     */
    @Nullable
    private List<HandlerMapping> handlerMappings;

    /**
     * List of HandlerAdapters used by this servlet
     */
    @Nullable
    private List<HandlerAdapter> handlerAdapters;

    @Nullable
    private View view;

    private static final String DEFAULT_STRATEGIES_PATH = "/Dispatcher.properties";

    /**
     * Well-known name for the HandlerMapping object in the bean factory for this namespace.
     * Only used when "detectAllHandlerMappings" is turned off.
     */
    public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";

    /**
     * Well-known name for the HandlerAdapter object in the bean factory for this namespace.
     * Only used when "detectAllHandlerAdapters" is turned off.
     */
    public static final String HANDLER_ADAPTER_BEAN_NAME = "handlerAdapter";

    public static final String HANDLER_VIEW_BEAN_NAME = "view";

    /** Throw a NoHandlerFoundException if no Handler was found to process this request? **/
    private boolean throwExceptionIfNoHandlerFound = false;

    /**
     * Detect all HandlerMappings or just expect "handlerMapping" bean?
     */
    private boolean detectAllHandlerMappings = true;

    /**
     * Detect all HandlerAdapters or just expect "handlerAdapter" bean?
     */
    private boolean detectAllHandlerAdapters = true;

    private static final Properties defaultStrategies;

    static {
        // Load default strategy implementations from properties file.
        // This is currently strictly internal and not meant to be customized
        // by application developers.
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, DispatcherService.class);
            defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load '" + DEFAULT_STRATEGIES_PATH + "': " + ex.getMessage());
        }
    }

    public DispatcherService(int serviceInvalidSerialNumber) {
        super(serviceInvalidSerialNumber);
    }

    public DispatcherService(int serviceInvalidSerialNumber, ApplicationContext applicationContext) {
        super(serviceInvalidSerialNumber, applicationContext);
    }


    @Override
    protected void initStrategies(ApplicationContext context) {
        super.initStrategies(context);
        try {
            initHandlerMappings(context);
            initHandlerAdapters(context);
            initView(context);
        } catch (Exception e) {
            Logger.error(e);
        }

    }

    private void initView(ApplicationContext context) throws Exception{
        try {
            View view = context.getBean(HANDLER_VIEW_BEAN_NAME, View.class);
            this.view = view;
        } catch (NoSuchBeanDefinitionException ex) {
            // Ignore, we'll add a default HandlerAdapter later.
        }

        if(view == null) {
            this.view = getDefaultStrategies(context, View.class).get(0);
        }
    }

    public void setDetectAllHandlerMappings(boolean detectAllHandlerMappings) {
        this.detectAllHandlerMappings = detectAllHandlerMappings;
    }

    public void setDetectAllHandlerAdapters(boolean detectAllHandlerAdapters) {
        this.detectAllHandlerAdapters = detectAllHandlerAdapters;
    }

    private void initHandlerAdapters(ApplicationContext context) throws Exception{
        this.handlerAdapters = null;

        if (this.detectAllHandlerAdapters) {
            // Find all HandlerAdapters in the ApplicationContext, including ancestor contexts.
            Map<String, HandlerAdapter> matchingBeans =
                    BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerAdapter.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.handlerAdapters = new ArrayList<>(matchingBeans.values());
                // We keep HandlerAdapters in sorted order.
                AnnotationAwareOrderComparator.sort(this.handlerAdapters);
            }
        } else {
            try {
                HandlerAdapter ha = context.getBean(HANDLER_ADAPTER_BEAN_NAME, HandlerAdapter.class);
                this.handlerAdapters = Collections.singletonList(ha);
            } catch (NoSuchBeanDefinitionException ex) {
                // Ignore, we'll add a default HandlerAdapter later.
            }
        }

        // Ensure we have at least some HandlerAdapters, by registering
        // default HandlerAdapters if no other adapters are found.
        if (this.handlerAdapters == null) {
            this.handlerAdapters = getDefaultStrategies(context, HandlerAdapter.class);
            Logger.info("No HandlerAdapters declared for servlet '" + getServiceName() +
                    "': using default strategies from DispatcherServlet.properties");

        }
    }

    private void initHandlerMappings(ApplicationContext context) throws Exception{
        this.handlerMappings = null;

        if (this.detectAllHandlerMappings) {
            // Find all HandlerMappings in the ApplicationContext, including ancestor contexts.
            Map<String, HandlerMapping> matchingBeans =
                    BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.handlerMappings = new ArrayList<>(matchingBeans.values());
                // We keep HandlerMappings in sorted order.
                AnnotationAwareOrderComparator.sort(this.handlerMappings);
            }
        } else {
            try {
                HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
                this.handlerMappings = Collections.singletonList(hm);
            } catch (NoSuchBeanDefinitionException ex) {
                // Ignore, we'll add a default HandlerMapping later.
            }
        }

        // Ensure we have at least one HandlerMapping, by registering
        // a default HandlerMapping if no other mappings are found.
        if (this.handlerMappings == null) {
            this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);

            Logger.info("No HandlerMappings declared for service '" + getServiceName() +
                    "': using default strategies from DispatcherServlet.properties");
        }
    }

    protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
        return context.getAutowireCapableBeanFactory().createBean(clazz);
    }

    protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) throws Exception{
        String key = strategyInterface.getName();
        String value = defaultStrategies.getProperty(key);
        if (value != null) {
            String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
            List<T> strategies = new ArrayList<>(classNames.length);
            for (String className : classNames) {
                try {
                    Class<?> clazz = ClassUtils.forName(className, DispatcherService.class.getClassLoader());
                    Object strategy = createDefaultStrategy(context, clazz);
                    strategies.add((T) strategy);
                } catch (ClassNotFoundException ex) {
                    throw new BeanInitializationException(
                            "Could not find DispatcherServlet's default strategy class [" + className +
                                    "] for interface [" + key + "]", ex);
                } catch (LinkageError err) {
                    throw new BeanInitializationException(
                            "Unresolvable class definition for DispatcherServlet's default strategy class [" +
                                    className + "] for interface [" + key + "]", err);
                }
            }
            return strategies;
        } else {
            return new LinkedList<>();
        }
    }

    public void setThrowExceptionIfNoHandlerFound(boolean throwExceptionIfNoHandlerFound) {
        this.throwExceptionIfNoHandlerFound = throwExceptionIfNoHandlerFound;
    }

    private void logRequest(RequestInfo request) {
        Logger.info(request.request);
    }

    private void validateTrust(RequestInfo info) throws Exception {
        String actionLC = getAction(info);
        // Verify the requester has at least trust level 3
        STAFResult result = STAFUtil.validateTrust(3, getServiceName(), actionLC, getLocalMachineName(), info);
        if (result.rc != STAFResult.Ok) {
            throw new Exception(result.result);
        }
    }

    private String getAction(RequestInfo info) {
        return Util.getActionStr(info.request);
    }

    @Override
    protected void doService(RequestInfo request, STAFResult response) throws Exception {
        logRequest(request);
        validateTrust(request);
        try {
            doDispatch(request, response);
        } finally {
        }
    }

    protected void doDispatch(RequestInfo request, STAFResult response) throws Exception {
        HandlerMethod mappedHandler = getHandle(request);
        if (mappedHandler == null) {
            noHandlerFound(request, response);
            return;
        }
        // Determine handler adapter for the current request.
        HandlerAdapter handlerAdapter = getHandlerAdapter(mappedHandler);
        // Actually invoke the handler.
        ModelAndView model = handlerAdapter.handle(mappedHandler.getPocessedRequest(), response, mappedHandler);
        this.view.render(model, response);
    }

    private HandlerAdapter getHandlerAdapter(HandlerMethod handler) throws Exception{
        if (this.handlerAdapters != null) {
            for (HandlerAdapter adapter : this.handlerAdapters) {
                if (adapter.supports(handler)) {
                    return adapter;
                }
            }
        }
        throw new Exception("No adapter for handler [" + handler + "]: The Dispatcher configuration needs to include a HandlerAdapter that supports this handler");
    }

    private void noHandlerFound(RequestInfo request, STAFResult response) throws Exception{
        String action = getAction(request);
        if(throwExceptionIfNoHandlerFound) {
            throw new NoHandlerFoundException(action);
        }
        setResponse(response, STAFResult.DoesNotExist, "No handler support the " + action + ", please use help command! ");
    }

    @Nullable
    private HandlerMethod getHandle(RequestInfo request) throws Exception{
        if (this.handlerMappings != null) {
            for (HandlerMapping mapping : this.handlerMappings) {
                HandlerMethod handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }
}

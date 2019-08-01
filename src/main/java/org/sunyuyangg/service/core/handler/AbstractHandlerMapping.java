package org.sunyuyangg.service.core.handler;


import com.ibm.staf.service.STAFServiceInterfaceLevel30.*;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.sunyuyangg.service.core.HandlerMapping;

public abstract class AbstractHandlerMapping extends ApplicationObjectSupport implements HandlerMapping, Ordered, BeanNameAware {

    @Nullable
    private Object defaultHandler;

    @Nullable
    private String beanName;

    private int order = Ordered.LOWEST_PRECEDENCE;  // default: same as non-Ordered


    @Override
    public HandlerMethod getHandler(RequestInfo request) throws Exception {
        Object handler = getHandlerInternal(request);
        if (handler == null) {
            handler = getDefaultHandler();
        }
        if (handler == null) {
            return null;
        }

        // Bean name or resolved handler?
        if (handler instanceof String) {
            String handlerName = (String) handler;
            handler = obtainApplicationContext().getBean(handlerName);
        }

        return (HandlerMethod) handler;
    }

    abstract protected Object getHandlerInternal(RequestInfo request) throws Exception;

    /**
     * Set the default handler for this handler mapping.
     * This handler will be returned if no specific mapping was found.
     * <p>Default is {@code null}, indicating no default handler.
     */
    public void setDefaultHandler(@Nullable Object defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    /**
     * Return the default handler for this handler mapping,
     * or {@code null} if none.
     */
    @Nullable
    public Object getDefaultHandler() {
        return this.defaultHandler;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    protected String formatMappingName() {
        return this.beanName != null ? "'" + this.beanName + "'" : "<unknown>";
    }
}

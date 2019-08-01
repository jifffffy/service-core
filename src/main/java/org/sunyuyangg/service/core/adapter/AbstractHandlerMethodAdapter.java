package org.sunyuyangg.service.core.adapter;


import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParseResult;
import org.sunyuyangg.service.core.HandlerAdapter;
import org.sunyuyangg.service.core.handler.HandlerMethod;

public abstract class AbstractHandlerMethodAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof HandlerMethod && supportsInternal((HandlerMethod) handler));
    }

    protected abstract boolean supportsInternal(HandlerMethod handlerMethod);

    @Override
    public ModelAndView handle(STAFCommandParseResult request, STAFResult response, Object handler) throws Exception {
        return handleInternal(request, response, (HandlerMethod) handler);
    }

    protected abstract ModelAndView handleInternal(STAFCommandParseResult request, STAFResult response, HandlerMethod handlerMethod) throws Exception;
}

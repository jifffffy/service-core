package org.sunyuyangg.service.core;


import com.ibm.staf.STAFResult;
import org.sunyuyangg.service.core.adapter.ModelAndView;
import org.sunyuyangg.service.core.handler.ServiceRequest;

public interface HandlerAdapter {

    boolean supports(Object handler);

    ModelAndView handle(ServiceRequest request, STAFResult response, Object handler) throws Exception;
}

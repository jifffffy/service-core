package org.sunyuyangg.service.core;


import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParseResult;
import org.sunyuyangg.service.core.adapter.ModelAndView;

public interface HandlerAdapter {

    boolean supports(Object handler);

    ModelAndView handle(STAFCommandParseResult request, STAFResult response, Object handler) throws Exception;
}

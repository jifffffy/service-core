package org.sunyuyangg.service.core;


import com.ibm.staf.service.STAFServiceInterfaceLevel30.*;
import org.sunyuyangg.service.core.handler.HandlerMethod;

public interface HandlerMapping {
    HandlerMethod getHandler(RequestInfo request) throws Exception;
}

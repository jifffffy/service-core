package org.sunyuyangg.service.core.handler;

import com.ibm.staf.service.STAFCommandParseResult;

public interface ServiceRequest<T> {

    STAFCommandParseResult getParseResult();

    T getMapping();
}

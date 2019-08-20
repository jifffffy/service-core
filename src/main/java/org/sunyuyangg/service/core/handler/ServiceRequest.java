package org.sunyuyangg.service.core.handler;

import com.ibm.staf.service.STAFCommandParseResult;
import org.sunyuyangg.service.core.method.MappingInfo;

public interface ServiceRequest<T extends MappingInfo> {

    STAFCommandParseResult getParseResult();

    T getMapping();
}

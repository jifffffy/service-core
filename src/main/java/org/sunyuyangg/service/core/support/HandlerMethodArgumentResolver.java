package org.sunyuyangg.service.core.support;

import com.ibm.staf.service.STAFCommandParseResult;
import org.springframework.core.MethodParameter;

public interface HandlerMethodArgumentResolver {

    boolean supportsParameter(MethodParameter parameter, STAFCommandParseResult parseResult);

    Object resolveArgument(MethodParameter parameter, STAFCommandParseResult parseResult) throws Exception;
}

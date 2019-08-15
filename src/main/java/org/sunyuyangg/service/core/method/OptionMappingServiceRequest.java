package org.sunyuyangg.service.core.method;

import com.ibm.staf.service.STAFCommandParseResult;
import org.sunyuyangg.service.core.handler.ServiceRequest;

public class OptionMappingServiceRequest implements ServiceRequest<OptionMappingInfo> {

    private final OptionMappingInfo optionMappingInfo;

    public OptionMappingServiceRequest(OptionMappingInfo optionMappingInfo) {
        this.optionMappingInfo = optionMappingInfo;
    }

    @Override
    public STAFCommandParseResult getParseResult() {
        return optionMappingInfo.getParseResult();
    }

    @Override
    public OptionMappingInfo getMapping() {
        return optionMappingInfo;
    }
}

package org.sunyuyangg.service.core;

import com.ibm.staf.STAFResult;

public interface HandlerClient {

    STAFResult waitForQueueType(String... types);

    STAFResult submit(Object object, String type);
}

package org.sunyuyangg.service.core.bean;

import com.ibm.staf.STAFResult;

public interface HandlerClient {

    STAFResult waitForQueueType(String... types);

    STAFResult submit(Object object, String type);

    String getError(int rc);
}

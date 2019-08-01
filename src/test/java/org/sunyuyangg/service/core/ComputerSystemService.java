package org.sunyuyangg.service.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ComputerSystemService extends DispatcherService {

    private static int serviceInvalidSerialNumber = 5001;
    private static String version = "1.0.0";

    public ComputerSystemService() {
        super(serviceInvalidSerialNumber, version, new AnnotationConfigApplicationContext(Configuration.class));
    }

}

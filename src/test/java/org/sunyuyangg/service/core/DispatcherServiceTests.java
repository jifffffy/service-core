package org.sunyuyangg.service.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.sunyuyangg.service.core.model.Person;

import static org.junit.Assert.assertTrue;

public class DispatcherServiceTests {

    private DispatcherService sampleDispatcherService;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        InitInfo initInfo = new InitInfo(
                "test",
                "",
                null,
                0,
                "");
        sampleDispatcherService = new DispatcherService(1000,"1.0", new AnnotationConfigApplicationContext(CustomConfig.class));
        sampleDispatcherService.init(initInfo);
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Test
    public void okRequest() throws Exception {
        RequestInfo requestInfo = createRequestInfo("Test demo 123456");
        STAFResult result = sampleDispatcherService.acceptRequest(requestInfo);
        Logger.info("result: rc = {}, message={}", result.rc, result.result);
        assertTrue("OK", result.rc == STAFResult.Ok);
    }

    @Test
    public void personRequest() throws Exception{
        Person person = new Person("2121", 23);
        String personJson = objectMapper.writeValueAsString(person);
        RequestInfo requestInfo = createRequestInfo("Test person " + STAFUtil.wrapData(personJson));
        STAFResult result = sampleDispatcherService.acceptRequest(requestInfo);
        Logger.info("result: rc = {}, message={}", result.rc, result.result);
        assertTrue("OK", result.rc == STAFResult.Ok);
    }

    @Test
    public void helpRequest() {
        RequestInfo requestInfo = createRequestInfo("help ");
        STAFResult result = sampleDispatcherService.acceptRequest(requestInfo);
        Logger.info("result: rc = {}, message={}", result.rc, result.result);
        assertTrue("OK", result.rc == STAFResult.Ok);
    }

    private RequestInfo createRequestInfo(String request) {
        return new RequestInfo(
                "12345678",
                "local",
                "sun",
                    "12",
                1,
                6,
                false,
                0,
                request,
                0,
                "sun",
                "",
                ""
        );
    }
}

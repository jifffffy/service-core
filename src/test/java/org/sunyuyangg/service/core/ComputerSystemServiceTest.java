package org.sunyuyangg.service.core;

import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.Logger;

import static org.junit.Assert.assertTrue;

public class ComputerSystemServiceTest {

    private ComputerSystemService computerSystemService;

    @Before
    public void setUp() {
        InitInfo initInfo = new InitInfo(
                "ComputerSystemService",
                "",
                null,
                0,
                "");
        computerSystemService = new ComputerSystemService();
        computerSystemService.init(initInfo);
    }

    @Test
    public void helpTest() {
        RequestInfo requestInfo = createRequestInfo("help");
        STAFResult result = computerSystemService.acceptRequest(requestInfo);
        Logger.info("result: rc = {}, message={}", result.rc, STAFMarshallingContext.unmarshall(result.result).getRootObject());
        assertTrue("OK", result.rc == STAFResult.Ok);
    }


    @Test
    public void computerSystemTest() {
        RequestInfo requestInfo = createRequestInfo("query computerSystem");
        STAFResult result = computerSystemService.acceptRequest(requestInfo);
        Logger.info("result: rc = {}, message={}", result.rc, result.result);
        assertTrue("OK", result.rc == STAFResult.Ok);
    }

    @Test
    public void memoryTest() {
        RequestInfo requestInfo = createRequestInfo("query memory");
        STAFResult result = computerSystemService.acceptRequest(requestInfo);
        Logger.info("result: rc = {}, message={}", result.rc, result.result);
        assertTrue("OK", result.rc == STAFResult.Ok);
    }

    @Test
    public void processorTest() {
        RequestInfo requestInfo = createRequestInfo("query processor");
        STAFResult result = computerSystemService.acceptRequest(requestInfo);
        Logger.info("result: rc = {}, message={}", result.rc, result.result);
        assertTrue("OK", result.rc == STAFResult.Ok);
    }

    @Test
    public void cpuTest() {
        RequestInfo requestInfo = createRequestInfo("query cpu");
        STAFResult result = computerSystemService.acceptRequest(requestInfo);
        Logger.info("result: rc = {}, message={}", result.rc, result.result);
        assertTrue("OK", result.rc == STAFResult.Ok);
    }

    @Test
    public void usbTest() {
        RequestInfo requestInfo = createRequestInfo("query usb");
        STAFResult result = computerSystemService.acceptRequest(requestInfo);
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

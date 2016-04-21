package mmsTest.servlet;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.rongyifu.mms.servlet.MonitorApi;

/**
 * MoniterApi 测试用例
 */
public class Test_MonitorApi {

	private MonitorApi monitorApi;  
	private MockHttpServletRequest req;  
	private MockHttpServletResponse resp; 
	private final Log logger = LogFactory.getLog(getClass()); 
      
    @Before  
    public void setUp(){  
    	monitorApi = new MonitorApi();  
    	req = new MockHttpServletRequest(); 
        resp = new MockHttpServletResponse();  
    }  
      
    @Test  
    public void testDoPostHttpServletRequestHttpServletResponse() throws ServletException, IOException {
    	req.addParameter("type", "0");
    	req.addParameter("targetId", "1");
    	req.addParameter("monitorType", "0");
        monitorApi.doPost(req, resp);  
        logger.info(resp.getContentAsString());;
    } 

}

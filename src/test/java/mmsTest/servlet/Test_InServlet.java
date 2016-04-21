package mmsTest.servlet;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.rongyifu.mms.servlet.InServlet;

/**
 * MoniterApi 测试用例
 */
public class Test_InServlet {

	private InServlet in;  
	private MockHttpServletRequest req;  
	private MockHttpServletResponse resp; 
	private String[] transCodes;
	private String[] paramsArr;
	private final Log logger = LogFactory.getLog(getClass()); 
      
    @Before  
    public void setUp(){  
    	in = new InServlet();  
    	req = new MockHttpServletRequest(); 
        resp = new MockHttpServletResponse(); 
        transCodes = new String[]{"TCI001"};
        paramsArr = new String[]{"mid=764"};
    }  
      
    @Test  
    public void testDoPostHttpServletRequestHttpServletResponse() throws ServletException, IOException {
    	for (int i = 0; i < transCodes.length; i++) {
    		fillParams(i);
    		in.doPost(req, resp); 
			String respStr = resp.getContentAsString();
			logger.info(respStr);
			JSONObject json = JSONObject.fromObject(respStr);
			String status = json.get("status").toString();
			assertTrue("接口调用失败","0".equals(status));
		}
    } 
    
    private Map<String,String> fillParams(int index){
    	Map<String,String> map = new HashMap<String,String>();
    	String transCode = transCodes[index];
    	req.addParameter("transCode", transCode);
    	if (StringUtils.isNotBlank(paramsArr[index])) {
			String[] paramStr = paramsArr[index].split("\\|");
			for (String string : paramStr) {
				String[] keyValue = string.split("=");
				req.addParameter(keyValue[0], keyValue[1]);
			}
		}
		return map;
    }
    public static void main(String[] args) {
		System.out.println(Arrays.asList("a=b".split("=")));
	}

}

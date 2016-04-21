package mmsTest.modules.sysmanage;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.modules.sysmanage.dao.YQGateDao;
import com.rongyifu.mms.modules.sysmanage.service.YQGateService;

public class Test_YQGateService {
	private YQGateService gateService=null;
	private B2EGate b=null;
	private YQGateDao dao=null;
	private Logger log=Logger.getLogger(Test_YQGateService.class);
	private Integer addGid=500001;
	@Before
	public void setUp() throws Exception {
		gateService=new YQGateService();
		dao=new YQGateDao();
		b=getGate();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testQueryB2EGate() {
		gateService.queryB2EGate(1, "");
	}

	@Test
	public void testAddB2EGate() {
		b.setGid(addGid);
		gateService.addB2EGate(b);
		deleteGate();
	}

	@Test
	public void testEditOneB2EGate() {
		gateService.editOneB2EGate(b);
	}
	
	private B2EGate getGate(){
		String sql="select * from b2e_gate where gid=40000;";
		List<B2EGate> l=dao.query(sql, B2EGate.class);
		return l.get(0);
		
	}
	
	
	private void deleteGate(){
		String sql="delete from b2e_gate where gid="+addGid+";";
		dao.update(sql);
		log.info("deleteGate:成功");
		
	}

}

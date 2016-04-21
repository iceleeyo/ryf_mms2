package mmsTest.modules.sysmanage;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.rongyifu.mms.bean.DfGateRoute;
import com.rongyifu.mms.modules.sysmanage.service.DfGateRouteService;

public class Test_DfGateRouteService {
	
	private DfGateRouteService svc = new DfGateRouteService();
	private final Logger logger = Logger.getLogger(getClass());
	
	@Test
	public void testRefresh(){
		String msg = svc.refresh();
		logger.info(msg);
	}
	
	@Test
	public void testQuery(){
		DfGateRoute config = new DfGateRoute();
		config.setConfigType((short)1);
		config.setType((short)1);
		config.setGid(1);
		svc.query(config, 1);
	}
	
	@Test
	public void testQueryById(){
		svc.queryById(1);
	}
	
	@Test
	public void testAdd(){
//		type,gate_id,gid,limit_type,config_type
		DfGateRoute config = new DfGateRoute();
		config.setType((short)1);
		config.setGid(1);
		config.setGateId(1);
		config.setLimitType((short)1);
		svc.add(config);
	}
	
	@Test
	public void doUpdate(){
		DfGateRoute config = new DfGateRoute();
		config.setId(-1);
		config.setGid(-1);
		svc.doUpdate(config);
	}
}

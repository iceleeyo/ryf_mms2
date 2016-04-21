package com.rongyifu.mms.ewp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/***
 * init posserver   初始化pos  支付 服务 
 * @author Administrator
 *
 */
public class InitPosServer implements ServletContextListener {
	
	private static Log logger = LogFactory.getLog(InitPosServer.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		if(logger.isInfoEnabled())
			logger.info("init dcsp2 client start ...");
		
		PosServer.getInstance();
		
		
		if(logger.isInfoEnabled())
			logger.info("init dcsp2 client end.");
	}

/*	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		try {
			PosServer.main(null);
		} catch (InterruptedException e) {
			// TODO: handle exception
		}
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}
*/

}

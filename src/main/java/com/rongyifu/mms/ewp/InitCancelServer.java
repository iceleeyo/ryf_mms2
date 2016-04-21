package com.rongyifu.mms.ewp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.rongyifu.mms.common.LimitThread;
import com.rongyifu.mms.common.Ryt;

/***
 * init revocation 初始化pos 撤销 服务
 * 
 * @author Administrator
 * 
 */
public class InitCancelServer implements ServletContextListener {

//	private static Log logger = LogFactory.getLog(InitCancelServer.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		if(!Ryt.isStartService("启动POS消费撤销服务"))
			return;
		LimitThread limitThread=new LimitThread();
		Thread thread=new Thread(limitThread);
		thread.start();
	
	}
	/*
	 * @Override public void init() throws ServletException { // TODO
	 * Auto-generated method stub try { PosServer.main(null); } catch
	 * (InterruptedException e) { // TODO: handle exception } }
	 * 
	 * @Override protected void doGet(HttpServletRequest req,
	 * HttpServletResponse resp) throws ServletException, IOException { // TODO
	 * Auto-generated method stub super.doGet(req, resp); }
	 * 
	 * @Override protected void doPost(HttpServletRequest req,
	 * HttpServletResponse resp) throws ServletException, IOException { // TODO
	 * Auto-generated method stub super.doPost(req, resp); }
	 * 
	 * @Override public void destroy() { // TODO Auto-generated method stub
	 * super.destroy(); }
	 */

}

package com.rongyifu.mms.ewp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.rongyifu.mms.service.SysManageService;
import com.rongyifu.mms.utils.RYFMapUtil;

public class RefreshMinfoAndGateConfig extends HttpServlet {
	private static Logger logger = Logger.getLogger(RefreshMinfoAndGateConfig.class);
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		res.setContentType("text/html");
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		String merId=req.getParameter("mid");
		PrintWriter out=res.getWriter();
		try {
			logger.info("pos商户同步   刷新商户信息  商户网关 开始 。。");
			RYFMapUtil obj=RYFMapUtil.getInstance();
			SysManageService service=new SysManageService();
			obj.refreshMinfoMap(merId);
			obj.refreshFeeCalcModel(merId);
			if(!service.refreshGateRoute())throw new Exception("EWP网关刷新失败");
			out.print("SUCCESS");
			logger.info("pos商户同步   刷新商户信息  商户网关 成功结束。。");
		} catch (Exception e) {
			out.print("FAIL");
			e.printStackTrace();
			
		}
		out.flush();
		out.close();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
	}

}

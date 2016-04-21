package com.rongyifu.mms.ewp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


import com.rongyifu.mms.service.SysService;
import com.rongyifu.mms.utils.CryptoUtils;

public class RecvPayResult extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger  =  Logger.getLogger(RecvPayResult.class);
	/**
	 * 
	 * 支付结果通知接口
	 * Constructor of the object.
	 */
	public RecvPayResult() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	@Override
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		
		logger.info("接受支付结果应答开始..................");
		
		PrintWriter out = response.getWriter();
		
		try {
			String merId = request.getParameter("merId");
			if(isNull(merId)) throw new Exception("merId is null");
			
			logger.info("merId：" + merId);
			
			String ordId = request.getParameter("ordId");
			if(isNull(ordId)) throw new Exception("ordId is null");
			
			logger.info("ordId：" + ordId);
			
			String transAmt = request.getParameter("transAmt");
			if(isNull(transAmt)) throw new Exception("transAmt is null");
			
			logger.info("transAmt：" + transAmt);
			
			String transDate = request.getParameter("transDate");
			if(isNull(transDate)) throw new Exception("transDate is null");
			
			String tseq = request.getParameter("tseq");
			if(isNull(tseq)) throw new Exception("tseq is null");
			
			logger.info("tseq：" + tseq);
			
			String transStat = request.getParameter("transStat");
			if(isNull(transStat)) throw new Exception("transStat is null");
			
			logger.info("transStat：" + transStat);
			
			String pSigna2 = request.getParameter("pSigna2");
			if(isNull(pSigna2)) throw new Exception("pSigna2 is null");
			String plainTxt = ordId + merId + transAmt + transDate+ tseq + transStat;
			CryptoUtils.rytRsaVerify(plainTxt, pSigna2);
			
			logger.info("验签成功...................");
			
			SysService service = new SysService();
			if(transStat.equals("S")){//交易成功
				service.doOrderWhenSuccess(ordId,tseq,transAmt);
				logger.info("tseq="+tseq+"的订单交易成功！");
			}else{//交易失败
				service.doOrderWhenFail(ordId);
				logger.info("tseq="+tseq+"的订单交易失败！");
			}
			out.print("RECV_RYT_ORD_ID_"+ordId);
			
		} catch (Exception e) {
			e.printStackTrace();
			out.print("error:");
			out.print(e.getMessage());
			logger.error(e.getMessage());
		}
		
		
		logger.info("接受支付结果应答结束..................");
		
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	@Override
	public void init() throws ServletException {
		// Put your code here
	}
	
	boolean isNull(String s){
		if(s==null) return true;
		if(s.trim().length()==0) return true;
		return false;
	}

}

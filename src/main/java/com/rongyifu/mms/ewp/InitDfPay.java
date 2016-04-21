package com.rongyifu.mms.ewp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.utils.LogUtil;

/****
 * 代付代扣批量触发
 * @author wa
 *
 */
public class InitDfPay extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		System.out.println("----------init----------df");
		ParamCache.getInstance();
		System.out.println("是否加载商户："+ParamCache.getMinfo_list().size());
		Daifu1 daifu1=new Daifu1();
		Thread thread=new Thread(daifu1);
		thread.run();
		
	}
	
	public String gettime(Date date){
		int hour=date.getHours();
		int mm=date.getMinutes();
		return String.valueOf(hour)+":"+String.valueOf(mm);
	}
	/***
	 * 
	 * @param multi_process_day
	 * @param day //0123456 0 周日 ，1 周一 ，2 周二
	 * @return
	 */
	public boolean checkDay(String multi_process_day,int day){
		if(!multi_process_day.contains("1"))return false;
		if(day==0)return multi_process_day.subSequence(6, 7).equals("1")?true:false;
		else if(day==1)return multi_process_day.subSequence(0, 1).equals("1")?true:false;
		else if(day==2)return multi_process_day.subSequence(1, 2).equals("1")?true:false;
		else if(day==3)return multi_process_day.subSequence(2, 3).equals("1")?true:false;
		else if(day==4)return multi_process_day.subSequence(3, 4).equals("1")?true:false;
		else if(day==5)return multi_process_day.subSequence(4, 5).equals("1")?true:false;
		else if(day==6)return multi_process_day.subSequence(5, 6).equals("1")?true:false;				
		return false;
	}
	
	public class Daifu implements Runnable{

		@Override
		public void run()  {
			List<Minfo> list=null;
			HttpClient client=new HttpClient();
			HttpMethod method=null;
			String url=ParamCache.getStrParamByName("EWP_PATH");
			int i=0;
			while(true){
			    list=ParamCache.getMinfo_list();
				for (Minfo minfo : list) {
					Date date=new Date();
					String dfConfig=minfo.getDfConfig();
					String dkConfig=minfo.getDkConfig();
					String multi_process_day=minfo.getMultiProcessDay();
					String multi_process_time=minfo.getMultiProcessTime();
					int day=date.getDay(); 
					String time=gettime(date);
//					if(!dfConfig.contains("1"))continue;
//					if(!dkConfig.contains("1"))continue;				
					if(!checkDay(multi_process_day,day))continue;
					if(!multi_process_time.equals(time))continue;
					//请求pay  支付。。
					method=new GetMethod(url+"df/batch_df?"+"merId="+minfo.getId()+"&transType="+"C2");
					try {
						client.executeMethod(method);
						if(method.getStatusCode() == HttpStatus.SC_OK){
							
						}else{
							LogUtil.printInfoLog("InitDfPay", "Daifu-run", "请求失败！。。。。。。。。。");
						}
					} catch (HttpException e) {
						// TODO Auto-generated catch block
						Daifu daifu=new Daifu();
						Thread thread=new Thread(daifu);
						thread.start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Daifu daifu=new Daifu();
						Thread thread=new Thread(daifu);
						thread.start();
					}
				}
			}
		}
		
	}
	
	public class Daifu1 implements Runnable{

		@Override
		public void run() {
				Daifu daifu=new Daifu();
				Thread thread=new Thread(daifu);
				thread.start();
			
			
		}

		
	}
	/**
	  * 判断当前操作是否Windows.
	  * 
	  * @return true---是Windows操作系统
	  */
	public static boolean isWindowsOS(){
	  boolean isWindowsOS = false;
	  String osName = System.getProperty("os.name");
	  if(osName.toLowerCase().indexOf("windows")>-1){
	   isWindowsOS = true;
	  }
	  return isWindowsOS;
	}

	/**
	 * 获取本机ip地址，并自动区分Windows还是linux操作系统
	 * 
	 * @return String
	 */
	public static String getLocalHostName() {
		String hostName = "";
		InetAddress ip = null;
		try {
			// 如果是Windows操作系统
			if (isWindowsOS()) {
				ip = InetAddress.getLocalHost();
			}
			// 如果是Linux操作系统
			else {
				boolean bFindIP = false;
				Enumeration<NetworkInterface> netInterfaces = NetworkInterface
						.getNetworkInterfaces();
				while (netInterfaces.hasMoreElements()) {
					if (bFindIP) {
						break;
					}
					NetworkInterface ni = netInterfaces
							.nextElement();
					// ----------特定情况，可以考虑用ni.getName判断
					// 遍历所有ip
					Enumeration<InetAddress> ips = ni.getInetAddresses();
					while (ips.hasMoreElements()) {
						ip = ips.nextElement();
						if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() // 127.开头的都是lookback地址
								&& ip.getHostAddress().indexOf(":") == -1) {
							bFindIP = true;
							break;
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null != ip) {
			hostName = ip.getHostName();
		}
		return hostName;
	}
}

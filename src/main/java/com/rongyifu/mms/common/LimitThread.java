package com.rongyifu.mms.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.com.chinaebi.dcsp2.exception.PipeUnreadyException;
import cn.com.chinaebi.qz.api.bean.KeyResetBean;
import cn.com.chinaebi.qz.api.engine.CustEngine;
import cn.com.chinaebi.qz.api.exception.BeanDataException;
import cn.com.chinaebi.qz.api.exception.NetWorkException;
import cn.com.chinaebi.qz.api.exception.ParseDataException;

import com.rongyifu.mms.utils.DateUtil;

public class LimitThread implements Runnable {
	private static Log logger = LogFactory.getLog(LimitThread.class);
	public LimitThread() {
		super();
	}

	@Override
	public void run() {
		while(true){
			Integer today=DateUtil.today();
			if(today>=20130829){
				
				  if(logger.isInfoEnabled())
					 logger.info("init pos revocation client start ...");
				  
				  Date date = new Date();
				  SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
				  String formatDate=sdf.format(date);
				  String RandomData=formatDate+Ryt.createRandomStr(2);
				  KeyResetBean keyResetBean = new KeyResetBean();
				  keyResetBean.setSendCde("0700");//发送机构代码 0700
				  keyResetBean.setSendDate(date);
				  keyResetBean.setSendTime(date);
				  keyResetBean.setRandomData(RandomData);// 随机
				  keyResetBean.setUserData("110 test"); //随便
					try {
						keyResetBean=CustEngine.getInstance().dealKeyReset(keyResetBean);		
					} catch (NetWorkException e) {
						   System.out.println("网络异常");
					} catch (PipeUnreadyException e) {
							System.out.println("Socket连接异常");
					} catch (ParseDataException e) {
							System.out.println("解析数据异常");
					} catch (BeanDataException e) {
						e.printStackTrace();
					}	
					
					if(logger.isInfoEnabled())
						logger.info("init pos revocation client end.");
					
				break;
			}else{
				try {
					Thread.sleep(3600*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
		}

	}
	

}

package com.rongyifu.mms.ewp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.com.infosec.util.Base64;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.MD5;

public class SendEmailServer implements Runnable {
	private String content;
	private String title;
	private String receive;
	
	
	public SendEmailServer(String content, String title, String receive) {
		super();
		this.content = content;
		this.title = title;
		this.receive = receive;
	}


	@Override
	public void run() {
		if(Ryt.empty(title) || Ryt.empty(content))
			return;
		
		if(Ryt.empty(receive))
			receive = "";
		
		String url = Ryt.getEwpPath();
		String recAddr;
		try {
			recAddr = Base64.encode(receive.trim());
			Map<String, Object> paramMap=new HashMap<String, Object>();
			paramMap.put("title", Base64.encode(title.trim()));
			paramMap.put("content", Base64.encode(content.trim()));
			paramMap.put("receive",recAddr );
			paramMap.put("sign",MD5.getMD5(recAddr.getBytes()) );
			Ryt.requestWithPost(paramMap, url + "pub/sendMail");
		} catch (IOException e) {
			LogUtil.printErrorLog(getClass().getCanonicalName(), "run", "", e);
		}
		
	}

}

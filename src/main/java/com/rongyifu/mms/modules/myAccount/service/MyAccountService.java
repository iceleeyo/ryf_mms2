package com.rongyifu.mms.modules.myAccount.service;

import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.common.BankNoUtil;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.RYFMapUtil;

public class MyAccountService {
	
	protected static Object lock = new Object();
	
	/**
     * 省份号，省份名称下载
     * @return
     * @throws Exception
     */
	protected FileTransfer downloadProvId() throws Exception {
		Map<Integer, String> map = RYFMapUtil.getProvMap();
		StringBuffer sheet = new StringBuffer("省份号,省份名\r\n");
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			sheet.append(entry.getKey()).append(",").append(entry.getValue());
			sheet.append("\r\n");
		}
		String filename = "Prov.txt";
		return new DownloadFile().downloadTXTFile(sheet.toString(), filename);
	}
    
    /**
     * 判断联行号
     * @param key
     * @return
     */
    protected boolean isInBankNo(String key) {
  		return BankNoUtil.getDaifuMiddletMap().containsKey(key);
  	}
 	/**
 	 * 判断账号是否是数字
 	 * @param str
 	 * @return
 	 */
    protected static boolean isAccNo(String str){
		return str.matches("[0-9]{10,30}");
	}
	/**
	 * 判断是否金额格式
	 * @param str
	 * @return
	 */
    protected static boolean isAmt(String str){
		return str.matches("[0-9]{1,8}(.[0-9]{0,2})?");
	}
	
	/**
  	 * 判断填写省份是否存在
  	 * 
  	 * @param prov 省份ID
  	 * @return
  	 */
    protected boolean provInMap(int prov) {
  		return RYFMapUtil.getProvMap().containsKey(prov);
  	}
    
}

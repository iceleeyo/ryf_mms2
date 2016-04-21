package com.rongyifu.mms.settlement;

import java.util.List;
import java.util.Map;


public interface SettltData {
	
	//上传文件对账，fileContent 文件内容
	List<SBean>  getCheckData(String bank,String fileContent) throws Exception;
	//银行接口对账，m 接口参数
	List<SBean>  getCheckData(String bank,Map<String,String> m) throws Exception;

}

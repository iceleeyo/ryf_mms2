/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 *农行新接口对账模块
 */
public class CheckDataAbchinaImp4 implements SettltData{

    public List<SBean> getCheckData(String bank, String fileContent) throws Exception {
        //        商户号|交易类型|订单编号|交易时间|交易金额|商户账号|商户动账金额|客户账号|账户类型|商户回佣手续费|商户分期手续费|会计日期|主机流水号|9014流水号|原订单号
        //103880999990010|Sale|7496989|20150905095147|19.80|348700040017209|19.80|6228480402686291517|401|0.00|0.00|20150905|336401153|9015090509514810244|
        //103880999990010|Sale|7497003|20150905095408|19.80|348700040017209|19.80|6228480408848938671|401|0.00|0.00|20150905|336944184|9015090509540879351|
		List<SBean> sBeanList=new ArrayList<SBean>();
        String[] contents=fileContent.split("\n");
        for (int i=1;i<contents.length;i++) {
            if(contents[i].indexOf("Sale") == -1)continue;
            String[] arr=contents[i].split("\\|");
            SBean sbean=new SBean();
			sbean.setGate(bank);
//			sbean.setBkSeq(arr[13]);
			sbean.setAmt(arr[4]);
			sbean.setDate(StringUtils.left(arr[3], 8));
			
			String bankOrder = arr[2];
			if(isMerOrder(bankOrder)){ // 根据商户订单号对账
				sbean.setMerOid(bankOrder);
				sbean.setFlag(3);
			} else { // 根据电银流水号对账
				sbean.setTseq(arr[2]);
				sbean.setFlag(1);
			}
			sBeanList.add(sbean);
        }
        return sBeanList;
    }

    public List<SBean> getCheckData(String bank, Map<String, String> m) throws Exception {
        return null;
    }
    
    /**
     * 判断是否商户订单号
     * @param bankOrder
     * @return
     */
	public boolean isMerOrder(String bankOrder) {
		Pattern pattern = Pattern.compile("^[A-Z]{2}.*");
		Matcher matcher = pattern.matcher(bankOrder);
		return matcher.matches();
	}
}
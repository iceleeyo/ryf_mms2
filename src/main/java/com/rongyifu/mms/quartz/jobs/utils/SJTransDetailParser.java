package com.rongyifu.mms.quartz.jobs.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.rongyifu.mms.bank.b2e.SjBankMXCX;
import com.rongyifu.mms.bean.TrDetails;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.LogUtil;

public class SJTransDetailParser {
	
	private static final int gid = 40006;
	
	/**
	 * 将格式化的字符串解析成TrDetails对象
	 * @param list
	 * @return
	 */
	public static List<TrDetails> parse(List<String> list){
		if(CollectionUtils.isEmpty(list)){
			return null;
		}else{
			List<TrDetails> trList = new ArrayList<TrDetails>();
			for (String record : list) {
				try {
					String[] cols = record.split("\\|");
					TrDetails td = new TrDetails();
					td.setGid(gid);
					td.setBkSerialNo(StringUtils.isNotBlank(cols[0])?cols[0]:null);
					td.setAcno(StringUtils.isNotBlank(cols[1])?cols[1]:null);
					td.setAcname(StringUtils.isNotBlank(cols[7])?cols[7]:null);
					td.setTrBkNo(StringUtils.isNotBlank(cols[6])?cols[6]:null);
					td.setCurCode(StringUtils.isNotBlank(cols[2])?cols[2]:null);
					td.setTrDate(StringUtils.isNotBlank(cols[3])?Integer.valueOf(cols[3]):null);
					td.setTrTime(StringUtils.isNotBlank(cols[4])?Integer.valueOf(cols[4]):null);
					td.setTrTimestamp(null);//cols[36] 银行给的参考文件里面这个字段为空 不知道格式
					
					td.setOldTrDate(StringUtils.isNotBlank(cols[21])?Integer.valueOf(cols[21]):null);
					td.setOppAcno(StringUtils.isNotBlank(cols[8])?cols[8]:null);
					td.setOppAcname(StringUtils.isNotBlank(cols[10])?cols[10]:null);
					td.setOppBkNo(StringUtils.isNotBlank(cols[12])?cols[12]:null);
					td.setOppBkName(StringUtils.isNotBlank(cols[11])?cols[11]:null);
					td.setOppCurCode(StringUtils.isNotBlank(cols[9])?cols[9]:null);
					td.setJdFlag(StringUtils.isNotBlank(cols[23])?("C".equals(cols[23])?0:1):null);//C对应rcvamt
					td.setRcvamt(StringUtils.isNotBlank(cols[44])?Long.valueOf((Ryt.mul100(cols[44]))):null);
					td.setPayamt(StringUtils.isNotBlank(cols[43])?Long.valueOf((Ryt.mul100(cols[43]))):null);
					td.setAmt(StringUtils.isNotBlank(cols[24])?Long.valueOf((Ryt.mul100(cols[24]))):null);
					td.setFeeAmt(StringUtils.isNotBlank(cols[17])?Long.valueOf((Ryt.mul100(cols[17]))):null);
					td.setBalance(StringUtils.isNotBlank(cols[25])?Long.valueOf((Ryt.mul100(cols[25]))):null);
					td.setLastBalance(StringUtils.isNotBlank(cols[26])?Long.valueOf((Ryt.mul100(cols[26]))):null);
					td.setFreezeAmt(StringUtils.isNotBlank(cols[27])?Long.valueOf((Ryt.mul100(cols[27]))):null);
					td.setSummary(StringUtils.isNotBlank(cols[3])?cols[34]:null);
					td.setPostscript(StringUtils.isNotBlank(cols[35])?cols[35]:null);
//					
					td.setCertType(StringUtils.isNotBlank(cols[28])?cols[28]:null);
					td.setCertBatchNo(StringUtils.isNotBlank(cols[29])?cols[29]:null);
					td.setCertNo(StringUtils.isNotBlank(cols[30])?cols[30]:null);
					td.setOldSerialNo(StringUtils.isNotBlank(cols[20])?cols[20]:null);
					td.setHostSerialNo(StringUtils.isNotBlank(cols[5])?cols[5]:null);
					td.setTrType(StringUtils.isNotBlank(cols[13])?Short.valueOf(cols[13]):null);
					td.setChFlag(StringUtils.isNotBlank(cols[14])?Short.valueOf(cols[14]):null);
					td.setBkFlag(StringUtils.isNotBlank(cols[15])?Short.valueOf(cols[15]):null);
					td.setAreaFlag(StringUtils.isNotBlank(cols[16])?Short.valueOf(cols[16]):null);
					td.setTrFrom(StringUtils.isNotBlank(cols[18])?Short.valueOf(cols[18]):null);
					td.setTrFlag(StringUtils.isNotBlank(cols[19])?Short.valueOf(cols[19]):null);
					td.setCashFlag(StringUtils.isNotBlank(cols[22])?Short.valueOf(cols[22]):null);
					td.setTrCode(StringUtils.isNotBlank(cols[31])?cols[31]:null);
					td.setUserNo(StringUtils.isNotBlank(cols[32])?cols[32]:null);
					td.setSubNo(StringUtils.isNotBlank(cols[33])?cols[33]:null);
					td.setReserved1(StringUtils.isNotBlank(cols[37])?cols[37]:null);
					td.setReserved2(StringUtils.isNotBlank(cols[38])?cols[38]:null);
					
					td.setTrBankName(StringUtils.isNotBlank(cols[39])?cols[39]:null);
					td.setBankNo(StringUtils.isNotBlank(cols[40])?cols[40]:null);
					td.setBankName(StringUtils.isNotBlank(cols[41])?cols[41]:null);
					td.setPrintCount(StringUtils.isNotBlank(cols[42])?cols[42]:null);
					trList.add(td);
				} catch (Exception e) {
					e.printStackTrace();
					// TODO Auto-generated catch block
					LogUtil.printErrorLog(SJTransDetailParser.class.getName(), "parse", "record:"+record);
				}
			}
			return trList;
		}
	}
}

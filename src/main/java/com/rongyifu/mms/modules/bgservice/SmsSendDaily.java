package com.rongyifu.mms.modules.bgservice;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.EmaySMS;
import com.rongyifu.mms.utils.LogUtil;

public class SmsSendDaily {
	@SuppressWarnings("rawtypes")
	static PubDao pubdao = new PubDao();

	public void send() {
		String phone = queryphone();
		if(Ryt.empty(phone) || ",".equals(phone)){
			LogUtil.printInfoLog("SmsSendDaily", "send", "短信日报手机号配置为空！");
			return;
		}
		
		// 1－个人网银支付
		// 7－企业网银支付 
		// 8－手机WAP支付 
		// 3－信用卡支付 
		// 6－语音支付 
		// 18－快捷支付 
		Map<Integer, SmsSendDailyBean> listmap = getSmsSendDailyBean();
		// b2c
		Integer b2cSuCount = listmap.get(1) == null ? 0 : listmap.get(1).getSuccessConut();
		String b2cSuSumAmt = listmap.get(1) == null ? "0" : listmap.get(1).getSumSuccessAmount();
		String b2cSuccessRate = getrates(listmap.get(1) == null ? 0.00: listmap.get(1).getSuccessRate());
		String b2c = "b2c产品，成功交易笔数：" + b2cSuCount + "笔，成功交易总金额:" + b2cSuSumAmt+ "元，成功率（成功交易笔数/总交易笔数）:" + b2cSuccessRate + "";
		// b2b
		Integer b2bSuCount = listmap.get(7) == null ? 0 : listmap.get(7).getSuccessConut();
		String b2bSuSumAmt = listmap.get(7) == null ? "0" : listmap.get(7).getSumSuccessAmount();
		String b2bSuccessRate = getrates(listmap.get(7) == null ? 0.00: listmap.get(7).getSuccessRate());
		String b2b = "b2b产品，成功交易笔数：" + b2bSuCount + "笔，成功交易总金额:" + b2bSuSumAmt+ "元，成功率（成功交易笔数/总交易笔数）:" + b2bSuccessRate + "";

		// wap
		Integer WAPSuCount = listmap.get(8) == null ? 0 : listmap.get(8).getSuccessConut();
		String WAPSuSumAmt = listmap.get(8) == null ? "0" : listmap.get(8).getSumSuccessAmount();
		String WAPSuccessRate = getrates(listmap.get(8) == null ? 0.00: listmap.get(8).getSuccessRate());
		String WAP = "手机WAP支付产品，成功交易笔数：" + WAPSuCount + "笔，成功交易总金额:"+ WAPSuSumAmt + "元，成功率（成功交易笔数/总交易笔数）:" + WAPSuccessRate + "";

		// card
		Integer CardSuCount = listmap.get(3) == null ? 0 : listmap.get(3).getSuccessConut();
		String CardSuSumAmt = listmap.get(3) == null ? "0" : listmap.get(3).getSumSuccessAmount();
		String CardSuccessRate = getrates(listmap.get(3) == null ? 0.00: listmap.get(3).getSuccessRate());
		String Card = "信用卡支付产品，成功交易笔数：" + CardSuCount + "笔，成功交易总金额:"+ CardSuSumAmt + "元，成功率（成功交易笔数/总交易笔数）:" + CardSuccessRate + "";

		// 语音支付
		Integer voicepaySuCount = listmap.get(6) == null ? 0 : listmap.get(6).getSuccessConut();
		String voicepaySuSumAmt = listmap.get(6) == null ? "0" : listmap.get(6).getSumSuccessAmount();
		String voicepaySuccessRate = getrates(listmap.get(6) == null ? 0.00: listmap.get(6).getSuccessRate());
		String voice = "语音支付产品，成功交易笔数：" + voicepaySuCount + "笔，成功交易总金额:"+ voicepaySuSumAmt + "元，成功率（成功交易笔数/总交易笔数）:"+ voicepaySuccessRate + "";

		// 快捷支付
		Integer QuickpaySuCount = listmap.get(18) == null ? 0 : listmap.get(18).getSuccessConut();
		String QuickpaySuSumAmt = listmap.get(18) == null ? "0" : listmap.get(18).getSumSuccessAmount();
		String QuickpaySuccessRate = getrates(listmap.get(18) == null ? 0.00: listmap.get(18).getSuccessRate());
		String Quick = "快捷支付支付产品，成功交易笔数：" + QuickpaySuCount + "笔，成功交易总金额:"+ QuickpaySuSumAmt + "元，成功率（成功交易笔数/总交易笔数）:"+ QuickpaySuccessRate + "";

		String date = getberforedate();
		String content = date + ":" + b2c + ";" + b2b + ";" + WAP + ";" + Card+ ";" + voice + ";" + Quick;
		
		String[] phones = phone.split(",");
		int flag = EmaySMS.sendSMS(phones, content);
		if (flag == 0) {
			LogUtil.printInfoLog("EmaySMS", "Send", "phone:" + phone+ " content:" + content);
		}

	}

	// 得到发送短信的号码
	public String queryphone() {
		String sql = " select par_value from global_params where par_name='SMS_SEND_DAILY'";
		return pubdao.queryForString(sql);

	}

	public Map<Integer, SmsSendDailyBean> getSmsSendDailyBean() {
		List<Map<String, Object>> mapdata = queryData();
		List<Map<String, Object>> succmapdata = querySuccessData();
		Map<Integer, SmsSendDailyBean> map = new HashMap<Integer, SmsSendDailyBean>();
		Map<Integer, SmsSendDailyBean> map2 = new HashMap<Integer, SmsSendDailyBean>();
		if (succmapdata.size() != 0) {
			for (Map<String, Object> succmap : succmapdata) {
				SmsSendDailyBean successBean = new SmsSendDailyBean();
				Integer succtype = Integer.parseInt(succmap.get("type1").toString());
				successBean.setSuccessConut(Integer.parseInt(succmap.get("SuccessConut").toString()));
				successBean.setSumSuccessAmount(Ryt.div100(succmap.get("SumSuccessAmount").toString()));
				successBean.setType(succtype);
				map.put(succtype, successBean);
			}
		}
		for (Map<String, Object> countdata : mapdata) {
			SmsSendDailyBean successBean2 = new SmsSendDailyBean();
			Integer type = Integer.parseInt(countdata.get("type2").toString());
			if (map.containsKey(type)) {
				double SuccessSum = map.get(type).getSuccessConut();
				double Sum = Integer.parseInt(countdata.get("Count").toString());
				double SuccessRate = SuccessSum / Sum;
				successBean2.setType(type);
				successBean2.setSuccessConut(map.get(type).getSuccessConut());
				successBean2.setSumSuccessAmount(map.get(type).getSumSuccessAmount());
				successBean2.setSuccessRate(SuccessRate);

			} else {
				successBean2.setType(type);
				successBean2.setSuccessConut(0);
				successBean2.setSumSuccessAmount("0");
				successBean2.setSuccessRate(0.00);
			}
			map2.put(type, successBean2);
		}
		return map2;
	}

	// 查询交易成功的总金额，总笔数
	public List<Map<String, Object>> querySuccessData() {

		String date = getberforedate();
		String sql = " select type as type1 ,sum(amount) as SumSuccessAmount ,count(*) as SuccessConut from hlog where type in (1,7,8,3,6,18) and tstat=2 and sys_date="+ date + " group by type ";
		List<Map<String, Object>> successmapList = pubdao.queryForList(sql);
		return successmapList;

	}

	// 查询交易的总笔数
	public List<Map<String, Object>> queryData() {
		String date = getberforedate();
		String sql = " select type as type2,count(*) as Count  from hlog where type in (1,7,8,3,6,18) and sys_date="+ date + " group by type ";
		List<Map<String, Object>> mapList = pubdao.queryForList(sql);
		return mapList;

	}

	// 得到前一天的时间
	public static String getberforedate() {

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String mDateTime = formatter.format(c.getTime());
		return mDateTime;

	}

	// 转化为百分比
	public static String getrates(double SuccessRate) {
		NumberFormat nFromat = NumberFormat.getPercentInstance();
		String rates = nFromat.format(SuccessRate);
		return rates;
	}


}

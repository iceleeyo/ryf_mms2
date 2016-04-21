package com.rongyifu.mms.modules.liqmanage.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.SettleMinfo;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SettlementDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.settlement.AmountBean;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class LiqService {

	private SettlementDao dao = new SettlementDao();
	private static final Object lock = new Object();
	
	private enum Results{
		
		/**
		 * 结算发起成功
		 */
		SUCCESS(0,"结算发起成功"),
		
		/**
		 * 商户没有可以结算的订单
		 */
		NO_ORDER(1,"商户没有可以结算的订单"),
		
		/**
		 * 未对帐
		 */
		NOT_CHECK(2,"未对帐"),
		
		/**
		 * 账户余额不足
		 */
		BALANCE_NOT_ENOUGH(3,"账户余额不足"),
		
		/**
		 * 为满足结算最低限额
		 */
		NOT_MEET_LIMIT(4,"为满足结算最低限额"),
		
		/**
		 * 结算冻结
		 */
		FROZEN(5,"结算冻结"),
		
		/**
		 * 结算金额小于零
		 */
		NEGTIVE_VALUE(6,"结算金额小于零"),
		
		/**
		 * 商户有退款为完成的订单
		 */
		REFUNDING(8,"商户有退款未完成的订单"),
		
		/**
		 * 其他
		 */
		OTHER(7,"其他");
		
		Results(int code,String desc){
			this.code = code;
			this.desc = desc;
		}
		
		private int code;
		private String desc;

		public static Results getByCode(int code){
			Results[] rs = Results.values();
			for (Results r : rs) {
				if(r.code ==  code){
					return r;
				}
			}
			return OTHER;
		}
		
		public static Map<Integer,String> getFailReasonsAsMap(){
			Map<Integer,String> map = new HashMap<Integer,String>();
			Results[] rs = Results.values();
			for (Results r : rs) {
				if(r.code != 0){
					map.put(r.code, r.desc);
				}
			}
			return map;
		}
	}
	
	public Map<Integer,String> getFailReasonsAsMap(){
		return Results.getFailReasonsAsMap();
	}
	
	/**
	 * 下载结算发起失败记录
	 * @throws Exception 
	 */
	public FileTransfer downLiqFailList(Integer category, Integer bToDate, Integer eToDate, String mid, Integer bLiqDate, Integer eLiqDate, Integer reason){
		try {
			ArrayList<String[]> list = new ArrayList<String[]>();
			String[] title = {"商户类型","商户号","商户简称","结算截至日期","结算发起时间","失败原因"};
			String[] categorys = {"RYF商户","VAS商户","POS商户","POS代理商"};
			list.add(title);
				List<SettleMinfo> liqFailList=dao.queryLiqFailList(category, bToDate, eToDate, mid, bLiqDate, eLiqDate,reason);
				for (int j = 0; j < liqFailList.size(); j++) {
					SettleMinfo sm=liqFailList.get(j);
					String[] strArr={categorys[sm.getCategory()],
							sm.getMid(),
							sm.getName(),
							DateUtil.formatDate(sm.getExpDate()),
							DateUtil.formatDate(sm.getLiqDate())+" "+DateUtil.getStringTime(sm.getLiqTime()),
							Results.getByCode(sm.getReason()).desc
					};
					list.add(strArr);
				}
				String filename = "Liq_Fail_List_" + DateUtil.getIntDateTime() + ".xls";
				return new DownloadFile().downloadXLSFileBase(list, filename, "结算失败表");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	 
	/**
	 * 查询失败发起记录
	 * @return
	 */
	public CurrentPage<SettleMinfo> queryLiqFailRecord(Integer category, Integer bToDate, Integer eTodate, String mid, Integer bLiqDate, Integer eLiqDate,Integer reason, Integer pageNo){
		return dao.queryLiqFailRecord(category, bToDate, eTodate, mid, bLiqDate, eLiqDate,reason, pageNo);
	}
	
	/**
	 * 获取按商户类型汇总的结算信息
	 * @param toDate 结算截至日期
	 */
	public Map<Integer,Map<String,Integer>> showLiqInfoByType(Integer toDate){
		return dao.showLiqInfoByType(toDate);
	}
	/**
	 * 按商户分类批量结算
	 * @param mTypes 要进行结算的商户类型
	 */
	public String startBatchLiq(List<Integer> categories,Integer toDate){
		List<SettleMinfo> liqMinfos = dao.getLiqMinfos(toDate,categories);
		for (SettleMinfo settleMinfo : liqMinfos) {
			try {
				//检查指定商户是否满足发起结算的条件
				int checkRslt = isCheck(settleMinfo.getMid(), toDate);
				int refCount = isRefunding(settleMinfo.getMid(),toDate);
				if(refCount==Results.REFUNDING.code){
					settleMinfo.setReason(refCount);
					addFailRecord(settleMinfo,toDate);//结算发起失败 生成失败记录
					return "OK";
				}
				if(checkRslt == Results.SUCCESS.code){//检查通过 可以发起结算
					int flag = doSettlement(settleMinfo, toDate);
					if(flag != Results.SUCCESS.code){//失败
						settleMinfo.setReason(flag);
					}
				}else{
					settleMinfo.setReason(checkRslt);
				}
			} catch (Exception e) {
				LogUtil.printErrorLog("LiqService", "startBatchLiq", "mid=" + settleMinfo.getMid(), e);
				settleMinfo.setReason(Results.OTHER.code);
			}
			if(settleMinfo.getReason() != null){
				addFailRecord(settleMinfo,toDate);//结算发起失败 生成失败记录
			}
			LogUtil.printInfoLog("LiqService", "startBatchLiq", "批量结算", settleMinfo.getKvMap());
		}
		dao.saveOperLog("批量结算发起", "发起成功,categories:"+Arrays.toString(categories.toArray(new Integer[0]))+",toDate:"+toDate);
		return "OK";
	}
	/**
	 * 单个商户发起结算
	 */
	public int startLiqByMid(String mid,Integer toDate){
		int msg = 0;
		SettleMinfo sm = dao.getLiqMinfoByMid(mid,toDate);
		if(null != sm){
			try {
				//检查指定商户是否满足发起结算的条件
				int checkRslt = isCheck(sm.getMid(), toDate);
				//检查是否有退款为完成的订单
				int refCount = isRefunding(sm.getMid(),toDate);
				if(refCount==Results.REFUNDING.code){
					sm.setReason(refCount);
					msg = sm.getReason();
					addFailRecord(sm,toDate);//结算发起失败 生成失败记录
					return msg;
				}
				if(checkRslt == 0 ){//检查通过 可以发起结算
					int rslt = doSettlement(sm, toDate);
					if(rslt != Results.SUCCESS.code){//失败
						sm.setReason(rslt);
					}else{
						dao.saveOperLog("单个商户发起结算", "发起成功,mid:"+mid+",toDate:"+toDate);
					}
				}else{
					sm.setReason(checkRslt);
				}
			} catch (Exception e) {
				LogUtil.printErrorLog("LiqService", "startLiqByMid", "mid=" + sm.getMid(), e);
				sm.setReason(Results.OTHER.code);
			}
			if(sm.getReason() != null){
				msg = sm.getReason();
				addFailRecord(sm,toDate);//结算发起失败 生成失败记录
			}
			
			LogUtil.printInfoLog("LiqService", "startLiqByMid", "单个商户结算", sm.getKvMap());
		}else{
			msg = Results.OTHER.code;
		}
		return msg;
	}
	
	/**
	 * 根据商户类型 和 是否在结算周期 查询详情
	 * @param type 类型 0周期内 1 周期外
	 * @param category 商户类型
	 * @param pageNo
	 * @return
	 */
	public CurrentPage<SettleMinfo> showLiqDetails(String mid,Integer type,Integer category,Integer toDate, Integer pageNo){
		return dao.showLiqDetails(mid,type, category, toDate, pageNo);
	}

	/**
	 * @param settleMinfo
	 * 结算发起失败记录
	 */
	private int addFailRecord(SettleMinfo settleMinfo,Integer toDate){
		return dao.addFailRecord(settleMinfo,toDate);
	}
	
	/**
	 * 结算发起核心代码
	 * @param theMinfo 商户结算信息
	 * @param intExpDate 截至日期
	 * @throws Exception 
	 */
	private int doSettlement(SettleMinfo theMinfo, int intExpDate) throws Exception {
		int flag = Results.SUCCESS.code;
		if(theMinfo == null || intExpDate == 0) return Results.OTHER.code;
		// 商户号
		String mid = theMinfo.getMid();
		// 本次结算批次号 
		String batch = theMinfo.getMid() + "" + intExpDate;
		// 结算条件 有条件结算/无条件结算
		int hasCondLiq = theMinfo.getLiqLimit() == 0 ? 0 : 1;
		int liqType = theMinfo.getLiqType();
		// 查询上次结算的日期
		int lastLiqDate = theMinfo.getLastLiqDate();
		String lastBatch = theMinfo.getLastBatch();

		ArrayList<String> arrayList = new ArrayList<String>();
		List<AmountBean> list = getAmountBean(lastLiqDate,lastBatch,intExpDate,mid);

		// 支付总额
		long zfzeSum = 0L;
		// 退款总额
		long tkzeSum = 0L;
		// 手续费总额
		int sxfzeSum = 0;
		// 结算金额
		long liq_amtSum = 0L;
		// 支付总笔数
		int purCntSum = 0;
		// 退款总笔数
		int refCntSum = 0;
		//退回手续费总额
		int thFeeSum = 0;
		//银行退回的手续费
		int bkthFeeSum = 0;
		
		StringBuffer sqlBuff = new StringBuffer();
		for (AmountBean amountBean : list) {
			long zfze = amountBean.getPAmount();// 每个网关的支付总金额
			long tkze = amountBean.getRAmount();// 每个网关的退款总金额
			int sxfze = amountBean.getBankFee();// 每个网关的手续费总额
			int thze = amountBean.getRefFee();//退回手续费总额
			
			/*每个网关的交易金额=支付总金额-退款总金额*/
			long trans_amt = zfze - tkze;

			/*(全额结算)清算金额=交易金额，(净额结算)清算金额= 交易金额-手续费总金额 + 退回的手续费*/
//			long liq_amt = liqType == 1 ? trans_amt : (trans_amt - sxfze + thze);
			/*去掉净额全额之分*/
			long liq_amt = (trans_amt - sxfze + thze);
			

			arrayList.add(getFeeLiqLogSql(mid, liqType, batch, amountBean));
			/*清空sqlBuff，后续在使用*/
			sqlBuff.delete(0, sqlBuff.length());
			
			zfzeSum += zfze; // 所有网关的支付总金额
			tkzeSum += tkze; // 所有网关的退款总金额
			sxfzeSum += sxfze;// 所有网关的手续费总额
			liq_amtSum += liq_amt;// 所有网关的清算金额
			purCntSum += amountBean.getPurConut(); // 支付总笔数
			refCntSum += amountBean.getRefConut(); // 退款总笔数
			thFeeSum += thze;//所有网关的退回总额
			bkthFeeSum += amountBean.getBkRefFee();//银行退回的手续费
		}
		Map<String, Object> addMap =dao.getHandMap(mid,1);// getMapValue(mapSql.toString() + " and a.type = 1 ");
		Map<String, Object> subMap = dao.getHandMap(mid,2);//getMapValue(mapSql.toString() + " and a.type = 2 ");

		long manualAddSum = getLongValue(addMap.get("amt"));
		long manualSubSum = getLongValue(subMap.get("amt"));
		int addCount = getIntValue(addMap.get("cnt"));
		int subCount = getIntValue(subMap.get("cnt"));
		
		liq_amtSum = liq_amtSum + manualAddSum - manualSubSum; // 结算金额
		long balance = dao.get100BalanceById(mid);
		// 判断结算金额是否小于余额是否大于结算满足额度
		if (balance >= liq_amtSum && liq_amtSum >= Integer.parseInt(Ryt.mul100(theMinfo.getLiqLimit()))) {
			sqlBuff.append("insert into fee_liq_bath (mid,trans_amt,ref_amt,fee_amt,liq_amt,liq_date");
			sqlBuff.append(",gen_date,liq_cond,liq_type,batch,last_batch,last_liq_date,manual_add,manual_sub");
			sqlBuff.append(",pur_cnt,ref_cnt,add_cnt,sub_cnt,ref_fee,bk_ref_fee) values ( '");
			sqlBuff.append(mid).append("',").append(zfzeSum).append(",").append(tkzeSum).append(",");
			sqlBuff.append(sxfzeSum).append(",").append(liq_amtSum).append(",");
			sqlBuff.append(intExpDate).append(",").append(DateUtil.today()).append(",");
			sqlBuff.append(hasCondLiq).append(",").append(liqType).append(",'");
			sqlBuff.append(batch).append("',").append(lastBatch).append(",").append(lastLiqDate).append(",");
			sqlBuff.append(manualAddSum).append(",").append(manualSubSum).append(",");
			sqlBuff.append(purCntSum).append(",").append(refCntSum).append(",").append(addCount).append(",");
			sqlBuff.append(subCount).append(",").append(thFeeSum).append(",").append(bkthFeeSum).append(")");
			
			arrayList.add(sqlBuff.toString());
			
			/*清空sqlBuff，后续在使用*/
			sqlBuff = null;
			// 向商户表中插入批次号
			arrayList.add("update minfo set last_batch = '" + batch + "',last_liq_date=" + intExpDate + " where id = "
					+Ryt.addQuotes(mid) );
			
			// 向hlog中插入批次号
			arrayList.add("update hlog set batch = '"+ batch+ "' " +
			              " where mid = "+ Ryt.addQuotes(mid)+ " and tstat = 2 and batch = 0 and is_liq = 0" +
			              "   and ((sys_date >= " + lastLiqDate+ " and sys_date < " + intExpDate+ " and gid not in(55000,55001,590016) and bk_chk=1) " +
			              "    or (sys_date >= " + lastLiqDate+ " and sys_date < " + intExpDate+ " and gid in(55000,55001,90016)))");
			
			// 向退款表中插入批次号
			arrayList.add("update refund_log set batch='" + batch + "' where batch=0 and stat in(2,3,4) and mid=" + Ryt.addQuotes(mid)
					+ " and sys_date< " + intExpDate + " and req_date< " + intExpDate + " and pro_date<=" + intExpDate);
			// 向手工调账表中插入批次号
			arrayList.add("update adjust_account set batch='" + batch + "' where state=1 and batch=0 and mid=" + Ryt.addQuotes(mid));

			String[] sqlList = arrayList.toArray(new String[arrayList.size()]);
			int[] r = null;
			//进行同步 防止出现数据库deadlock
			synchronized (lock) {
				r = dao.batchSqlTransaction(sqlList);
			}
			if (r == null || r.length == 0) {
				flag = Results.OTHER.code;
			}

		} else {
			Map<String, String> logParams = LogUtil.createParamsMap();
			logParams.put("mid", mid);
			logParams.put("batch", batch);
			logParams.put("lastLiqDate", String.valueOf(lastLiqDate));
			logParams.put("intExpDate", String.valueOf(intExpDate));
			logParams.put("liq_amtSum", String.valueOf(liq_amtSum));
			logParams.put("balance", String.valueOf(balance));
			logParams.put("liqLimit", String.valueOf(theMinfo.getLiqLimit()));
			
			if (liq_amtSum < 0){//结算金额小于0
				flag=Results.NEGTIVE_VALUE.code;
				logParams.put("liqFailMsg", Results.NEGTIVE_VALUE.desc);
				LogUtil.printInfoLog("SettlementService", "doSettlement", "结算发起", logParams);
			} else if (balance < liq_amtSum) {//账户余额不足
				flag= Results.BALANCE_NOT_ENOUGH.code;
				logParams.put("liqFailMsg", Results.BALANCE_NOT_ENOUGH.desc);
				LogUtil.printInfoLog("SettlementService", "doSettlement", "结算发起", logParams);
			} else {//未达到限额
				flag = Results.NOT_MEET_LIMIT.code;
				logParams.put("liqFailMsg", Results.NOT_MEET_LIMIT.desc);
				LogUtil.printInfoLog("SettlementService", "doSettlement", "结算发起", logParams);
			}
			arrayList = null;
		}
		return flag;
	}
	//获取结算金额的list
	private  List<AmountBean> getAmountBean(int lastLiqDate,String lastBatch,int intExpDate,String mid){
		List<AmountBean> amountList = new ArrayList<AmountBean>();
		Map<Integer, AmountBean> map = new HashMap<Integer, AmountBean>();
		//按网关统计的支付总金额
		List<Map<String, Object>> payM = dao.getPayAmountMap(lastLiqDate,lastBatch, intExpDate, mid);
		//按网关统计的退款总金额
		List<Map<String, Object>> refM = dao.getRefAmountMap(lastLiqDate,lastBatch, intExpDate, mid);
		if (refM.size() != 0) {
			for (Map<String, Object> refm : refM) {
				AmountBean ab1 = new AmountBean();
				//退款网关
				Integer tkgate = Integer.parseInt(refm.get("gate").toString());
				//退款总额
				ab1.setRAmount(getLongValue(refm.get("rsum")));
				//退款次数
				ab1.setRefConut(getIntValue(refm.get("rcount")));
				//电银手续费
				ab1.setRefFee(getIntValue(refm.get("rfee")));
				//银行手续费
				ab1.setBkRefFee(getIntValue(refm.get("bkfee")));
				ab1.setGate(tkgate);
				map.put(tkgate, ab1);
			}
		}
		for (Map<String, Object> paym : payM) {
			Integer gate = Integer.parseInt(paym.get("gate").toString());
			AmountBean ab = null;
			if (map.containsKey(gate)) {
				ab = map.get(gate);
				ab.setPAmount(getLongValue(paym.get("psum")));
				ab.setPurConut(getIntValue(paym.get("pcount")));
				ab.setBankFee(getIntValue(paym.get("fsum")));
			} else {
				ab = new AmountBean();
				ab.setPAmount(getLongValue(paym.get("psum")));
				ab.setPurConut(getIntValue(paym.get("pcount")));
				ab.setBankFee(getIntValue(paym.get("fsum")));
				ab.setRAmount(getLongValue(0));
				ab.setRefConut(getIntValue(0));
				ab.setRefFee(getIntValue(0));
				ab.setBkRefFee(getIntValue(0));
				ab.setGate(gate);
				map.put(gate, ab);
			}
		}
		for (Integer dataKey : map.keySet()) {
			AmountBean ab2 = new AmountBean();
			ab2.setPAmount(map.get(dataKey).getpAmount() == null ? 0 : map.get(dataKey).getpAmount());
			ab2.setPurConut(map.get(dataKey).getPurConut() == null ? 0 : map.get(dataKey).getPurConut());
			ab2.setBankFee(map.get(dataKey).getBankFee() == null ? 0 : map.get(dataKey).getBankFee());
			ab2.setRAmount(map.get(dataKey).getRAmount());
			ab2.setRefConut(map.get(dataKey).getRefConut());
			ab2.setRefFee(map.get(dataKey).getRefFee());
			ab2.setBkRefFee(map.get(dataKey).getBkRefFee());
			ab2.setGate(dataKey);
			amountList.add(ab2);
		}

		return amountList;
	}
	
	private String getFeeLiqLogSql(String mid,int liqType,String batch,AmountBean amountBean ){
		long zfze = amountBean.getPAmount();// 每个网关的支付总金额
		long tkze = amountBean.getRAmount();// 每个网关的退款总金额
		int sxfze = amountBean.getBankFee();// 每个网关的手续费总额
		int thze = amountBean.getRefFee();//退回手续费总额
		
		/*每个网关的交易金额=支付总金额-退款总金额*/
		long trans_amt = zfze - tkze;
		/*手续费率=手续费总金额/支付总金额*/
		int fee_ratio = zfze > 0 ? (int) (sxfze/ zfze) : 0;
		/*(全额结算)清算金额=交易金额，(净额结算)清算金额= 交易金额-手续费总金额 + 退回的手续费*/
		//long liq_amt = liqType == 1 ? trans_amt : (trans_amt - sxfze + thze);
		
		long liq_amt = (trans_amt - sxfze + thze);
		
		int purCnt = amountBean.getPurConut();
		int refCnt = amountBean.getRefConut();
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append("insert into fee_liq_log ");
		sqlBuff.append("(mid,gate,amount,fee_ratio,liq_amt,pur_amt,ref_amt,fee_amt,pur_cnt,ref_cnt,batch,ref_fee)");
		sqlBuff.append("values ( '").append(mid).append("',").append(amountBean.getGate()).append(",");
		sqlBuff.append(trans_amt).append(",").append(fee_ratio).append(",");
		sqlBuff.append(liq_amt).append(",").append(zfze).append(",").append(tkze).append(",");
		sqlBuff.append(sxfze).append(",").append(purCnt).append(",");
		sqlBuff.append(refCnt).append(",'").append(batch).append("',").append(thze).append(")");
		return sqlBuff.toString();
	}
	
	/**
	 * 检查商户是否为完成对账
	 * 
	 * @param mids
	 *            商户号,商户号,...
	 * @param date
	 *            结算日期
	 * @return 完成对账商户号 + ; + 未完成对账商户号
	 * @throws Exception 
	 */
	public List<SettleMinfo> checkMinfo2(List<SettleMinfo> objs, int date) throws Exception {
		
		for (SettleMinfo m : objs) {
			
			if(m.getExpDate() < DateUtil.today()){
				m.setFlag(false);
				m.setMsg("合同过期!");
				continue;
			}
			// 0--有效的结算商户 1--没有结算订单 2--没有对账
			int flag = isCheck(m.getMid(), date);
			if (flag == 0) {
				m.setFlag(true);
			} else if (flag == Results.NO_ORDER.code) {
				m.setFlag(false);
				m.setMsg(Results.NO_ORDER.desc);
			} else if(flag == Results.NOT_CHECK.code){
				m.setFlag(false);
				m.setMsg(Results.NOT_CHECK.desc);
			}else{
				m.setFlag(false);
				m.setMsg(Results.OTHER.desc);
			}
		}
		return objs;
	}
	
	/**
	 * 检查结算商户信息
	 * 
	 * @param mid
	 *            商户号
	 * @param date
	 *            结算日期
	 * @return //验证结算用户是否已经对账 0--有效的结算商户 1--没有结算订单 2--没有对账
	 * @throws Exception 
	 */
	private int isCheck(String mid, int date) throws Exception {
		int flag = Results.SUCCESS.code;
		//m.getLiq_limit(),该商户的结算额度。只有jt.queryForInt(queryAllAmount)大于该额度才能结算
			Map m = dao.getMap(mid, date);
			if (m.get("allBK").toString().equals("0")) { // 商户没有可以结算的订单
				flag = Results.NO_ORDER.code;
			} else if (m.get("minBK").toString().equals("0")) { // 结算的订单没有对账
				flag = Results.NOT_CHECK.code;
			}
		return flag;
	}
	
	private int isRefunding(String mid, int date) throws Exception {
		int flag =	dao.getIsRefunding(mid,date);
		return flag;
	}
	
	private int getIntValue(Object o) {
		try {
			return Integer.parseInt(o.toString());
		} catch (Exception e) {
			return 0;
		}
	}

	private long getLongValue(Object o) {
		try {
			return Long.parseLong(o.toString());
		} catch (Exception e) {
			return 0l;
		}
	}
	
}

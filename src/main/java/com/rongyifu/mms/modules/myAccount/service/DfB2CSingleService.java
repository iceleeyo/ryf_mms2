package com.rongyifu.mms.modules.myAccount.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.xml.security.utils.Base64;

import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.common.BankNoUtil;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.exception.B2EException;
import com.rongyifu.mms.modules.myAccount.dao.DfB2CSingleDao;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class DfB2CSingleService {
	
	private DfB2CSingleDao dao = new DfB2CSingleDao();
	
	/***
	 * 代付单笔录入  验证账户是否可以代付 验证余额是否足够代付  计算手续费
	 * @param aid 账号号
	 * @param transAmt 交易金额  格式以分为单位
	 * @param toBkNo 收款行号
	 * @param oid 订单号  
	 * @return
	 */
	public Map<String, String> checkSinglePay(String aid, String oid, String transAmt,String toBkNo) {
		Map<String, String> ret = new HashMap<String, String>();
		ret.put("flag", "1"); // 默认设置失败
		
		AccInfos accInfos = dao.queryAccount(aid);
		long transAmt2 = Ryt.mul100toInt(transAmt);
		if (accInfos.getState() != 1){
			ret.put("msg", "该账户非正常状态!");
			return ret;
		}
		// 计算手续费 实际支付金额
		FeeCalcMode feeCalcMode = null;
		try {
			feeCalcMode = dao.getFeeModeByGate(dao.getLoginUserMid(), toBkNo);
		} catch (Exception e) {
			ret.put("msg", e.getMessage());
			return ret;
		}
		// 交易手续费 格式 (以分为单位)
		long transFee = (long) Ryt.mul100toInt(ChargeMode.reckon(feeCalcMode.getCalcMode(), Ryt.div100(transAmt2)));
		long payAmt = transAmt2 + transFee;// 实际支付金额 格式 (以分为单位)
		long balance = accInfos.getBalance();// 账户可用余额 格式 (以分为单位)
		
		if (Ryt.empty(oid))
			oid = Ryt.genOidBySysTime();
		
		if (accInfos.getBalance() < payAmt){
			ret.put("msg", "该笔交易金额大于该账户可用余额！");
			return ret;
		}
		
		// 设置返回参数
		ret.put("flag", "0"); // 设置成处理成功
		ret.put("oid", oid);
		ret.put("transAmt", Ryt.div100(transAmt2));
		ret.put("transFee", Ryt.div100(transFee));
		ret.put("payAmt", Ryt.div100(payAmt));
		ret.put("balance", Ryt.div100(balance));
		ret.put("gid", String.valueOf(feeCalcMode.getGid()));
		
		return ret;
	}
	
	/**
	 * 确定支付一笔，单笔付款到个人银行账户。
	 * @param data  array [aid,oid,toAccNo,toAccName,toBkName,toBkNo,toProvId,cardFlag,payAmt,transAmt,transFee,priv,gid,gate]
	 * @param pwd
	 * @return
	 */
	public String updateSinglePay(String[] data,String pwd)throws Exception{
		String mid=data[0];//商户号
		String oid=data[1];//订单号
		
		String oldPass = dao.getPass(mid);
		if (!oldPass.equals(pwd)) {
			return "1|操作失败，密码错误！";
		}
		
		try{
			handleAccount(dao, data);
		}catch(Exception e){
			LogUtil.printErrorLog("DfB2CSingleService", "updateSinglePay", "支付失败", e);
			dao.saveOperLog("单笔付款到个人银行账户", "支付失败：" + e.getMessage());
			return "1|单笔付款到个人银行账户，操作失败";
		}
		dao.saveOperLog("单笔付款到个人银行账户", "订单号 "+oid+"支付成功");
		return "0|请代付确认";
	}
	
	/**
	 * 处理账户操作
	 * @param aid
	 * @param payAmt
	 * @throws Exception 
	 */
	private static synchronized void handleAccount(DfB2CSingleDao dfDao, String[] data) throws Exception{
		String mid = data[0];			// 商户号
		String oid = data[1];			// 订单号
		String toAccNo = data[2];		// 收款账号
		String toAccName = data[3];		// 收款账户名
		String toBkName = data[4];		// 收款银行名称
		String toBkNo = data[5];		// 收款银行行号
		String toProvId = data[6];		// 收款人所在省份
		String cardFlag = data[7];		// 卡折标志
		String payAmt = data[8];		// 实际支付金额
		String transAmt = data[9];		// 交易金额
		String transFee = data[10];		// 交易手续费
		String priv = data[11];			// 用途
		String gid = data[12];			// 支付渠道
		String gate = data[13];			// 支付网关号
		
		AccInfos accInfos = dfDao.queryAccount(mid);
		if(accInfos.getState() != 1){
			LogUtil.printInfoLog("DfB2CSingleService", "handleAccount", "商户[" + mid + "]状态异常！");
			throw new B2EException("账户状态异常");
		}
		
		long payAmt1 = Ryt.mul100toInt(payAmt); // 单位分
		if(!Ryt.isPay(accInfos.getBalance(), payAmt1)){
			LogUtil.printInfoLog("DfB2CSingleService", "handleAccount", "商户[" + mid + "]付款金额[" + payAmt1 + "]大于账户余额[" + accInfos.getBalance() + "]");
			throw new B2EException("交易金额大于账户余额");
		}
		
		String[] bkInfo=BankNoUtil.getBankNo(toBkNo,toBkName, gate, Integer.parseInt(toProvId), Integer.parseInt(gid));
		toBkNo = bkInfo[0];
//		toBkName = bkInfo[1];
		
		int sysDate = DateUtil.today();
		int sysTime = DateUtil.now();
		
		TlogBean order = new TlogBean();
		order.setVersion(Constant.VERSION);
		order.setIp(10191L);
		order.setSys_date(sysDate);
		order.setInit_sys_date(sysDate);
		order.setSys_time(DateUtil.getUTCTime(String.valueOf(sysTime)));
		order.setMid(mid);
		order.setMdate(sysDate);
		order.setOid(oid);
		order.setAmount(Long.parseLong(Ryt.mul100(transAmt)));
		order.setPay_amt(Long.parseLong(Ryt.mul100(payAmt)));
		order.setFee_amt(Integer.parseInt(Ryt.mul100(transFee)));
		order.setType(Constant.TlogTransType.PAYMENT_FOR_PRIVATE);
		order.setGate(Integer.parseInt(gate));
		order.setGid(Integer.parseInt(gid));
		order.setTstat(Constant.PayState.INIT);
		order.setIs_liq(Constant.IsLiq.NO); // 不参与结算
		order.setData_source(Constant.DataSource.TYPE_DFDKMMS); // 标识数据来源：管理平台发起的代付交易  
		order.setP1(Ryt.desEnc(toAccNo)+"|noDec"); // 收款账号
		order.setP2(toAccName); // 收款户名
		order.setP3(toBkNo); // 联行号
		order.setP6(cardFlag); // 卡/折标志
		order.setP7(Base64.encode(priv.getBytes("UTF-8"))); // 用途 
		order.setP10(toProvId); // 开户所在省份 
		LogUtil.printInfoLog("DfB2CSingleService", "handleAccount", "order.setp1:"+Ryt.desEnc(toAccNo));
		dfDao.insertOrder(order);
	}
	
	public Map<String, String> getMerGate(String mid){
		return dao.getMerGate(mid, Constant.TlogTransType.PAYMENT_FOR_PRIVATE);
	}
	
	/**
	 * 查询账户下的状态为正常的用户名称（结算账户）
	 * @param uid，用户ID
	 * @return
	 */
	public Map<String, String> getZHUid(String uid) {
		return dao.getZHUid(uid);
	}
}
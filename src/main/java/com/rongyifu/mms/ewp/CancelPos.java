package com.rongyifu.mms.ewp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.bank.b2e.GenB2ETrnid;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

import cn.com.chinaebi.dcsp2.exception.PipeUnreadyException;
import cn.com.chinaebi.qz.api.bean.TradeDischargeBean;
import cn.com.chinaebi.qz.api.engine.CustEngine;
import cn.com.chinaebi.qz.api.exception.BeanDataException;
import cn.com.chinaebi.qz.api.exception.KeyException;
import cn.com.chinaebi.qz.api.exception.NetWorkException;
import cn.com.chinaebi.qz.api.exception.ParseDataException;

public class CancelPos implements Runnable {

	private String psam=null;
	private String termid=null;
	private String UserCode=null;
	private String bill=null;
	private String merId=null;
	private String tOutCardNo=null;
	private String tReference=null;
	private String tAuthCode=null;
	private String tTrace=null;
	private AdminZHDao dao=null;
	private String tseq=null;
	private Integer bk_flag=null;
	private Integer bk_date=null;
	private Integer bk_time=null;
	private String bk_code=null;
	private String bk_seq=null;
	public CancelPos(String psam, String termid, String userCode, String bill,
			String merId, String tOutCardNo, String tReference,
			String tAuthCode, String tTrace,Integer bk_flag,
			Integer bk_date,Integer bk_time,String bk_code,String bk_seq,String tseq) {
		super();
		this.psam =psam;
		this.termid = termid;
		this.UserCode =  userCode;
		this.bill = bill;
		this.merId = "";
		this.tOutCardNo =  tOutCardNo;
		this.tReference =  tReference;
		this.tAuthCode = tAuthCode;
		this.tTrace =  tTrace;
		this.bk_flag=bk_flag;
		this.bk_date=bk_date;
		this.bk_time=bk_time;
		this.bk_code=bk_code;
		this.bk_seq=bk_seq;
		this.tseq=tseq;
		this.dao=new AdminZHDao();
	}


	/*撤销接口 SK转账*/
	@Override
	public void run() {
		TradeDischargeBean tradeDischargeBean = new TradeDischargeBean();
		Date date = new Date();
		tradeDischargeBean.setSendDate(date);
		tradeDischargeBean.setSendTime(date);
		tradeDischargeBean.setSendCde("0700");//发送机构代码  固定 0700
		tradeDischargeBean.setPsam(psam);//刷卡设备编号
		tradeDischargeBean.setTermid(termid);//终端号
		tradeDischargeBean.setUserCode(UserCode);//暂时传入11个0
		tradeDischargeBean.setBill(bill);//Pos订单号
		tradeDischargeBean.setMerId(merId);//增值商户号   传空
		tradeDischargeBean.setTOutCardNo(tOutCardNo);
		tradeDischargeBean.setTReference(tReference);//银联参考号
		tradeDischargeBean.setTAuthCode(tAuthCode);//授权码
		tradeDischargeBean.setTTrace(tTrace);//交易流水
		tradeDischargeBean.setTTradeDate(date);
		Map<String, String> map=new HashMap<String, String>();
		map.put("psam", this.psam);map.put("termid", this.termid);map.put("UserCode", UserCode);
		map.put("bill", bill);map.put("merId", merId);map.put("tOutCardNo", tOutCardNo);
		map.put("tReference", tReference);map.put("tAuthCode", tAuthCode);map.put("tTrace", tTrace);
		LogUtil.printInfoLog("Cancel_pos", "Cancel_Pos", "各参数信息", map);
		try {
			 Thread.sleep(new Long("90000"));
		     tradeDischargeBean =  CustEngine.getInstance().dealDischarge(tradeDischargeBean);
		     String response=tradeDischargeBean.getResponse();
		     Integer cancel_iden=null;
		     if (response.equals("F0")||response.equals("F3")||response.equals("F1")||response.equals("A0")||response.equals("F7")){
		    	 cancel_iden=3;//撤销失败
		     }else if (response.equals("00")){
		    	 cancel_iden=2;//撤消成功
		     }else{
		    	 cancel_iden=1; //待处理
		     }
		     String sql =this.modifyCancelIden(bill+"-R", cancel_iden);
		     String cancel_id=DateUtil.getNowDateTime()+GenB2ETrnid.getRandOid();
		     String sql2=dao.savecancelLog(cancel_id, tseq, "2", "3", ""+cancel_iden, null, "Pos 撤销", bk_flag, bk_date, bk_time, bk_code, bk_seq);
		     String[] sqls={sql,sql2};
		     dao.batchSqlTransaction(sqls);
		     System.out.println("cancel_pos result :"+tradeDischargeBean.getResponse());
		} catch (NetWorkException e) {
			System.out.println("网络异常");
		} catch (PipeUnreadyException e) {
		    System.out.println("Socket连接异常");
		} catch (ParseDataException e) {
			System.out.println("解析数据异常");
		} catch (BeanDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	
	public String changeLen(String params,String t){
		String  t0=t;
		int len2=t0.length();
		int len=params.length();
		if(len< len2 && len >0){
			t0=t0.substring(len, len2);
			params=t0+params;
		}
		String result=params;
		return result;
		
	}
	/****
	 *修改 订单撤消标识
	 * @param tseq
	 * @param oid
	 * @param CancelIden
	 * @return
	 */
	public String modifyCancelIden(String oid,Integer CancelIden){
		StringBuffer buffer=new StringBuffer("update tlog set p10=").append(CancelIden);
		buffer.append(" where ").append("oid=").append(Ryt.addQuotes(oid));
		return buffer.toString();
	}

}

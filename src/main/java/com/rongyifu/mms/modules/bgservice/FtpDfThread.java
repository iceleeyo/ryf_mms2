package com.rongyifu.mms.modules.bgservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.rongyifu.mms.bank.query.QueryCommon;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.common.BankNoUtil;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.SqlGenerator;
import com.rongyifu.mms.dbutil.sqlbean.BatchLogBean;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.modules.bgdao.FtpDfDao;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

/***
 * FTPd代付处理类
 * @author shdy
 *
 */
public class FtpDfThread implements Runnable {
	private FtpDfDao dao=null; 
	private SqlGenerator sqlGenerator=null;
	private Logger log=Logger.getLogger(InitFtpTransData.class);
	private String fileName=null;
	private String filePath=null;
	private FtpDfFileHandle fileHandle=null;
	private final String md5Key="iFv5x6Cu"; //支付请求签名用
	private final String transType="FTP_2"; //支付类型 批量FTP代付
	
	public FtpDfThread(FtpDfDao dao, SqlGenerator sqlGenerator, String fileName,String filePath) {
		super();
		this.dao = dao;
		this.sqlGenerator = sqlGenerator;
		this.fileName = fileName;
		this.filePath=filePath;
		this.fileHandle=new FtpDfFileHandle();
	}
	public void run2(){
		String merbatchNo=fileName.split("\\.")[0];//商户批次号
		String mid=String.valueOf(Integer.parseInt(fileName.substring(0,9)));//商户号为文件名固定前9位字符
		String dybatchNo=Ryt.crateBatchNumber();;
		Object[] res=readFile(filePath+fileName,merbatchNo,dybatchNo);
		String insertSql=null;
		String sum_tramt=null;//批次交易金额
		Integer orderNum=0;
		Map<String, String> params=new HashMap<String, String>();
		if(res!=null){
			insertSql=String.valueOf(res[0]);
			sum_tramt=String.valueOf(res[1]);
			orderNum=Integer.parseInt(String.valueOf(res[2]));
		}
		try {
			String saveBatchLog=save_batchLog(dybatchNo,5,mid,orderNum);
			String[] sqls=new String[]{insertSql,saveBatchLog};
			int[] ret=dao.batchSqlTransaction(sqls); //录入订单
			String url=Ryt.getEwpPath();
			params.put("sql1", insertSql);
			params.put("sql2", saveBatchLog);
			if(ret==null){
				throw new Exception("插入订单报错");
			}
			//验证账户余额是否充足，存储订单
			if(checkBalance(mid,Long.valueOf(sum_tramt))){
			   Integer date=DateUtil.today();
				//发送支付请求至ewp
				String resp=ftpDf_req(mid,dybatchNo,date,url);
				if(resp.equals("suc")){
					//发送支付请求成功，再次循环
					log.info("发送支付请求成功");
				}
			}
		}catch (Exception e){
			LogUtil.printErrorLog("FtpDfThread", "run", "errorMsg:"+e.getMessage(), params);
		}
	}
	
	@Override
	public void run() {
		String merbatchNo=fileName.split("\\.")[0];//商户批次号
		String mid=String.valueOf(Integer.parseInt(fileName.substring(0,9)));//商户号为文件名固定前9位字符
		String dybatchNo=Ryt.crateBatchNumber();;
		Object[] res=readFile(filePath+fileName,merbatchNo,dybatchNo);
		String insertSql=null;
		String sum_tramt=null;//批次交易金额
		Integer orderNum=0;
		Map<String, String> params=new HashMap<String, String>();
		if(res!=null){
			insertSql=String.valueOf(res[0]);
			sum_tramt=String.valueOf(res[1]);
			orderNum=Integer.parseInt(String.valueOf(res[2]));
		}
		try {
			String saveBatchLog=save_batchLog(dybatchNo,5,mid,orderNum);
			String[] sqls=new String[]{insertSql,saveBatchLog};
			int[] ret=dao.batchSqlTransaction(sqls); //录入订单
			String url=Ryt.getEwpPath();
			params.put("sql1", insertSql);
			params.put("sql2", saveBatchLog);
			if(ret==null){
				throw new Exception("插入订单报错");
			}
			//验证账户余额是否充足，存储订单
			if(checkBalance(mid,Long.valueOf(sum_tramt))){
				Integer sysDate=DateUtil.today();
			   StringBuffer signStr=new StringBuffer();
			   signStr.append(mid);
			   signStr.append(sysDate);
			   signStr.append(dybatchNo);
			   signStr.append(md5Key);
			   String chkValue=QueryCommon.md5Encrypt(signStr.toString());//md5加密merId+orderDate+dybatchNo+md5Key
			   Map<String, Object> map=new HashMap<String, Object>();
			   map.put("merId", mid);
			   map.put("merbatchNo", merbatchNo);
			   map.put("transType", transType);
			   map.put("dybatchNo", dybatchNo);
			   map.put("orderDate", sysDate);
			   map.put("chkValue", chkValue);
				//发送支付请求至ewp
			   log.info("开始发送支付请求,批次号["+dybatchNo+"]");
				String resp=Ryt.requestWithPost(map, url+"df/ftp_df");
				if(resp.equals("suc")){
					//发送支付请求成功，再次循环
					log.info("发送支付请求成功,批次号["+dybatchNo+"]");
				}
			}
		}catch (Exception e){
			LogUtil.printErrorLog("FtpDfThread", "run", "errorMsg:"+e.getMessage(), params);
		}
	}

	/****
	 * 
	 * @param mid
	 * @param dfAmt
	 * @return
	 * @throws InterruptedException 
	 */
	private  boolean  checkBalance(String mid,long dfAmt) throws InterruptedException{
		 boolean res=true;
		 String sql="select balance from acc_infos where aid="+Ryt.addQuotes(mid);
		 Long balance=new Long(dao.queryForString(sql));
		 if(dfAmt>balance){
			 log.info("余额不足，余额为："+balance+";交易金额为："+dfAmt);
			 Thread.sleep(10000);
			 checkBalance(mid,dfAmt);
		 }
		 log.info("余额验证通过。。。。。。。。。。。");
		 return res;
	}
	
	
	/***
	 * @param filePath
	 * @param merbatchNo
	 * @param dybatchNo
	 * @return string[] ->
	 */
	private Object[] readFile(String filePath,String merbatchNo,String dybatchNo){
		Object[] res=null;
		long sum_tramt=0;
		StringBuffer bodySql=null;
		int orderNum=0;
		try{
		
			String headSql=null;
			String temp="";
			FeeCalcMode calcMode=null;
			bodySql=new StringBuffer();
			List<String> result=fileHandle.readFile(filePath);
			for (String tmpLine : result) {
				String[] oneData=tmpLine.split("\\|");
				String mid=oneData[0];
				String gate=oneData[7];
				if(calcMode==null){
					calcMode=dao.getFeeModeByGate(mid, gate);
				}
				TlogBean tlogBean=addTlogs(oneData,merbatchNo,dybatchNo,calcMode);
				if(headSql==null){
					headSql=sqlGenerator.generateInsertSql(tlogBean);
				}
				temp=sqlGenerator.generateInsertSql(tlogBean);
				temp=temp.split("VALUES")[1].replace(";", ",");
				bodySql.append(temp);
				sum_tramt+=tlogBean.getAmount();
				orderNum++;
			}
			headSql=headSql.split("VALUES")[0];
			String sql=headSql+" VALUES "+bodySql.toString();		
			res=new Object[]{sql.substring(0, sql.length()-1)+";",sum_tramt,orderNum};
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
	
	private TlogBean addTlogs(String[] oneData,String merbatchNo,String dybatchNo,FeeCalcMode feeCalcMode) throws Exception{
		TlogBean tlogBean=new TlogBean();
		int data_source=9;//FTP方式代付 数据来源
		int type=11;//对私代付
		int date=DateUtil.today();
		int time=DateUtil.getCurrentUTCSeconds();
		String gate=oneData[7];
		String accProvId=oneData[5];
		String mid=oneData[0];
		String recAccName=new String(oneData[3]);
//		String recBkNo="";
		String recBkNo=BankNoUtil.getDaifuMiddletMap().get(gate);
		String feeMode=feeCalcMode.getCalcMode();
		String amount=Ryt.mul100(oneData[2]); //交易金额  分单位
		String transFee=Ryt.mul100(ChargeMode.reckon(feeMode,amount));//手续费 分单位
		Integer payAmt=Integer.parseInt(amount)+Integer.parseInt(transFee);
		tlogBean.setMid(mid);
		tlogBean.setBid(mid);
		tlogBean.setOid(oneData[1]);
		tlogBean.setAmount(new Long(amount));
		tlogBean.setP2(recAccName);
		tlogBean.setP1(oneData[4]);
		tlogBean.setP10(accProvId);
		tlogBean.setP7(net.rytong.encrypto.provider.Base64.encode(oneData[6].getBytes()));
		tlogBean.setGate(Integer.parseInt(gate));
		tlogBean.setTstat(0);
		tlogBean.setP8(dybatchNo);
		tlogBean.setP9(merbatchNo);
		tlogBean.setVersion(10);
		tlogBean.setIp(new Long(0));
		tlogBean.setMdate(date);
		tlogBean.setType(type);
		tlogBean.setSys_date(date);
		tlogBean.setInit_sys_date(date);
		tlogBean.setSys_time(time);
		tlogBean.setTstat(0);
		tlogBean.setBk_flag(0);
		tlogBean.setTrans_period(30);
		tlogBean.setPay_amt(Long.valueOf(payAmt));
		tlogBean.setCard_no(oneData[4]);
		tlogBean.setP3(recBkNo);
		tlogBean.setP6("0");
		tlogBean.setIs_liq(1);
		tlogBean.setFee_amt(Integer.parseInt(transFee));
		tlogBean.setGid(feeCalcMode.getGid());
		tlogBean.setData_source(data_source);
		return tlogBean;
	}
	
	/***
	 * 保存批量处理表
	 * @param dybatchNo
	 * @param type
	 * @param mid
	 * @param orderNum
	 * @return
	 * @throws Exception
	 */
	private String save_batchLog(String dybatchNo,Integer type,String mid,Integer orderNum) throws Exception{
		BatchLogBean batchLogBean=new BatchLogBean();
		batchLogBean.setBatch_id(dybatchNo);
		batchLogBean.setBatch_state(0); //batch_state :0未处理 1 已处理
		batchLogBean.setMid(mid);
		batchLogBean.setNotify_mer(0); //notify_mer : 0 未通知   1:已通知 2:五次通知未返回
		batchLogBean.setOrder_num(orderNum);
		batchLogBean.setProcess_num(0);//已处理完成订单数
		batchLogBean.setSys_date(DateUtil.today());
		batchLogBean.setType(type);
		return sqlGenerator.generateInsertSql(batchLogBean);
		
	}
	
	/***
	 * FTP 代付 -> 批量代付
	 * @param mid
	 * @param dybatchNo
	 * @param date
	 * @param url
	 * @return
	 */
	private String ftpDf_req(String mid,String dybatchNo,Integer date,String url){
			Integer sysDate=DateUtil.today();
		   StringBuffer signStr=new StringBuffer();
		   signStr.append(mid);
		   signStr.append(sysDate);
		   signStr.append(dybatchNo);
		   signStr.append(md5Key);
		   String chkValue=QueryCommon.md5Encrypt(signStr.toString());//md5加密merId+orderDate+dybatchNo+md5Key
		   Map<String, Object> map=new HashMap<String, Object>();
		   map.put("merId", mid);
		   map.put("transType", transType);
		   map.put("dybatchNo", dybatchNo);
		   map.put("orderDate", sysDate);
		   map.put("chkValue", chkValue);
			//发送支付请求至ewp
			String resp=Ryt.requestWithPost(map, url+"df/ftp_df");
			return resp;
	}
	
	class handleBalance{
		public synchronized void handleBalance(String mid,long transAmt,String dybatchNo){
			String update="update tlog set tstat=1 where mid="+Ryt.addQuotes(mid)+" and p8="+Ryt.addQuotes(dybatchNo)+";";
			 dao.batchSqlTransaction(new String[]{update});
		}
	}
	
}

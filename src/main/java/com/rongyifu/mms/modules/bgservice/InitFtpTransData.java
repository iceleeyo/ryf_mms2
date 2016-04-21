package com.rongyifu.mms.modules.bgservice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.SqlGenerator;
import com.rongyifu.mms.modules.bgdao.FtpDfDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.ParamUtil;

/***
 * FTP代付扫描服务
 * @author shdy
 *
 */
public class InitFtpTransData {
	private FtpDfDao dao=new FtpDfDao(); 
	private SqlGenerator sqlGenerator=new SqlGenerator();
	private Logger log=Logger.getLogger(InitFtpTransData.class);
    private String filePath=ParamUtil.getProperties("ftpPath", "df_param.properties").trim(); //Ftp存储目录
    private FtpDfFileHandle fileHandle=new FtpDfFileHandle();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InitFtpTransData ftp_transData=new InitFtpTransData();
//		ftp_transData.scan_batchLog();
		ftp_transData.handleTransResult("764", "20140328174000231655", 7, "20140328");
		
	}
	
	/***
	 * 初始化扫描服务
	 */
	public void initFtpScanService(){
		log.info("FTP代付 扫描服务启动。。。。。。。");
		scan_ftpDir();
	}
	
	/****
	 * 初始化FTP 返回结果服务
	 * 定时任务 30分钟运行一次
	 */
	public void initFtpDfResultService(){
		log.info("FTP返回结果服务启动。。。。。。。");
		scan_batchLog();
	}
	
	/***
	 * 初始化FTP异常处理服务，
	 * 判断批次订单处理结果，
	 * 是否满足生成结果文件的条件
	 * 生成结果文件至指定目录
	 * 定时任务 30分钟运行一次
	 */
	public void initFtpAbnormalService(){
		log.info("FTP异常交易处理服务启动");
		scan_batchLog_abnormal();
	}
	
	/****
	 * 手工生产FTP结果文件
	 * @param mid
	 * @param dybatchNo
	 * @param date
	 * @return
	 */
	public String sgCreateResult(String mid,String dybatchNo,String date){
		try {
			createResultFile(mid,dybatchNo,date);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "ok";
	}
	
	
	public void  scan_ftpDir(){
		File file=null;
		String[] lists=null;
		String fn=null;
		String today=String.valueOf(DateUtil.today());
		try {
			file=new File(filePath);
			lists=file.list();
			for (String fileName : lists) {
				if(fileName.contains(".d")){
					continue;
				}else if(fileName.length()!=24){
					continue;
				}else if(!fileName.contains(today)){
					continue;
				}
				fn=fileName;
				FtpDfThread dfThread=new FtpDfThread(dao, sqlGenerator, fn, filePath);
				Thread dft=new Thread(dfThread);
				dft.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/***
	 * 扫描batchLog表中FTP方式代付的数据
	 */
	public void scan_batchLog(){
		String today=String.valueOf(DateUtil.today());
		StringBuffer sql=new StringBuffer("select concat(mid,',',batch_id,',',process_num) from batch_log where sys_date=").append(today);
		sql.append(" and order_num=process_num");
		sql.append(" and batch_state!=2");//异常处理标识  2已发起异常处理，
		sql.append(" and notify_mer=0");//未发起通知的批量帝订单
		sql.append(" and type=5"); //FTP方式代付
		log.info("batch_log_sql:"+sql.toString());
		List<String> res=dao.queryForList(sql.toString(), String.class);
			if(res==null){
				log.info("暂无支付完成订单");
			}else{
				for (String str : res) {
					String[] strs=str.split(",");
					String mid=strs[0];
					String dybatchNo=strs[1];
					Integer processNum=Integer.parseInt(strs[2]); 
					handleTransResult(mid, dybatchNo, processNum, today);
				}
			}
		}
	
	/***
	 * FTP 交易返回结果 处理方法
	 * @param mid
	 * @param dybatchNo
	 * @param processNum
	 */
	public void handleTransResult(String mid,String dybatchNo,Integer processNum,String today){
		StringBuffer sql=new StringBuffer("select concat(count(tstat),',',tstat,',',gid) from tlog  where ");
		sql.append(" mid=").append(Ryt.addQuotes(mid));
		sql.append(" and mdate=").append(today);
		sql.append(" and p8=").append(Ryt.addQuotes(dybatchNo));
		sql.append(" group by tstat order by tstat desc");
		sql.append(";");
		List<String> res=dao.queryForStringList(sql.toString());
		if(res==null){
			return;
		}else{
			Map<String, Object> map=new HashMap<String, Object>();
			handleTransTstat(res,map);//查询成功笔数，失败笔数，待支付笔数
			Integer gid=(Integer)map.get("gid");
			Integer waitNum=map.get("wait")==null?0:(Integer)map.get("wait");
			Integer FailNum=map.get("fail")==null?0:(Integer)map.get("fail");//失败笔数
			Integer SucNum=map.get("suc")==null?0:(Integer)map.get("suc");//成功笔数
			if(waitNum!=0){
				log.warn("该批次["+dybatchNo+"]有未完成订单！");
				return;
			}
			double suc_rate=Double.parseDouble(dao.getB2eGate(gid).getSucRate());
			double percentage=SucNum / processNum*100;
			if(percentage<suc_rate){//异常处理
				FtpDfFailHandle dfFailHandle=new FtpDfFailHandle(dao, mid, dybatchNo, today);
				Thread thread=new Thread(dfFailHandle);
				thread.start();
			}else{
				log.info("开始生产交易结果文件,批次号["+dybatchNo+"]");
				try {
					createResultFile(mid, dybatchNo, today);
					updateBatchNotify(mid,dybatchNo,1); //notify_mer 1 为已通知
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/***
	 * 查询结果数据，生产结果文件
	 * @param mid
	 * @param dybatchNo
	 * @param date
	 * @return
	 * @throws IOException 
	 */
	public void createResultFile(String mid,String dybatchNo,String date) throws IOException{
		StringBuffer sql=new StringBuffer("select mid,tseq,oid,tstat,amount,fee_amt,pay_amt,p1,p2,error_msg,p9 from tlog ");
		sql.append(" where  mid=").append(Ryt.addQuotes(mid));
		sql.append(" and mdate=").append(date);
		sql.append(" and tstat !=1 ");
		sql.append(" and p8=").append(Ryt.addQuotes(dybatchNo));
		sql.append(";");
		List<OrderInfo> os=dao.query(sql.toString(), OrderInfo.class);
		List<String> fs=new ArrayList<String>();
		String fname=null;
		for (OrderInfo orderInfo : os) {
			StringBuffer fdata=new StringBuffer();
			if(fname==null){
				fname="R"+orderInfo.getP9();//商户批次号
			}
			String tstat=convertTransTstat(orderInfo.getTstat().intValue());
			fdata.append(orderInfo.getMid()).append("|");
			fdata.append(orderInfo.getTseq()).append("|");
			fdata.append(orderInfo.getOid()).append("|");
			fdata.append(tstat).append("|");
			fdata.append(Ryt.div100(orderInfo.getAmount())).append("|");
			fdata.append(Ryt.div100(orderInfo.getFeeAmt())).append("|");
			fdata.append(Ryt.div100(orderInfo.getPayAmt())).append("|");
			fdata.append(orderInfo.getP2()).append("|");
			fdata.append(orderInfo.getP1()).append("|");
			String errorMsg=orderInfo.getError_msg().equals("")?" ":orderInfo.getError_msg();
			fdata.append(errorMsg);
			fs.add(fdata.toString());
		}
		fileHandle.createFile(fname, fs);
	}
	
	/***
	 * 扫描batch_log 异常的批次
	 * 查询订单表 判断异常是否处理完成
	 */
	public void scan_batchLog_abnormal(){
		String today=String.valueOf(DateUtil.today());
		StringBuffer qBatchLog=new StringBuffer("select concat(mid,',',batch_id,',',process_num) from batch_log where sys_date=").append(today);
		qBatchLog.append(" and order_num=process_num");
		qBatchLog.append(" and batch_state=2");//异常处理标识  2已发起异常处理，
		qBatchLog.append(" and notify_mer!=1 ");//已经生产结果文件 ：标识已通知商户
		qBatchLog.append(" and type=5"); //FTP方式代付
		List<String> res=dao.queryForList(qBatchLog.toString(), String.class);
		for (String string : res) {
			String[] batchInfo=string.split(",");
			String mid=batchInfo[0];
			String dybatchNo=batchInfo[1];
			handleAbnormalOrder(mid,dybatchNo);
		}
	}

	/***
	 * 处理异常批次订单
	 * 1.查询是否处理完成，完成则生成结果文件，否则继续。。
	 * @param mid
	 * @param dybatchNo
	 */
	public void handleAbnormalOrder(String mid,String dybatchNo){
		StringBuffer sql=new StringBuffer("select count(tseq) from ").append(Constant.TLOG);
		sql.append("  where mid=").append(Ryt.addQuotes(mid));
		sql.append(" and p8=").append(Ryt.addQuotes(dybatchNo));
		sql.append(" and mdate=").append(DateUtil.today());
		sql.append(" and tstat !=1 ");
		sql.append(" and (againPay_status !=3 and againPay_status !=4");
		sql.append(")");
		Integer resCount=dao.queryForInt(sql.toString());
		if(resCount==0){
			//可以生成文件
			String today=String.valueOf(DateUtil.today());
			try {
				createResultFile(mid,dybatchNo,today);
				updateBatchNotify(mid,dybatchNo,1); //notify_mer 1 为已通知
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/****
	 * 修改batch_log 通知状态
	 * @param mid
	 * @param dybatchNo
	 * @param notifyState
	 */
	public void updateBatchNotify(String mid,String dybatchNo,Integer notifyState){
		StringBuffer sql=new StringBuffer();
		sql.append("update batch_log set notify_mer=").append(notifyState);
		sql.append(" where mid=").append(Ryt.addQuotes(mid));
		sql.append(" and batch_id=").append(Ryt.addQuotes(dybatchNo));
		dao.batchSqlTransaction(new String[]{sql.toString()});
	}
	
	/***
	 * 处理查询订单交易结果
	 * @param res
	 * @param map
	 */
	public void handleTransTstat(List<String> res,Map<String, Object> map){
		Integer tstatNum=null;
		Integer tstatType=null;
		Integer gid=null;
		for (String str : res) {
			//查询数据格式 ->笔数,订单状态类型,支付渠道 
			String[] tstatInfo=str.split(",");
			 tstatNum=Integer.parseInt(tstatInfo[0]);
			 tstatType=Integer.parseInt(tstatInfo[1]);
			 gid=Integer.parseInt(tstatInfo[2]); 
			 map.put("gid", gid);
			 if(tstatType==Constant.PayState.FAILURE){
				 map.put("fail", tstatNum);
			 }else if(tstatType==Constant.PayState.SUCCESS){
				 map.put("suc", tstatNum);
			 }else if(tstatType ==Constant.PayState.WAIT_PAY){
				 map.put("wait", tstatNum);
			 }
		}
		
		return ;
	}
	/****
	 * 转换状态
	 * @return
	 */
	public String convertTransTstat(Integer tstat) {
			String identify="W";
			if(tstat==2){
				identify="S";
			}else if(tstat==3 ){
				identify="F";
			}else if(tstat==4){
				identify="F";
			}else if(tstat == 5){
				identify="F";
			}
			return identify;
	}

}
	



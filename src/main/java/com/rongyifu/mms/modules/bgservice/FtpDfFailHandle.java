package com.rongyifu.mms.modules.bgservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.rongyifu.mms.bean.GlobalParams;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.ewp.SendEmailServer;

/***
 * FTP代付满足网关配置报警率的情况 
 * 处理类
 * @author shdy
 *
 */
public class FtpDfFailHandle implements Runnable{
	private PubDao dao=null;
	private String mid=null;
	private String dybatchNo=null;
	private String today=null;
	
	public FtpDfFailHandle(PubDao dao, String mid, String dybatchNo,
			String today) {
		super();
		this.dao = dao;
		this.mid = mid;
		this.dybatchNo = dybatchNo;
		this.today = today;
	}

	@Override
	public void run() {
		//修改订单表中的标识  标识可以申请再次代付
		StringBuffer updateSql=new StringBuffer("update tlog set againPay_status=1");
		updateSql.append(" where mid=").append(mid);
		updateSql.append(" and sys_date=").append(today);
		updateSql.append(" and p8=").append(dybatchNo);
		updateSql.append(" and tstat=").append(Constant.PayState.FAILURE);
		//修改batch_log 表中  标识该批次已发起异常处理
		StringBuffer updateBatchLog=new StringBuffer("update batch_log set batch_state=2");
		updateBatchLog.append(" where  mid=").append(mid);
		updateBatchLog.append(" and sys_date=").append(today);
		updateBatchLog.append(" and batch_id=").append(Ryt.addQuotes(dybatchNo));
		//修改订单是否申请再次代付
		dao.batchSqlTransaction(new String[]{updateSql.toString(),updateBatchLog.toString()});
		//查询订单信息等 发送报警邮件 通知。。。
		StringBuffer selSql=new StringBuffer("select p8,mid,tseq,gid from tlog ");
		selSql.append(" where mid=").append(mid);
		selSql.append(" and sys_date=").append(today);
		selSql.append(" and p8=").append(dybatchNo);
		selSql.append(" and tstat=").append(Constant.PayState.FAILURE);
		System.out.println("SqlForMail:"+selSql);
		List<OrderInfo> os=dao.query(selSql.toString(), OrderInfo.class);
		if(os==null){
			return;
		}
		String subject="报警系统告警[重要]:FTP大批量付款交易失败报警";
		StringBuffer content=new StringBuffer("产品：").append("批量代付(FTP)\r\n");
		content.append("事件类型：").append("连续失败-渠道异常\r\n");
		content.append("告警级别：").append("重要\r\n");
		content.append("告警时间：").append(formatDate()).append("\r\n");
		content.append("告警内容：\t").append("批次号	").append("电银流水号	").append("商户号	").append("支付渠道	\r\n");
		for (OrderInfo orderInfo : os) {
			content.append("\t").append(orderInfo.getP8()).append("	");
			content.append(orderInfo.getTseq()).append("	");
			content.append(orderInfo.getMid()).append("	");
			//支付渠道需处理
			content.append(orderInfo.getGid()).append("	");
			content.append("\r\n");
		}
		content.append("解决方案：").append("技术部核查是否渠道异常、结算部人工处理 （出款交易查询对失败的交易重新提交扣款）\r\n");
		//发送报警邮件
		sendMail(subject, content.toString());
		
	}
	
	
		public  void sendMail(String subject, String content) {
			List<GlobalParams> list = new SystemDao().queryAllParams();
			System.out.println("subject:"+subject+"\r\n"+"content:"+content);
			for (GlobalParams globalParams : list) {
				if (globalParams.getParName().equals("MailTos")) {
					if (globalParams.getParValue() != null
							|| !globalParams.getParValue().equals("")) {
						SendEmailServer emailServer=new SendEmailServer(content, subject, globalParams.getParValue());
						Thread sendMail=new Thread(emailServer);
						sendMail.run();
					}
				}
			}
		}
		
		/***
		 * 转换日期时间格式：yyyy-MM-dd HH:mm:ss
		 * @return
		 */
		private String formatDate(){
			String today=null;
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			today=dateFormat.format(new Date());
			return today;
		}

}

package com.rongyifu.mms.modules.transaction.dao;

import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

public class QueryCreditCardResultDao extends PubDao{
	private  PubDao pubdao=new PubDao();
	
	
	/**
	 * 手机支付订单查询(Mlog)
	 * @param 
	 * @return
	 */
	public CurrentPage<OrderInfo> queryMlogList(Integer pageNo,Integer pageSize, String mid, Integer bdate, Integer edate, Integer tstat, String tseq,
			Integer cardType, String cardVal, long payAmount,Integer mstate,String begintrantAmt,String endtrantAmt) {
		  String sql=getCreditCardPaySql( mid,  bdate,  edate,  tstat,  tseq, cardType,  cardVal,  payAmount,mstate,begintrantAmt,endtrantAmt);
		String sqlCount = "select count(*) from  (" + sql + ") as c";
		String sqlCountTotle = "select sum(pay_amount) from (" + sql + ") as s";
		Map<String, String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, sqlCountTotle);
		return queryForPage(sqlCount,sql, pageNo,pageSize,OrderInfo.class,sumSQLMap) ;
	}
	
	//信用卡支付的sql语句
		public String getCreditCardPaySql(String mid, Integer bdate, Integer edate, Integer tstat, String tseq,
				Integer cardType, String cardVal, long payAmount,Integer mstate,String begintrantAmt,String endtrantAmt){
            String cardValEnc="";
            if(cardVal!=null && !"".equals(cardVal)){
                cardValEnc=Ryt.desEnc(cardVal);
            }
			cardVal=Ryt.sql(cardVal);
			StringBuffer sqlCondition = new StringBuffer();
            sqlCondition.append(" and ( m.trans_type=3 or m.trans_type=18 ) ");//新增查询条件 信用卡查询 只能查trans_type=3的信息
			if (!Ryt.empty(mid)) sqlCondition.append(" and m.mid=").append(Ryt.addQuotes(mid));
			if (bdate != 0) sqlCondition.append(" and m.sys_date>=").append(bdate);
			if (edate != 0) sqlCondition.append(" and m.sys_date<=").append(edate);
			if (tstat != null) sqlCondition.append(" and m.tstat=").append(tstat);
			if (!Ryt.empty(tseq)) sqlCondition.append(" and m.tseq=").append(Ryt.addQuotes(String.valueOf(tseq)));
			if (cardVal != null && !cardVal.equals("") && cardType != 0 &&!cardValEnc.equals("")) {// cardType 1为卡�?2身份证号 3手机�?
				if (cardType == 1) sqlCondition.append(" and (m.pay_card=").append(Ryt.addQuotes(cardVal)).append(" or m.pay_card=").append(Ryt.addQuotes(cardValEnc)).append(")");
				if (cardType == 2) sqlCondition.append(" and (m.Pay_id=").append(Ryt.addQuotes(cardVal)).append(" or m.Pay_id=").append(Ryt.addQuotes(cardValEnc)).append(")");
				if (cardType == 3) sqlCondition.append(" and (m.mobile_no=").append(Ryt.addQuotes(cardVal)).append(" or m.mobile_no=").append(Ryt.addQuotes(cardValEnc)).append(")");
			}
			if(mstate!=null) sqlCondition.append(" and mo.mstate=").append(mstate);
			if(!Ryt.empty(begintrantAmt)) sqlCondition.append(" and m.pay_amount>=").append(Ryt.mul100toInt(begintrantAmt));
			if(!Ryt.empty(endtrantAmt)) sqlCondition.append(" and m.pay_amount<=").append(Ryt.mul100toInt(endtrantAmt));
			String groupSql="";
			if (payAmount!=0)groupSql=" and m.pay_card in (select pay_card from mlog m where pay_card!='' "+ sqlCondition.toString()
					+"  group by pay_card HAVING sum(m.pay_amount)>= "+payAmount+")";
			
			String sql = "select m.tseq,m.sys_date,m.mid,m.pay_amount,m.tstat,m.pay_card,m.pay_id,m.mobile_no from mlog m ,minfo mo where m.mid=mo.id "+groupSql+sqlCondition.toString();
			return sql; 
		}
 
}

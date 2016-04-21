package com.rongyifu.mms.dao;

import java.util.List;

import com.rongyifu.mms.bean.SettleHlog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.settlement.SBean;
import com.rongyifu.mms.utils.DateUtil;

@SuppressWarnings("rawtypes")
public class AccountDao extends PubDao {
	
	/**
	 * 根据gid查询className(对账)
	 * @param gid
	 * @return
	 */
	public  String getClassNameById(String gid){
		String sql="select class_name from gate_route where gid=?";
		Object[] objArr=new Object[]{gid};
		return queryForString(sql,objArr);
	}
	
	
	public SettleHlog getSettleHlog(SBean bean) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("select h.tseq,h.pay_amt,h.amount,h.gate,h.bk_chk,h.tstat,h.bk_fee_model,h.gid,h.oid ");
		sql.append("from hlog h where ");
		
		if (bean.getFlag() == 5) {
			sql.append("      gate = ").append(bean.getGate());
			sql.append("  and (substring(sys_date,5) = substring(" + bean.getDate() + ",5) or substring(mdate,5) = substring(" + bean.getDate() + ",5))");
			sql.append("  and (bk_seq1=" + Ryt.addQuotes(bean.getBkSeq()) + " or bk_seq2=" + Ryt.addQuotes(bean.getBkSeq()) + ")");
			sql.append("  limit 1 ");
		} else if (bean.getFlag() == 4) {
			sql.append("  sys_date = ");
			sql.append(bean.getDate());
			sql.append(" and gid= ");
			sql.append(bean.getGate());
			sql.append(" and p1=");
			sql.append(Ryt.addQuotes(bean.getP1()));
			sql.append(" and p2=");
			sql.append(Ryt.addQuotes(bean.getP2()));
			sql.append(" limit 1 ");
		} else if (bean.getTseq() != null || bean.getFlag() == 1) {
			sql.append(" tseq  = ").append(bean.getTseq());
		} else if (bean.getFlag() == 6) { // 根据网关号+银行流水对账
			sql.append("  gate = ").append(bean.getGate());
			sql.append("  and (bk_seq1=").append(Ryt.addQuotes(bean.getBkSeq()));
			sql.append("   or  bk_seq2=").append(Ryt.addQuotes(bean.getBkSeq())).append(")");
			sql.append("  limit 1 ");
		} else if ((bean.getBkSeq() != null && bean.getGate() != null) || bean.getFlag() == 2) {
			sql.append("  sys_date = ").append(bean.getDate());
			sql.append("  and gid  = ").append(bean.getGate());
			sql.append("  and (bk_seq1=").append(Ryt.addQuotes(bean.getBkSeq()));
			sql.append("   or  bk_seq2=").append(Ryt.addQuotes(bean.getBkSeq())).append(")");
			sql.append("  limit 1 ");
		} else if ((bean.getMerOid() != null && bean.getGate() != null) || bean.getFlag() == 3) {
			sql.append("  gate  = ").append(bean.getGate());
			sql.append(" and oid  = '").append(bean.getMerOid()).append("'");
			sql.append(" limit 1 ");
		} else {
			return null;
		}
		
		List data = query(sql.toString(), SettleHlog.class);
		if (data.size() > 0) {
			return (SettleHlog) data.get(0);
		}
		return null;
	}


	public String getBankFeeModel(String mid, int gate) {
		String sql = "select bk_fee_mode from fee_calc_mode where mid='"+ mid+"' and gate=" + gate;
		String bkFeeMode = queryForString(sql);
		if(bkFeeMode==null) return "AMT*0";
		return bkFeeMode;
		
	}
	/**
	 * 根据tseq修改银行手续费
	 * @param bankFee
	 * @param tseq
	 * @return
	 */
	public int  updateBankFeeById(String bankFee,long tseq){
		String sql = " update hlog set bk_chk = 1,bank_fee = "+ bankFee + " where tseq = " + tseq;
		return update(sql);
	}

	/**
	 * 银行手续费计算公式
	 * @param gate
	 * @param gid
	 * @param bkAmt
	 * @return 返回 BkFeeModel
	 */
	public String getBkFeeModel(Integer gate, Integer gid) {
		try {
			String sql = "select fee_model from gates where ryt_gate = " + gate + " and gid = " + gid;
			return queryForString(sql);
		} catch (Exception e) {
			System.err.println("ChargeMode error : gate:"+gate+",gid:"+gid);
			return "AMT*0";
		}
	}
	
	/**
	 * 对账的失败和可疑对hlog的处理
	 * @param bk_chk
	 * @param element
	 * @return
	 * @throws Exception 
	 */
	public int[] errorCheckHandle(int bk_chk, SBean element) throws Exception{
		String[] sqls = new String[2];
		sqls[0]=getUpdateHlogSql(bk_chk, element);
		sqls[1]=getUpdateAnalysisSql(bk_chk, element);
		return batchSqlTransaction(sqls);
	}
	//获得修改hlog的sql语句
	private String getUpdateHlogSql(int bk_chk, SBean element) {
		String sql="update hlog set bk_chk = " + bk_chk + " where tseq = " + Ryt.addQuotes(element.getTseq());
		return sql; 
	}
	//获得插入error_analysis的sql语句
	private String getUpdateAnalysisSql(int bk_chk, SBean element){
		int error_type = 1;
		String tseq = element.getTseq();
		String amount = Ryt.mul100(element.getAmt());
		StringBuffer s1 = new StringBuffer("insert into error_analysis (");
		if (bk_chk == 2)
			s1.append("tseq,");
		s1.append("bk_mer_oid,bk_amount,bk_date,pay_date,error_type,check_date,gate)");
		s1.append("values(");
		if (bk_chk == 2)
			s1.append(tseq).append(",");
		// 银行订单号
		if(!Ryt.empty(element.getTseq()))
			s1.append(Ryt.addQuotes(element.getTseq())).append(",");
		else if(!Ryt.empty(element.getMerOid()))
			s1.append(Ryt.addQuotes(element.getMerOid())).append(",");
		else
			s1.append(Ryt.addQuotes(element.getBkSeq())).append(",");
		s1.append(amount).append(",");
		s1.append(element.getDate()).append(",");
		s1.append(element.getDate()).append(",");
		if (bk_chk == 2)
			error_type = 0;
		s1.append(error_type).append(",");
		s1.append(DateUtil.today()).append(",");
		s1.append(element.getGate());
		s1.append(")");
		return s1.toString();
	}
	
	/**
	 * 对账成功后增加存管数据
	 * @param h
	 * @throws Exception
	 */
	
	/**
	public void createCGData(SettleHlog h) {
		
		StringBuffer sql1 = new StringBuffer();
		sql1.append("insert into tr_seq (liq_date,obj_id,tr_date,tr_time,tr_amt,tr_type,tr_flag,sys_date)");
		sql1.append(" values (0,");
		sql1.append(h.getMid()).append(",");
		sql1.append(h.getSysDate()).append(",");
		sql1.append(h.getSysTime()).append(",");
		sql1.append(h.getAmount()).append(",");
		sql1.append(0).append(",");//支付
		sql1.append(h.getTseq()).append(",");
		sql1.append(DateUtil.today());
		sql1.append(")");
		
		
		StringBuffer sql2 = new StringBuffer();
		sql2.append("insert into tr_seq (liq_date,obj_id,tr_date,tr_time,tr_amt,tr_type,tr_flag,sys_date)");
		sql2.append(" values (0,");
		sql2.append(h.getMid()).append(",");
		sql2.append(h.getSysDate()).append(",");
		sql2.append(h.getSysTime()).append(",");
		sql2.append(h.getFeeAmt()).append(",");
		sql2.append(1).append(",");//支付手续费
		sql2.append(h.getTseq()).append(",");
		sql2.append(DateUtil.today());
		sql2.append(")");
		
		int amt = h.getAmount()- h.getFeeAmt();
		//存管银行行号
		String bkNo = getBKNOByPayChannel(h.getGid());
		
		if(bkNo==null || bkNo == "") return;
		
		String sql3 = "update bk_account set bk_bl = bk_bl + " + h.getAmount()+", bf_bl = bf_bl+"+amt + " where bk_no='"+bkNo+"'";
		
		try {
			// 增加 支付金额流水
			update(sql1.toString());
			//增加手续费金额流水
			update(sql2.toString());
			//修改银行余额数据
			update(sql3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

**/
	
}

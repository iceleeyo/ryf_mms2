package com.rongyifu.mms.ewp.ryf_df;

import java.util.List;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.DfTransaction;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Constant.PayState;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

/***
 * 商户账户操作
 *
 */
public class MerAccHandleUtil {
	@SuppressWarnings("rawtypes")
	private static AdminZHDao dao=new AdminZHDao();

    public static void doModifyForFail(String orderId, String accountId, String errorMsg, Integer gate, Integer bkDate, Integer bkTime, String transAmt, String bkSeq, Integer gid, TlogBean tlog) throws NumberFormatException {
        updateTlogState(orderId, accountId, PayState.FAILURE, errorMsg, gate, bkDate, bkTime, transAmt, bkSeq, Constant.HLOG, gid);
        if (tlog.getData_source() == 8) {
            AccSeqs param = new AccSeqs();
            param.setTrAmt(Long.parseLong(transAmt));
            param.setAid(accountId);
            param.setUid(accountId);
            param.setTrFee(tlog.getFee_amt());
            param.setAmt(tlog.getPay_amt());
            param.setTbId(orderId);
            param.setTbName("fee_liq_bath2");
            param.setRemark(seqRemark(tlog.getData_source()));
            increaseAccSeq(param);
        } else {
            String sql = RecordLiveAccount.calUsableBalance(accountId, accountId, tlog.getPay_amt(), Constant.RecPay.INCREASE);
            dao.batchSqlTransaction(new String[]{sql});
        }
    }
	
	
	/***
	 * 处理成功交易
	 * @return
	 */
	public static boolean handleSuc(String accountId,String orderId,String transAmt,String sysDate,String sysTime,
													String tseq,String transStatus,String errorMsg){
		DfTransaction dfTrans=getDfTrans(tseq, orderId,sysDate);
		if(dfTrans==null){
			LogUtil.printErrorLog("MerAccHandleUtil", "handleSuc", "df_transaction 查不到原交易，tseq:"+tseq+" orderId:"+orderId+" sysDate:"+sysDate);
			return false;
		}
		Object[] objs=getTlog(orderId,accountId);
		if(objs==null||objs.length<=0 || objs.length>2){
			LogUtil.printErrorLog("MerAccHandleUtil", "handleSuc", "查询tlog信息异常,method:getTlog(orderId,accountId)");
			return false;
		}
		String tab=(String)objs[0];
		TlogBean tlog=(TlogBean)objs[1];
		if(tlog.getTstat()==PayState.SUCCESS){
			LogUtil.printInfoLog("MerAccHandleUtil", "handleSuc", "原交易已经成功！tseq:"+orderId);
			return true;
		}
		
		String bkSeq=dfTrans.getBkSeq();
		Integer bkDate=dfTrans.getBkDate();
		Integer bkTime=dfTrans.getBkTime();
		Integer gate=dfTrans.getGate();
		Integer gid=dfTrans.getGid();
		updateTlogState(orderId, accountId, PayState.SUCCESS, errorMsg, gate, bkDate, bkTime, transAmt, bkSeq,tab,gid);
		
		AccSeqs param=new AccSeqs();
		param.setTrAmt(Long.parseLong(transAmt));
		param.setAid(accountId);
		param.setUid(accountId);
		param.setTrFee(tlog.getFee_amt());
		param.setAmt(tlog.getPay_amt());
		param.setTbId(orderId);
        if(tlog.getData_source()==7||tlog.getData_source()==8){
            param.setTbName("fee_liq_bath");   
        }else{
            param.setTbName(tab);
        }
        LogUtil.printInfoLog("MerAccHandleUtil", "handleSuc", "data_source:"+tlog.getData_source()+"    param.gettbname:"+param.getTbName());
		param.setRemark(seqRemark(tlog.getData_source()));
        
		increaseAccSeq(param);
		return true;
	}
	
	/**
	 * 处理失败交易
	 * @return
	 */
	public static boolean handleFail(String accountId,String orderId,String transAmt,String sysDate,String sysTime,
													String tseq,String transStatus,String errorMsg){
		DfTransaction dfTrans=getDfTrans(tseq, orderId,sysDate);
		if(dfTrans==null){
			LogUtil.printErrorLog("MerAccHandleUtil", "handleFail", "df_transaction 查不到原交易，tseq:"+tseq+" orderId:"+orderId+" sysDate:"+sysDate);
			return false;
		}
		Object[] objs=getTlog(orderId,accountId);
		if(objs==null ||objs.length<=0 || objs.length>2){
			LogUtil.printErrorLog("MerAccHandleUtil", "handleFail", "查询tlog信息异常,method:getTlog(orderId,accountId)");
			return false;
		}
		TlogBean tlog=(TlogBean)objs[1];
		if(tlog.getTstat()==PayState.FAILURE){
			LogUtil.printInfoLog("MerAccHandleUtil", "handleFail", "原交易已经失败！tseq:"+orderId);
			return true;
		}
		String bkSeq=dfTrans.getBkSeq();
		Integer bkDate=dfTrans.getBkDate();
		Integer bkTime=dfTrans.getBkTime();
		Integer gate=dfTrans.getGate();
		Integer gid=dfTrans.getGid();

        doModifyForFail(orderId, accountId, errorMsg, gate, bkDate, bkTime, transAmt, bkSeq, gid, tlog);
		
		return true;
	}

    private static void increaseAccSeq(AccSeqs param) {
        // 插入流水表 tb_name-流水
        List<String> sqlList = RecordLiveAccount.handleBalanceForTX(param);
        String[] sqls = sqlList.toArray(new String[sqlList.size()]);
        dao.batchSqlTransaction(sqls);
    }
	
	/***
	 * 
	 * @param orderId
	 * @param accountId
	 * @param sys_date
	 * @param tstat
	 * @param errMsg
	 * @param gate
	 * @param bkDate
	 * @param bkTime
	 * @param transAmt
	 * @param bkSeq
	 */
	private static void updateTlogState(String orderId,String accountId,Integer tstat,String errMsg,Integer gate,Integer bkDate,
																Integer bkTime,String transAmt,String bkSeq,String tab,Integer gid){
		
		StringBuffer sql=new StringBuffer("update");
		sql.append(" ").append(tab).append(" set");
		sql.append(" tstat=").append(tstat).append(",");
		sql.append("error_msg=").append(Ryt.addQuotes(errMsg)).append(",");
		sql.append("gid=").append(gid).append("");
		
		if(tstat==PayState.SUCCESS){
			String bkFeeMode=dao.getBkFeeModeByGate(accountId, String.valueOf(gate));
			String bkFeeAmt=ChargeMode.reckon(bkFeeMode,transAmt);
			sql.append(" ,bk_seq1=").append(Ryt.addQuotes(bkSeq));
			sql.append(" ,bk_seq2=").append(Ryt.addQuotes(bkSeq));
			sql.append(" ,bk_date=").append(bkDate);
			sql.append(" ,bank_fee=").append(bkFeeAmt);
			sql.append(" ,bk_recv=").append(bkTime);
		}
		
		sql.append(" where mid = " + Ryt.addQuotes(accountId));
		sql.append("   and tseq =" + orderId);
		sql.append("   and tstat!=" + PayState.SUCCESS);
		
		if(dao.update(sql.toString())==0){
			if(tab.equals(Constant.HLOG)){
				String sql2=sql.toString().replace(Constant.HLOG, Constant.TLOG);
				dao.update(sql2);
			}else if(tab.equals(Constant.TLOG)){
				String sql2=sql.toString().replace(Constant.TLOG, Constant.HLOG);
				dao.update(sql2);
			}
				
		}
		
	}
	
	
	public static DfTransaction getDfTrans(String tseq,String orderId,String sysDate){
		String sql="select tseq,order_id,bk_send_date,bk_send_time,bk_date,bk_time,sys_date,sys_time,gate,gid,bk_seq,tstat " +
					"from df_transaction where tseq="+Ryt.addQuotes(tseq)+" and order_id="+Ryt.addQuotes(orderId)+" " +
					"and sys_date="+sysDate+";";
		DfTransaction dfTrans=null;
		try{
            LogUtil.printInfoLog("MerAccHadleUtil", "getDfTrans", " 执行查询Sql:["+sql+"]");
			dfTrans=(DfTransaction)dao.getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper(DfTransaction.class));
		}catch (Exception e) {
			LogUtil.printErrorLog("MerAccHandleUtil", "getDfTrans", "执行查询异常，sql:"+sql, e);
		}
		return dfTrans;
	}
	
	public static Object[] getTlog(String tseq,String accountId){
		Object[] res=null;
		String sql="";
		try{
			res=new Object[2];
			sql="select pay_amt,fee_amt,type,data_source,tstat from tlog where mid="+Ryt.addQuotes(accountId)+" and tseq="+Ryt.addQuotes(tseq);
            LogUtil.printInfoLog("MerAccHadleUtil", "getTlog", " 执行查询Sql:["+sql+"]");
            TlogBean tb=(TlogBean)dao.getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper(TlogBean.class));
			res[0]="tlog";
			res[1]=tb;
        }catch(EmptyResultDataAccessException e) {
           try {
                sql=sql.replace("tlog", "hlog");
                LogUtil.printInfoLog("MerAccHadleUtil", "getTlog", " 执行查询Sql:["+sql+"]");
                TlogBean tb=(TlogBean)dao.getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper(TlogBean.class));
                res[0]="hlog";
                res[1]=tb;
            } catch (Exception e2) {
                LogUtil.printErrorLog("MerAccHandleUtil", "getTlog", "查询异常,sql:"+sql, e2);
                res=null;
            }
            
		}catch (Exception e) {
			LogUtil.printErrorLog("MerAccHandleUtil", "getTlog", "查询异常,sql:"+sql, e);
            res=null;
		}
		return res;
	}
	
	
	private static String seqRemark(Integer dataSource){
			String remark="";
			if(dataSource==0){
				  remark="常规渠道";
			}else if(dataSource==1){
				remark="接口代付";
			}else if(dataSource==2){
				remark="HTML5支付";
			}else if(dataSource==3){
				remark="POS同步";
			}else if(dataSource==4){
				remark="VAS同步";
			}else if(dataSource==5){
				remark="管理平台代付";
			}else if(dataSource==6){
				remark="POS转账";
			}else if(dataSource==7){
				remark= "自动代付";
		   }else if(dataSource==8){
			   remark= "自动結算";
		   }
		
		 return remark;
	}
	
	
	
	
}

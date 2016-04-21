package com.rongyifu.mms.modules.accmanage.dao;

import java.sql.Types;

import com.rongyifu.mms.bean.BankNoInfo;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

public class AutoDFInfoPreserveDao extends PubDao {
	
	
	/*
	 * 自动代付信息维护查询
	 */
	public CurrentPage<Minfo> queryAutoDf(Integer pageNo, String mid,
			Integer mstate) {

		StringBuffer sql = new StringBuffer("select id,name,mstate,aid,auto_df_state,pbk_prov_id,");
		sql.append("pbk_name,pbk_branch,pbk_no,pbk_acc_no,pbk_acc_name, pbk_gate_id");
		sql.append(" from acc_infos ,minfo where  id=uid and acc_infos.aid=acc_infos.uid ");
		if (!Ryt.empty(mid))sql.append(" and id =").append(Ryt.addQuotes(mid));
		if (mstate != null)sql.append(" and mstate=").append(mstate);
		StringBuffer sqlCount = new StringBuffer("select count(*) from acc_infos ,minfo");
		sqlCount.append(" where id=uid  and acc_infos.aid=acc_infos.uid ");
		if (!Ryt.empty(mid))sqlCount.append(" and id =").append(Ryt.addQuotes(mid));
		if (mstate != null)sqlCount.append(" and mstate=").append(mstate);
		return queryForPage(sqlCount.toString(), sql.toString(), pageNo,new AppParam().getPageSize(), Minfo.class);
	}
	
	/*
	 * 根据商户号查询出自动代付的信息
	 */
	
	public Minfo queryByidAutoDf(String mid){
		StringBuffer sql = new StringBuffer("select id,name,mer_trade_type,aid,auto_df_state,pbk_prov_id,");
		sql.append("pbk_name,pbk_branch,pbk_no,pbk_acc_no,pbk_acc_name, pbk_gate_id");
		sql.append(" from acc_infos ,minfo where  id=uid and acc_infos.aid=acc_infos.uid ");
		sql.append(" and id=").append(Ryt.addQuotes(mid));
		return queryForObject(sql.toString(), Minfo.class);
	}
	
	public void updateMerAutoDf(Minfo minfo) throws Exception {
		Object[] obj = {
				minfo.getAutoDfState(),minfo.getPbkProvId(),minfo.getPbkName(),
				minfo.getPbkBranch(),minfo.getPbkAccNo(),minfo.getPbkAccName(),
				minfo.getPbkNo(),minfo.getPbkGateId(),minfo.getId()
				};
		int[] type = { 
				Types.TINYINT,Types.VARCHAR,Types.VARCHAR,
				Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,		
				Types.VARCHAR,Types.TINYINT,Types.VARCHAR
				};
		StringBuffer sb = new StringBuffer();
		sb.append("update minfo set auto_df_state =?,pbk_prov_id =?,pbk_name =?,pbk_branch =?,");
		sb.append("pbk_acc_no =?,pbk_acc_name =?,pbk_no =?, pbk_gate_id=?,");	
		sb.append(" last_update=DATE_FORMAT(NOW(),'%Y%m%d%H%i%s') where id =?");
		update(sb.toString(), obj, type);
}
/*
 * 查询联行号
 */
	public CurrentPage<BankNoInfo> queryBKNo(Integer PageNo,String gate,String bkname ){
		String sql="select bk_no,bk_name";
		StringBuffer querysql = new StringBuffer(" from bank_no_info where type=01  ");
		if (!Ryt.empty(gate)){
			String gate1=gate.substring(2);
			querysql.append(" and gid =").append(Ryt.addQuotes(gate1));
		}
		if (!Ryt.empty(bkname)&&!"请输入开户行关键字检索联行行号".equals(bkname))querysql.append(" and bk_name like'%").append(bkname).append("%'").append(" or bk_no like'%").append(bkname).append("%'");
		return queryForPage("select count(*)"+querysql.toString(), sql+querysql.toString(), PageNo,new AppParam().getPageSize(), BankNoInfo.class);
		
	}

}

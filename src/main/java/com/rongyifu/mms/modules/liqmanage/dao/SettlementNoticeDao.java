package com.rongyifu.mms.modules.liqmanage.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.bean.AccSyncDetail;
import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.bean.MinfoNotice;
import com.rongyifu.mms.bean.MinfoNoticeSync;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.SettleDetail;
import com.rongyifu.mms.bean.SettlementDetail;
import com.rongyifu.mms.bean.SettlementSum;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
@SuppressWarnings("rawtypes")
public class SettlementNoticeDao extends PubDao{
	
	public CurrentPage<MinfoNotice> getMinfoNotices(int pageNo,Integer pageSize,
			int beginDate, int endDate, String mid, String name) {
		// TODO Auto-generated method stub
		String sql = "select  * from minfo_notice mc";
		
		StringBuffer condSql = new StringBuffer(" where mc.id > 0 ");
		if (beginDate != 0)
			condSql.append(" and mc.create_date >= ").append(beginDate);
		if (endDate != 0)
			condSql.append(" and mc.create_date <= ").append(endDate);
	
		if (!mid.trim().equals(""))
			condSql.append(" and mc.mid='"+ mid+"'");
			//condSql.append(" and mc.state ='0'");
		if (!name.trim().equals(""))
			condSql.append(" and mc.name like'%" + name+"%'");
		
		String querySql = sql + condSql.toString();
		
		String countSql = "select count(*) from minfo_notice mc  "+condSql+" ;";
		
		
		return queryForPage(countSql,querySql, pageNo,pageSize,MinfoNotice.class);
	}

	public int addNotice(MinfoNotice minfoNotice) {
		String sql = "insert into minfo_notice (mid,name,ip,create_date) values (?,?,?,?)";
		System.out.println(DateUtil.today());
		
		Object[] args = new Object[]{minfoNotice.getMid(),minfoNotice.getName(),minfoNotice.getIp(),DateUtil.today()};
		int count = update(sql, args);
		saveOperLog("新增同步商户", count==1?"添加成功":"添加失败");
		return count;
	}
	
	public int addNoticeSync(FeeLiqBath feeLiqBath) {
		String sql = "insert into minfo_notice_sync (mid,batch,sync_state,liq_date,reason) values (?,?,?,?,?)";
		
		
		Object[] args = new Object[]{feeLiqBath.getMid(),feeLiqBath.getBatch(),"2",feeLiqBath.getLiqDate(),""};
		int count = update(sql, args);
		saveOperLog("新增同步记录", count==1?"添加成功":"添加失败");
		return count;
	}
	
	public int delById(Integer id){
		if(id == null){
			return 0;
		}
		String sql = "delete from   minfo_notice where id = ?";
		int count = update(sql, new Object[]{id});
		saveOperLog("删除同步同步", count==1?"删除成功":"删除失败");
		return count;
	}
	
	public int update(MinfoNotice minfoNotice){
		if(minfoNotice == null || minfoNotice.getId() == 0 ||(StringUtils.isBlank(minfoNotice.getIp()))){
			return 0;
		}
		StringBuilder sql = new StringBuilder("UPDATE minfo_notice SET");
		List<Object> args = new ArrayList<Object>();
		if(StringUtils.isNotBlank(minfoNotice.getIp())){
			sql.append(" ip = ?,");
			args.add(minfoNotice.getIp());
		}
		
		
		sql.deleteCharAt(sql.length()-1);//删除末尾的','
		sql.append(" WHERE id = ?");
		args.add(minfoNotice.getId());
		int count = update(sql.toString(), args.toArray());
		saveOperLog("更新同步商户信息", count==1?"更新成功":"更新失败");
		return count;
	}

	public MinfoNotice queryById(Integer id) {
		// TODO Auto-generated method stub
		if(id == null){
			return null;
		}
		String sql = "select * from minfo_notice where id = ?";
		return queryForObject(sql, new Object[]{id}, MinfoNotice.class);
	}

	public String getMinfoName(Integer mid) {
		// TODO Auto-generated method stub
		if(mid == null){
			return null;
		}
		String sql = "select name from minfo where id = '"+mid+"'";
		return queryForString(sql);
		 
	}

	public CurrentPage<MinfoNoticeSync> getNoticeSyncs(int pageNo,
			int pageSize, int beginDate, int endDate, String mid, String name,
			String syncState) {
		// TODO Auto-generated method stub
		String sql = "select  mns.*,m.name from minfo_notice_sync mns LEFT JOIN minfo m on m.id=mns.mid";
		
		StringBuffer condSql = new StringBuffer(" where mns.id > 0 ");
		if (beginDate != 0)
			condSql.append(" and mns.liq_date >= ").append(beginDate);
		if (endDate != 0)
			condSql.append(" and mns.liq_date <= ").append(endDate);
	
		if (!mid.trim().equals(""))
			condSql.append(" and mns.mid='"+ mid+"'");
		if (!syncState.trim().equals(""))
			condSql.append(" and mns.sync_state ='"+syncState+"'");
		if (!name.trim().equals(""))
			condSql.append(" and m.name like'%" + name+"%'");
		
		String querySql = sql + condSql.toString();
		
		String countSql = "select count(*) from minfo_notice_sync mns LEFT JOIN minfo m on m.id=mns.mid "+condSql+" ;";
		
		
		return queryForPage(countSql,querySql, pageNo,pageSize,MinfoNoticeSync.class);

	}

	public MinfoNoticeSync isCheckState(String batch) {
		// TODO Auto-generated method stub
		if(batch == null){
			return null;
		}
		String sql = "select * from minfo_notice_sync where batch = ?";
		return queryForObject(sql, new Object[]{batch}, MinfoNoticeSync.class);
	}

	/**
	 * 修改同步狀態
	 */
	public void updateNoticSync(String batch,String syncState,String reason){
		String sql = "update minfo_notice_sync set sync_state='"+syncState+"',reason='"+reason+"'  where batch='"+ Ryt.sql(batch) + "'";
		update(sql);
	}
	
	//结算单汇总数据
	public SettlementSum getSettlementSum(String  batch) {
		// TODO Auto-generated method stub
		if(batch == null){
			return null;
		}
		String sql = "select mid merId ,m.name merName,batch liqBatch,liq_date,FORMAT(trans_amt/100,2) transAmtSum,pur_cnt transCount, FORMAT(ref_amt/100,2) refundAmtSum,  ref_cnt refundCount," +
				"FORMAT(manual_add/100,2) manualAddAmt,  add_cnt manualAddCount, FORMAT(manual_sub/100,2)  manualSubAmt, sub_cnt  manualSubCount, FORMAT(fee_amt/100,2)  feeAmtSum, FORMAT(ref_fee/100,2) refundFeeSum," +
				" FORMAT(liq_amt/100,2)  liqAmt  from fee_liq_bath  LEFT JOIN minfo m on mid=m.id where batch=?";
		return queryForObject(sql, new Object[]{batch}, SettlementSum.class);
	}
	
	//结算单明细数据
		public List<SettlementDetail> getSettlementDetail(String  batch) {
			// TODO Auto-generated method stub
			if(batch == null){
				return null;
			}
			String sql = "select mdate merDate, oid orderId, FORMAT(amount/100, 2) transAmt, FORMAT(fee_amt/100,2) feeAmt,   'P' as transType,  tseq from hlog where batch='"+Ryt.sql(batch)+"'";
			String refSql = "select org_mdate  merDate,org_oid orderId,FORMAT(ref_amt/100,2) as transAmt,FORMAT(mer_fee/100,2)feeAmt,'R' as transType,tseq from refund_log where batch='"
					+ batch+"'";
			 List<SettlementDetail> detailList = query(sql, SettlementDetail.class);
			 detailList.addAll(query(refSql,SettlementDetail.class));
			 return detailList;
		}
		//获取需要推送的url
		public String  getNoticeUrl(String mid) {
			if(mid == null){
				return null;
			}
			String sql = "select ip from minfo_notice where mid = '"+mid+"'";
			return queryForString(sql);
		}


		public MinfoNotice queryByMid(String mid) {
			// TODO Auto-generated method stub
			if(mid == null){
				return null;
			}
			String sql = "select * from minfo_notice where mid = ?";
			return queryForObject(sql, new Object[]{mid}, MinfoNotice.class);
		}
		
		
	
	
}

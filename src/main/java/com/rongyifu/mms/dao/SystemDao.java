package com.rongyifu.mms.dao;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.Gate;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.GlobalParams;
import com.rongyifu.mms.bean.MMSNotice;
import com.rongyifu.mms.bean.NewGate;
import com.rongyifu.mms.bean.VisitIpConfig;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;

@SuppressWarnings("unchecked")
public class SystemDao extends PubDao {
	
	
	/**
	 * @param pageNo
	 * @param pageSize
	 * @param transMode
	 * @param rytGate
	 * @return
	 */
	public CurrentPage<Gate> queryApplyPage(Integer pageNo, Integer pageSize, Integer transMode, Integer rytGate){
		StringBuilder sqlCountRows = new StringBuilder("SELECT COUNT(*) FROM batch_swith_route WHERE status = 0");
		StringBuilder sqlFetchRows = new StringBuilder("SELECT gid,trans_mode,ryt_gate,id FROM batch_swith_route WHERE status=0");
		StringBuilder condition = new StringBuilder();
		List<Object> args = new ArrayList<Object>();
		if(null != transMode){
			condition.append(" AND trans_mode = ?");
			args.add(transMode);
		}
		if(null != rytGate){
			condition.append(" AND ryt_gate = ?");
			args.add(rytGate);
		}
		sqlCountRows.append(condition);
		sqlFetchRows.append(condition);
		return queryForPage(sqlCountRows.toString(), sqlFetchRows.toString(), args.toArray(), pageNo, pageSize, Gate.class);
	}
	
	public void applySetRouteOfGate(List<Integer> gateList,Integer gid,Integer transMode){
		int size = gateList.size();
		for(int i = 0; i < size; i++){
			int rytGate = gateList.get(i);
			StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM batch_swith_route WHERE gid=");
 			sql.append(gid).append(" AND trans_mode=").append(rytGate).append(" AND status=0");
			int count  = queryForInt(sql.toString());
			if(count == 0){//有重复申请的不在申请
				update("INSERT INTO batch_swith_route (gid,trans_mode,ryt_gate) values (?,?,?)",
					   new Object[] {gid, transMode, rytGate}, 
					   new int[] { Types.INTEGER, Types.TINYINT, Types.INTEGER});
			}
		}
		saveOperLog("批量修改网关渠道申请 ", "申请成功,gid:"+gid+",transMode:"+transMode+",gateList:"+Arrays.toString(gateList.toArray(new Integer[0])));
	}
	
	/**
	 * @param ids
	 * @param status
	 * @return int[] 成功修改的申请
	 * 批量审核
	 */
	public int[] checkBatchChangeRouteByGate(List<Integer> ids, Integer status){
		List<String> sqls = new ArrayList<String>();
		if (status == 1) {//批量成功
			for (Integer id : ids) {
				String sql = "SELECT ryt_gate,gid,trans_mode FROM batch_swith_route WHERE id = ? AND status = 0";
				Object[] args = new Object[] { id };
				int[] argTypes = new int[] { Types.INTEGER };
				Gate gate = queryForObject(sql, args, argTypes, Gate.class);
				if (null != gate) {
					Gate g= queryForObject("SELECT gate_id,fee_model from gates WHERE ryt_gate="+gate.getRytGate()+" AND gid="+gate.getGid(), Gate.class);
					StringBuilder sql2 = new StringBuilder("UPDATE fee_calc_mode SET gid=").append(gate.getGid());
					sql2.append(",gate=").append(g.getGateId()).append(",gate_id=").append(g.getGateId());
					sql2.append(", bk_fee_mode=").append(Ryt.addQuotes(g.getFeeModel()));
					sql2.append(" WHERE trans_mode = ");
					sql2.append(gate.getTransMode()).append(" AND gate = ");
					sql2.append(gate.getRytGate());
					StringBuilder sql3 = new StringBuilder("UPDATE batch_swith_route SET status=");
					sql3.append(status).append(" WHERE id = ").append(id);
					sqls.add(sql2.toString());
					sqls.add(sql3.toString());
				}
			}
		}else{//批量取消
			for (Integer id : ids) {
				StringBuilder sql = new StringBuilder("UPDATE batch_swith_route SET status = ").append(status).append(" WHERE id =").append(id);
				sqls.add(sql.toString());
			}
		}
		String[] sqlArray = new String[sqls.size()];
		sqls.toArray(sqlArray);
		saveOperLog("批量修改网关渠道批量审核","申请id:"+Arrays.toString(ids.toArray(new Integer[0]))+",审核结果:"+(status==1?"启用":"撤销"));
		return batchSqlTransaction(sqlArray);
	}
	
	/**
	 * @return
	 * @throws Exception 
	 * 批量修改网关渠道审核
	 */
	public int[] checkChangeRouteByGate(Integer id, Integer status){
		int [] rst = null;
		if (status == 1) {//启用
			String sql = "SELECT ryt_gate,gid,trans_mode FROM batch_swith_route WHERE id = ? AND status = 0";
			Object[] args = new Object[] { id };
			int[] argTypes = new int[] { Types.INTEGER };
			Gate gate = queryForObject(sql, args, argTypes, Gate.class);
			if (null != gate) {
				Gate g= queryForObject("SELECT gate_id,fee_model from gates WHERE ryt_gate="+gate.getRytGate()+" AND gid="+gate.getGid(), Gate.class);
				StringBuilder sql2 = new StringBuilder("UPDATE fee_calc_mode SET gid=").append(gate.getGid());
				sql2.append(",gate=").append(g.getGateId()).append(",gate_id=").append(g.getGateId());
				sql2.append(", bk_fee_mode=").append(Ryt.addQuotes(g.getFeeModel()));
				sql2.append(" WHERE trans_mode = ").append(gate.getTransMode());
				sql2.append(" AND gate = ").append(gate.getRytGate());
				StringBuilder sql3 = new StringBuilder("UPDATE batch_swith_route SET status=");
				sql3.append(status).append(" WHERE id = ").append(id);
				rst = batchSqlTransaction(new String[]{sql2.toString(),sql3.toString()});
			}
		}else{//撤回
			String sql = "UPDATE batch_swith_route SET status = ? WHERE id = ?";
			Object[] args = new Object[]{status,id};
			update(sql, args, new int[]{Types.INTEGER,Types.INTEGER});
			rst=new int[]{1};
		}
		saveOperLog("批量修改网关渠道审核","审核成功,申请id:"+id+",审核结果:"+(status==1?"启用":"撤销"));
		return rst;
	}
	
   /**
     * 商户网关配置页面更新
     * @param mid
     * @param ng
     * @return
     */
    public int updateFeeCalcMode(FeeCalcMode ng){
    	
    	StringBuffer sel = new StringBuffer();
    	sel.append(" SELECT gate_id ,author_type, fee_model, trans_mode FROM gates ");
    	sel.append(" WHERE author_type = ").append(ng.getAuthorType());
    	sel.append(" AND trans_mode = ").append(ng.getTransMode());
    	sel.append(" AND gate_desc_short = ").append(Ryt.addQuotes(Ryt.sql(Ryt.sql(ng.getGateName().split("\\(")[0]))));
    	
         NewGate l = queryForObject(sel.toString(),NewGate.class);
         
         
         StringBuffer updt = new StringBuffer();
         updt.append(" UPDATE fee_calc_mode  SET gate_id = ").append(Ryt.addQuotes(l.getGateId()));
         updt.append(" , author_type= ").append(l.getAuthorType());
         updt.append(" ,bk_fee_mode= ").append(Ryt.addQuotes(l.getFeeModel()));
         updt.append(" ,trans_mode=").append(l.getTransMode());
         updt.append(" WHERE mid = ").append(ng.getMid());
         updt.append(" AND gate=  ").append(ng.getGate());
         int rslt = update(updt.toString());
         saveOperLog("更新FeeCalcMode",rslt==1?"成功":"失败");
         return rslt;
    }
    
     public List<FeeCalcMode> query4MinfoBank(String mid){
        String sql = "select mid,gate,author_type,trans_mode , gate_id , bk_fee_mode from fee_calc_mode where mid = '"+mid+"'";
        return  query(sql,FeeCalcMode.class);   
    } 
   
	/**
	 * 删除系统通知信息
	 * @param id
	 * @return
	 */
	public String deleteNotice(String id) {
		String msg = "ok";
		String deleteSql = "delete from mms_notice where id = " + Ryt.sql(id);
		try {
			update(deleteSql);
		} catch (Exception e) {
			e.printStackTrace();
			msg = "error";
		}
		return msg;
	}

	public List<MMSNotice> queryMMSNotice(String mid) {
		String sql="";
		if("1".equals(mid)){ //管理平台操作
			sql="select id,title,expiration_date,notice_type,begin_date from mms_notice where  end_date >=" + DateUtil.today() + " and begin_date <="
					+ DateUtil.today() + " and (mid = 0 or mid = '"+mid+"')" +" union all select id,title,expiration_date,notice_type,begin_date from mms_notice where  end_date >=" + DateUtil.today() + " and begin_date <="
					+ DateUtil.today()+" and expiration_date is not null and expiration_date<>0 ;";
		}else{
			sql = "select id,title from mms_notice where  end_date >=" + DateUtil.today() + " and begin_date <="
					+ DateUtil.today() + " and (mid = 0 or mid = '"+mid+"')";
		}
		return  query(sql, MMSNotice.class);
	}
	
	public MMSNotice getMessageById(int id) {
		String queryById = "select  *  from mms_notice where id =  " +id;
		return queryForObject(queryById, MMSNotice.class);
	}

	public List<MMSNotice> getMessage(MMSNotice notice) {
		List<MMSNotice> l = null;
	String querySQL = "select id,begin_date,end_date,title,mid from mms_notice where id >0 and (mid=0 or mid="+notice.getMid()+")";
	if(notice.getBeginDate()!=null && notice.getEndDate()==null){
		
		querySQL += " and begin_date >=" + notice.getBeginDate();
		
	}else if(notice.getBeginDate()==null && notice.getEndDate()!=null){
		
		querySQL += " and end_date <= " + notice.getEndDate();
		
	}else if(notice.getBeginDate()!=null && notice.getEndDate()!=null){
		
		querySQL += " and (( begin_date >=" + notice.getBeginDate() + " and begin_date <=" + notice.getEndDate() ;
		querySQL += ") or (end_date >=" + notice.getBeginDate() + " and end_date <=" + notice.getEndDate() +"))     " ;
		
	}else{
		querySQL += "";
	}
//		if(notice.getMid()!=1){
//			querySQL+=" and (mid=0 or mid="+notice.getMid()+")";
//		}
//		if(notice.getBeginDate()!=null){
//		
//		querySQL += " and begin_date >=" + notice.getBeginDate();
//	
//	    }
//		if(notice.getEndDate()!=null){
//	
//		   querySQL += " and end_date <= " + notice.getEndDate();
//	
//	    }
		try {
			l =  query(querySQL, MMSNotice.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}

	public void addOrEditMessage(MMSNotice notice, String type) {
		String insertsql = "insert into mms_notice (begin_date, end_date ,title, content, oper_id,mid,expiration_date,notice_type) value(?,?,?,?,?,?,?,?)";

		String updatesql = "update mms_notice set begin_date = ? ,end_date = ?,title = ?,content = ? ,oper_id = ? where id ="
						+ notice.getId();
		Object[] args;
		int[] argTypes;
		String queryStr = "";
		String action = "";
		if (type.equals("add")) {
			action = "通知信息新增";
			args = new Object[]{ notice.getBeginDate(), notice.getEndDate(), notice.getTitle(), notice.getContent(),
					notice.getOperId(),notice.getMid(),notice.getExpiration_date(),notice.getNoticeType() };
			argTypes = new int[] { Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.INTEGER,Types.VARCHAR,Types.INTEGER,Types.INTEGER };
			queryStr = insertsql;
		} else {
			action = "通知信息修改";
			args = new Object[]{ notice.getBeginDate(), notice.getEndDate(), notice.getTitle(), notice.getContent(),
					notice.getOperId(),notice.getMid() };
			argTypes =new int[] { Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR,Types.VARCHAR };
			queryStr = updatesql;
		}
	  int count = update(queryStr, args, argTypes);
	  saveOperLog(action, count == 1?"成功":"失败");
	}

	public void editParam(GlobalParams globalParams) {
		String sql = "update global_params set par_desc='" + Ryt.sql(globalParams.getParDesc()) + "'";
		if (globalParams.getParValue() != null || "".equals(globalParams.getParValue())) {
			sql += ", par_value='" + Ryt.sql(globalParams.getParValue()) + "'";
		}
		if (globalParams.getParEdit() == 1) {
			sql += ", par_edit=0 ";
		}
		sql += " where par_id=" + globalParams.getParId();
		 update(sql);
	}

	public List<GlobalParams> queryAllParams() {
		String sql = "select * from global_params";
		return  query(sql, GlobalParams.class);
	}

	public void addParams(GlobalParams globalParams) {
		String sql = "insert into global_params (par_edit,par_desc,par_value,par_name) values(?,?,?,?)";
		Object[] object = { globalParams.getParEdit(), globalParams.getParDesc(), globalParams.getParValue(),
				globalParams.getParName() };
		int[] types = { Types.SMALLINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
		 update(sql, object, types);
	}

	public List<Integer> getAuthorType(String gatename, String transMode) {
		String sql = "select author_type from  gates where gate_desc_short =  '" + Ryt.sql(gatename) + "' and trans_mode = "
						+ Ryt.sql(transMode);
		return  queryForIntegerList(sql );
	}

	public int hasGate(String gateDescS, int transMode, int authorType) {
		String sql = "select count(*) from gates where gate_desc_short='" + Ryt.sql(gateDescS) + "' and trans_mode = "
						+ transMode + " and author_type = " + authorType;
		return  queryForInt(sql);
	}

	public List<Gate> getGateByTJ(int tranModel, int gateRouteId) {
		String sql = "select * from gates where 1=1";
		if (tranModel!=-1) {
			sql += " and  trans_mode = " + tranModel ;
		}
		if (gateRouteId!=-1) {
			sql += " and gid = " + gateRouteId;
		}
		return  query(sql,Gate.class);
	}

	
	public boolean editGate(NewGate g){
		StringBuilder editSql = new StringBuilder();
		editSql.append(" update gates set refund_flag="+g.getRefundFlag()+",gate_desc = ");
		editSql.append(Ryt.addQuotes(Ryt.sql(g.getGateDesc()))).append(",");
		editSql.append(" fee_model=").append(Ryt.addQuotes(Ryt.sql(g.getFeeModel())));
		editSql.append(",gate_id=").append(Ryt.sql(g.getGateId()));
		editSql.append(" where id=" + g.getId());
		int a =  update(editSql.toString());
		saveOperLog("更新gates",a==1?"成功":"失败");
		return a==1;
	}
	
	

	public void collect(String sql) {
		final int yesterday = DateUtil.systemDate(-1);//new RypCommon().getIntDate(-1);
		update("delete from tlog_collect where sys_date=" + yesterday);
		getJdbcTemplate().execute(sql, new CallableStatementCallback() {
			@Override
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				cs.setString(1, yesterday + "");
				cs.execute();
				System.err.println("store procedure is finish");
				return null;
			}
		});
	}

	public void dailyCollect(String sql) {
		getJdbcTemplate().execute(sql, new CallableStatementCallback() {
			@Override
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				cs.execute();
				System.err.println("store procedure is finish");
				return null;
			}
		});
	}
	public void addeditGateroute(NewGate newgate, String action) {
		StringBuilder addSql = new StringBuilder();
		addSql.append(" insert into gates (trans_mode,refund_flag,gate_desc,gate_desc_short,");
		addSql.append(" fee_model,gate_id,author_type,ryt_gate)"); // fee_rate fee_type
		addSql.append(" values (?,?,?,?,?,?,?,?)");

		StringBuilder editSql = new StringBuilder();
		editSql.append(" update gates set trans_mode =?,refund_flag=?,gate_desc =?,");
		editSql.append(" gate_desc_short =?,fee_model=?,gate_id =?,author_type =?,ryt_gate = ?");
		editSql.append(" where id=" + newgate.getId());

		String querySQL = action.equals("add") ? addSql.toString() : editSql.toString();
		Object[] argsAdd = { newgate.getTransMode(), newgate.getRefundFlag(), newgate.getGateDesc(),
				newgate.getGateDescShort().replaceAll("\\(M\\)", "").replaceAll("\\(B2B协议\\)", "").replaceAll("\\(B2B网上\\)", "").replaceAll("\\(WAP\\)", ""),
				newgate.getFeeModel(),newgate.getGateId(), newgate.getAuthorType() ,newgate.getGate()};
		int[] argTypesAdd = { Types.TINYINT, Types.TINYINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, 
				Types.VARCHAR, Types.INTEGER , Types.INTEGER};
		
		 update(querySQL, argsAdd, argTypesAdd);
	}

	public List<NewGate> getGateByTJ(String gate_d, String type) {
		String sql = "select * from gates where";
		if (!gate_d.equals("") && gate_d != null) {
			sql += " gate_desc_short like '";
			String[] aa = gate_d.split("");
			for (int i = 1; i < aa.length; i++) {
				sql += "%" + aa[i];
			}
			sql += "%'   and ";
		}
		if (!type.equals("") && type != null) {
			sql += " author_type = " + Ryt.sql(type) + "  and ";
		}
		sql = sql.substring(0, sql.length() - 5);
		Ryt.print(sql);
		return  query(sql,NewGate.class);
	}
	public List<Integer> getAuthorTypeByGate(int gate) {
		String sql = "select author_type from  gates where ryt_gate =  " + gate;
		return queryForIntegerList(sql);
	}
	/**
	 * 判断网关是否存在
	 * @param rytGate
	 * @param authorType
	 * @return
	 */
	public int gateIsExist(int rytGate,int gid){
		String sql1 = "select count(*) from gates where ryt_gate= ? and gid= ?";
		return queryForInt(sql1, new Object[] { rytGate, gid },new int[] { Types.INTEGER, Types.TINYINT });
	}
	
	public int addGateRoute(Integer gid,String name,String merNo,String requestUrl,String remark){
		String sql = "insert into gate_route(gid,name,mer_no,request_url,remark)values(?,?,?,?,?)";
		return update(sql,gid,name,merNo,requestUrl,remark);
	}
	/**
	 * 增加网关
	 * @param rytGate
	 * @param authorType
	 * @return
	 */
	public int addGates(String gateName, String bankgate, String fee_model, Integer rytGate, Integer transMode,
			Integer gid,Integer feeFlag)throws Exception{
		StringBuilder addSql = new StringBuilder();
		addSql.append(" insert into gates (trans_mode, gate_desc_short,fee_model,gate_id,ryt_gate,fee_flag,gid,author_type )");
		addSql.append(" values (?,?,?,?,?,?,?,?)");
		/*//TODO 增加GID 字段，需要测试
		int gid = 0;
		if(authorType > 0 ){
			if(authorType == 1 && rytGate < 20000 ) {
				gid = 6;//汇付网银
			}else{
				//if (authorType == 5) bankgate = "kh";
				gid = authorType;
			}
		}else{
			if(rytGate== 10001 || rytGate== 90001){
				gid = 5;
				bankgate = "jh";
			}else{
				gid = rytGate;
			}
			
		}
		*/
		Object[] argsAdd = { transMode, gateName, fee_model, bankgate, rytGate,feeFlag ,gid,gid};
		return update(addSql.toString(),argsAdd);
	}
	/**
	 * 修改银行网关
	 * @return
	 */
	public int[] editGates(int gid,int id, int transModel, int rytGate, String gateId, String feeMode,int feeFlag){
		gateId=Ryt.sql(gateId);
		feeMode=Ryt.sql(feeMode);
		String sql1 = " update gates set ryt_gate="+rytGate+",gate_id='"+gateId+"',fee_model='"+feeMode+"',fee_flag="+feeFlag+" where id = "+id;
		String sql2 = " update fee_calc_mode set bk_fee_mode='"+feeMode+"',gate_id='"+gateId+"' where gid="+gid+" and gate="+rytGate;
		String [] sqls={sql1,sql2};
		int[] count=batchSqlTransaction(sqls);
		return count;
	}
	 
	/**
	 * 增值业务网关新增
	 * @param rytGate
	 * @return
	 */
	public int addRytGate(Integer gate,Integer transMode,String gateName)throws Exception{
		String sql="insert into ryt_gate (gate,stat_flag,trans_mode,gate_name) values(?,0,?,?)";
		Object[] objArr=new Object[]{gate,transMode,gateName};
		return update(sql, objArr);
	}
	/**
	 * 支付网关表list
	 * @return
	 */
	public List<GateRoute> queryGateRouteList(){
		String sql="select * from gate_route";
		return query(sql, GateRoute.class);
	}
	/**
	 * 根据gid 修改支付渠道Name
	 * @param gid
	 * @param gateRoutName
	 * @return
	 */
	public int updateGateRoute(Integer gid,String name,String merNo,String requestUrl,String remark){
		String sql="update gate_route set name=?,mer_no=?,request_url = ?,remark=? where gid=?";
		return update(sql,name,merNo,requestUrl,remark,gid);
	}
	/**
	 * 根据gid 修改收款账号信息
	 * @return
	 */
	public int eidtGateMassage(GateRoute gateRoute){
		
		StringBuffer sql= new StringBuffer("update gate_route set ");
		sql.append("mer_no=?,");
		sql.append("remark=?,");
		sql.append("mer_key=?,");
		sql.append("mer_key_pwd=?,");
		sql.append("p1=?,");
		sql.append("p2=?,");
		sql.append("p3=?,");
		sql.append("p4=?,");
		sql.append("p5=?,");
		sql.append("class_name=?,");
		sql.append("rec_acc_no=?,");
		sql.append("rec_acc_name=?,");
		sql.append("rec_bank_no=?,");
		sql.append("rec_bank_name=?,");
		sql.append("bf_bk_no=? ");
		sql.append("where gid=? ");
		
		
		//String sql="mer_no=? ,rec_acc_name=?,rec_bank_no=?,rec_bank_name=?,bf_bk_no=? where gid=?";
		Object [] objArr=new Object[]{
					gateRoute.getMerNo(),
					gateRoute.getRemark(),
					gateRoute.getMerKey(),
					gateRoute.getMerKeyPwd(),
					gateRoute.getP1(),
					gateRoute.getP2(),
					gateRoute.getP3(),
					gateRoute.getP4(),
					gateRoute.getP5(),
					gateRoute.getClassName(),
					gateRoute.getRecAccNo(),
					gateRoute.getRecAccName(),
					gateRoute.getRecBankNo(),
					gateRoute.getRecBankName(),
					gateRoute.getBfBkNo(),
					gateRoute.getGid()
				};
		
		return update(sql.toString(),objArr);
	}
	
	/**
	 * 根据gid 修改备份金银行行号
	 * @return
	 */
	public int eidtBfBkNo(Integer gid,String bfBkNo){
		String sql="update gate_route set bf_bk_no=? where gid=?";
		Object [] objArr=new Object[]{bfBkNo,gid};
		return update(sql,objArr);
	}
	/**
	 * 查询    备份金银行行号下拉框
	 * @return
	 */
	
	public Map<String, String> getBkNoMap(){
		String sql="select bk_no , bk_abbv from bk_account";
		return queryToMap(sql);
	}
	/**
	 * 根据支付渠道查询 电银在银行的商户号
	 * @param gid
	 * @return
	 */
    public Map<String, String> getMerNoByGid(String gid){
    	String sql="select mer_no,rec_acc_no from gate_route where gid="+gid;
    	Map<String, Object> objMap=queryForMap(sql);
    	Map<String, String> strMap=new HashMap<String, String>();
    	strMap.put("mer_no", (String)objMap.get("mer_no"));
    	strMap.put("rec_acc_no", (String)objMap.get("rec_acc_no"));
    	return strMap;
    }
    public List<GateRoute> selectByKey(String key){
    	StringBuffer sql =new StringBuffer("select * from gate_route where name like '%"+key+"%' or remark like '%"+key+"%'");
    	sql.append(" or gid like '%"+key+"%' ");
    	return query(sql.toString(), GateRoute.class);
    }
    
    /**
	 * 查看商户ip配置
	 * @param gid
	 * @return
	 */ 
	public CurrentPage<VisitIpConfig> queryMerIP(Integer pageNo, String mid,
			short type) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from visit_ip_config where 1=1");
		if (!Ryt.empty(mid)) {
			sql.append(" and mid=").append(Ryt.addQuotes(mid));
		}
		if (type != 0) {
			sql.append(" and type=").append(type);
		}
		String sqlCount = sql.toString().replace("*","count(*)");
		return queryForPage(sqlCount, sql.toString(), pageNo,new AppParam().getPageSize(), VisitIpConfig.class);

	}
	 
	public int countMer(String mid){
		String sql="select count(*) from visit_ip_config where mid="+Ryt.addQuotes(mid);
		return queryForInt(sql);
		
	}
	 /**
		 * 新增商户ip配置
		 * @param gid
		 * @return
		 */ 
	public void addMerIPConfig(String mid,String ip,short type){
		Object[] obj = new Object[] {mid,ip,type };
		int[] type2 = new int[] {Types.VARCHAR,Types.VARCHAR,Types.TINYINT};
		String sql = "insert into visit_ip_config (mid,ip,type ) values (?,?,?)";
		update(sql, obj, type2);
		
	}
	/**
	 * 根据id查看商户ip配置
	 * @param gid
	 * @return
	 */ 
public VisitIpConfig queryMerconfigByid(Integer id){
	StringBuffer sql=new StringBuffer("select * from  visit_ip_config where id =");
	sql.append(id);
	return queryForObject(sql.toString(), VisitIpConfig.class);
	
}
/**
 * 修改商户ip配置
 * @param gid
 * @return
 */ 
public void updateMerIPConfig(Integer id,String mid,String ip,short type){
	Object[] obj = new Object[] {mid,ip,type,id };
	int[] type2 = new int[] {Types.VARCHAR,Types.VARCHAR,Types.TINYINT,Types.INTEGER};
	String sql = "update visit_ip_config set mid=?,ip=?,type=? where id=?";
	update(sql, obj, type2);
	
}
//删除
public String deleteMerIpConfig(Integer id){
	String msg = "删除成功！";
	String deleteSql = "delete from visit_ip_config where id = " +id;
	try {
		update(deleteSql);
	} catch (Exception e) {
		e.printStackTrace();
		msg = "删除失败！";
	}
	this.saveOperLog("删除商户ip配置", "操作成功-删除id "+id);
	return msg;
}
	
	/**
	 * 查询
	 * @param gid
	 * @param pageNo
	 * @return
	 */
	public CurrentPage<B2EGate> queryDFBankInfo(String gid,int pageNo){
		StringBuffer sql=new StringBuffer("select * from b2e_gate where 1=1 ");
		if(!Ryt.empty(gid))
		sql.append("and gid="+Ryt.sql(gid));
		String sqlCount =	sql.toString().replace("*","count(*)");
		return queryForPage(sqlCount,sql.toString(), pageNo, new AppParam().getPageSize(),B2EGate.class) ;
	}
	/**
	 * 初始化
	 * @return
	 */
	public List<B2EGate> initDFYEBJ(){
		StringBuffer sql=new StringBuffer("select * from b2e_gate");
		return query(sql.toString(),B2EGate.class);
	}
	
	/**
	 * 根据网关号查询信息
	 * @param gid 网关号
	 * @return
	 */
	public B2EGate queryDFBankInfoByGid(String gid){
		StringBuffer sql=new StringBuffer("select * from b2e_gate where 1=1 ");
		if(!Ryt.empty(gid))
			sql.append("and gid="+Ryt.sql(gid));
		else 
			return null;
		return queryForObject(sql.toString(),B2EGate.class);
	}
	
	/**
	 * 更新代付报警信息
	 * @param gid
	 * @param amt
	 * @param phone
	 * @param email
	 * @param state
	 * @return
	 * @throws Exception 
	 */
	public int updateConf(String gid,String amt,String phone,String email,String state){
		StringBuffer sql=new StringBuffer("update b2e_gate set alarm_amt=");
		sql.append(Ryt.mul100(Ryt.sql(amt))+",");
		sql.append("alarm_phone="+Ryt.addQuotes(Ryt.sql(phone))+",");
		sql.append("alarm_status="+Ryt.sql(state)+",");
		sql.append("alarm_mail="+Ryt.addQuotes(Ryt.sql(email)));
		sql.append(" where gid=");
		sql.append(Ryt.sql(gid));
		return update(sql.toString());
	}
	
	/**
	 * 开启或关闭报警状态
	 * @param gid
	 * @param state
	 * @return
	 * @throws Exception
	 */
	public int updateStateByGid(String gid,String state){
		StringBuffer sql=new StringBuffer("update b2e_gate set alarm_status=");
		sql.append(Ryt.sql(state));
		sql.append(" where gid=");
		sql.append(Ryt.sql(gid));
		return update(sql.toString());
	}
	
	/*
	 * 查询ewp地址
	 */
	public String queryewp(){
		String Sql = "select par_value from global_params where par_name='EWP_PATH'";
		return queryForString(Sql) ;
	}
	
	public List<MMSNotice> getMessageById(String idStr){
		String[] ids=idStr.split(",");
		String sql="select * from mms_notice where id in ("+ids[0];
		for (int i = 1; i < ids.length; i++) {
			sql+=","+ids[i];
			
		}
		sql+=");";
		return query(sql, MMSNotice.class);
	}

	public GateRoute queryGateRoteByGid(String gid) {
		// TODO Auto-generated method stub
		
		String sql="select * from gate_route where gid ="+Ryt.sql(gid);
		return queryForObject(sql, GateRoute.class);
	}
	
}

package com.rongyifu.mms.dao;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.WebContextFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.rongyifu.mms.bean.BankNoInfo;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.MinfoH;
import com.rongyifu.mms.bean.QkCardInfo;
import com.rongyifu.mms.bean.RYTGate;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.db.datasource.CustomerContextHolder;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.PaginationHelper;
import com.rongyifu.mms.utils.RYFMapUtil;
import com.rongyifu.mms.web.WebConstants;

@SuppressWarnings("unchecked")
public class MerInfoDao extends PubDao {
	
	public void effectConfigChange(FeeCalcMode feeCalcMode){
		StringBuilder sql1 = new StringBuilder("UPDATE fee_calc_mode SET calc_mode = ").append(Ryt.addQuotes(feeCalcMode.getToCalcMode()));
		if(feeCalcMode.getGateId()!=null){
			sql1.append(",gate=").append(feeCalcMode.getGateId()).append(",gate_id=").append(feeCalcMode.getGateId());	
		}
		if(feeCalcMode.getBkFeeMode()!=null){
			sql1.append(", bk_fee_mode=").append(Ryt.addQuotes(feeCalcMode.getBkFeeMode()));
		}
		sql1.append(", gid=").append(feeCalcMode.getToGid());
		sql1.append(" WHERE mid=").append(Ryt.addQuotes(feeCalcMode.getMid()));
		sql1.append(" AND gate =").append(feeCalcMode.getGate());
		String sql2 = "UPDATE mer_gate_config SET status=3 WHERE id="+feeCalcMode.getId();
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(sql1.toString());
		sqlList.add(sql2);
		String[] sqls = new String[2];
		sqlList.toArray(sqls);
		batchSqlTransaction(sqls);
	}
	
	public List<FeeCalcMode> queryEffectableConfig(String date){
		String sql = "SELECT mgc.id,mgc.mid,mgc.gate,mgc.type,mgc.effective_time,mgc.calc_mode,mgc.gid,mgc.to_calc_mode,mgc.to_gid,mgc.to_state," +
					 "g.ryt_gate gateId,g.fee_model bkFeeMode " +
					 "FROM mer_gate_config mgc " +
					 "LEFT JOIN gates g " +
					 "ON (g.ryt_gate=mgc.gate AND g.gid=mgc.to_gid) WHERE mgc.status = 1 and mgc.type = 1 AND mgc.effective_time !=0 AND mgc.effective_time<="+date;
		return query(sql, FeeCalcMode.class);
	}
	
	/**
	 * @param status
	 * @param id
	 * @return
	 * 审核网关配置 需要分类型分别处理
	 */
	public String checkGateConfig(Integer status, Integer id){
		String msg = "FAIL";
		List<String> sqlList = new ArrayList<String>();
		if(status == 1){//成功
			String sql = "SELECT mgc.mid,mgc.gate,mgc.type,mgc.effective_time,mgc.to_calc_mode,mgc.to_gid,mgc.to_state," +
						 "g.ryt_gate gateId,g.fee_model bkFeeMode " +
						 "FROM mer_gate_config mgc " +
						 "LEFT JOIN gates g " +
						 "ON (g.ryt_gate=mgc.gate AND g.gid=mgc.to_gid) WHERE mgc.status = 0 and mgc.id = "+id;
			FeeCalcMode fcm = queryForObject(sql, FeeCalcMode.class);
			int status1 = 1;
			if(null != fcm){
				if(fcm.getType() == 0){//开关申请
					StringBuilder sql2 = new StringBuilder("UPDATE fee_calc_mode SET state = ").append(fcm.getToState());
					sql2.append(" WHERE mid=").append(Ryt.addQuotes(fcm.getMid()));
					sql2.append(" AND gate = ").append(fcm.getGate());
					sqlList.add(sql2.toString());
				}else{//配置申请
					if(fcm.getEffectiveTime() == 0){//立即生效
						status1 = 3;
						StringBuilder sql2 = new StringBuilder("UPDATE fee_calc_mode SET calc_mode = ").append(Ryt.addQuotes(fcm.getToCalcMode()));
						if(fcm.getGateId()!=null){
							sql2.append(",gate=").append(fcm.getGateId()).append(",gate_id=").append(fcm.getGateId());	
						}
						if(fcm.getBkFeeMode()!=null){
							sql2.append(", bk_fee_mode=").append(Ryt.addQuotes(fcm.getBkFeeMode()));
						}
						sql2.append(", gid=").append(fcm.getToGid());
						sql2.append(" WHERE mid=").append(Ryt.addQuotes(fcm.getMid()));
						sql2.append(" AND gate =").append(fcm.getGate());
						sqlList.add(sql2.toString());
					}
				}
				String sql3 = "UPDATE mer_gate_config set status = "+status1+" WHERE id= " + id;
				sqlList.add(sql3);
			}
			String[] sqls = null;
			if(CollectionUtils.isNotEmpty(sqlList)){
				sqls = new String[sqlList.size()];
				sqlList.toArray(sqls);
				int[] rslts = batchSqlTransaction(sqls);
				if(null != rslts){
					msg="OK";
					//刷新缓存和ewp;
					msg += "["+fcm.getMid()+"]";
				}
			}
		}else{//失败
			String sql = "UPDATE mer_gate_config set status = ? WHERE id= ?";
			Object[] args = new Object[]{2,id}; 
			int[] argTypes = new int[]{Types.TINYINT,Types.INTEGER};
			int rowCount = update(sql, args, argTypes);
			if(rowCount == 1){
				msg="OK";
			}
		}
		return msg;
	}
	
	/**
	 * @param mid
	 * @param mName
	 * @param status
	 * @param state
	 * @return
	 */
	public CurrentPage<FeeCalcMode> showGateConfigApply(String mid,String mName,Integer pageNo,Integer status,Integer state,Integer type){
		StringBuilder sqlCountRows = new StringBuilder();
		StringBuilder sqlFetchRows = new StringBuilder();
		if(type == 0){
			//网关开关申请 
			sqlCountRows.append("SELECT COUNT(*) ");
			sqlCountRows.append("FROM mer_gate_config mgc, minfo m,fee_calc_mode fcm ");
			sqlCountRows.append("WHERE m.id = mgc.mid AND fcm.mid=mgc.mid AND fcm.gate=mgc.gate AND mgc.type = 0");
			sqlFetchRows.append("SELECT mgc.id, mgc.mid,fcm.gate,fcm.trans_mode,fcm.state,mgc.type,mgc.status,mgc.to_state ");
			sqlFetchRows.append("FROM mer_gate_config mgc, minfo m,fee_calc_mode fcm ");
			sqlFetchRows.append("WHERE m.id = mgc.mid AND fcm.mid=mgc.mid AND fcm.gate=mgc.gate AND mgc.type = 0");
		}else{
			//网关配置申请 银行网关号 银行手续费 	商户手续费 	正在使用  状态  生效时间
			sqlCountRows.append("SELECT COUNT(*) ");
			sqlCountRows.append("FROM mer_gate_config mgc, minfo m,fee_calc_mode fcm ");
			sqlCountRows.append("WHERE m.id = mgc.mid AND fcm.mid=mgc.mid AND fcm.gate=mgc.gate AND mgc.type = 1");
			sqlFetchRows.append("SELECT mgc.id, mgc.mid,fcm.gate,fcm.trans_mode,fcm.state,mgc.type,mgc.status,mgc.to_state, ");
			sqlFetchRows.append("mgc.calc_mode,mgc.gid,mgc.effective_time,mgc.to_calc_mode ,mgc.to_gid ");
			sqlFetchRows.append("FROM mer_gate_config mgc, minfo m,fee_calc_mode fcm");
			sqlFetchRows.append(" WHERE m.id = mgc.mid AND fcm.mid=mgc.mid AND fcm.gate=mgc.gate AND mgc.type = 1");
		}
		if(StringUtils.isNotBlank(mid)){
			sqlCountRows.append(" AND mgc.mid=").append(Ryt.addQuotes(mid));
			sqlFetchRows.append(" AND mgc.mid=").append(Ryt.addQuotes(mid));
		}else if(StringUtils.isNotBlank(mName)){
			sqlCountRows.append(" AND m.name=").append(Ryt.addQuotes(mName));
			sqlFetchRows.append(" AND m.name=").append(Ryt.addQuotes(mName));
		}
		if(null != state){
			sqlCountRows.append(" AND fcm.state=").append(state);
			sqlFetchRows.append(" AND fcm.state=").append(state);
		}
		if(null != status){
			if(status==1){
				sqlCountRows.append(" AND mgc.status IN (1,3)");
				sqlFetchRows.append(" AND mgc.status IN (1,3)");
			}else{
				sqlCountRows.append(" AND mgc.status=").append(status);
				sqlFetchRows.append(" AND mgc.status=").append(status);
			}
		}
		String fetchSql = "";
		if(type == 0){
			fetchSql=sqlFetchRows.toString();
		}else{
			fetchSql = "SELECT a.*,g.fee_model bkFeeMode,g.ryt_gate gateId FROM ("+sqlFetchRows.toString()+") a LEFT JOIN gates g on a.gate=g.ryt_gate AND a.to_gid=g.gid ORDER BY a.id DESC";
		}
		return queryForPage(sqlCountRows.toString(), fetchSql, pageNo, new AppParam().getPageSize(), FeeCalcMode.class);
	}
	
	public int queryGateState(String mid,Integer rytGate){
		String sql="SELECT state FROM fee_calc_mode WHERE mid=? and gate=?";
		Object[] args = new Object[]{mid,rytGate};
		int[] argTypes = new int[]{Types.VARCHAR,Types.INTEGER};
		return queryForInt(sql, args, argTypes);
	}
	
	/**
	 * @param mid
	 * @param rytGate
	 * @param type
	 * @return
	 * 查询是否存在未审核的申请
	 */
	public boolean isGateConfigApplyExist(String mid, Integer rytGate, Integer type){
		boolean flag = false;
		String sql = "SELECT COUNT(*) FROM mer_gate_config mgc,ryt_gate rg WHERE mgc.gate = rg.gate AND status = 0 AND mid=? AND mgc.gate=? AND type=?";
		Object[] args = new Object[]{mid,rytGate,type};
		int[] argTypes = new int[]{Types.VARCHAR,Types.INTEGER,Types.INTEGER};
		int rowCount = queryForInt(sql, args, argTypes);
		if(rowCount != 0 ){
			flag = true;
		}
		return flag;
	}
	/**
	 * @param rytGateList
	 * @param toState
	 * @return
	 */
	public int[] batchToggleGateApply(List<String> rytGateList,Integer toState){
		List<String> sqlList = new ArrayList<String>();
		try {
			for (String midGate : rytGateList) {
				String[] args = midGate.split(",");
				String sql = "INSERT INTO mer_gate_config (mid,gate,to_state) VALUES("+Ryt.addQuotes(args[0])+","+args[1]+","+toState+")";
				if(!isGateConfigApplyExist(args[0],Integer.valueOf(args[1]),0)){//如果存在未审核的申请 则忽略本次申请
					int currentState = queryGateState(args[0],Integer.valueOf(args[1]));
					if(currentState != toState){
						sqlList.add(sql);
					}
				}
			}
			String[] sqls = new String[sqlList.size()];
			sqlList.toArray(sqls);
			return batchSqlTransaction(sqls);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * @param mid
	 * @param rytGate
	 * @param gid
	 * @param feeModel
	 * @param effectiveTime
	 * @return
	 * 网关配置申请
	 */
	public int addGateConfigApply(String mid,Integer rytGate, Integer toGid, String toCalcMode, Long effectiveTime){
		FeeCalcMode fcm = queryForObject("SELECT calc_mode,gid FROM fee_calc_mode WHERE mid="+Ryt.addQuotes(mid)+" AND gate="+rytGate, FeeCalcMode.class);
		if(null != fcm){
			String sql = "INSERT INTO mer_gate_config (type,mid,to_calc_mode,to_gid,gate,calc_mode,gid,effective_time) VALUES(?,?,?,?,?,?,?,?)";
			Object[]  args = new Object[]{1,mid,toCalcMode,toGid,rytGate,fcm.getCalcMode(),fcm.getGid(),effectiveTime};
			int[] argTypes = new int[]{Types.TINYINT, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.INTEGER,Types.BIGINT}; 
			return update(sql, args, argTypes);
		}else{
			return 0;
		}
	}
	
	/**
	 * @return
	 * 网关开关申请
	 */
	public int addGateOpenCloseApply(String mid, Integer state, Integer rytGate){
		String sql = "INSERT INTO mer_gate_config (mid,gate,to_state) VALUES(?,?,?)";
		Object[]  args = new Object[]{mid,rytGate,state==0?1:0};//1开启申请 0 关闭申请
		int[] argTypes = new int[]{Types.VARCHAR, Types.INTEGER, Types.TINYINT}; 
		return update(sql, args, argTypes);
	}
	
	
	public int toggleGate(String mid, int state, int rytGate){
		int rslt=0;
		if(state == 0 ){
			rslt=openGate(mid,rytGate);
		}else{
			rslt=closeGate(mid, rytGate);
		}
		return rslt;
	}
	/**
	 * @param date yyyyMMdd
	 * @return
	 * 拿到应该生效的记录
	 */
	public List<MinfoH> queryEffectableInfo(String date){
		String sql = "select * from minfo_change_apply where status=1 and effective_time!=0 AND effective_time<="+date;
		return getJdbcTemplate().query(sql, new BeanPropertyRowMapper(MinfoH.class));
	}
	
	/**
	 * 更新数据库并刷新缓存
	 * @param applyInfo
	 * @return
	 */
	public int effectiveChange(MinfoH applyInfo){
		Object[] obj = {applyInfo.getAbbrev(), applyInfo.getBankName(),applyInfo.getBankBranch(),applyInfo.getBankAcctName(),
						applyInfo.getBankAcct(),applyInfo.getBankProvId(),applyInfo.getBeginDate(),applyInfo.getCategory(),
						applyInfo.getCodeExpDate(),applyInfo.getCorpCode(),applyInfo.getCorpName(),applyInfo.getExpDate(),
						applyInfo.getGateId(),applyInfo.getIdNo(),applyInfo.getIdType(),applyInfo.getLiqState(),
						applyInfo.getLiqObj(),applyInfo.getManLiq(),applyInfo.getName(),applyInfo.getOpenDate(),
						applyInfo.getOpenBkNo(),applyInfo.getRegCode(),applyInfo.getIsSgdf(),applyInfo.getUpmpMid(),applyInfo.getLiqLimit(),applyInfo.getMid()};

		int[] type = {Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,
					  Types.VARCHAR,Types.SMALLINT,Types.INTEGER,Types.TINYINT,
					  Types.INTEGER,Types.VARCHAR,Types.VARCHAR,Types.INTEGER,
					  Types.INTEGER,Types.VARCHAR,Types.VARCHAR,Types.TINYINT,
					  Types.TINYINT,Types.TINYINT,Types.VARCHAR,Types.INTEGER,
					  Types.VARCHAR,Types.VARCHAR,Types.INTEGER,Types.VARCHAR,Types.INTEGER,Types.VARCHAR};

		StringBuffer sb = new StringBuffer();
		sb.append("update minfo set ");
		sb.append("abbrev=?,bank_name=?,bank_branch=?,bank_acct_name = ?,");
		sb.append("bank_acct=?,bank_prov_id=?,begin_date=?,category = ?,");
		sb.append("code_exp_date=?,corp_code=?,corp_name=?,exp_date = ?,");
		sb.append("gate_id=?,id_no=?,id_type=?,liq_state = ?, ");
		sb.append("liq_obj=?,man_liq=?,name=?,open_date = ?,");
		sb.append("open_bk_no=?,reg_code=?,is_sgdf=?,upmp_mid=?,liq_limit=? ");
		sb.append(" where id = ?");
		int count = update(sb.toString(), obj, type);
		if(count>0){
			refreshMinfo(applyInfo.getMid());
		}
		return count;
	}
	
	private String refreshMinfo(String mid){
		/* 存管系统不上，先注释*/ 
//			merOperDao.addCgTask( "M", String.valueOf(mid));
		RYFMapUtil.getInstance().refreshMinfoMap(mid+"");//刷新缓存
		// 刷新ets
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("t", "minfo");
		p.put("k", mid);
		return EWPService.refreshEwpETS(p) ? AppParam.SUCCESS_FLAG : "修改成功，但刷新ewp失败!";
	}
	
	public void updateMinfoChangeApplyStatus(String id){
		String sql = "UPDATE minfo_change_apply SET status = 3 WHERE id = "+id;
		update(sql);
	}
	
	/**
	 * @param mid
	 * @return
	 */
	public Map<String,Object> querycheckInfoPair(int id){
		Map<String,Object> map = new HashMap<String,Object>();
		Minfo applyInfo = getApplyInfoById(id);
		Minfo currentInfo = null;
		if(null != applyInfo){
			currentInfo = getImportentMsgByMid(applyInfo.getMid());
			if(null == currentInfo){
				return null;
			}
		}else{
			return null;
		}
		map.put("currentInfo", currentInfo);
		map.put("applyInfo", applyInfo);
		return map;
	}
	
	public Minfo getApplyInfoById(int id){
		StringBuilder sql = new StringBuilder("SELECT m.*,oper.oper_name AS applyOperName,oper.oper_name AS checkOperName, bni.bk_name openBkName FROM minfo_change_apply m");
		sql.append(" LEFT JOIN bank_no_info bni ON m.open_bk_no = bni.bk_no");
		sql.append(" LEFT JOIN oper_info oper ON (m.apply_oper_id=oper.oper_id AND oper.mid = 1)");
		sql.append(" WHERE m.id=").append(id);
		return queryForObject(sql.toString(),Minfo.class);
	}
	
	/**
	 * 根据ID查询更改过的记录
	 * @param id
	 * @return
	 * @author yang.yaofeng 2015-01-26
	 */
	public MinfoH getApplyInfoByIdH(int id) {
		String sql="select mca.*,bni.bk_name openBkName from minfo_change_apply mca left join bank_no_info bni ON mca.open_bk_no = bni.bk_no where mca.id=" + id;
		return queryForObject(sql, MinfoH.class);
	}

	/**
	 * @param minfo
	 * @param currentStatus
	 * @return 更新申请状态
	 */
	public int updateApplyInfo(MinfoH minfo){
		HttpSession session = WebContextFactory.get().getSession(false);
		LoginUser user = (LoginUser) session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER);
		StringBuilder sql=new StringBuilder("update minfo_change_apply set status=?,check_oper_id=?,check_time=?");
		List<Object> argList = new ArrayList<Object>();
		argList.add(minfo.getStatus());
		argList.add(user.getOperId());
		argList.add(new Date());
		if(minfo.getEffectiveTime() != null){
			sql.append(", effective_time=?");
			argList.add(minfo.getEffectiveTime());
		}
		sql.append(" where id=?");
		argList.add(minfo.getId());
		int[] argTypes = null;
		if(argList.size()==4){
			argTypes = new int[]{Types.TINYINT,Types.BIGINT,Types.TIMESTAMP,Types.INTEGER};
		}else{
			argTypes = new int[]{Types.TINYINT,Types.BIGINT,Types.TIMESTAMP,Types.BIGINT,Types.INTEGER};
		}
		return update(sql.toString(), argList.toArray(), argTypes);
	}
	
	/**
	 * @param midList
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * 商户重要信息修改审核 分页
	 */
	public CurrentPage<Minfo> queryCheckPage(int pageNo, int pageSize,String mid,String mname,int status){
		StringBuilder sqlCountRows = new StringBuilder("select count(*) from minfo m1,minfo_change_apply m2 where m1.id=m2.mid and m2.status=?");
		StringBuilder sqlFetchRows = new StringBuilder("SELECT m2.*,oper.oper_name AS applyOperName,oper2.oper_name AS checkOperName,m1.bank_name,m1.prov_id FROM minfo_change_apply m2");
		sqlFetchRows.append(" LEFT JOIN minfo m1 ON m1.id=m2.mid");
		sqlFetchRows.append(" LEFT JOIN oper_info oper ON (m2.apply_oper_id=oper.oper_id AND oper.mid = 1)");
		sqlFetchRows.append(" LEFT JOIN oper_info oper2 ON (m2.check_oper_id=oper2.oper_id AND oper2.mid = 1)");
		sqlFetchRows.append(" WHERE m2.status=?");
		List<Object> argList= new ArrayList<Object>();
 		argList.add(status);
		StringBuilder conditions = new StringBuilder();
		if(StringUtils.isNotBlank(mid)){
			conditions.append(" AND m1.id=?");
			argList.add(mid);
		}
		if(StringUtils.isNotBlank(mname)){
			conditions.append(" AND m1.name=?");
			argList.add(mname);
		}
		sqlCountRows.append(conditions);
		sqlFetchRows.append(conditions).append(" ORDER BY m2.id DESC");
		return queryForPage(sqlCountRows.toString(), sqlFetchRows.toString(), argList.toArray(), pageNo, pageSize, Minfo.class);
	}
	
	/**
	 * 检查是否存在未审核的申请
	 * @param mid
	 * @return
	 * adfadsf
	 */
	public boolean queryUncheckedApplyCount(String mid){
		String sql = "select count(*) from minfo_change_apply where mid =? and status = 0";
		Object[] args = new Object[]{mid};
		int[] argTypes = new int[]{Types.VARCHAR};
		return queryForInt(sql,args,argTypes) > 0;
	}
	
	
	/**
	 * 
	 * @param minfo
	 * @return
	 */
	public int addImpChangeApply(MinfoH minfo){
		HttpSession session = WebContextFactory.get().getSession(false);
		LoginUser user = (LoginUser) session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER);
		CustomerContextHolder.setMaster();
		minfo.setStatus((short)0);
		StringBuffer sb = new StringBuffer();
		sb.append("insert into minfo_change_apply (");
		sb.append("mid,abbrev,bank_name,bank_branch,bank_acct_name,");
		sb.append("bank_acct,bank_prov_id,begin_date,category,");
		sb.append("code_exp_date,corp_code,corp_name,exp_date,");
		sb.append("gate_id,id_no,id_type,liq_state,");
		sb.append("liq_obj,man_liq,name,open_date,");
		sb.append("open_bk_no,reg_code,status,effective_time,is_sgdf,upmp_mid,liq_limit,");
		sb.append("apply_oper_id");
		sb.append(") values (");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,?,?,?,");
		sb.append("?)");
		Object[] args = new Object[]{minfo.getId(),minfo.getAbbrev(),minfo.getBankName(),minfo.getBankBranch(),minfo.getBankAcctName(),
									 minfo.getBankAcct(),minfo.getBankProvId(),minfo.getBeginDate(),minfo.getCategory(),
									 minfo.getCodeExpDate(),minfo.getCorpCode(),minfo.getCorpName(),minfo.getExpDate(),
									 minfo.getGateId(),minfo.getIdNo(),minfo.getIdType(),minfo.getLiqState()
									 ,minfo.getLiqObj(),minfo.getManLiq(),minfo.getName(),minfo.getOpenDate(),
									 minfo.getOpenBkNo(),minfo.getRegCode(),minfo.getStatus(),minfo.getEffectiveTime(),minfo.getIsSgdf(),
									 minfo.getUpmpMid(),minfo.getLiqLimit(),user.getOperId()};
		String sql = sb.toString();
		return update(sql, args);
	}


	public int updateMerPubKey(String pk, String mid) {
		try {
			return update(" update minfo set public_key = ? where id = ? ",new Object[] { pk, mid });
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	public List<FeeCalcMode> queryMerFeeCalcModes(String mid,Short transMode) {
		List<FeeCalcMode> l = new ArrayList<FeeCalcMode>();
		StringBuilder sql = new StringBuilder("select * from fee_calc_mode where mid =?");
		List<Object> argList = new ArrayList<Object>();
		argList.add(mid);
		if(null != transMode){
			sql.append("and trans_mode=?");
			argList.add(transMode);
		}
		try {
			l = query(sql.toString(),argList.toArray(),FeeCalcMode.class);
		} catch (Exception e) {
		}
		return l;
	}

	/**
	 * 商户信息增加
	 * @param minfo
	 * @return
	 * @throws Exception
	 */
	public String addMinfoBase(MinfoH minfo) throws Exception {
		minfo.setMstate((short)1);
		StringBuffer sb = new StringBuffer();
		sb.append("insert into minfo (");
		sb.append("id,name,abbrev,prov_id,begin_date,");
		sb.append("exp_date,mer_chk_flag,liq_type,liq_period,");
		sb.append("trans_limit,liq_limit,bank_prov_id,bank_name,");
		sb.append("bank_branch,bank_acct,corp_code,addr,");
		sb.append("zip,mdesc,mstate,web_url,");
		sb.append("open_date,open_bk_no,bank_acct_name,reg_code ,");
		sb.append("refund_flag,begin_fee,annual_fee,caution_money,");
		sb.append("fax_no,signatory,category,refund_fee,");
		sb.append("mer_trade_type,code_exp_date,id_type,id_no,corp_name,");
		sb.append("contact0,tel0,email0,cell0,");
		sb.append("contact1,tel1,email1,cell1,");
		sb.append("contact2,tel2,email2,cell2,");
		sb.append("contact3,tel3,email3,cell3, ");
		sb.append("contact4,tel4,email4,cell4,");
		sb.append("contact5,tel5,email5,cell5,");
		sb.append("acc_succ_count,acc_fail_count,phone_succ_count,");
		sb.append("phone_fail_count,id_succ_count,id_fail_count,open_time,mer_type,liq_obj,liq_state,gate_id,dls_code,is_ptop");
		sb.append(") values (");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,");
		sb.append("?,?,?,?,?,?,?,?,?,?)");
		Object[] args = new Object[]{
					minfo.getId(),minfo.getName(),minfo.getAbbrev(),minfo.getProvId(),minfo.getBeginDate(),
					minfo.getExpDate(),minfo.getMerChkFlag(),minfo.getLiqType(),minfo.getLiqPeriod(),
					minfo.getTransLimit(),minfo.getLiqLimit(),minfo.getBankProvId(),minfo.getBankName(),
					minfo.getBankBranch(),minfo.getBankAcct(),minfo.getCorpCode(),minfo.getAddr(),
					minfo.getZip(),minfo.getMdesc(),minfo.getMstate(),minfo.getWebUrl(),
					minfo.getOpenDate(),minfo.getOpenBkNo(),minfo.getBankAcctName(),minfo.getRegCode() ,
					minfo.getRefundFlag(),minfo.getBeginFee(),minfo.getAnnualFee(),minfo.getCautionMoney(),
					minfo.getFaxNo(),minfo.getSignatory(),minfo.getCategory(),minfo.getRefundFee(),
					minfo.getMerTradeType(),minfo.getCodeExpDate(),minfo.getIdType(),minfo.getIdNo(),minfo.getCorpName(),
					minfo.getContact0(),minfo.getTel0(),minfo.getEmail0(),minfo.getCell0(),
					minfo.getContact1(),minfo.getTel1(),minfo.getEmail1(),minfo.getCell1(),
					minfo.getContact2(),minfo.getTel2(),minfo.getEmail2(),minfo.getCell2(),
					minfo.getContact3(),minfo.getTel3(),minfo.getEmail3(),minfo.getCell3(),
					minfo.getContact4(),minfo.getTel4(),minfo.getEmail4(),minfo.getCell4(),
					minfo.getContact5(),minfo.getTel5(),minfo.getEmail5(),minfo.getCell5(),
					minfo.getAccSuccCount(),minfo.getAccFailCount(),minfo.getPhoneSuccCount(),
					minfo.getPhoneFailCount(),minfo.getIdSuccCount(),minfo.getIdFailCount(),0,minfo.getMerType(),
					minfo.getLiqObj(),minfo.getLiqState(),minfo.getGateId(),minfo.getDlsCode(),minfo.getIsPtop()
				};
		update(sb.toString(), args);
		return minfo.getId();
	}
	
	

	/**
	 * 商户联系人信息增加
	 * 
	 * @param minfo
	 * @return
	 * @throws Exception
	 */
	public int editMinfoContact(MinfoH minfo) throws Exception {
		Object[] obj = { 
				minfo.getContact0(), minfo.getTel0(),minfo.getEmail0(), minfo.getCell0(), 
				minfo.getContact1(),minfo.getTel1(), minfo.getEmail1(), minfo.getCell1(),
				minfo.getContact2(), minfo.getTel2(), minfo.getEmail2(),minfo.getCell2(),
				minfo.getContact3(), minfo.getTel3(),minfo.getEmail3(), minfo.getCell3(),
				minfo.getContact4(),minfo.getTel4(), minfo.getEmail4(), minfo.getCell4(),
				minfo.getContact5(), minfo.getTel5(), minfo.getEmail5(),minfo.getCell5(), 
				minfo.getId() };

		int[] type = { 
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,Types.VARCHAR, 
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR};

		StringBuffer sb = new StringBuffer();
		sb.append("update minfo set ");
		sb.append("contact0=?,tel0=?,email0=?,cell0 = ?,");
		sb.append("contact1=?,tel1=?,email1=?,cell1 = ?,");
		sb.append("contact2=?,tel2=?,email2=?,cell2 = ?,");
		sb.append("contact3=?,tel3=?,email3=?,cell3 = ?, ");
		sb.append("contact4=?,tel4=?,email4=?,cell4 = ?,");
		sb.append("contact5=?,tel5=?,email5=?,cell5 = ? ");
		sb.append(" where  id = ?");
			return update(sb.toString(), obj, type);
	}

	/**
	 * 商户风险信息增加
	 * 
	 * @param minfo
	 * @return
	 * @throws Exception
	 */
	public int editMinfoFX(Minfo minfo) throws Exception {
		Object[] obj = { minfo.getAccSuccCount(), minfo.getAccFailCount(),minfo.getPhoneSuccCount(),
				minfo.getPhoneFailCount(),minfo.getIdSuccCount(), minfo.getIdFailCount(),
				minfo.getTransLimit()*100,minfo.getMerChkFlag(),minfo.getRefundFlag(),minfo.getAmtLimitD()*100,
				minfo.getAmtLimitM()*100,minfo.getCardLimit()*100,minfo.getId() };
		int[] type = { 
				Types.INTEGER, Types.INTEGER, Types.INTEGER,
				Types.INTEGER, Types.INTEGER, Types.INTEGER,
				Types.INTEGER, Types.TINYINT, Types.TINYINT,
				Types.VARCHAR,Types.INTEGER,Types.INTEGER,Types.VARCHAR};
		StringBuffer sb = new StringBuffer();
		sb.append("update minfo set ");
		sb.append("acc_succ_count=?,acc_fail_count=?,phone_succ_count=?,");
		sb.append("phone_fail_count=?,id_succ_count=?,id_fail_count=?, ");
		sb.append("trans_limit=?,mer_chk_flag=?,refund_flag=?,amt_limit_d=?,amt_limit_m=?,card_limit=? ");
		sb.append(" where  id = ?");
		return update(sb.toString(), obj, type);
	}
	
	/**
	 * 商户基本信息修改
	 * @param minfo
	 * @throws Exception
	 */
	public void editMinfos(Minfo minfo) throws Exception {
		// 根据商户类型修改自动结算网关号
		String gateId = String.valueOf(minfo.getGateId() == null ? 0 : minfo.getGateId());
		if (gateId.length() > 2) {
			// 商户类型是企业，则网关号改成对公代付网关
			if (Constant.MerType.ENTERPRISE.equals(minfo.getMerType()))
				gateId = "72" + gateId.substring(2);
			// 商户类型是个人，则网关号改成对私代付网关
			else if (Constant.MerType.PERSON.equals(minfo.getMerType()))
				gateId = "71" + gateId.substring(2);
		}
		minfo.setGateId(Integer.parseInt(gateId));
		
		Object[] obj = {
				minfo.getName(), minfo.getAbbrev(),minfo.getProvId(),minfo.getAddr(),
				minfo.getLiqPeriod(),minfo.getLiqType()/*,minfo.getLiqLimit()*/,minfo.getZip(),
				minfo.getBeginFee()*100,minfo.getAnnualFee()*100, minfo.getCautionMoney()*100, minfo.getSignatory(),
				minfo.getWebUrl(),minfo.getFaxNo(),minfo.getMerTradeType(),minfo.getRefundFee(),
				minfo.getMdesc(),minfo.getMerType(),minfo.getGateId(),minfo.getId()
				};
		int[] type = { 
				Types.VARCHAR, Types.VARCHAR,Types.SMALLINT,Types.VARCHAR,
				Types.TINYINT, Types.TINYINT, /*Types.INTEGER,*/ Types.CHAR,
				Types.INTEGER,Types.INTEGER,Types.INTEGER, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR,Types.INTEGER,Types.TINYINT,
				Types.VARCHAR,Types.TINYINT,Types.INTEGER,Types.VARCHAR
				};
		StringBuffer sb = new StringBuffer();
		sb.append("update minfo set name =?,abbrev =?,prov_id =?,addr =?,");
		sb.append("liq_period =?,liq_type =?,zip =?,");
		sb.append("begin_fee=?,annual_fee=?,caution_money=?,signatory=?,");
		sb.append("web_url=?,fax_no=?,mer_trade_type=?,refund_fee=?,mdesc=?,mer_type=?, ");	
		sb.append(" last_update=DATE_FORMAT(NOW(),'%Y%m%d%H%i%s'),gate_id=? where id =?");
		update(sb.toString(), obj, type);
	}


	/**
	 * 获得单个商户对象
	 * @param mid
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Minfo getOneMinfo(String mid) throws Exception {
		String sql = "select * from minfo where id =" + Ryt.addQuotes(mid);
		
		try {
			Minfo m =(Minfo) getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper(Minfo.class));
			
			return m;
		} catch (Exception e) {
			Map<String, String> logParams = LogUtil.createParamsMap();
			logParams.put("DB", CustomerContextHolder.getCustomerType());
			logParams.put("sql", sql);
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("MERINFODAO", "getOneMinfo", " ", logParams);
			return null;
		}
		//return (Minfo) getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper(Minfo.class));
		//return queryForObject(sql, Minfo.class);
	}


	/**
	 * 获得单个商户对象的操作员map
	 * 
	 * @param mid
	 * @return
	 */
	public Map<Integer, String> getAllopersMap(String mid) {
		String sql = "select oper_id,oper_name from oper_info where mtype=0 and mid ='"+ mid+"'";
		return queryToMap2(sql);
	}

	public boolean checkMerName(String name, String mid, String abbrev) {
//		String sql = "select count(*) from minfo where name=? or abbrev=? ";
		String sql = "select count(*) from minfo where abbrev=? ";
//		if (!mid.trim().equals("") ) {
//			sql += " and id!='"+ mid+"'";
//		}
		return queryForInt(sql,new Object[]{abbrev},new int[]{Types.VARCHAR})>0;

	}

	public void privateEdit(String mid, String addr, String zip, String mdesc,
			String web_url, String contact0, String tel0, String email0,
			String contact1, String tel1, String email1, String contact2,
			String tel2, String email2, String contact3, String tel3,
			String email3, String contact4, String tel4, String email4,
			String contact5, String tel5, String email5) {
		String sql = "update minfo set addr =?,zip =?,mdesc =?,web_url =?,contact0 =?,tel0 =?,email0 =?, "
				+ "contact1 =?,tel1 =?,email1 =?,contact2 =?,tel2 =?,email2 =?,contact3 =?,tel3 =?,email3 =?,"
				+ "contact4 =?,tel4 =?,email4 =?,contact5 =?,tel5 =?,email5 =?"
				+ " where id =" + mid;
		Object[] obj = { addr, zip, mdesc, web_url, contact0, tel0, email0,
				contact1, tel1, email1, contact2, tel2, email2, contact3, tel3,
				email3, contact4, tel4, email4, contact5, tel5, email5 };
		int[] type = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
		update(sql, obj, type);
	}

	public void manageKey(String pub, String mid) throws Exception {
		String keysql = "update minfo set public_key=? where id =? ";
		update(keysql,new Object[]{pub,mid},new int[]{Types.VARCHAR,Types.VARCHAR});
	}

	public boolean checkMidExist(String mid) {
		return queryForInt("select count(*) from minfo where id='"+ mid+"'") > 0;
	}

	public void addGatesToMer(String mid, int gate) {
		RYTGate gateObj = RYFMapUtil.getRYTGateById(gate);
		if (gateObj == null)
			return ;
		int transM = gateObj.getTransMode();
		update("insert into fee_calc_mode(mid,gate,trans_mode)values(?,?,?)",
				new Object[] { mid, gate, transM }, new int[] { Types.VARCHAR,
						Types.INTEGER, Types.TINYINT });
	}

	public void editMerFeelModels(String mid, int transModel, String feelModel) {
		if (transModel == -1)
			update("update fee_calc_mode set calc_mode = ? where mid = ? ",
					new Object[] { feelModel, mid }, new int[] { Types.VARCHAR,
							Types.VARCHAR });
		else
			update("update fee_calc_mode set calc_mode = ? where mid = ? and trans_mode = ? ",
							new Object[] { feelModel, mid, transModel },
							new int[] { Types.VARCHAR, Types.VARCHAR,
									Types.TINYINT });
	}

	public List<Integer> getAuthTypeListByGate(int gate) {
		String sql = "select gid  from gates where ryt_gate =  " + gate;
		List<Integer> gidArr= queryForIntegerList(sql);
		return gidArr;
	}

	/**
	 * 商户网关配置页面更新
	 * 
	 * @param mid
	 * @param ng
	 * @return
	 */
	public int updateFeeCalcMode(String mid, int gate, int gid,String calcMode) {
		//增加GID 字段
		StringBuffer sel = new StringBuffer();
		sel.append(" SELECT gate_id ,author_type, fee_model, trans_mode,stat_flag , gid FROM gates ");
		sel.append(" WHERE gid = ? AND ryt_gate = ?");
		Map<String, Object> m = queryForMap(sel.toString(), new Object[] {
			gid, gate }, new int[] { Types.TINYINT, Types.INTEGER });

		StringBuffer updt = new StringBuffer();
		updt.append(" UPDATE fee_calc_mode  SET gate_id = ?,calc_mode = ? ");
		updt.append(" ,bk_fee_mode= ?,state=?,gid = ? ");
		updt.append(" WHERE mid = ?  AND gate= ?");
		Object[] obj=new Object[]{m.get("gate_id"),calcMode,m.get("fee_model"),1, m.get("gid"),mid,gate};
	
		return update(updt.toString(),obj);
	}
	
	/**
	 * 关闭网关
	 * 
	 */
	public int closeGate(String mid,int gate){
		String sql="update fee_calc_mode set state=? where mid=? and gate=? ";
		return update(sql,new Object[]{0,mid,gate},new int[]{Types.TINYINT,Types.VARCHAR,Types.INTEGER});
	}
	
	/**
	 * 关闭网关
	 * 
	 */
	public int openGate(String mid,int gate){
		String sql="update fee_calc_mode set state=? where mid=? and gate=? ";
		return update(sql,new Object[]{1,mid,gate},new int[]{Types.TINYINT,Types.VARCHAR,Types.INTEGER});
	}

	/**
	 * 根据状态标志，结算方式，联机查询，所在省份，商户名，商户号查找商户信息
	 * 
	 * @return
	 */
	public CurrentPage<Minfo> getMinfos(String mid, String name, String prov, String liqPeriod, String liq_type,
			String stat_flag, int pageNo) {
		mid= Ryt.sql(mid);
		name = Ryt.sql(name);
		prov = Ryt.sql(prov);
		liqPeriod = Ryt.sql(liqPeriod);
		liq_type = Ryt.sql(liq_type);
		stat_flag = Ryt.sql(stat_flag);
		String sql = "select id,name,prov_id,exp_date,bank_name,bank_acct,open_date,mstate,liq_obj,liq_state,man_liq from minfo ";
		StringBuffer conditBuff = new StringBuffer(" where id > 0 ");
		if (!Ryt.empty(mid)) {
			conditBuff.append(" and id = ").append(Ryt.addQuotes(mid));
		}
		if (!Ryt.empty(name)) {
			conditBuff.append(" and name like ").append(Ryt.addQuotes("%"+name+"%"));
		}
		if (!Ryt.empty(prov)) {
			conditBuff.append(" and prov_id = ").append(prov);
		}
		if (!Ryt.empty(liqPeriod)) {
			conditBuff.append(" and liq_period = ").append(liqPeriod);
		}
		if (!Ryt.empty(liq_type)) {
			conditBuff.append(" and liq_type = ").append(liq_type);
		}
		if (!Ryt.empty(stat_flag)) {
			conditBuff.append(" and mstate = ").append(stat_flag);
		}
		sql = sql + conditBuff.toString() + " order by id ";
		String countSql = " select count(*) from minfo " + conditBuff.toString();
		PaginationHelper<Minfo> ph = new PaginationHelper<Minfo>();
		// 页面对象
		CurrentPage<Minfo> p = ph.fetchPage(getJdbcTemplate(), countSql, sql, new Object[] {},
				pageNo, new AppParam().getPageSize(), new BeanPropertyRowMapper(Minfo.class));
		return p;
	}
	/**
	 * 根据mid查询商户属于行业
	 */
	public int gettrade_typeByMid(String mid){
		int category = 0;
		try {
			category= queryForInt("select mer_trade_type from minfo where id='"+mid+"'");
		} catch (Exception e) {
			category=-1;
		}
	  return category;
	}
	/**
	 * 根据mid查询商户属于哪个类别
	 */
	public int getCategoryByMid(String mid){
		int category = 0;
		try {
			category= queryForInt("select category from minfo where id='"+mid+"'");
		} catch (Exception e) {
			category=-1;
		}
	  return category;
	}
	/**
	 * 根据mid修改商户类别
	 */
	public int editCategoryByMid(String mid,int category){
		String sql="update minfo set category=? where id=? ";
		int row=update(sql, new Object[] {category, mid}, new int[] {Types.TINYINT,Types.VARCHAR});
	  return row;
	}
	/**
	 * 查询商户重要信息
	 * @param mid
	 * @return
	 */
	public Minfo getImportentMsgByMid(String mid){
		String sql="select m.name,m.abbrev,m.liq_limit,m.category,m.code_exp_date,m.begin_date,m.exp_date,m.corp_code,m.reg_code,m.bank_name,m.bank_acct,m.bank_branch,m.bank_prov_id,m.open_date,m.bank_acct_name,m.corp_name,m.id_type,m.id_no,m.open_bk_no,m.liq_obj,m.liq_state,m.man_liq,m.gate_id,bni.bk_name openBkName,m.mer_type,m.is_sgdf,upmp_mid from minfo m left join bank_no_info bni ON m.open_bk_no = bni.bk_no where m.id=?";
		//查询字段新增is_sgdf 是否支持手工代付
		Object[] args = new Object[]{mid};
		int[] argTypes = new int[]{Types.VARCHAR};
		return queryForObject(sql, args, argTypes, Minfo.class);
	}
	/**
	 * 商户名是否存在
	 * @param name
	 * @param abbrev
	 * @return true 为存在
	 */
	public boolean isExistMinfoName(String name,String abbrev,String mid){
		String sql="select count(*) from minfo where abbrev = ? and id != ?";
		Object[] args = new Object[]{abbrev,mid};
		int[] argTypes = new int[]{Types.VARCHAR,Types.VARCHAR};
		return  queryForInt(sql,args,argTypes) > 0 ;
	}
	/**
	 * 查询所有商户（得到商户号和商户名字）
	 * @return 所有商户
	 */
	public List<Minfo> allMid(){
		String sql="select id,name from minfo";
				List<Minfo>	list=getJdbcTemplate().query(sql, new BeanPropertyRowMapper(Minfo.class));
				return  list;
	}
	
	/**
	 * 根据商户号查询商户功能配置
	 * @param mid 商户号
	 * @return
	 */
	public Minfo selectMinfoConfigByMid(String mid){
		StringBuffer sql=new StringBuffer("select df_config,dk_config,multi_process_day,multi_process_time from minfo where id=");
		sql.append(Ryt.addQuotes(Ryt.sql(mid)));
		return queryForObject(sql.toString(), Minfo.class);
	}
	
	/**
	 * 根据商户号修改功能配置
	 * @param mid 商户号
	 * @param dfCFGStr 代付配置
 	 * @param dkCFGStr 代扣配置
	 * @param weekStr  星期配置
	 * @param sysHandleTimesStr 执行时间配置
	 * @return
	 */
	public int updateZHGNPZ(String mid,String dfCFGStr,String dkCFGStr,String weekStr,String sysHandleTimesStr){
		StringBuffer sql=new StringBuffer("update minfo set ");
		sql.append("df_config="+Ryt.addQuotes(Ryt.sql(dfCFGStr))+",");
		sql.append("dk_config="+Ryt.addQuotes(Ryt.sql(dkCFGStr))+",");
		sql.append("multi_process_day="+Ryt.addQuotes(Ryt.sql(weekStr))+",");
		sql.append("multi_process_time="+Ryt.addQuotes(Ryt.sql(sysHandleTimesStr)));
		sql.append(" where id=");
		sql.append(Ryt.addQuotes(Ryt.sql(mid)));
		return update(sql.toString());
	}
	
	public List<RYTGate> querygate(String bankname,String bankNo){
		StringBuffer sql=new StringBuffer();
		 sql.append(" select gate from ryt_gate where trans_mode=11");
		 if(!Ryt.empty(bankname)){
		 sql.append(" and gate_name like '%"+bankNo+"%'");
		 }
		 if(!Ryt.empty(bankNo)){
			 sql.append(" and gate =").append(bankNo); 
		 }
		return query(sql.toString(), RYTGate.class);
		
		
	}
	
	public CurrentPage<BankNoInfo> queryBKNo(Integer PageNo,String gate,String bkname ){
		String sql="select bk_no,bk_name,gid";
		StringBuffer querysql = new StringBuffer(" from bank_no_info where type=01  ");
		if (!Ryt.empty(gate)){
			String gate1=gate.substring(2);
			querysql.append(" and gid =").append(Ryt.addQuotes(gate1));
		}
		if (!Ryt.empty(bkname)&&!"请输入开户行关键字检索联行行号".equals(bkname))querysql.append(" and bk_name like'%").append(bkname).append("%'").append(" or bk_no like'%").append(bkname).append("%'");
			return queryForPage("select count(*)"+querysql.toString(), sql+querysql.toString(), PageNo,new AppParam().getPageSize(), BankNoInfo.class);
		}
	
		/**
		 * 查询商户upmp商户id
		 * @param mid 电银商户号
		 * @return
		 */
		public String getUpmpMerId(String mid){
				String sql="select upmp_mid from minfo where id="+Ryt.addQuotes(Ryt.sql(mid));
				return queryForString(sql);
			}
	 
		public List<MinfoH> queryAllMinfoHs(){
			String sql="select * from minfo";
			return query(sql, MinfoH.class);
		}	
		public List<Minfo> queryAllMinfos(){
			String sql="select * from minfo";
			return query(sql, Minfo.class);
		}
	}

package com.rongyifu.mms.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.BkAccount;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;
import com.rongyifu.mms.web.WebConstants;

public class PubDao<T> extends DAOSupport {
	
	
	public static final String SUCCESS = "OK";
	
	private Logger logger  =  Logger.getLogger(PubDao.class);
	
	
	/**
	 * 交易、退款、代扣调用方法
	 * @param uid
	 * @param aid
	 * @param trAmt
	 * @param trFee
	 * @param amt
	 * @param recPay
	 * @param tbName
	 * @param tbId
	 * @param remark
	 * @return
	 */
	public List<String> genAddAccSeqSqls(String uid,String aid,long trAmt,int trFee,long amt ,int recPay,String tbName,String tbId,String remark){
		AccSeqs param=new AccSeqs();
		param.setTbName(tbName);
		param.setTbId(tbId);
		param.setUid(uid);
		param.setAid(aid);
		param.setTrAmt(trAmt);
		param.setTrFee(trFee);
		param.setAmt(amt);
		param.setRemark(remark);
		param.setRecPay((short)recPay);
		return RecordLiveAccount.recordAccSeqsAndCalLiqBalance(param);
	}
	
	/**
	 * 产生tr_orders表中
	 * @param oid，系统订单号
	 * @param mid，用户ID
	 * @param aid，账户ID
	 * @param ptype，交易类型
	 * @param oid2，对方系统订单号
	 * @param aname，账户名称
	 * @param initTime，生成订单时间
	 * @param date，系统日期
	 * @param time，系统时间
	 * @param toUid，交易对方用户ID
	 * @param toAid，交易对方账户ID
	 * @param pay_amt，结算金额
	 * @param state，状态
	 * @return
	 */
	public String genAddTrOrdersSql(String oid,String mid,String aid,int ptype,String oid2,String aname,
			long initTime ,int date,int time,String toUid,String toAid,long pay_amt,int state){
		StringBuffer sql=new StringBuffer("insert into tr_orders (oid,uid,aid,aname,init_time,sys_date,");
		sql.append("sys_time,ptype,org_oid,trans_amt,trans_fee,pay_amt,to_uid,to_aid, state,remark) values ( ");
		sql.append("'").append(Ryt.sql(oid)).append("',");
		sql.append("'").append(Ryt.sql(mid)).append("',");
		sql.append("'").append(Ryt.sql(aid)).append("',");
		sql.append("'").append(aname).append("',");
		sql.append(initTime).append(",");
		sql.append(date).append(",");
		sql.append(time).append(",");
		sql.append(ptype).append(",");
		sql.append("'").append(Ryt.sql(oid2)).append("',");
		sql.append(pay_amt).append(",");
		sql.append(0).append(",");
		sql.append(pay_amt).append(",");
		sql.append("'").append(Ryt.sql(toUid)).append("',");
		sql.append("'").append(Ryt.sql(toAid)).append("',");
		sql.append(state).append(",");
		sql.append("'订单生成')");
		return sql.toString();
	}
	/**
	 * 更新tr_orders表中的state状态
	 * @param oid
	 * @param state
	 * @param remark
	 * @return
	 */
	public String updateTrOrdersStateSql(String oid,Integer state,String remark){
		int date = DateUtil.today();
		int time= DateUtil.getCurrentUTCSeconds();
		StringBuffer sql=new StringBuffer("update tr_orders set state= ");
		sql.append(state).append(",sys_date=");
		sql.append(date).append(",sys_time=");
        sql.append(time).append(",remark=");
        sql.append("'").append(Ryt.sql(remark)).append("' where oid=");
        sql.append("'").append(Ryt.sql(oid)).append("'");
        return sql.toString();
	}
	
	public String updateTrOrdersStateSql(String oid,Integer state){
		int date = DateUtil.today();
		int time= DateUtil.getCurrentUTCSeconds();
		StringBuffer sql=new StringBuffer("update tr_orders set state= ");
		sql.append(state).append(",sys_date=");
		sql.append(date).append(",sys_time=");
        sql.append(time).append(" where oid=");
        sql.append("'").append(Ryt.sql(oid)).append("'");
        return sql.toString();
	}
	
	
	/**
	 * pay_chl_bk 关联支付渠道和银行行号表,别的地方也用到
	 * @param authorType
	 * @param gate
	 * @return
	 */
	public String getBKNOByPayChannel(int gid) {
		return queryForString("select bf_bk_no from gate_route where gid = "+ gid);
	}
	
	
	public long queryBalance(String mid){
		
		try {
			return queryForLong("select all_balance from acc_infos where aid ='" + Ryt.sql(mid) + "'");
		} catch (Exception e) {
			LogUtil.printErrorLog("PubDao", "queryBalance", "mid=" + mid, e);
			return 0;
		}
		
	}
	
//	public int queryBalance(int mid){
//		return queryBalance(String.valueOf(mid));
//	}
	
	
	/**
	 * 得到存管银行对象
	 * @return
	 */
	public BkAccount getCGBank(){
		final String sql = "select * from bk_account where bk_type=1 limit 1";
		return  queryForObject(sql,BkAccount.class);
	}
	
	/**
	 * 根据商户Id 得到商户对象
	 * @param id
	 * @return
	 */
	public Minfo getMinfoById(String id){
		final String sql = "select * from minfo where id = '"+ id+"'";
		return queryForObject(sql,Minfo.class);
	}
	
	/**
	 * 根据批次号得到FeeLiqBath对象
	 * @return
	 */
	public FeeLiqBath getFeeLiqBathByBatch(String batch){
		final String sql = "select * from fee_liq_bath where batch='" + batch+"'";
		return queryForObject(sql,FeeLiqBath.class);
	
	}
	/**
	 * 查询支付渠道map
	 * @return
	 */
	public Map<Integer,String> queryGateRouteToMap(){
		String sql="select gid,name from gate_route order by gid";
		return queryToMap2(sql,"MASTER");
	}
	
	/**
	 * 查询支付渠道map通过名字模糊查询
	 * @return
	 */
	public Map<Integer,String> queryGRouteByName(String gateName){
		String sql="select gid,name from gate_route where name like '%"+gateName+"%' order by gid";
		return queryToMap2(sql);
	}
	
	/**
	 * 查询daifu支付渠道map
	 * @return
	 */
	public Map<Integer,String> queryDFGateRouteToMap(){
		String sql="select gid,name from gate_route where gid like '4%' and gid not in (4,45000) order by gid";
		return queryToMap2(sql,"MASTER");
	}
	
	/**
	 * 查询返回objectList
	 * @param sql
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List query(String sql, Class clazz) {
		return this.executeQuery(sql, clazz);
	}
	/**
	 * 查询返回objectList
	 * @param sql
	 * @param args
	 * @param argTypes
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List query(String sql, Object[] args, int[] argTypes, Class clazz) {
		return this.executeQuery(sql, args, argTypes, clazz);
	}
	/**
	 * 查询返回objectList
	 * @param sql
	 * @param args
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List query(String sql, Object[] args, Class clazz) {
		return this.executeQuery(sql, args, clazz);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CurrentPage<T> queryForPage(final String sqlCountRows, final String sqlFetchRows, final int pageNo,
					final int pageSize, final Class clazz, final Map<String, String> SumSQLMap) {

		CurrentPage o = queryForPage(sqlCountRows, sqlFetchRows, null, pageNo, pageSize, new BeanPropertyRowMapper(clazz));
		if (o != null && SumSQLMap != null) {
			for (String key : SumSQLMap.keySet()) {
				o.setSumResult(key, queryForLong(SumSQLMap.get(key)));
			}
		}
		return o;
	}

	public CurrentPage<T> queryForPage(final String sqlCountRows, final String sqlFetchRows, final Object[] args,
					final int pageNo, final int pageSize, final Class<T> clazz, final Map<String, String> SumSQLMap) {
		CurrentPage<T> o = queryForPage(sqlCountRows, sqlFetchRows, args, pageNo, pageSize, new BeanPropertyRowMapper<T>(clazz));
		if (o != null && SumSQLMap != null) {
			for (String key : SumSQLMap.keySet()) {
				o.setSumResult(key, queryForLong(SumSQLMap.get(key)));
			}
		}
		return o;
	}

	public CurrentPage<T> queryForPage(final String sqlCountRows, final String sqlFetchRows, final int pageNo,
					final int pageSize, final Class<T> clazz) {

		return queryForPage(sqlCountRows, sqlFetchRows, null, pageNo, pageSize, new BeanPropertyRowMapper<T>(clazz));
	}
	//去pageSize,args
	public CurrentPage<T> queryForPage(final String sqlCountRows, final String sqlFetchRows,
			final int pageNo,final Class<T> clazz) {
	  return queryForPage(sqlCountRows, sqlFetchRows, null, pageNo, AppParam.getPageSize(), new BeanPropertyRowMapper<T>(clazz));
	}
	//去pageSize,args 有SumSQLMap
	public CurrentPage<T> queryForPage(final String sqlCountRows, final String sqlFetchRows, 
			final int pageNo,final Class<T> clazz, final Map<String, String> SumSQLMap) {
	   return queryForPage(sqlCountRows,sqlFetchRows, null,pageNo, AppParam.getPageSize(), clazz,SumSQLMap);
	}
	public CurrentPage<T> queryForPage(final String sqlCountRows, final String sqlFetchRows, final Object[] args,
					final int pageNo, final int pageSize, final Class<T> clazz) {
		return queryForPage(sqlCountRows, sqlFetchRows, args, pageNo, pageSize, new BeanPropertyRowMapper<T>(clazz));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private CurrentPage<T> queryForPage(final String sqlCountRows, final String sqlFetchRows, final Object[] args1,
					final int pageNo, final int pageSize1, final RowMapper<T> rowMapper) {
		LogUtil.printInfoLog("PubDao", "queryForPage", sqlFetchRows);
		
		Object[] args = (args1 == null) ? new Object[0] : args1;
		// determine how many rows are available
		final int rowCount = queryForInt(sqlCountRows, args);
		// pageSize1 ==-1 标识 查询所有满足条件的数据，供下载使用
		final int pageSize = (pageSize1 == -1) ? (rowCount==0?1:rowCount) : pageSize1;
		// calculate the number of pages
		int pageCount = rowCount / pageSize;
		if (rowCount > pageSize * pageCount) {
			pageCount++;
		}
		// create the page object
		final CurrentPage<T> page = new CurrentPage<T>();
		page.setPageNumber(pageNo);
		page.setPagesAvailable(pageCount);
		page.setPageSize(pageSize);
		page.setPageTotle(rowCount);
		// fetch a single page of results
		final int startRow = (pageNo - 1) * pageSize;
		// final RowMapper rowMapper = new BeanPropertyRowMapper(clazz);
		getJdbcTemplate().query(sqlFetchRows, args, new ResultSetExtractor() {
			@Override
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				final List pageItems = page.getPageItems();
				int currentRow = 0;
				while (rs.next() && currentRow < startRow + pageSize) {
					if (currentRow >= startRow) {
						pageItems.add(rowMapper.mapRow(rs, currentRow));
					}
					currentRow++;
				}
				return page;
			}
		});
		return page;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CurrentPage<T> queryForPageV1(final String sumSql, final String sqlFetchRows, final int pageNo,
					final int pageSize, final Class clazz, final List<String> SumFields) {

		return queryForPageV1(sumSql, sqlFetchRows, null, pageNo, pageSize, new BeanPropertyRowMapper(clazz), SumFields);
	}
	
	/**
	 * 分页方法，统一统计累计值 
	 * @param sumSql
	 * @param sqlFetchRows
	 * @param args1
	 * @param pageNo
	 * @param pageSize1
	 * @param rowMapper
	 * @param SumFields
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private CurrentPage<T> queryForPageV1(final String sumSql,
			final String sqlFetchRows, final Object[] args1, final int pageNo,
			final int pageSize1, final RowMapper<T> rowMapper, final List<String> SumFields) {
		LogUtil.printInfoLog("PubDao", "queryForPageV1", sqlFetchRows);

		Object[] args = (args1 == null) ? new Object[0] : args1;
		
		Map<String, Object> sumMap = this.queryForMap(sumSql, args);
		
		// create the page object
		final CurrentPage<T> page = new CurrentPage<T>();
		if (SumFields != null && SumFields.size() > 0) {
			for (String field : SumFields) 
				page.setSumResult(field, sumMap.get(field));
		}
		
		// determine how many rows are available
		final int rowCount = Integer.parseInt(String.valueOf(sumMap.get(AppParam.ROW_COUNT)));
		// pageSize1 ==-1 标识 查询所有满足条件的数据，供下载使用
		final int pageSize = (pageSize1 == -1) ? (rowCount == 0 ? 1 : rowCount)
				: pageSize1;
		// calculate the number of pages
		int pageCount = rowCount / pageSize;
		if (rowCount > pageSize * pageCount) {
			pageCount++;
		}
		
		page.setPageNumber(pageNo);
		page.setPagesAvailable(pageCount);
		page.setPageSize(pageSize);
		page.setPageTotle(rowCount);
		// fetch a single page of results
		final int startRow = (pageNo - 1) * pageSize;
		// final RowMapper rowMapper = new BeanPropertyRowMapper(clazz);
		getJdbcTemplate().query(sqlFetchRows, args, new ResultSetExtractor() {
			@Override
			public Object extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				final List pageItems = page.getPageItems();
				int currentRow = 0;
				while (rs.next() && currentRow < startRow + pageSize) {
					if (currentRow >= startRow) {
						pageItems.add(rowMapper.mapRow(rs, currentRow));
					}
					currentRow++;
				}
				return page;
			}
		});
		return page;
	}
	
	// 获得用户
	public LoginUser  getLoginUser() {
		try {
			WebContext context = WebContextFactory.get();
			if(context == null)
				return null;
			
			HttpSession session = context.getSession();			
			LoginUser user = (LoginUser)session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER);
			return user;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	// 获得用户
	public String  getLoginUserName() {
		try {
			return getLoginUser().getOperName();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "";
		}
	}
	
	public String  getLoginUserMid() throws Exception {
		try {
		return getLoginUser().getMid();
		} catch (Exception e) {
			throw new Exception("用户未登陆，或session超时");
		}
	}
	
	//登陆和注销时保存日记
	public int saveOperLog(String loginmid, int loginuid,String action, String action_desc, HttpServletRequest request) {
		try {
			return saveOperLogBase(loginmid,
					               loginuid,
					               DateUtil.today(),
					               DateUtil.getCurrentUTCSeconds(),
					               action,
					               action_desc,
					               request);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return 0;
		}
	}
	// 保存日记
	public int saveOperLog(String action, String action_desc) {
		try {
			WebContext webContext = WebContextFactory.get();
			HttpServletRequest request = null;
			if(webContext != null)
				request = webContext.getHttpServletRequest();
			return saveOperLog(action, action_desc, request);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return 0;
		}
	}
	
	// 保存日记
	public int saveOperLog(String action, String action_desc, HttpServletRequest request) {
		LoginUser user = getLoginUser();
		if (user == null)
			return 0;
		try {
			return saveOperLogBase(user.getMid(), 
					               user.getOperId(),
					               DateUtil.today(),
					               DateUtil.getCurrentUTCSeconds(),
					               action,
					               action_desc,
					               request);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return 0;
		}
	}
	
	private int saveOperLogBase(String loginmid, int loginuid, int now_d, int now_t, String action, String action_desc, HttpServletRequest request) {
		String operIp = null;// 操作者IP
		if(request == null){
			operIp = "";
		} else 
			operIp = request.getRemoteHost();
		
		action_desc = action_desc.length() > 500 ? action_desc.substring(0, 500) : action_desc;
		final String sql = "insert into oper_log (mid,sys_date,sys_time,oper_id,action,action_desc,oper_ip) values (?,?,?,?,?,?,?)";
		Object[] object = new Object[] { loginmid, now_d, now_t, loginuid, action, action_desc, operIp };
		int[] types = new int[] { Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.CHAR, Types.VARCHAR, Types.VARCHAR };
		
		return update(sql, object, types);
	}
	/**
	 * 分页封装（简化版）
	 * @param sqlFetchRows
	 * @param pageNo 页数  （为-1时将查询全部成一页中）
	 * @param clazz
	 * @return
	 */
	public CurrentPage<T> queryForCurrPage( String sqlFetchRows,int pageNo,Class<T> clazz){
		   int pageSize=ParamCache.getIntParamByName("pageSize");
		return queryForCurrPage(sqlFetchRows, null,pageNo,pageSize,clazz);
	}
	/**
	 * 分页封装（左下脚统计数据）
	 * @param sqlFetchRows
	 * @param pageNo(为-1时将查询全部到一页中)
	 * @param clazz
	 * @param SumSQLMap
	 * @return
	 */
	public CurrentPage<T> queryForCurrPage(String sqlFetchRows,
					int pageNo, Class<T> clazz, final Map<String, String> SumSQLMap) {
			CurrentPage<T> page = queryForCurrPage(sqlFetchRows, pageNo, clazz);
			if (page != null && SumSQLMap != null) {
				for (String key : SumSQLMap.keySet()) {
					page.setSumResult(key, queryForLong(SumSQLMap.get(key)));
				}
			}
			return page;
	}
	
	public CurrentPage<T> queryForCurrPage(String sqlFetchRows,int pageNo,int pageSize,Class<T> clazz){
		return queryForCurrPage(sqlFetchRows, null,pageNo,pageSize,clazz);
	}
	
	private CurrentPage<T> queryForCurrPage(String sqlFetchRows,Object[] args1, 
			final int pageNo1,final int pageSize1,Class<T> clazz) {
		final BeanPropertyRowMapper<T> rowMapper= new BeanPropertyRowMapper<T>(clazz);
		Object[] args = (args1 == null) ? new Object[0] : args1;
		final CurrentPage<T> page = new CurrentPage<T>();
		getJdbcTemplate().query(sqlFetchRows, args, new ResultSetExtractor<CurrentPage<T>>() {
			@Override
			public CurrentPage<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.last(); 
				int rowCount=rs.getRow();
				rs.beforeFirst();
				
				int pageSize=pageSize1;
				int pageNo=pageNo1;
				if(pageNo1==-1){
					 pageSize=rowCount==0?1:rowCount;//pageNo=-1将查询全部记录（供下载）
					 pageNo=1;
				}
				int pageCount = rowCount / pageSize;
				if (rowCount > pageSize * pageCount) {
					pageCount++;
				}
				page.setPageNumber(pageNo);
				page.setPageSize(pageSize);
				page.setPagesAvailable(pageCount);
				page.setPageTotle(rowCount);
				final List<T> pageItems = page.getPageItems();
				int currentRow = 0;
				while (rs.next() && currentRow < pageNo*pageSize) {
					if (currentRow >= (pageNo - 1) * pageSize) {
						pageItems.add(rowMapper.mapRow(rs, currentRow));
					}
					currentRow++;
				}
				return null;
			}
		});
		return page;
	}
	/**
	 * 新版分页封装（sql分页）
	 */
	/*
	private CurrentPage<T> queryForCurrPage(String sqlFetchRows,Object[] args1, 
			final int pageNo1,final int pageSize1,Class<T> clazz) {
		final BeanPropertyRowMapper<T> rowMapper= new BeanPropertyRowMapper<T>(clazz);
		Object[] args = (args1 == null) ? new Object[0] : args1;
		final CurrentPage<T> page = new CurrentPage<T>();
		
		String countSql="select count(*) "+sqlFetchRows.substring(sqlFetchRows.toLowerCase().indexOf(" from "));
		 int rowCount=queryForInt(countSql);
		int pageSize=pageSize1;
		 int pageNo=pageNo1;
		if(pageNo1==-1){
			 pageSize=rowCount==0?1:rowCount;//pageNo=-1将查询全部记录（供下载）
			 pageNo=1;
		}
		int pageCount = rowCount / pageSize;
		if (rowCount > pageSize * pageCount) {
			pageCount++;
		}
		page.setPageNumber(pageNo);
		page.setPageSize(pageSize);
		page.setPagesAvailable(pageCount);
		page.setPageTotle(rowCount);
		
		String resultSql=sqlFetchRows+" limit "+(pageNo-1)*pageSize+","+pageSize;
		getJdbcTemplate().query(resultSql, args, new ResultSetExtractor<CurrentPage<T>>() {
			public CurrentPage<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
				final List<T> pageItems = page.getPageItems();
				int currentRow = 0;
				while (rs.next()) {
						pageItems.add(rowMapper.mapRow(rs, currentRow));
					currentRow++;
				}
				return null;
			}
		});
		return page;
	}
	*/	
	/**
	 * 判断是否有交易账户
	 * @param mid
	 * @return
	 */
	public boolean hasTransAcc(String mid){
		String sql = "select count(0) from acc_infos where uid = '" + mid + "' and acc_type = 1";
		if(queryForInt(sql) >0)
			return true;
		return false;
		}
	/**
	 * 判断交易账户是否为开启状态（只适合在开启交易账户时判断）
	 * @param mid 商户ID
	 * @return
	 */
	public boolean transAccIsNomal(String mid){
		if(hasTransAcc(mid)){
			return false;
		}
		String sql = "select count(0) from acc_infos where uid = '" + mid + "' and acc_type = 1 and state=1";
		if(queryForInt(sql) > 0)
			return true;
		return false;
	}
	/**
	 * 判断交易账户是否为开启状态
	 * @param mid 商户ID
	 * @return
	 */
	public boolean transAccIsNomals(String mid){
		if(!hasTransAcc(mid)){
			return false;
		}
		String sql = "select count(0) from acc_infos where uid = '" + mid + "' and acc_type = 1 and state=1";
		if(queryForInt(sql) > 0)
			return true;
		return false;
	}
	
	/**
	 * 充值、提现、代付调用方法
	 * @param uid
	 * @param aid
	 * @param trAmt
	 * @param trFee
	 * @param amt
	 * @param recPay
	 * @param tbName
	 * @param tbId
	 * @param remark
	 * @return
	 */
	public List<String> genAddAccSeqSqls2(String uid,String aid,long trAmt,int trFee,long amt ,int recPay,String tbName,String tbId,String remark){
		AccSeqs param=new AccSeqs();
		param.setTbName(tbName);
		param.setTbId(tbId);
		param.setUid(uid);
		param.setAid(aid);
		param.setTrAmt(trAmt);
		param.setTrFee(trFee);
		param.setAmt(amt);
		param.setRemark(remark);
		param.setRecPay((short)recPay);
		return RecordLiveAccount.recordAccSeqsAndCalUsableBalance(param);
	}
	
	/**
	 * 通过网关获取手续费
	 * @param mid
	 * @param gate
	 * @return
	 * @throws Exception 
	 */
	public FeeCalcMode getFeeModeByGate(String mid,String gate) throws Exception{
		StringBuffer sql=new StringBuffer("select calc_mode,gid from fee_calc_mode where mid =");
		sql.append(Ryt.addQuotes(mid));
		sql.append(" and gate=");
		sql.append(gate);
		FeeCalcMode mode=queryForObject(sql.toString(),FeeCalcMode.class);
		if(null==mode)
			throw new Exception("网关[" + gate + "]尚未配置！");
		return mode;
	}
	
}

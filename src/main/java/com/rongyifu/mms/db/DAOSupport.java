package com.rongyifu.mms.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionStatus;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.ReverseObject;
import com.rongyifu.mms.exception.RytException;
import com.rongyifu.mms.db.datasource.CustomerContextHolder;
import com.rongyifu.mms.utils.LogUtil;

public abstract class DAOSupport {
	
	private static JdbcTemplate myjt = null;
	private static SimpleJdbcTemplate simpleJt = null;
	private static ApplicationContext ac = null;
	private static DataSourceTransactionManager dtm = null;
	private static Object syncLock = new Object();
	static {
		ac = new ClassPathXmlApplicationContext("beans.xml");
		dtm = (DataSourceTransactionManager) ac.getBean("txManager");
		myjt = new JdbcTemplate(dtm.getDataSource());
		simpleJt = new SimpleJdbcTemplate(dtm.getDataSource());
	}

	/**
	 * 得到数据库Connection对象
	 * @return Connection
	 */
	public Connection getConnection() {
		CustomerContextHolder.setMaster();
		return DataSourceUtils.getConnection(dtm.getDataSource());
	}

	/**
	 * 得到JDBC工具类 JdbcTemplate
	 * @return JdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		CustomerContextHolder.setMaster();
		return myjt;
	}

	/**
	 * 得到JDBC工具类 SimpleJdbcTemplate
	 * @return SimpleJdbcTemplate
	 */
	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		CustomerContextHolder.setMaster();
		return simpleJt;
	}
	
	/**
	 * 一个事务中批量执行sql语句
	 * @param sqls sql语句数组
	 * @return int[]
	 */
	public int[] batchSqlTransaction(String[] sqls) {
		int[] r = {};
		
		synchronized(syncLock){
			CustomerContextHolder.setMaster();
			
			TransactionStatus status = dtm.getTransaction(null);
			Map<String, Object> params = LogUtil.createTreeMap();
			params.put("DB", CustomerContextHolder.getCustomerType());
			int i = 0;
			for(String sql : sqls){
				params.put("sql[" + (i++) + "]", sql);
			}
			try {
				checkUpdateIsLegal(sqls);
				r = myjt.batchUpdate(sqls);
				dtm.commit(status);
				
				LogUtil.printInfoLog("T01", params);
			} catch (Exception e) {
				params.put("errorMsg", e.getMessage());
				LogUtil.printErrorLog("T01", params);
				
				dtm.rollback(status); // 回滚
				r = null;
			}
		}
		
		return r;
	}

	/**
	 * 一个事务中批量执行sql语句
	 * @param sqlList sql语句List
	 * @return int[]
	 */
	public void batchSqlTransaction2(List<String> sqlList) throws Exception {
		String[] sqls = sqlList.toArray(new String[sqlList.size()]);
		checkUpdateIsLegal(sqls);
		
		int[] ret = batchSqlTransaction(sqls);
		if (ret.length == 0) throw new RytException("batch sql error ");
	}
	/**
	 * 预处理方式的批处理
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public int[] batchSqlTransaction(String sql,List<Object[]> params) throws Exception{
		checkUpdateIsLegal(sql);
		CustomerContextHolder.setMaster();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		int i = 0;
		for(Object[] obj : params){
			logParams.put("params["+(i++)+"]", String.valueOf(Arrays.asList(obj)));
		}
		
		TransactionStatus status = dtm.getTransaction(null);
		try {
			   int[] rows=simpleJt.batchUpdate(sql, params); 
			   dtm.commit(status);
			   
			   LogUtil.printInfoLog("T02", logParams);
			   
			   return rows;
		} catch (DuplicateKeyException e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T02", logParams);
			
			dtm.rollback(status); 
			throw new DuplicateKeyException("数据有唯一约束，不能重复！");
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T02", logParams);
			
			dtm.rollback(status); 
			throw new Exception("数据库批处理异常！");
		}
	}
	/**
	 * 查询单一记录，返回map，
	 * @param sql
	 * @return
	 */
	public Map<String, Object> queryForMap(String sql) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		
		try {
			Map<String, Object> map = myjt.queryForMap(sql);
			LogUtil.printInfoLog("T03", logParams);
			return map;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T03", logParams);
			
			return new HashMap<String, Object>();
		}

	}

	public Map<String, Object> queryForMap(String sql, Object[] args) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		
		try {
			Map<String, Object> map = myjt.queryForMap(sql, args);
			
			LogUtil.printInfoLog("T04", logParams);
			return map;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T04", logParams);
			
			return new HashMap<String, Object>();
		}
	}

	public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		try {
			Map<String, Object> map = myjt.queryForMap(sql, args, argTypes);
			
			LogUtil.printInfoLog("T05", logParams);
			return map;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T05", logParams);
			
			return new HashMap<String, Object>();
		}
	}
	/**
	 * 把表中两个字段查询成Map
	 * @param sql 如sql="select id,name from table ..."
	 * @return
	 */
	public Map<String, String> queryToMap(String sql) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		try {
	         final Map<String, String> map=new TreeMap<String, String>();
	         myjt.query(sql,new RowMapper<Object>(){
					@Override
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						map.put(rs.getString(1),rs.getString(2));
						return null;
					}
	         });
	         
	         LogUtil.printInfoLog("T06", logParams);
	         
	         return map;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T06", logParams);
			
			return null;
		}
	}
	//返回Map<Integer, String>
	public Map<Integer, String> queryToMap2(String sql) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		try {
	         final Map<Integer, String> map=new TreeMap<Integer, String>();
	         myjt.query(sql,new RowMapper<Object>(){
					@Override
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						map.put(rs.getInt(1),rs.getString(2));
						return null;
					}
	         });
	         
	         LogUtil.printInfoLog("T07", logParams);
	         
	         return map;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T07", logParams);
			
			return null;
		}
	}

	//返回Map<Integer, String>
	public Map<Integer, String> queryToMap2(String sql,String MASTER) {
		CustomerContextHolder.setMaster();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		try {
	         final Map<Integer, String> map=new TreeMap<Integer, String>();
	         myjt.query(sql,new RowMapper<Object>(){
					@Override
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						map.put(rs.getInt(1),rs.getString(2));
						return null;
					}
	         });
	         
	         LogUtil.printInfoLog("T07", logParams);
	         
	         return map;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T07", logParams);
			
			return null;
		}
	}
	
	/**
	 * 把表中两个字段查询成Map
	 * @param sql 如sql="select id,name from table ..."
	 * @return
	 */
	public Map<Long, String> queryToMap3(String sql) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		try {
	         final Map<Long, String> map=new TreeMap<Long, String>();
	         myjt.query(sql,new RowMapper<Object>(){
					@Override
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						map.put(rs.getLong(1),rs.getString(2));
						return null;
					}
	         });
	         
	         LogUtil.printInfoLog("T06", logParams);
	         
	         return map;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T06", logParams);
			
			return null;
		}
	}
	public int queryForInt(String sql) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		LogUtil.printInfoLog("T08", logParams);
		return myjt.queryForInt(sql);
	}

	public int queryForInt(String sql, Object[] args) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		LogUtil.printInfoLog("T09", logParams);
		
		return myjt.queryForInt(sql, args);
	}

	public int queryForInt(String sql, Object[] args, int[] argTypes) {
		CustomerContextHolder.setSlave();

		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		LogUtil.printInfoLog("T10", logParams);
		
		return myjt.queryForInt(sql, args, argTypes);
	}

	public long queryForLong(String sql) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		LogUtil.printInfoLog("T11", logParams);
		
		return myjt.queryForLong(sql);
	}

	public long queryForLong(String sql, Object[] args) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		LogUtil.printInfoLog("T12", logParams);
		
		return myjt.queryForLong(sql, args);
	}

	public long queryForLong(String sql, Object[] args, int[] argTypes) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		LogUtil.printInfoLog("T13", logParams);
		
		return myjt.queryForLong(sql, args, argTypes);
	}

	public List<Map<String, Object>> queryForList(String sql) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		
		LogUtil.printInfoLog("T14", logParams);
		return myjt.queryForList(sql);
	}

	public List<Map<String, Object>> queryForList(String sql, Object[] args) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		LogUtil.printInfoLog("T15", logParams);
		
		return myjt.queryForList(sql, args);
	}
	
	public List<T> queryForList(String sql, Object[] args,Class<T> elementType) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		LogUtil.printInfoLog("T16", logParams);
		
		return myjt.queryForList(sql, args, elementType);
	}
	
	@SuppressWarnings("hiding")
	public <T>List<T> queryForList(String sql,Class<T> elementType) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		LogUtil.printInfoLog("T17", logParams);
		
		return myjt.queryForList(sql, elementType);
	}
	@SuppressWarnings("hiding")
	public <T>List<Integer> queryForIntegerList(String sql) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		LogUtil.printInfoLog("T18", logParams);
		
		return myjt.queryForList(sql, Integer.class);
	}
	
	public List<String> queryForStringList(String sql){
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);		
		LogUtil.printInfoLog("T19", logParams);
		
		return myjt.queryForList(sql, String.class);
	}

	public List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		LogUtil.printInfoLog("T20", logParams);
		
		return myjt.queryForList(sql, args, argTypes);
	}

	@SuppressWarnings("hiding")
	public <T> T queryForObject(String sql, Class<T> clazz) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		
		try {
			T t = myjt.queryForObject(sql, new BeanPropertyRowMapper<T>(clazz));
			LogUtil.printInfoLog("T21", logParams);
			return t;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T21", logParams);
			return null;
		}
	}
	
	@SuppressWarnings("hiding")
	public <T> T queryForObjectThrowException(String sql, Class<T> clazz){
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		LogUtil.printInfoLog("T22", logParams);
		
		return myjt.queryForObject(sql, new BeanPropertyRowMapper<T>(clazz));		
	}
	
	public String queryForString(String sql) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		
		try {
			String result = myjt.queryForObject(sql, String.class);
			LogUtil.printInfoLog("T23", logParams);
			return result;
		} catch (Exception e) {			
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T23", logParams);
			
			return null;
		}
	}
	
	public String queryForStringThrowException(String sql) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		LogUtil.printInfoLog("T24", logParams);
		
		return myjt.queryForObject(sql, String.class);
	}
	
	public String queryForString(String sql,Object[] args) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);		
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		
		try {
			String result = myjt.queryForObject(sql,args, String.class);
			LogUtil.printInfoLog("T25", logParams);
			return result;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T25", logParams);
			
			return null;
		}
	}
	
	@SuppressWarnings("hiding")
	public <T> T queryForObject(String sql, Object[] args, Class<T> clazz) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		
		try {
			T t = myjt.queryForObject(sql, args, new BeanPropertyRowMapper<T>(clazz));
			LogUtil.printInfoLog("T26", logParams);
			return t;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T26", logParams);
			return null;
		}
	}
	
	@SuppressWarnings("hiding")
	public <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> clazz) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		
		try {
			T t = myjt.queryForObject(sql, args, argTypes, new BeanPropertyRowMapper<T>(clazz));
			LogUtil.printInfoLog("T27", logParams);
			return t;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T27", logParams);
			return null;
		}
	}

	public int update(String sql) {
		CustomerContextHolder.setMaster();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);
		
		try {
			checkUpdateIsLegal(sql);
			int result = myjt.update(sql);
			LogUtil.printInfoLog("T28", logParams);
			return result;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T28", logParams);
			
			return 0;
		}
	}

	public int update(String sql, Object[] args, int[] argTypes) {
		CustomerContextHolder.setMaster();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);			
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		
		try {
			checkUpdateIsLegal(sql);
			int result = myjt.update(sql, args, argTypes);
			LogUtil.printInfoLog("T29", logParams);
			return result;
		}catch (Exception e) {			
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T29", logParams);
			
			return 0;
		}
	}

	public int update(String sql, Object... args) {
		CustomerContextHolder.setMaster();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("sql", sql);			
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		
		try {
			checkUpdateIsLegal(sql);
			int result = myjt.update(sql, args);
			LogUtil.printInfoLog("T30", logParams);
			return result;
		} catch (DuplicateKeyException e){			
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T30", logParams);
			
			throw new  DuplicateKeyException("该表有唯一约束，不能重复插入记录！"); 
		}catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T30", logParams);
			
			return 0;
		}
	}
	
	public int saveObject(Object obj)throws Exception{
		CustomerContextHolder.setMaster();
		
		Object[] objArr = new ReverseObject().reverseObjToSql(obj);
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		if(objArr != null && objArr.length >= 2){
			logParams.put("sql", objArr[0].toString());			
			logParams.put("args", String.valueOf(Arrays.asList((Object[])objArr[1])));
		}
		
		try {
			checkUpdateIsLegal(objArr[0].toString());
			int result = myjt.update(objArr[0].toString(), (Object[])objArr[1]);
			LogUtil.printInfoLog("T31", logParams);
			return result;
		} catch (DuplicateKeyException e){
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T31", logParams);
			
			throw new  DuplicateKeyException("该表有唯一约束，不能重复插入记录！");
		}catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T31", logParams);
			
			throw new Exception("保存异常！请检查对象及属性是否与数据库表对应。"); 
		}
	}
	
	/**
	 * 一个事务中批量执行sql语句
	 * @param sqls sql语句数组
	 * @return int[]
	 */
	public int[] batchSqlTransaction(String[] sqls,String type)throws Exception {
		CustomerContextHolder.setMaster();
		
		int[] r = {};
		TransactionStatus status = dtm.getTransaction(null);
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		int i = 0;
		for(String sql : sqls){
			logParams.put("sql[" + (i++) + "]", sql);
		}
		logParams.put("type", type);
		
		try {
			checkUpdateIsLegal(sqls);
			
			r = myjt.batchUpdate(sqls);
			dtm.commit(status);
			
			LogUtil.printInfoLog("T32", logParams);
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T32", logParams);
			
			dtm.rollback(status); // 回滚
			throw new Exception(e.getMessage()); 
		}

		return r;
	}
	
	/**
	 * 检查update语句：如果没有where条件代表是不合法的语句
	 * @param sql
	 * @throws Exception
	 */
	protected void checkUpdateIsLegal(String[] sqls) throws Exception{
		for(String sql : sqls)
			checkUpdateIsLegal(sql);
	}
	
	/**
	 * 检查update语句：如果没有where条件代表是不合法的语句
	 * @param sql
	 * @throws Exception
	 */
	protected void checkUpdateIsLegal(String sql) throws Exception{
		if(Ryt.empty(sql))
			return;
		
		String tsql = sql.trim().toUpperCase();
		if(tsql.startsWith("UPDATE") && tsql.indexOf("WHERE") == -1)
			throw new Exception("update语句没有where条件，请检查sql是否正确！");
	}
	
	/**
	 * 查询返回objectList
	 * @param sql
	 * @param clazz
	 * @return
	 */
	public List<T> executeQuery(String sql, Class<T> clazz) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("Class", clazz.getName());
		logParams.put("sql", sql);
		
		try {
			List<T> list = myjt.query(sql, new BeanPropertyRowMapper<T>(clazz));
			LogUtil.printInfoLog("T33", logParams);
			return list;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T33", logParams, e);
			return new ArrayList<T>();
		}
	}
	/**
	 * 查询返回objectList
	 * @param sql
	 * @param args
	 * @param argTypes
	 * @param clazz
	 * @return
	 */
	public List<T> executeQuery(String sql, Object[] args, int[] argTypes, Class<T> clazz) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("Class", clazz.getName());
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		logParams.put("sql", sql);
		
		try {
			List<T> list = myjt.query(sql, args, argTypes, new BeanPropertyRowMapper<T>(clazz));
			LogUtil.printInfoLog("T34", logParams);
			return list;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T34", logParams, e);
			return new ArrayList<T>();
		}
	}
	/**
	 * 查询返回objectList
	 * @param sql
	 * @param args
	 * @param clazz
	 * @return
	 */
	public List<T> executeQuery(String sql, Object[] args, Class<T> clazz) {
		CustomerContextHolder.setSlave();
		
		Map<String, Object> logParams = LogUtil.createTreeMap();
		logParams.put("DB", CustomerContextHolder.getCustomerType());
		logParams.put("Class", clazz.getName());
		logParams.put("args", String.valueOf(Arrays.asList(args)));
		logParams.put("sql", sql);
		
		try {
			List<T> list = myjt.query(sql, args, new BeanPropertyRowMapper<T>(clazz));
			LogUtil.printInfoLog("T35", logParams);
			return list;
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("T35", logParams, e);
			return new ArrayList<T>();
		}
	}
}

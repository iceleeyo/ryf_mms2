package com.rongyifu.mms.api.bizobjs;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.api.BizObj;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.DateUtil;

/**
 * 给账户系统提供充值、代付交易对账数据接口实现类 交易码TCO004
 */
public class SendSettlementDataObj implements BizObj {
	
	/**
	 * 充值
	 */
	private static final String CHONG_ZHI = "0";
	/**
	 * 代付
	 */
	private static final String DAI_FU = "1";
	
	@SuppressWarnings("rawtypes")
	private PubDao dao = new PubDao();

	/**
	 * 参数：
	 * 		mid 商户号
	 * 		dataSource 数据源
	 * 		sysDate 日期
	 * 		type 0 充值数据 1 代付数据
	 * 当拉充值交易对账数据时mid必须有 
	 * 代付的话mid为空 拉全部
	 * (充值只拉成功且对过账的数据、代付拉成功的数据)
	 */
	@Override
	public Object doBiz(Map<String, String> params) throws Exception {
		String type = params.get("type");
		JSONArray arr = new JSONArray();
		if(StringUtils.isBlank(type)){
			throw new Exception("参数type不能为空");
		}
		if(!CHONG_ZHI.equals(type) && !DAI_FU.equals(type)){
			throw new Exception("参数错误[type = "+type+"]");
		}
		String sql = genSql(type, params);
		List<Map<String,Object>> list = dao.queryForList(sql);
		for (Map<String,Object> map : list) {
			JSONObject json = new JSONObject();
			json.accumulateAll(map);
			arr.add(json);
		}
		return arr;
	}
	
	private String genSql(String type, Map<String,String> params) throws Exception{
		StringBuilder sbr = new StringBuilder();
		String sysDate = params.get("sysDate");
		if(StringUtils.isBlank(sysDate)){
			throw new Exception("sysDate不能为空");
		}else if(!DateUtil.validate(sysDate)){
			throw new Exception("sysDate格式错误: [sysDate = "+ sysDate+"], sysDate格式为yyyyMMdd且必须为合法的日期");
		}
		String dataSource = params.get("dataSource");
		String mid = params.get("mid");
		
		if(CHONG_ZHI.equals(type)){//充值只拉成功且对账成功的数据
			if(StringUtils.isBlank(mid)){
				throw new Exception("拉取充值交易对账数据时, mid不能为空");
			}else{
				sbr.append("SELECT tseq,oid,amount,sys_date AS sysDate FROM hlog WHERE tstat =2 AND bk_chk = 1");
				sbr.append(" AND mid = ").append(Ryt.addQuotes(mid));
			}
		}else{//代付拉成功的数据
			sbr.append("SELECT tseq,order_id AS oid,trans_amt AS amount,sys_date AS sysDate FROM df_transaction WHERE tstat =2");
			if(StringUtils.isNotBlank(mid)){
				sbr.append(" AND account_id = ").append(Ryt.addQuotes(mid));
			}
		}
		if(StringUtils.isNotBlank(dataSource)){
			sbr.append(" AND data_source = ").append(Ryt.sql(dataSource));
		}
		sbr.append(" AND sys_date = ").append(sysDate);
		return sbr.toString();
	}

}

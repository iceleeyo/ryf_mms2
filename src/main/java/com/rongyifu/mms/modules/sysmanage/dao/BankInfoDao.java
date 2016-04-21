package com.rongyifu.mms.modules.sysmanage.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.rongyifu.mms.bean.BankNoInfo;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

public class BankInfoDao extends PubDao<BankNoInfo> {
	private Logger logger = Logger.getLogger(getClass());
	
	public BankNoInfo queryById(Integer id){
		if(id == null){
			return null;
		}
		String sql = "select * from bank_no_info where id = ?";
		return queryForObject(sql, new Object[]{id}, BankNoInfo.class);
	}

	
	public CurrentPage<BankNoInfo> queryForPage(BankNoInfo bni,Integer pageNo){
		String sql = genSql(bni);
		logger.info(sql);
		return queryForCurrPage(sql.toString(), pageNo, BankNoInfo.class);
	}

	public CurrentPage<BankNoInfo> queryForPage(BankNoInfo bni,Integer pageNo,Integer pageSize){
		String sql = genSql(bni);
		logger.info(sql);
		return queryForCurrPage(sql.toString(), pageNo,pageSize, BankNoInfo.class);
	}
	
	private String genSql(BankNoInfo bni){
		StringBuilder sql = new StringBuilder("SELECT bni.id,bni.bk_name,bni.bk_no,bni.gid,g.gate_name superBankName FROM bank_no_info bni");
		sql.append(" left join ryt_gate g on (bni.gid = right(g.gate,3) and left(g.gate,2) = '71' ) WHERE 1=1");
		if(StringUtils.isNotBlank(bni.getBkName())){
			sql.append(" AND bni.bk_name LIKE '%").append(Ryt.sql(bni.getBkName())).append("%'");
		}
		if(StringUtils.isNotBlank(bni.getBkNo())){
			sql.append(" AND bni.bk_no = ").append(Ryt.addQuotes(bni.getBkNo()));
		}
		if(StringUtils.isNotBlank(bni.getGid())){
			if(bni.getGid().length()>3){
				sql.append(" AND bni.gid = ").append(Ryt.addQuotes(bni.getGid().substring(2)));
			}else{
				sql.append(" AND bni.gid = ").append(Ryt.addQuotes(bni.getGid()));
			}
		}
		if(bni.getType() != null){
			sql.append(" AND bni.type = ").append(Ryt.addQuotes(bni.getType()));
		}
		if(StringUtils.isNotBlank(bni.getCityId())){
			sql.append(" AND bni.city_id = ").append(Ryt.addQuotes(bni.getCityId()));
		}
		return sql.toString();
	}
	
	public int update(BankNoInfo bni){
		if(bni == null || bni.getId() == 0 ||(StringUtils.isBlank(bni.getBkName())&&StringUtils.isBlank(bni.getBkNo())&&StringUtils.isBlank(bni.getGid()))){
			return 0;
		}
		StringBuilder sql = new StringBuilder("UPDATE bank_no_info SET");
		List<Object> args = new ArrayList<Object>();
		if(StringUtils.isNotBlank(bni.getBkName())){
			sql.append(" bk_name = ?,");
			args.add(bni.getBkName());
		}
		if(StringUtils.isNotBlank(bni.getBkNo())){
			sql.append(" bk_no = ?,");
			args.add(bni.getBkNo());
			sql.append(" city_id = ?,");
			String cityId = null;
			try {
				cityId = bni.getBkNo().substring(3,7);//联行号第4-7位为城市编号
			} catch (Exception e) {
				logger.info(e.getMessage(),e);
			}
			args.add(cityId);
			//根据cityId查询area_code表获取provId;
			sql.append(" prov_id = ?,");
			String provId = null;
			if(StringUtils.isNotBlank(cityId)){
				String sql1 = "SELECT attr_prov_no FROM area_code WHERE attr_city_no = "+Ryt.addQuotes(cityId);
				provId = queryForString(sql1);
			}
			args.add(provId);
		}
		if(StringUtils.isNotBlank(bni.getGid())){
			sql.append(" gid = ?,");
			args.add(bni.getGid().substring(2, bni.getGid().length()));
		}
		sql.deleteCharAt(sql.length()-1);//删除末尾的','
		sql.append(" WHERE id = ?");
		args.add(bni.getId());
		int count = update(sql.toString(), args.toArray());
		saveOperLog("更新联行号信息", count==1?"更新成功":"更新失败");
		return count;
	}
	
	public int delById(Integer id){
		if(id == null){
			return 0;
		}
		String sql = "delete from bank_no_info where id = ?";
		int count = update(sql, new Object[]{id});
		saveOperLog("删除联行号", count==1?"删除成功":"删除失败");
		return count;
	}
	
	public int add(BankNoInfo bni){
		String sql = "insert into bank_no_info (bk_no,prov_id,city_id,bk_name,gid,type) values (?,?,?,?,?,?)";
		String cityId = null;
		try {
			cityId = bni.getBkNo().substring(3,7);//联行号第4-7位为城市编号
		} catch (Exception e) {
			logger.info(e.getMessage(),e);
		}
		//根据cityId查询area_code表获取provId;
		String provId = null;
		if(StringUtils.isNotBlank(cityId)){
			String sql1 = "SELECT attr_prov_no FROM area_code WHERE attr_city_no = "+Ryt.addQuotes(cityId);
			provId = queryForString(sql1);
		}
		Object[] args = new Object[]{bni.getBkNo(),provId,cityId,bni.getBkName(),bni.getGid(),bni.getType()};
		int count = update(sql, args);
		saveOperLog("添加联行号", count==1?"添加成功":"添加失败");
		return count;
	}
}

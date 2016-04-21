package com.rongyifu.mms.dao;

import java.sql.Types;
import java.util.List;


//import org.apache.poi.hssf.record.formula.functions.T;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;

import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;

@SuppressWarnings("unchecked")
public class MerFeeDao extends PubDao {

//	private JdbcTemplate jt = getJdbcTemplate();

	public Minfo getMinfo(String id) {
		String sql = "select * from minfo where id = " + id;
		return queryForObject(sql,Minfo.class);
	}

	public void addFeeCalcModel(String mid, Integer gate,String model){
		String sql = "insert into fee_calc_mode (mid,gate,calc_mode,trans_mode) ";
		sql += "values(" +Ryt.sql( mid) + "," + gate + "," + Ryt.addQuotes(Ryt.sql(model)) + ")";
		try {
			update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addOneBank(String mid, String bid, String fee, int base_fee, int min_fee, int max_fee) throws Exception {
		Object[] obj = new Object[] { Ryt.sql(mid), Ryt.sql(bid), Ryt.sql(fee), base_fee, min_fee, max_fee };
		int[] type = new int[] { Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.INTEGER,
				Types.INTEGER };
		if (queryForInt("select count(*) from fee_calc_mode where mid=" + mid + " and gate=" + bid) > 0) {
			update("update fee_calc_mode set base_fee=" + (base_fee) + ",min_fee=" + (min_fee) + ",max_fee="
							+ (max_fee) + ",calc_mode=" + fee + " where mid =" + mid + " and gate=" + bid);
		} else {
			update("insert into fee_calc_mode (mid ,gate,calc_mode,base_fee,min_fee,max_fee) values (?,?,?,?,?,?)",
				obj, type);
		}
	}

	public void addCalcModel(String mid, String model, String editedID, String addedID) throws Exception {	
		model=Ryt.sql(model);
		editedID=Ryt.sql(editedID);
		addedID=Ryt.sql(addedID);
		if (editedID != null && !editedID.equals("")) {
			for (String gate : editedID.trim().split(",")) {
				update("update fee_calc_mode set calc_mode = '" + model + "',trans_mode="+ gate.split(":")[1]+ 
					" where mid = '"+ mid+"' and gate = "+ gate.split(":")[0]);
			}
		}
		if (addedID != null && !addedID.equals("")) {
			for (String gate : addedID.trim().split(",")) {
				String sql = "insert into fee_calc_mode (mid ,gate,calc_mode,trans_mode) ";
				sql += "values('" + mid + "'," + Integer.parseInt(gate.split(":")[0]) + ",'" + model + "'," + gate.split(":")[1] + ")";
				update(sql);
			}
		}
	}


	public List<FeeCalcMode> searchGatesFee(String mid, int bank) {
		String sql = "select mid,gate,calc_mode from fee_calc_mode where mid ='"+ mid+"'";
		if (bank >0) {
			sql += " and gate = " + bank;
		}
		return query(sql,FeeCalcMode.class);
	}

//	@SuppressWarnings("unchecked")
//	public List<NewGate> getNoFixGate(String mid) {
//		String sql = "select * from gate_route where gate not in (select gate from fee_calc_mode where mid = " +Ryt.sql( mid)
//						+ ")";
//		// 排序
//		return jt.query(sql, new BeanPropertyRowMapper(NewGate.class));
//	}

	public List<FeeCalcMode> getFixGate(String mid) {

		String sql = "select * from fee_calc_mode where mid = " + Ryt.sql(mid);
		return query(sql, FeeCalcMode.class);
	}
}

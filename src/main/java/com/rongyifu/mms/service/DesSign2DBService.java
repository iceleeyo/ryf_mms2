package com.rongyifu.mms.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.MinfoH;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.LogUtil;

public class DesSign2DBService {
	
		/**
		 * 商户重要信息加密保存
		 * @return
		 */
		public boolean encMinfo2DB(){
			MerInfoDao infoDao=new MerInfoDao();
			List<MinfoH> minfos=infoDao.queryAllMinfoHs();
			return batchEncUpdate(minfos);
		}
		
		/**
		 * 商户重要信息解密保存
		 * @return
		 */
		public boolean decMinfo2DB(){
			MerInfoDao infoDao=new MerInfoDao();
			List<Minfo> minfos=infoDao.queryAllMinfos();
			return batchDecUpdate(minfos);
		} 
		
		/**
		 * DES批量加密商户重要信息
		 * @param list 商户集合
		 * @author yang.yaofeng
		 */
		public static boolean batchEncUpdate(final List<MinfoH> list){
			
			String sql="update minfo set id_no=? ,bank_acct=? ,tel0=? ,tel1=? ,tel2=? ," +
					"tel3=? ,tel4=? ,tel5=? ,cell0=? ,cell1=? ,cell2=? ,cell3=? ,cell4=? ," +
					"cell5=? ,pbk_acc_no=?  where id=?";
			try {
				new PubDao<T>().getJdbcTemplate().batchUpdate(sql,
		                new BatchPreparedStatementSetter() {

		            @Override
		            public int getBatchSize() {
		                return list.size();
		            }

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setObject(1, list.get(i).getIdNo());
						ps.setObject(2, list.get(i).getBankAcct());
						ps.setObject(3, list.get(i).getTel0());
						ps.setObject(4, list.get(i).getTel1());
						ps.setObject(5, list.get(i).getTel2());
						ps.setObject(6, list.get(i).getTel3());
						ps.setObject(7, list.get(i).getTel4());
						ps.setObject(8, list.get(i).getTel5());
						ps.setObject(9, list.get(i).getCell0());
						ps.setObject(10, list.get(i).getCell1());
						ps.setObject(11, list.get(i).getCell2());
						ps.setObject(12, list.get(i).getCell3());
						ps.setObject(13, list.get(i).getCell4());
						ps.setObject(14, list.get(i).getCell5());
						ps.setObject(15, list.get(i).getPbkAccNo());
						ps.setObject(16, list.get(i).getId());
					}
		        });
				return true;
			} catch (Exception e) {
				LogUtil.printErrorLog("DesSign2DBService", "batchEncUpdate", e.getMessage());
				return false;
			}
			
		}
		
		/**
		 * DES批量解密商户重要信息
		 * @param list 商户集合
		 * @author yang.yaofeng
		 */
		public static boolean batchDecUpdate(final List<Minfo> list){
			String sql="update minfo set id_no=? ,bank_acct=? ,tel0=? ,tel1=? ,tel2=? ," +
					"tel3=? ,tel4=? ,tel5=? ,cell0=? ,cell1=? ,cell2=? ,cell3=? ,cell4=? ," +
					"cell5=? ,pbk_acc_no=?  where id=?";
			try {
				new PubDao<T>().getJdbcTemplate().batchUpdate(sql,
		                new BatchPreparedStatementSetter() {

		            @Override
		            public int getBatchSize() {
		                return list.size();
		            }

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setObject(1, list.get(i).getIdNo());
						ps.setObject(2, list.get(i).getBankAcct());
						ps.setObject(3, list.get(i).getTel0());
						ps.setObject(4, list.get(i).getTel1());
						ps.setObject(5, list.get(i).getTel2());
						ps.setObject(6, list.get(i).getTel3());
						ps.setObject(7, list.get(i).getTel4());
						ps.setObject(8, list.get(i).getTel5());
						ps.setObject(9, list.get(i).getCell0());
						ps.setObject(10, list.get(i).getCell1());
						ps.setObject(11, list.get(i).getCell2());
						ps.setObject(12, list.get(i).getCell3());
						ps.setObject(13, list.get(i).getCell4());
						ps.setObject(14, list.get(i).getCell5());
						ps.setObject(15, list.get(i).getPbkAccNo());
						ps.setObject(16, list.get(i).getId());
					}
		        });
				return true;
			} catch (Exception e) {
				LogUtil.printErrorLog("DesSign2DBService", "batchDecUpdate", e.getMessage());
				return false;
			}
		}
}

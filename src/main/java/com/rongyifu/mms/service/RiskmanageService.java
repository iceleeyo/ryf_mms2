package com.rongyifu.mms.service;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.io.FileTransfer;
import org.springframework.dao.DuplicateKeyException;

import com.rongyifu.mms.bean.CardAuth;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.Mlog;
import com.rongyifu.mms.bean.RealTran;
import com.rongyifu.mms.bean.RiskLog;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.RiskControlDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class RiskmanageService {
	RiskControlDao riskControlDao=new RiskControlDao();
	
	/**
	 * 可疑交易分页查询
	 * @param countSql
	 * @param sql
	 * @param pageNo
	 * @return
	 */
	public CurrentPage<Mlog> searchMlogs(int pageNo,String transtate, int tradetype,int begindate, int enddate, String otherId,
			int otherIdNum, boolean hasAmountNum, int amountNum) {
		//search_risk.jsp调用	
		return riskControlDao.searchMlogs(pageNo, transtate, tradetype, begindate, enddate, otherId, otherIdNum, hasAmountNum, amountNum);
	}
	/**
	 *  查询可疑交易详情
	 * @param queryStr
	 * @param key
	 * @return
	 */
	public List<Hlog> queryALogForRisk(String queryStr,String key){
		return riskControlDao.queryALogForRisk(queryStr,key);
		
	}
	
	/**
	 * @param month
	 * @return
	 * 批量添加白名单
	 */
	public String batchAddWhiteList(Integer month){
		String msg = "导入失败！";
		try {
			int count = riskControlDao.batchAddWhiteList(month);
            riskControlDao.saveOperLog("批量添加白名单", "批量添加白名单成功|month："+month);
			msg = "导入成功，共导入 "+count +" 条记录。";
		} catch (Exception e) {
			LogUtil.printErrorLog("RiskmanageService", "batchAddWhiteList", "month=" + month, e);
		}
		return msg;
	}
	
	public FileTransfer downloadTemplate(){
		ByteArrayOutputStream buffer=null;
		InputStream in = null;
		try {
			WebContext webContext = WebContextFactory.get(); 
			String realPath = webContext.getServletContext().getRealPath("/template/blackListBatchImportTemplate.xls");
			File f = new File(realPath);
			buffer = new ByteArrayOutputStream();
			in = new FileInputStream(f);
			buffer.write(in);
			return new FileTransfer("blackListBatchImportTemplate.xls", "application/x-xls", buffer.toByteArray());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.printErrorLog("RiskmanageService", "downloadTemplate","downloadTemplete failed", e);
			return null;
		}finally{
			try {
				if(in != null){
					in.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param fileTransfer 上传的excel
	 * @return
	 * 批量添加黑名单
	 */
	public String batchAddBlackList(FileTransfer file){
		try {
			String filename_Old = file.getName();// 文件名
			String extensions = filename_Old.substring(filename_Old.lastIndexOf("."));// 后缀名
			if (filename_Old.indexOf(".") <= 0||!extensions.equals(".xls")) {
				return "文件类型不正确！";
			}
			List<Integer> notValidList = new ArrayList<Integer>();
			List<CardAuth> list = parseXls(file,notValidList);
			int count=0;
			if(CollectionUtils.isNotEmpty(list)){
				//需要给field fieldType加unique key
				count=riskControlDao.batchAddBlackList(list);
			}
			String msg = CollectionUtils.isNotEmpty(notValidList)?("第"+Arrays.toString(notValidList.toArray())+ " 条记录校验失败"):"";
            riskControlDao.saveOperLog("批量添加黑名单", "添加成功|成功导入条数："+count);
			return "成功导入 "+count+" 条记录\r\n"+msg;
		} catch(Exception e){
			LogUtil.printErrorLog("RiskmanageService", "batchAddBlackList", "filename=" + file.getName(), e);
			return  "导入失败！";
		}
	}
	
	/**
	 * 解析上传的excel 
	 * @param file	文件格式：fieldType field
	 * @return List<CardAuth>
	 * @throws BiffException
	 * @throws IOException
	 */
	private List<CardAuth> parseXls(FileTransfer file,List<Integer> notValidList) throws BiffException, IOException{
		List<CardAuth> list = new ArrayList<CardAuth>();
		Workbook book = null;
		book = Workbook.getWorkbook(file.getInputStream());
		Sheet sheet = book.getSheet(0);
		int rows = sheet.getRows();
		//第 1 行为模版的表头
		for (int i = 1; i < rows; i++) {
			boolean flag = false;
			try {
				Cell[] cells = sheet.getRow(i);
				if (cells.length>=2) {
					String field = cells[0].getContents().trim();//第一列 证件号
					String type = cells[1].getContents().trim();
					FieldTypes fieldType = StringUtils.isEmpty(type)?null:FieldTypes.getByCode(Short.valueOf(type));//第二列证件类型
					if (fieldType != null && (flag=validate(field,fieldType))) {
						CardAuth ca = new CardAuth();
						ca.setField(field);
						ca.setFieldType(fieldType.code);
						list.add(ca);
					}
					if(!flag){
						notValidList.add(i);
					}
				}
			} catch (NumberFormatException e) {
				LogUtil.printErrorLog("RiskmanageService", "parseXls", "filename=" + file.getName(), e);
			}
		}
		return list;
	}
	
	/**
	 *	fieldType：1、卡号 2、身份证号 3、手机号
	 */
	private enum FieldTypes{
		AccNo((short)1,"^[1-9]{1}[0-9]{9,29}$"),
		Id((short)2,"^(\\d{15}|\\d{17}[\\dxX])$"),
		Mobile((short)3,"^(13|14|15|17|18)\\d{9}$");
		
		FieldTypes(short code,String regExp){
			this.code = code;
			this.regExp = regExp;
		}
		short code;
		
		String regExp;
		
		public static FieldTypes getByCode(short code){
			for (FieldTypes f : FieldTypes.values()) {
				if (f.code == code) {
					return f;
				}
			}
			return null;
		}
	}

	private boolean validate(String field,FieldTypes fieldType){
		Pattern p = Pattern.compile(fieldType.regExp);
		Matcher m = p.matcher(field);
		boolean result = m.matches();
		return result;
	}
	
	/**
	 * 查询黑名单、白名单详细信息
	 * @param field
	 * @param date
	 * @param auth_type
	 * @return
	 */
	public CurrentPage<CardAuth> getBlackWhiteList(int pageNo,int fieldType,String field, int date, String auth_type) {
		//blacklist_manage.jsp中调用
		return riskControlDao.getBlackWhiteList(pageNo,fieldType,field, date, auth_type);
	}
	/**
	 *  删除黑名单、白名单详细信息
	 * @param id
	 * @return
	 */
	public String deleteList(Integer id) {
		//blacklist_manage.jsp中调用
		try {
			riskControlDao.deleteList(id);
            riskControlDao.saveOperLog("删除黑白名单记录", "删除成功|id:"+id);
		} catch (Exception e) {
			e.printStackTrace();
			return "删除失败!";
		}
		return "ok";
	}
	/**
	 *  新增黑名单、白名单
	 * @param field
	 * @param auth_type
	 * @return
	 */
	public String addList2(String field,int fieldType, int auth_type) {
		// blacklist_manage.jsp中调用
		// 判断数据是否已经存在
		int check = checkAuthtype(field);
		if (check != -1) {
			if (check == 0) {
				return "数据已存在黑名单中，不可重复添加！";
			} else {
				return "数据已存在白名单中，不可重复添加！";
			}
		}
		try {
			riskControlDao.addList(field,fieldType, auth_type);
            riskControlDao.saveOperLog("新增"+(check==0?"黑名单":"白名单"), "新增成功|field:"+field+"    fieldType:"+fieldType);
		} catch (Exception e) {
			e.printStackTrace();
			return "增加失败!";
		}
		return "ok";
	}
	/**
	 * 查询已存在的数据是在黑名单还是白名单中
	 * @param field
	 * @return
	 */
	private int checkAuthtype(String field) {
		//内部中调用
		int res = -1;
		try {
			res = riskControlDao.checkAuthtype(field);
		} catch (Exception e) {
			res = -1;
		}
		return res;
	}
	/**
	 * 增加录入的备注
	 * @param tseq
	 * @param remarks
	 * @return
	 */
	public String addRiskLog(Long tseq,String remarks){
		if(Ryt.empty(remarks)){
			return "请填写增加备注原因";
		}
		RiskLog log = new RiskLog();
		log.setTseq(tseq);
		log.setAddRemarks(remarks);
		log.setAddDate(DateUtil.today());
		log.setRstate(0);
		try {
		  int effcetRow = riskControlDao.addRiskLog(log);
			if(effcetRow==1){
                riskControlDao.saveOperLog("增加录入备注", "新增成功|tseq:"+tseq+" remarks:"+remarks);
				return AppParam.SUCCESS_FLAG;
			}else{
				return "操作失败！";
			}
		}catch (DuplicateKeyException e) {
			return "该订单已经录入！请不要重复录入。";
		} catch (Exception e) {
			return "操作失败！";
		}
	}
	/**
	 * 修改风险交易
	 * @param tseq
	 * @param remarks
	 * @param amt
	 * @param state
	 * @return
	 */
	public String editRiskLog(int tseq,String remarks,int amt,int state){
		//verify_risk.jsp中调用
		if(state<1 || state>4){
			return "数据传递有误";
		}
		int effectRow=riskControlDao.editRiskLog(tseq, remarks, amt, state);
		if(effectRow==1){
            riskControlDao.saveOperLog("修改风险交易", "修改成功|tseq:"+tseq+"  remarks:"+remarks+" amt:"+amt+" state:"+state);
			return AppParam.SUCCESS_FLAG;
		}else{
			return "操作失败!";
		}
	}
	/**
	 * 风险交易撤销
	 * @param tseq
	 * @param remarks
	 * @return
	 */
	public String editRiskLogC(int tseq,String remarks){
		int effectRow=riskControlDao.cancelRiskLog(tseq, remarks);
		return effectRow==1 ? AppParam.SUCCESS_FLAG : "操作失败!";
	}
	/**
	 * 删除RiskLog
	 * @param tseq
	 */
	public String removeRiskLog(String tseq){
		int effectRow=riskControlDao.removeRiskLog(tseq);
		return effectRow==1 ? AppParam.SUCCESS_FLAG : "操作失败!";
	}
	/**
	 * 查询风险交易
	 * @param rstate
	 * @param beginDate
	 * @param endDate
	 * @param tseq
	 * @return
	 */
	public List<RiskLog> queryRiskLog(String rstate,int beginDate,int endDate,String tseq){

		return riskControlDao.queryRiskLog(rstate, beginDate, endDate, tseq);
	}
	/**
	 * 可疑交易查询
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadReturn(Map<String, String> p) throws Exception {
		ArrayList<String[]> list = new ArrayList<String[]>();

		String state = p.get("transtate");
		int bdate_ = Integer.parseInt(p.get("begindate"));
		int edate_ = Integer.parseInt(p.get("enddate"));
		String other = p.get("other_id");
		int othernum_ = Integer.parseInt(p.get("other_id_num"));
		boolean isChecked =Boolean.parseBoolean(p.get("isChecked")) ;
		int amountnum = Integer.parseInt(p.get("amount_num"));
		int tradetype = Integer.parseInt(p.get("tradetype"));
		
		RiskControlDao riskControlDao = new RiskControlDao();
		List<Mlog> mlogs = riskControlDao.searchMlogList(state, tradetype,bdate_, edate_,other, othernum_, isChecked, amountnum);
		
		String[] title = { "序号","电银流水号", "商户号", "商户简称", "交易金额(元)", "交易日期", "卡号","交易类型",
				"证件号", "手机号", "交易状态" };
		list.add(title);
		Map<String, String> map = new HashMap<String, String>();
		map.put("3", "信用卡支付");
		map.put("11", "对私代付");
		map.put("12", "对公代付");
		map.put("15", "对私代扣");
		RYFMapUtil obj = RYFMapUtil.getInstance();
		int i=0;
		for (Mlog m : mlogs) {
			String[] s = new String[] { String.valueOf(i+1),m.getTseq() + "", m.getMid() + "",

			obj.getMerMap().get(m.getMid()), Ryt.div100(m.getPayAmount()),
					m.getSysDate() + "", Ryt.shadowAcc(m.getPayCard()),map.get(m.getTransType()+""),Ryt.shadowAcc(m.getPayId()),
					Ryt.shadowPhone(m.getMobileNo()), m.getTstat() == 2 ? "支付成功" : "支付失败" };
			list.add(s);
			i++;
		}
		String filename = "RISKMLOG_" + DateUtil.today() + ".xls";
		String name = "可疑交易表";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);
	}
	// 实时交易显示
	public RealTran queryRealTran() {

		RealTran rt = new RealTran();
		// 总成功交易笔数
		rt.setSuccesTranCount(riskControlDao.querySuccesCount());
		// 最近10笔交易记录
		rt.setSuccesTranList(riskControlDao.queryLastSuccTransList(10));
		// 所有失败交易
		List<Hlog> allFailTransList = riskControlDao.queryFailTransList();
		rt.setFailTranList(allFailTransList);
		return rt;
	}

}

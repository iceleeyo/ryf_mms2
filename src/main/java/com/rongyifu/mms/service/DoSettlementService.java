package com.rongyifu.mms.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import org.directwebremoting.io.FileTransfer;


import com.rongyifu.mms.bean.SettleHlog;
import com.rongyifu.mms.bean.SettleResultBean;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AccountDao;
import com.rongyifu.mms.settlement.*;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.LogUtil;

public class DoSettlementService {
	
	
	private static final int ORDER_ISNULL_OR_ISNOTSUCCESS = 0;
	private static final int ORDER_ISSETTLED = 1;
	private static final int ORDER_ISSUCCESS_BUTNOTSETTLE = 2;
	private static AccountDao dao = new AccountDao();

	/**
	 * 通过上传文件对账
	 */
	public SettleResultBean byUploadFileSettle(String bank, String fileContent) {
		SettleResultBean bean = new SettleResultBean();
		if (null == bank || null == fileContent) {
			bean.setErrMsg("对账失败，请检查输入参数是否正确!");
			return bean;
		}
		/*//如果是数字，从表中读取网关号
		if(Ryt.isDigit(bank)){
			form = dao.getGatesSettleFileForm(bank);
			if (form == null) {
				bean.setErrMsg("对账失败，" + bank + " 没有配置对账文件格式!");
				return bean;
			}
		}
		*/

		try {
			// 文件中对账数据
			List<SBean> dataList = getCheckDataFromFile(bank,fileContent);
			if (dataList == null) {
				bean.setErrMsg("对账失败，请检查上传的文件是否正确!");
				return bean;
			}
			if (dataList.size() == 0) {
				bean.setErrMsg("上传的对账文件没有对账数据!");
				return bean;
			}
			
			checkBankDate(dataList, bean);
			dao.saveOperLog("上传文件对账", "对账成功");
		} catch (Exception e) {
			e.printStackTrace();
			bean.setErrMsg("对账失败。" + e.getMessage());
		}
		
		return bean;
	}
	
	/**
	 * 通过上传文件对账（xls格式特殊处理,农行B2B）
	 */
	public SettleResultBean byUploadFileSettleXLS(String bank, FileTransfer fileTransfer) {
		SettleResultBean bean = new SettleResultBean();
		String filename_Old = fileTransfer.getName();// 文件名
		String filename_Old1 = null;
		/*try {
			System.out.println(new String(filename_Old.getBytes(),"utf-8"));
		} catch (Exception e) {
			// TODO: handle exception
		}*/
		try {
			 filename_Old1 =new String(filename_Old.getBytes(),"utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (filename_Old1.indexOf(".") <= 0) {
			bean.setErrMsg("请上传正确的文件！");
			return bean;
		}
		String extensions = filename_Old.substring(filename_Old
				.lastIndexOf("."));// 后缀名
		if (!extensions.equals(".xls")) {
			bean.setErrMsg("文件类型不正确！");
			return bean;
		}
		String fileContent=null;
		if("20004".equals(bank)){
			fileContent= analyzeFile(fileTransfer.getInputStream());
		}else if("30010".equals(bank)){
			fileContent= analyzeFileQBA(fileTransfer.getInputStream());
		}else{
			fileContent = parseXls(fileTransfer.getInputStream());
		}
		try {
			// 文件中对账数据
			List<SBean> dataList = getCheckDataFromFile(bank,fileContent);
			if (dataList == null) {
				bean.setErrMsg("对账失败，请检查上传的文件是否正确!");
				return bean;
			}
			if (dataList.size() == 0) {
				bean.setErrMsg("上传的对账文件没有对账数据!");
				return bean;
			}
			
			checkBankDate(dataList, bean);
			
		} catch (Exception e) {
			e.printStackTrace();
			bean.setErrMsg("对账失败。" + e.getMessage());
		}
		
		return bean;
	}
	
	/**
	 * 通过上传文件对账（中文特殊处理）
	 * @throws IOException 
	 */
	public SettleResultBean byUploadZWFileSettle(String bank, FileTransfer fileTransfer) throws IOException {
		SettleResultBean bean = new SettleResultBean();
			String fileContent=analZWeFile(fileTransfer.getInputStream());
			
		try {
			// 文件中对账数据
			List<SBean> dataList = getCheckDataFromFile(bank,fileContent);
			if (dataList == null) {
				bean.setErrMsg("对账失败，请检查上传的文件是否正确!");
				return bean;
			}
			if (dataList.size() == 0) {
				bean.setErrMsg("上传的对账文件没有对账数据!");
				return bean;
			}
			
			checkBankDate(dataList, bean);
			
		} catch (Exception e) {
			e.printStackTrace();
			bean.setErrMsg("对账失败。" + e.getMessage());
		}
		
		return bean;
	}

	/**
	 * 通过银行接口对账
	 * @param uploadFile
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public SettleResultBean byBKInterfaceSettle(String bank, Map<String, String> args) throws Exception {
		SettleResultBean bean = new SettleResultBean();
		if (null == bank || null == args) {
			bean.setErrMsg("对账失败，请检查输入参数是否正确!");
			return bean;
		}
		
		String className = dao.getClassNameById(bank);
		if(Ryt.empty(className)) {
			bean.setErrMsg("没有对账实现类！");
			return bean;
		}
		
		List<SBean> dataList = null;
		// 文件中对账数据
		try {
			Class calzz = Class.forName(className.trim());
			SettltData obj = (SettltData) calzz.newInstance();
			dataList = obj.getCheckData(bank,args);
		} catch (Exception e) {
			e.printStackTrace();
			bean.setErrMsg("对账失败。" + e.getMessage());
			return bean;
		}
		
		if (dataList == null) {
			bean.setErrMsg("对账失败，请检查输入是否正确!");
			return bean;
		}

		if (dataList.size() == 0) {
			bean.setErrMsg("没有找到对账数据!");
			return bean;
		}

		try {
			checkBankDate(dataList, bean);
		} catch (Exception e) {
			e.printStackTrace();
			bean.setErrMsg("对账失败。" + e.getMessage());
		}
		return bean;
	}

	/**
	 * 解析文件得到用来对账的数据
	 * @param bank
	 * @param form
	 * @param fileContent
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private List<SBean> getCheckDataFromFile(String bank,  String fileContent) {
		List<SBean> res = new ArrayList<SBean>();
			try {
				String className=dao.getClassNameById(bank);
				LogUtil.printInfoLog("DoSettlementService", "getCheckDataFromFile", "bank="+bank+" className=" + className.trim());
				Class calzz = Class.forName(className.trim());
				SettltData obj = (SettltData) calzz.newInstance();
				res = obj.getCheckData(bank,fileContent);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		return res;
	}
	
	public void checkBankData(List<SBean> dataList, SettleResultBean bean) throws Exception {
		checkBankDate( dataList, bean);
	}

	/**
	 * 对账操作 核心代码
	 * @param dataList
	 * @param bean
	 * @throws Exception
	 */
	private void checkBankDate(List<SBean> dataList, SettleResultBean bean) throws Exception {
		if (bean == null) bean = new SettleResultBean();
		// 已经对账
		int finish = 0;
		// 对账异常交易
		int exception = 0;

		List<SettleHlog> success = new ArrayList<SettleHlog>();// 对账成功记录
		List<SettleHlog> suspect = new ArrayList<SettleHlog>();// 可疑交易记录
		List<SettleHlog> fail = new ArrayList<SettleHlog>();// 失败交易记录
        
		List<String> sqlList = new ArrayList<String>(); // 对账完需要执行的sql
		
		//pre_amt1 int 0,*100
		
		for (SBean element : dataList) {
			SettleHlog h = dao.getSettleHlog(element);
			if (null != h) {
				if (h.getGid() == null || h.getGid() == 0 )  throw new Exception("流水号为" + h.getTseq() + "，支付渠道为空！");
				/*增值业务SBean中没有tseq的值 ，不能删掉*/
				element.setTseq(String.valueOf(h.getTseq()));
//				h.setBkSeq(element.getTseq());
				h.setBkAmount(element.getAmt());
				if (element.getTseq() != null) h.setBkSeq(element.getTseq());
				else if (element.getMerOid() != null) h.setBkSeq(element.getMerOid());
				else if (element.getBkSeq() != null) h.setBkSeq(element.getBkSeq());
//				else h.setBkSeq("");
				else{};
			}
			
			int flag = getFalgValue(h);
			//System.out.println(Integer.parseInt(Ryt.mul100(element.getAmt())));
			// 已经对账了
			if (flag == ORDER_ISSETTLED) finish = finish + 1;
			// 校验失败 系统不存在该条数据（可疑交易）
			if (flag == ORDER_ISNULL_OR_ISNOTSUCCESS) {
				if (dao.errorCheckHandle(3, element).length == 0) {
					exception = exception + 1;
				} else {
					SettleHlog nullhlog = new SettleHlog();
					nullhlog.setBkAmount(element.getAmt());
					if (element.getTseq() != null) nullhlog.setBkSeq(element.getTseq());
					else if (element.getMerOid() != null) nullhlog.setBkSeq(element.getMerOid());
					else if (element.getBkSeq() != null) nullhlog.setBkSeq(element.getBkSeq());
					else{};
//					else nullhlog.setBkSeq("");
					suspect.add(nullhlog);
				}
			}
			// 数据库中存在该订单且未对账
			if (flag == ORDER_ISSUCCESS_BUTNOTSETTLE) {
//				String gate = element.getGate();
				int dbAmt = h.getPayAmt();
				/**
				 * 如果pay_amt ==0  说明是之前的 数据，更新改数据为 交易金额
				 */
				if(dbAmt==0){
					sqlList.add("update hlog set pay_amt = " + h.getAmount() + " where tseq = " + h.getTseq());	
					executeBatchSql(sqlList, false);
					
					dbAmt = h.getAmount();
				}
				
				// 校验成功// 银行金额X100处理
				if (dbAmt == Integer.parseInt(Ryt.mul100(element.getAmt()))) {
					String bankFee = "0";
					/* 计算银行手续费 */
					String bkFeeModel=h.getBkFeeModel();
					if(h.getBkFeeModel()==null||h.getBkFeeModel().equals("")){
						 bkFeeModel = dao.getBkFeeModel(h.getGate(), h.getGid() );
					}
					
					if(bkFeeModel != null && bkFeeModel.indexOf("X") == 0)
						bkFeeModel = bkFeeModel.replaceFirst("X","");
					
					// 计算银行手续费
					bankFee = calcBankFee(bkFeeModel, element.getAmt(),element.getBkFee());
					
					/*修改对账标识为对账成功，并且更新银行手续费*/					
					sqlList.add("update hlog set bk_chk = 1,p10 = "+element.getDate()+",bank_fee = "+ bankFee + " where tseq = " + h.getTseq());
					executeBatchSql(sqlList, false);
					
					//TODO/* 增加存管数据 */
					//dao.createCGData(h);
					success.add(h);

				} else {
					// 校验失败 字段bk_chk =2金额不符
					if (dao.errorCheckHandle(2, element).length == 0) {
						exception = exception + 1;
					} else {
						h.setBkChk(2);

						if (element.getMerOid() != null)
							h.setBkSeq(element.getMerOid());
						else if (element.getTseq() != null)
							h.setBkSeq(element.getTseq());

						// else h.setBkSeq("");
						else {
						}
						;
						fail.add(h);
					}
				}
			}
		}
		
		executeBatchSql(sqlList, true);
		
		bean.setTotal(dataList.size());
		bean.setFlag(true);
		bean.setSuspect(suspect);
		bean.setSuccess(success);
		bean.setFail(fail);
		bean.setException(exception);
		bean.setFinish(finish);
	}
    
	/**
	 * 批量执行sql
	 * @param sqlList
	 * @param isImmExec  是否马上执行sql，如果值为false，则需达到一定数量时执行
	 * @throws Exception
	 */
	private void executeBatchSql(List<String> sqlList, boolean isImmExec) throws Exception{
		if (sqlList != null && sqlList.size() > 0) {
			if (sqlList.size() >= 100 || isImmExec) {
				dao.batchSqlTransaction2(sqlList);
				sqlList.clear();
			}
		}
	}
	
	/**
	 * 计算银行手续费（单位：分）
	 * @param bkFeeModel	手续费公式
	 * @param amt			交易金额
	 * @param defaultBkFee	默认值
	 * @return 
	 */
	private String calcBankFee(String bkFeeModel, String amt, String defaultBankFee){
		double amount = Double.parseDouble(amt);
		String bankFee = Ryt.mul100(ChargeMode.reckon(bkFeeModel, String.valueOf(Math.abs(amount)), defaultBankFee));
		if(amount >= 0)
			return bankFee;
		else 
			return "-" + bankFee;
	}
	
	/**
	 * @param h
	 * @return 
	 * 0-> 不存在成功交易（交易不存在，或者是交易存在但是状态为不成功）
	 * 1->已经对完帐
	 * 2-> 未对账
	 */
	private int getFalgValue(SettleHlog h) {
		if (h == null || h.getTstat() != 2) return ORDER_ISNULL_OR_ISNOTSUCCESS;
		if (h.getBkChk() != 0) return ORDER_ISSETTLED;// 已经对账
		if (h.getTstat() == 2 && h.getBkChk() == 0) return ORDER_ISSUCCESS_BUTNOTSETTLE;// 交易记录存在且未对账
		return ORDER_ISNULL_OR_ISNOTSUCCESS;
	}

	public String checkBkNo() {
		// 检查关联银行行号
		int aCount = dao.queryForInt("select count from gate_route where bf_bk_no = '' ");
		
		if(aCount>0)
		  return "有支付渠道 没有关联银行行号，请在支付渠道维护页面进行维护";
		 
		return "配置完成";
	}

	public String analyzeFile(InputStream input){
		StringBuffer fileContent=new StringBuffer();
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 获得第一个工作表对象
		Sheet sheet = book.getSheet(0);
		// 得到行数
		int rows = sheet.getRows();
		for (int i = 2; i < rows; i++) {
			String cell1=sheet.getCell(1, i).getContents().trim();//第一列第二行  企业客户代码
			String cell2=sheet.getCell(2, i).getContents().trim();
			String cell3=sheet.getCell(3, i).getContents().trim();
			String cell4=sheet.getCell(4, i).getContents().trim();
			String cell5=sheet.getCell(5, i).getContents().trim();
			String cell6=sheet.getCell(6, i).getContents().trim();
			String cell7=sheet.getCell(7, i).getContents().trim();
			String cell8=sheet.getCell(8, i).getContents().trim();
			String cell9=sheet.getCell(9, i).getContents().trim();
			String cell10=sheet.getCell(10, i).getContents().trim();
			String cell11=sheet.getCell(11, i).getContents().trim();
			String cell12=sheet.getCell(12, i).getContents().trim();
			String cell13=sheet.getCell(13, i).getContents().trim();
			String cell14=sheet.getCell(14, i).getContents().trim();
			String cell15=sheet.getCell(15, i).getContents().trim();
			fileContent.append(cell1).append("|");
			fileContent.append(cell2).append("|");
			fileContent.append(cell3).append("|");
			fileContent.append(cell4).append("|");
			fileContent.append(cell5).append("|");
			fileContent.append(cell6).append("|");
			fileContent.append(cell7).append("|");
			fileContent.append(cell8).append("|");
			fileContent.append(cell9).append("|");
			fileContent.append(cell10).append("|");
			fileContent.append(cell11).append("|");
			fileContent.append(cell12).append("|");
			fileContent.append(cell13).append("|");
			fileContent.append(cell14).append("|");
			fileContent.append(cell15);
			if(i!=rows-1)fileContent.append("\n");
		}
		return fileContent.toString();
	}
	
	/**
	 * 解析xls
	 * @param input
	 * @return
	 */
	public String parseXls(InputStream input){
		StringBuffer fileContent=new StringBuffer();
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 获得第一个工作表对象
		Sheet sheet = book.getSheet(0);
		// 得到行数
		int rows = sheet.getRows();
		// 得到列数
		int columns = sheet.getColumns();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				String cell = sheet.getCell(j, i).getContents().trim();
				fileContent.append(cell);				
				if(j != columns - 1)
					fileContent.append(";");
			}

			if (i != rows - 1)
				fileContent.append("\n");
		}
		return fileContent.toString();
	}
	
	/*青岛银行vas处理
	 * 
	 */
	public String analyzeFileQBA(InputStream input){
		StringBuffer fileContent=new StringBuffer();
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 获得第一个工作表对象
		Sheet sheet = book.getSheet(0);
		// 得到行数
		int rows = sheet.getRows();
		for (int i = 1; i < rows; i++) {
			String cell1=sheet.getCell(0, i).getContents().trim();
			String cell2=sheet.getCell(1, i).getContents().trim();
			String cell3=sheet.getCell(2, i).getContents().trim();
			String cell4=sheet.getCell(3, i).getContents().trim();
			String cell5=sheet.getCell(4, i).getContents().trim();
			String cell6=sheet.getCell(5, i).getContents().trim();
			String cell7=sheet.getCell(6, i).getContents().trim();
			fileContent.append(cell1).append("|");
			fileContent.append(cell2).append("|");
			fileContent.append(cell3).append("|");
			fileContent.append(cell4).append("|");
			fileContent.append(cell5).append("|");
			fileContent.append(cell6).append("|");
			fileContent.append(cell7);
			if(i!=rows-1)fileContent.append("\n");
		}
		return fileContent.toString();
	}
	
	public String analZWeFile(InputStream input) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(input,"gbk"));
		StringBuilder sb = new StringBuilder();
		String data=null;
		while ((data = br.readLine()) !=null) {
			sb.append(data);
			sb.append("\n");
		}
		br.close();
		return sb.toString();
		
	}
}

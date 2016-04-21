package com.rongyifu.mms.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.DaiKou;
import com.rongyifu.mms.bean.DaiKouBean;
import com.rongyifu.mms.common.BankNoUtil;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.GenerationOfBuckleDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.ParamUtil;


public class DaiKouService {
	private static GenerationOfBuckleDao daiKouDao= new GenerationOfBuckleDao();
	
	
	/**
	 * 单笔代扣获取订单信息
	 * @param dkAccNo 扣款人帐号
	 * @param dkAccName 扣款人户名
	 * @param dkBankNo 开户银行号
	 * @param dkIDType 扣款人证件类型
	 * @param dkIDNo 扣款人证件号
	 * @param dkKZType 卡折标志
	 * @param dkAmt 交易金额
	 * @param remark 备注
	 * @return 订单信息
	 * @throws Exception 
	 */
	public Map<String, String> getDBOrderIdInfo(String dkAccNo,String dkAccName,String dkBankNo,String dkIDType,
			String dkIDNo,Integer dkKZType,double dkAmt,String remark) throws Exception{
		Map<String, String> paramMap=new HashMap<String, String>();
		if(Ryt.empty(dkAccNo)||Ryt.empty(dkAccName)||Ryt.empty(dkBankNo)||Ryt.empty(dkIDNo)||Ryt.empty(dkAmt+"")){
			paramMap.put("error", "请检查必填信息填写是否为空!");
			return paramMap;
		}
		String bankName=BankNoUtil.getDaiKouResultMap("").get(dkBankNo);
		if(null==bankName){
			paramMap.put("error", "银行行号不存在,请重新填写!");
			return paramMap;
		}
		//手续费
//		Double feeAmt=10.00; 
		String orderId = Ryt.genOidBySysTime();//订单号
		paramMap.put("orderId", orderId);
		paramMap.put("dkAccNo", dkAccNo);
		paramMap.put("dkAccName", dkAccName);
		paramMap.put("dkBankName", bankName);
		paramMap.put("dkBankNo", dkBankNo);
		paramMap.put("dkIDType", dkIDType);
		paramMap.put("dkIDNo", dkIDNo);
		paramMap.put("dkKZType", dkKZType+"");
		paramMap.put("dkAmt", dkAmt+"");
		paramMap.put("remark", remark);
//		paramMap.put("feeAmt", feeAmt+"");
		return paramMap;
	}
	
	/**
	 * 保存单笔代扣订单信息（hlog、tr_orders）
	 * @param orderId
	 * @param dkAccNo
	 * @param dkAccName
	 * @param dkBankNo
	 * @param dkIDType
	 * @param dkIDNo
	 * @param dkKZType
	 * @param dkAmt
	 * @param remark
	 * @return
	 * @throws Exception
	 */
	public String saveOrderInfo(String orderId,String dkAccNo,String dkAccName,String dkBankNo,String dkIDType,
			String dkIDNo,Integer dkKZType,double dkAmt,String remark) throws Exception{
		if(Ryt.empty(dkAccNo)||Ryt.empty(dkAccName)||Ryt.empty(dkBankNo)||Ryt.empty(dkIDNo)||Ryt.empty(dkAmt+"")){
			return "请检查必填信息填写是否为空!";
		}
		String bankName=BankNoUtil.getDaiKouResultMap("").get(dkBankNo);
		if(Ryt.empty(bankName)){
			return  "银行行号不存在,请重新填写!";
		}
		int []flag=daiKouDao.saveOrderOfDBDaiKou(orderId, dkAccNo, dkAccName, dkBankNo, bankName, dkIDType, dkIDNo, dkKZType, dkAmt,remark);
		
		if(null!=flag&&flag.length==2)
			return "success";
		else
			return "订单录入失败!";
	}
	
	/**
	 * 批量代扣
	 * @param describe
	 * @param fileTransfer
	 * @return
	 * @throws Exception 
	 */
	public DaiKouBean uploadPLDKExcelFile(FileTransfer fileTransfer) throws Exception{
			
			String realPath =ParamUtil.getPropertie("upload_bak");
			String filename_Old = fileTransfer.getName();// 文件名
			String extensions = filename_Old.substring(filename_Old.lastIndexOf("."));// 后缀名
			/*
			 * 校验文件类型
			 */
			if (filename_Old.indexOf(".") <= 0) {
				DaiKouBean bean = new DaiKouBean();
				bean.setErrMsg("请上传正确的文件！");
				return bean;
			}
			if (!extensions.equals(".xls")) {
				DaiKouBean bean = new DaiKouBean();
				bean.setErrMsg("您上传的文件类型为"+extensions.substring(1)+",请上传xls类型文件！");
				return bean;
			}
			String batchNo=Ryt.crateBatchNumber();
			String name=daiKouDao.getLoginUserMid()+batchNo;//批次号
  			String fileName = name + extensions;
  			File file = new File(realPath, fileName);
  			if (!file.getParentFile().exists()) {
  				file.getParentFile().mkdirs();
  			}
  			/*
  			 * 保存文件
  			 */
  			byte[] bytes = new byte[1024 * 8];
  			FileOutputStream foutput = new FileOutputStream(file);
  			InputStream fis = fileTransfer.getInputStream();
  			int len = 0;
  			while ((len = fis.read(bytes)) > 0) {
  				foutput.write(bytes, 0, len);
  			}
  			foutput.flush();
  			foutput.close();
  			fis.close();
		return analysisFile(realPath + "/" + fileName,batchNo);
	}
	
	/**
	 * 解析excel文件
	 * @param filePath 文件路径
	 * @param batchNo 批次号
	 * @return
	 */
	private DaiKouBean analysisFile(String filePath,String batchNo){
		DaiKouBean bean = new DaiKouBean();
		List<DaiKou> sobj=new ArrayList<DaiKou>();//成功的记录
		List<DaiKou> fobj=new ArrayList<DaiKou>();//失败的记录
  		double allAmt = 0;// 付款总金额
  		Workbook book = null;
		try {
			book = Workbook.getWorkbook(new File(filePath));
			}
		  catch (Exception e) {
			  LogUtil.printErrorLog("DaiKouService", "analysisFile", "读取Excel文件出错，错误信息:"+e.getMessage());
		  }
			// 获得第一个工作表对象
			Sheet sheet = book.getSheet(0);
			int rows = sheet.getRows();
			if (rows <= 1) {
				bean.setErrMsg("请检查文件是否为空！");
				return bean;
			}
			int columns = sheet.getColumns();
			if (columns != 8) {
				bean.setErrMsg("列数不对,请检查文件！");
				return bean;
			}
			bean.setBatchNo(batchNo);
			int count=0;
			//代扣所有银行
			Map<String, String> allBank=BankNoUtil.getDaiKouResultMap("");
			for (int i = 1; i < rows; i++) {
				DaiKou daikou=new DaiKou();
				String dkAccNo = sheet.getCell(0, i).getContents().trim();//扣款人帐号
				String dkAccName= sheet.getCell(1, i).getContents().trim();//扣款人账户名
				String dkBankNo= sheet.getCell(2, i).getContents().trim();//行号
				String dkIDType=sheet.getCell(3, i).getContents().trim();//证件类型
				String dkIDNo =sheet.getCell(4, i).getContents().trim();;//证件号
				String dkKZType =sheet.getCell(5, i).getContents().trim();;//卡折标识
				String dkAmt =sheet.getCell(6, i).getContents().trim();;//金额
				String remark =sheet.getCell(7, i).getContents().trim();;//用途
				if(Ryt.empty(dkAccNo)&&Ryt.empty(dkAccName)&&Ryt.empty(dkBankNo)&&Ryt.empty(dkIDType)&&
						Ryt.empty(dkIDNo)&&Ryt.empty(dkKZType)&&Ryt.empty(dkAmt))
					continue;
				count++;
				if(Ryt.empty(dkAmt) || !Ryt.isMoney(dkAmt))
					daikou.setErrMsg("金额错误");
				if(Ryt.empty(dkKZType) || (!"0".equals(dkKZType) && !"1".equals(dkKZType)))
					daikou.setErrMsg("卡折标识错误");
				if(Ryt.empty(dkIDNo) || !Ryt.isNumber(dkIDNo.substring(0, dkIDNo.length()-1)))
					daikou.setErrMsg("身份证号码错误");
				if(Ryt.empty(dkIDType) || dkIDType.length()!=2 || !Ryt.isNumber(dkIDType) || Integer.parseInt(dkIDType)>6)
					daikou.setErrMsg("证件类型错误");
				if(Ryt.empty(dkBankNo))
					daikou.setErrMsg("银行号为空");
				if(Ryt.empty(allBank.get(dkBankNo)))
					daikou.setErrMsg("银行号不存在");
				if(Ryt.empty(dkAccName))
					daikou.setErrMsg("账户名为空");
				if(Ryt.empty(dkAccNo) || !Ryt.isNumber(dkAccNo))
					daikou.setErrMsg("卡号为空或不是数字");
				daikou.setDkAccNo(Ryt.sql(dkAccNo));
				daikou.setDkAccName(Ryt.sql(dkAccName));
				daikou.setDkBankNo(Ryt.sql(dkBankNo));
				daikou.setDkAmt(Double.parseDouble(dkAmt));
				daikou.setDkIDNo(Ryt.sql(dkIDNo));
				daikou.setDkIDType(Short.parseShort(dkIDType));
				daikou.setDkKZType(Short.parseShort(dkKZType));
				daikou.setRemark(Ryt.empty(remark)?"":Ryt.sql(remark));
				//校验通过的记录
				if(Ryt.empty(daikou.getErrMsg())){
					allAmt=allAmt+Double.parseDouble(dkAmt);
					sobj.add(daikou);
				}else{
					fobj.add(daikou);
				}
			}
			bean.setSum_lines(count);
			bean.setSum_amt(allAmt);
			if(fobj.size()>0){
				bean.setFlag(false);
				bean.setFobjs(fobj);
			}
			else{
				bean.setFlag(true);
				bean.setSobjs(sobj);
			}
		return bean;
	}
	
	/**
	 * 批量代扣确认提交
	 * @param bathchNo 批次号
	 * @param orders
	 * @return
	 * @throws Exception
	 */
	public String submitOrder(String bathchNo,List<DaiKou> orders) throws Exception{
		String mid = daiKouDao.getLoginUserMid();// 商户ID的
		String[]  sqls=createBatchSqlForPLDK(mid, bathchNo, orders);
		int[] flag=daiKouDao.batchSqlTransaction(sqls);
		if(sqls.length!=flag.length)
			return "fail";
		else
		return "success";
	}
	
	/**
	 * 批量生成代扣sql
	 * @param mid 商户号
	 * @param bathchNo 批次号
	 * @param orders 订单
	 * @return
	 */
	private static String[] createBatchSqlForPLDK(String mid,String bathchNo,List<DaiKou> orders){
		String[] sqls=new String[orders.size()*2];
		int date = DateUtil.today();
		int time= DateUtil.getCurrentUTCSeconds();
		AccInfos accinfo=daiKouDao.queryJSZHYE(mid);
  		// 余额相应减少，将订单批量写入数据
  		for (int i = 0; i < orders.size(); i++) {
  			String tseq=System.currentTimeMillis() + Ryt.createRandomStr(6);
  			DaiKou order=orders.get(i);
  			//保存到数据库的交易金额乘以100
  			String saveAmount=Ryt.mul100(order.getDkAmt()+"");
  			StringBuffer hlogSql=new StringBuffer("insert into tlog(tstat,version,ip,gate,mdate,sys_date,init_sys_date,sys_time,mid,amount,type,pay_amt,p1,p2,p3,p4,p5,p6,p9,p7) values(");
  			hlogSql.append(Constant.PayState.WAIT_PAY+",");
  			hlogSql.append(10+",");//版本
  			hlogSql.append("3232251650,");//ip
  			hlogSql.append("4000,");//网关，随便填的 待修改
  			hlogSql.append(date+",");
  			hlogSql.append(date+",");
  			hlogSql.append(date+",");
  			hlogSql.append(time+",");
  			hlogSql.append(Ryt.addQuotes(mid)+",");
  			hlogSql.append(saveAmount+",");
  			hlogSql.append(Constant.TlogTransType.PROXY_PAYABLE_PRIVATE+",");
  			hlogSql.append(saveAmount+",");
  			hlogSql.append(Ryt.addQuotes(order.getDkAccNo())+",");
  			hlogSql.append(Ryt.addQuotes(order.getDkAccName())+",");
  			hlogSql.append(Ryt.addQuotes(order.getDkBankNo())+",");
  			hlogSql.append(Ryt.addQuotes(order.getDkIDType()+"")+",");
  			hlogSql.append(Ryt.addQuotes(order.getDkIDNo())+",");
  			hlogSql.append(Ryt.addQuotes(order.getDkKZType()+"")+",");
  			hlogSql.append(Ryt.addQuotes(bathchNo)+",");
  			hlogSql.append(Ryt.addQuotes(Ryt.sql(order.getRemark()))+")");
  			StringBuffer trorderSql=new StringBuffer("insert into tr_orders(oid,uid,aid,aname,init_time,ptype,gate,trans_amt," +
  					"pay_amt,to_acc_name,to_acc_no,to_bk_name,to_bk_no,trans_flow,remark) values(");
  			trorderSql.append(Ryt.addQuotes(tseq)+",");
  			trorderSql.append(Ryt.addQuotes(mid)+",");
  			trorderSql.append(Ryt.addQuotes(mid)+",");
  			trorderSql.append(Ryt.addQuotes(accinfo.getAname())+",");
  			trorderSql.append(DateUtil.getIntDateTime()+",");
  			trorderSql.append(Constant.DaiFuTransType.PROXY_PAYABLE_PRIVATE+",");
  			trorderSql.append("4000,");
  			trorderSql.append(saveAmount+",");
  			trorderSql.append(saveAmount+",");
  			trorderSql.append(Ryt.addQuotes(order.getDkAccName())+",");
  			trorderSql.append(Ryt.addQuotes(order.getDkAccNo())+",");
  			trorderSql.append(Ryt.addQuotes(BankNoUtil.getDaiKouResultMap("").get(order.getDkBankNo()))+",");
  			trorderSql.append(Ryt.addQuotes(order.getDkBankNo())+",");
  			trorderSql.append(Ryt.addQuotes(bathchNo)+",");
  			trorderSql.append(Ryt.addQuotes(Ryt.sql(order.getRemark()))+")");
  			sqls[i*2]=hlogSql.toString();
  			sqls[i*2+1]=trorderSql.toString();
  		}
		return sqls;
	}
}

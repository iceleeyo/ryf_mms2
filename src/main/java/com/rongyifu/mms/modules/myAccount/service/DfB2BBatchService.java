package com.rongyifu.mms.modules.myAccount.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.xml.security.utils.Base64;
import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.BatchB2B;
import com.rongyifu.mms.bean.DaiFu;
import com.rongyifu.mms.bean.DaiFuBean;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.common.BankNoUtil;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.SqlGenerator;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.modules.myAccount.dao.DfB2BBatchDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.ParamUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class DfB2BBatchService extends MyAccountService{
	
	private DfB2BBatchDao dao = new DfB2BBatchDao();
	
	/**
     * 下载省份编码列表
     * @return
     * @throws Exception
     */
    public FileTransfer downloadProvId() throws Exception {
			Map<Integer,String> map=RYFMapUtil.getProvMap();
			StringBuffer sheet = new StringBuffer("省份号,省份名\r\n");
			for(Map.Entry<Integer, String> entry:map.entrySet()){ 
				sheet.append(entry.getKey()).append(",").append(entry.getValue());
				sheet.append("\r\n");
			}
			String filename = "Prov.txt";
			return new DownloadFile().downloadTXTFile(sheet.toString(), filename);
    }
    /**
     * 下载银行编号列表
     * @return
     * @throws Exception
     */
    public FileTransfer downloadPubDFBkNo() throws Exception {
		Map<String,String> map=BankNoUtil.getPubDaifuMap();
		StringBuffer sheet = new StringBuffer("银行号,银行名\r\n");
		for(Map.Entry<String, String> entry:map.entrySet()){ 
			sheet.append(entry.getKey()).append(",").append(entry.getValue());
			sheet.append("\r\n");
		}
		String filename = "BkNo.txt";
		return new DownloadFile().downloadTXTFile(sheet.toString(), filename);
    }
    /**
     * 下载上传模板
     * @return
     */
    public FileTransfer downloadXLSFileBase(){
  		List<String[]> list=new ArrayList<String[]>();
  		String filename="example.xls";//收款方开户账号名、收款方银行编号、收款方银行账号、订单金额、收款方开户银行省份编号，收款方联行行号
  		String head[]={"收款方开户账号名","收款方银行编号","收款方银行账号","订单金额","收款方开户银行省份编号","收款方联行行号"};
  		String a[]={"张三","72001","6222600810065058594","10000","310","104163708432"};
  		String b[]={"张四","72002","6222600810065058595","20000","310","102100099996"};
  		list.add(head);
  		list.add(a);
  		list.add(b);
  		FileTransfer ft=null;
  		try {
  			ft=new DownloadFile().downloadXLS(list, filename);
  		} catch (Exception e) {
  			LogUtil.printErrorLog("DfB2BBatchService", "downloadXLSFileBase", "", e);
  		}
  		return ft;
  	}
    
    /**
  	 * 批量付款到企业
  	 * @throws Exception 
  	 */
  	public DaiFuBean batchForExcel(String describe, FileTransfer fileTransfer) throws Exception {
  		AccInfos accInfos=dao.queryAccount(dao.getLoginUserMid());
  		DaiFuBean daifu = new DaiFuBean();
  		if(accInfos.getState()!=1){
			daifu.setErr("该账户非正常状态!");
			return daifu;
  		}
  		try {
  			String realPath =ParamUtil.getPropertie("upload_bak");
  			String filename_Old = fileTransfer.getName();// 文件名
  			if (filename_Old.indexOf(".") <= 0) {
  				daifu.setErr("请上传正确的文件！");
  				return daifu;
  			}
  			String extensions = filename_Old.substring(filename_Old.lastIndexOf("."));// 后缀名
  			if (!extensions.equalsIgnoreCase(".xls")) {
  				daifu.setErr("文件类型不正确！");
  				return daifu;
  			}
  			String name=dao.getLoginUserMid()+Ryt.crateBatchNumber();
  			String fileName = name + extensions;
  			File file = new File(realPath, fileName);
  			if (!file.getParentFile().exists()) {
  				file.getParentFile().mkdirs();
  			}
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
  			// 文件上传成功 开始解析
  			return analysisExcel(realPath + "/" + fileName, describe, accInfos);
  		} catch (Exception e) {
  			LogUtil.printErrorLog("DfB2BBatchService", "batchForExcel", "", e);
  			
  			daifu.setErr("上传失败!");
  			return daifu;
  		}
  	}
  	
  	/**
  	 * 解析Excel文件
  	 * 
  	 * @param filepath
  	 *            文件路径
  	 */
  	public DaiFuBean analysisExcel(String filepath, String describe, AccInfos accInfos){
  		DaiFuBean daiFuBean = new DaiFuBean();
  		List<DaiFu> flist = new ArrayList<DaiFu>();
  		List<DaiFu> slist = new ArrayList<DaiFu>();
  		DaiFu daifu = null;
  		double orderAmt = 0;// 订单金额
  		double feeAmt = 0;// 手续费
  		double allrAmt = 0;// 付款总金额
  		String batchNumber = Ryt.crateBatchNumber();// 批次号
  		try {
  			Workbook book = Workbook.getWorkbook(new File(filepath));
  			// 获得第一个工作表对象
  			Sheet sheet = book.getSheet(0);
  			// 得到行数
  			int rows = sheet.getRows();
  			if (rows <= 1) {
  				daiFuBean.setErr("请检查文件是否为空！");
  				daiFuBean.setFlag(false);
  				return daiFuBean;
  			}
  			// 得到列数
  			int columns = sheet.getColumns();
  			if (columns != 6) {
  				daiFuBean.setErr("列数不对,请检查文件！");
  				daiFuBean.setFlag(false);
  				return daiFuBean;
  			}
			// 手续费计算公式
			LoginUser loginUser = dao.getLoginUser();
			String uid = loginUser.getMid();// 商户ID
			if (accInfos.getState() != 1) {
				daiFuBean.setErr("该账户非正常状态！");
  				daiFuBean.setFlag(false);
  				return daiFuBean;
			}
  			//计算手续费
  			boolean flag = false;
  			int counts=0;//有效数据总条数
  			Map<String, FeeCalcMode> feeCalcModeMap = new HashMap<String, FeeCalcMode>();
  			for (int i = 1; i < rows; i++) {
  				// 实例化一个Bean
  				daifu = new DaiFu();
  				// 以下进行一系列取值以及判断
  				String province = sheet.getCell(4, i).getContents().trim();// 收款方开户银行省份
  				String gateId = sheet.getCell(1, i).getContents().trim();// 收款方银行名
  				String orderMoney = sheet.getCell(3, i).getContents().trim();// 订单金额
  				String bankNo = sheet.getCell(2, i).getContents().trim();// 收款方银行账号
  				String accName = sheet.getCell(0, i).getContents().trim();// 得到第一列第一行的数据
  				String openbankno = sheet.getCell(5, i).getContents().trim();// 收款方联行行号
  				if(Ryt.empty(province)&&Ryt.empty(gateId)&&Ryt.empty(orderMoney)&&Ryt.empty(bankNo)&&
  						Ryt.empty(accName)&&Ryt.empty(openbankno))															
  						continue;
  				counts++;
  					
  				// 收款方开户账号名
  				if (!Ryt.isNumber(province)
  						|| !provInMap(Integer.parseInt(province))) {
  					daiFuBean.setFlag(flag);
  					daifu.setErrMsg("省份ID填写错误");
  				}
  				// 收款方联行行号
  				if (!Ryt.isNumber(openbankno)) {
  					daiFuBean.setFlag(flag);
  					daifu.setErrMsg("联行行号填写错误");
  				}
  				if (!isAmt(orderMoney)) {
  					daiFuBean.setFlag(flag);
  					daifu.setErrMsg("订单金额错误");
  				}
  				
  				FeeCalcMode feeCalcMode = null;
  				try{
  					if(feeCalcModeMap.containsKey(gateId))
  						feeCalcMode = feeCalcModeMap.get(gateId);
  					else {
  						feeCalcMode = dao.getFeeModeByGate(uid, gateId);
  						feeCalcModeMap.put(gateId, feeCalcMode);
  					}
	  				String feeMode = feeCalcMode.getCalcMode();
	  				feeAmt += Double.parseDouble(ChargeMode.reckon(feeMode, orderMoney));// 手续费
  				}catch(Exception e){
  					LogUtil.printErrorLog("DfB2BBatchService", "analysisExcel", "mid=" + uid + " gateId=" + gateId + " errorMsg=" + e.getMessage());
  					
	  				daiFuBean.setFlag(flag);
	  				daifu.setErrMsg(e.getMessage());
  				}
  				
  				if (!isAccNo(bankNo)) {
  					daiFuBean.setFlag(flag);
  					daifu.setErrMsg("银行帐号格式错误");
  				}
  				
  				if (!gateId.equals("")) {
  					if (!Ryt.isNumber(gateId)) {
  						daiFuBean.setFlag(flag);
  						daifu.setErrMsg("收款银行填写错误");
  					}
  				}
  				if (accName.equals("")) {
  					daiFuBean.setFlag(flag);
  					daifu.setErrMsg("账户名错误");
  				}
  				
  				// 判断该Bean为失败还是成功
  				if (daifu.getErrMsg() == null) {
  					orderAmt = orderAmt + Double.parseDouble(orderMoney);// 金额累加
  					daifu.setAccName(Ryt.sql(accName));// 账户名
  					daifu.setAccNo(Ryt.sql(bankNo));// 账户银行卡号
  					daifu.setTrAmt(orderMoney);// 订单金额
  					daifu.setBkNo(Ryt.sql(gateId));// 银行名
  					daifu.setOpenBkNo(openbankno);
  					daifu.setToProvId(Integer.parseInt(province));// 省份ID
  					daifu.setUse(Ryt.sql(describe));//设置备注
  					slist.add(daifu);
  				} else {
  					daifu.setErrProvId(province);
  					daifu.setAccName(Ryt.sql(accName));// 账户名
  					daifu.setAccNo(Ryt.sql(bankNo));// 账户银行卡号
  					daifu.setTrAmt(orderMoney);// 订单金额
  					daifu.setBkNo(Ryt.sql(gateId));// 银行名
  					daifu.setOpenBkNo(openbankno);
  					flist.add(daifu);
  				}
  			}
  			daiFuBean.setSum_lines(counts);
  			// 如果有失败数据则不做操作
  			if (flist.size() != 0) {
  				daiFuBean.setFobjs(flist);// 设置flist中的DaifBean
  			} else if (slist.size() != 0) {
  				allrAmt = orderAmt + feeAmt;
  				int count = slist.size();// 交易笔数
  				daiFuBean.setBkName("ok");
  				BatchB2B batch = new BatchB2B();
  				batch.setAllrAmt(allrAmt);
  				batch.setBatchNumber(batchNumber);
  				batch.setCount(count);
  				batch.setFeeAmt(feeAmt);
  				batch.setOrderAmt(orderAmt);
  				batch.setOrderDescribe(describe);
  				daiFuBean.setBatch(batch);
  				daiFuBean.setSobjs(slist);
  			}
  			book.close();
  		} catch (Exception e) {
  			LogUtil.printErrorLog("DfB2BBatchService", "analysisExcel", "批量处理异常："+e.getMessage(), e);
  			daiFuBean.setErr("服务器异常，请稍后重试！");
  			return daiFuBean;
  		}
  		
  		return daiFuBean;
  	}
  	
  	/**
	 * 查询账户余额
	 * 
	 * @param mid 商户号（用户ID）
	 * @return
	 */
	public AccInfos queryAccount(String mid) {
		return dao.queryAccount(mid);
	}
	
	/**
  	 * 检查用户余额支付条件是否通过
  	 * 
  	 * @param transAmt 应付金额
  	 * @return
  	 */
  	public String checkBalance(double transAmt, String payPwd) {
  		LoginUser loginUser = dao.getLoginUser();
  		String mid = loginUser.getMid();
  		AccInfos accinf = dao.queryAccount(mid);
  		String paypass = dao.getPass(mid);// 得到用户支付密码
  		if (accinf == null)
  			return "商户信息异常或商户不存在,无法进行支付！";
  		if (!paypass.equals(payPwd)) {
  			return "支付密码错误，请重试！";
  		}
  		if (accinf.getState() != 1)
  			return "账户状态异常，无法使用支付功能！";
  		if (Double.parseDouble(Ryt.div100(accinf.getBalance())) < transAmt)
  			return "账户余额不足，请充值后重试！";
  		else
  			return "ok";
  	}
  	
  	/**
  	 * 批量代付
  	 * @param batchNo
  	 * @param payPwd
  	 * @param data
  	 * @return 处理结果标识（0-处理成功；1-处理失败）|处理描述
  	 * @throws Exception
  	 */
	public String doActions(String batchNo, String payPwd, List<DaiFu> data) throws Exception{
		String mid = dao.getLoginUserMid();
		//校验 支付密码
		String oldPass = dao.getPass(mid); 
		if (!oldPass.equals(payPwd)) {
			dao.saveOperLog("批量付款到企业银行账户", "支付密码错误！");
			return "1|支付密码错误！";
		}
		
		int sysDate = DateUtil.today();
		int sysTime = DateUtil.now();
		long sumPayAmt = 0;
		Map<String, FeeCalcMode> gateMap = new HashMap<String, FeeCalcMode>();
		List<String> orderSqlList = new ArrayList<String>();
		for(DaiFu o : data){
			// 查询网关手续费和付款渠道
			String gateId = o.getBkNo();
			FeeCalcMode feeCalcMode = null;
			if(!gateMap.containsKey(gateId)){
				feeCalcMode = dao.getFeeModeByGate(mid, o.getBkNo());
				gateMap.put(gateId, feeCalcMode);
			} else
				feeCalcMode = gateMap.get(gateId);
			// 计算手续费
			String feeMode=feeCalcMode.getCalcMode();
			long transAmt = Ryt.mul100toInt(o.getTrAmt()); // 交易金额
			int transFee=(int) Ryt.mul100toInt(ChargeMode.reckon(feeMode,o.getTrAmt()));
			long payAmt = transAmt + transFee; // 付款金额
			sumPayAmt += payAmt; // 累计付款金额
			// 处理联行号
			int gid = feeCalcMode.getGid();
			String[] bkInfo=BankNoUtil.getBankNo(o.getOpenBkNo(),"", o.getBkNo(), o.getToProvId(), gid);
			String toBkNo=bkInfo[0];
//			String toBkName=bkInfo[1];
			// 生成订单号
			String oid = Ryt.genOidBySysTime();
			
			TlogBean order = new TlogBean();
			order.setVersion(Constant.VERSION);
			order.setIp(10191L);
			order.setSys_date(sysDate);
			order.setInit_sys_date(sysDate);
			order.setSys_time(DateUtil.getUTCTime(String.valueOf(sysTime)));
			order.setMid(mid);
			order.setMdate(sysDate);
			order.setOid(oid);
			order.setAmount(transAmt);
			order.setPay_amt(payAmt);
			order.setFee_amt(transFee);
			order.setType(Constant.TlogTransType.PAYMENT_FOR_PUBLIC);
			order.setGate(Integer.parseInt(o.getBkNo()));
			order.setGid(gid);
			order.setTstat(Constant.PayState.INIT);
			order.setIs_liq(Constant.IsLiq.NO); // 不参与结算
			order.setData_source(Constant.DataSource.TYPE_DFDKMMS); // 标识数据来源：管理平台发起的代付交易  
			order.setP1(Ryt.desEnc(o.getAccNo())+"|noDec"); // 收款账号
			order.setP2(o.getAccName()); // 收款户名
			order.setP3(toBkNo); // 联行号
			order.setP6(String.valueOf(o.getCardFlag())); // 卡/折标志
			// 用途：做base64编码
			if(!Ryt.empty(o.getUse()))
				order.setP7(Base64.encode(o.getUse().getBytes("UTF-8"))); // 用途 
			order.setP8(batchNo); // 交易批次号
			order.setP10(String.valueOf(o.getToProvId())); // 开户所在省份
			// 生成insert sql
			orderSqlList.add(SqlGenerator.generateInsertSql(order));
		}
		
		// 账务处理
		synchronized (lock) {
			AccInfos accInfos = dao.queryAccount(mid);
			if (accInfos.getState() != 1){
				dao.saveOperLog("批量付款到企业银行账户", "该账户非正常状态!");
				return "1|该账户非正常状态!";
			}
			
			if(sumPayAmt > accInfos.getBalance()){
				dao.saveOperLog("批量付款到企业银行账户", "账户余额不足!");
				return "1|账户余额不足!";
			}
			
			try{
				dao.insertOrder(orderSqlList, sumPayAmt, mid);
			} catch(Exception e){
				dao.saveOperLog("批量付款到企业银行账户", "批次号 " + batchNo + " 支付失败");
				LogUtil.printErrorLog("DfB2BBatchService", "doActions", "批次号 " + batchNo + " 支付失败", e);
				return "1|操作失败！交易批次号：" + batchNo;
			}
		}
		
		dao.saveOperLog("批量付款到企业银行账户", "批次号 " + batchNo + " 受理成功");
		return "0|操作成功！交易批次号：" + batchNo;
	}
  	
  	/**
	 * 根据商户号查找结算帐户
	 * @param uid 商户号
	 * @return
	 */
	public Map<String, String> getJSZHByUid(String uid) {
		return dao.getZHUid(uid);
	}
}

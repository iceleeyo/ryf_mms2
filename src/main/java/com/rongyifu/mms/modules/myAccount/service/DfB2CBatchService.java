package com.rongyifu.mms.modules.myAccount.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
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
import com.rongyifu.mms.common.BankNoUtil;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.SqlGenerator;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.modules.myAccount.dao.DfB2CBatchDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.ParamUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class DfB2CBatchService extends MyAccountService{
	
	private DfB2CBatchDao dao = new DfB2CBatchDao();
	
	/**
	 * 银行号，银行名称下载
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadBkNo() throws Exception {
			Map<String,String> map=BankNoUtil.getDaiFuGateMap();
			StringBuffer sheet = new StringBuffer("银行号,银行名\r\n");
			for(Map.Entry<String, String> entry:map.entrySet()){ 
				sheet.append(entry.getKey()).append(",").append(entry.getValue());
				sheet.append("\r\n");
			}
			String filename = "BkNo.txt";
			return new DownloadFile().downloadTXTFile(sheet.toString(), filename);
      }
	
    /**
     * 下载批量格式
     * @return
     * @throws Exception
     */
    public FileTransfer downloadBatch() throws Exception {
//			String content = "张三,6222600810065058595,313227000012,110,0,10000,发工资\r\n";
//			content +="李四,6222600810065058456,402641000014,120,0,10000,发工资\r\n";
//			content +="王五,6333600810065058342,301290000007,130,0,10000,发工资\r\n";
//			content +="赵六,6234605465065058432,308584000013,140,0,10000,发工资\r\n";
			List<String[]> list=new ArrayList<String[]>();
			String[] a5={"收款人户名","收款人账号","收款银行（银行对应表编号）","开户所在省份（省份对应表编号）","卡折标志（添代号：0代表卡,1代表存折）","交易金额","用途","收款方联行行号"};
			String[] a1={"张三","6222600810065058595","71001","110","0","10000","发工资","102100099996"};
			String[] a2={"李四","6222600810065058456","71002","120","0","10000","发工资","104163708432"};
			String[] a3={"王五","6333600810065058342","71003","130","0","10000","发工资","301290000007"};
			String[] a4={"赵六","6234605465065058432","71004","140","0","10000","发工资","301361000013"};
			list.add(a5);list.add(a1);list.add(a2);list.add(a3);list.add(a4);
			//String beginTitle = "收款人户名,收款人账号，收款银行，开户所在省份，卡折标志，交易金额，用途\r\n";
			String filename = "example.xls";
			return new DownloadFile().downloadXLS(list, filename);
    }
    
    public Map<String, String> getGates(){
		return BankNoUtil.getDaiFuGateMap();
	}
    
    /**
	 * 根据商户号查找结算帐户
	 * @param uid 商户号
	 * @return
	 */
	public Map<String, String> getJSZHByUid(String uid) {
		return dao.getZHUid(uid);
	}
   
	public DaiFuBean batchAction(String accId, FileTransfer fileTransfer)throws Exception {
		DaiFuBean res = new DaiFuBean();
		String realPath =ParamUtil.getPropertie("upload_bak");
		String filename_Old = fileTransfer.getName();// 文件名
		if (filename_Old.indexOf(".") <= 0) {
			DaiFuBean daifu = new DaiFuBean();
			daifu.setErr("请上传正确的文件！");
			return daifu;
		}
		String extensions = filename_Old.substring(filename_Old.lastIndexOf("."));// 后缀名
		if (!extensions.equals(".xls")) {
			DaiFuBean daifu = new DaiFuBean();
			daifu.setErr("文件类型不正确！");
			return daifu;
		}
		AccInfos acc = dao.queryAccount(accId);
		if (acc.getState() != 1) {
			res.setErr("该账户非正常状态！");
			return res;
		}
		res.setAcc(acc);
		String name = dao.getLoginUserMid() + Ryt.crateBatchNumber();
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

		Workbook book = Workbook.getWorkbook(new File(realPath, fileName));
		// 获得第一个工作表对象
		Sheet sheet = book.getSheet(0);
		// 得到列数
		int columns = sheet.getColumns();
		if (columns != 8) {
			res.setErr("列行数不对,请检查文件！");
			return res;
		}
		// 得到行数
		int rows = sheet.getRows();
		if (rows <= 1) {
			res.setErr("请检查文件是否为空！");
			return res;

		}

		List<DaiFu> sList = new ArrayList<DaiFu>();
		List<DaiFu> fList = new ArrayList<DaiFu>();
		List<String> lines = analysisExcel(sheet,rows);
		res.setSum_lines(lines.size());
		
	    long sumAmt = 0l;
	    String mid = dao.getLoginUserMid();
	    List<String> l=new ArrayList<String>();
	    Map<String, FeeCalcMode> feeCalcModeMap = new HashMap<String, FeeCalcMode>();
		for (String aLine : lines) {
			String[] data = aLine.split(",");
			if (data.length != 8){
				res.setErr("文件格式错误");
				return res;
			}else{
				sumAmt += initData(acc, sList, fList, data, mid, feeCalcModeMap);
			}
			l.add(data[1]);
		}
		double sum_amt=0;//交易金额
		double fee_amt=0;//手续费
		for(int i=0;i<sList.size();i++){
			sum_amt+=Double.parseDouble(sList.get(i).getTrAmt());
			fee_amt+=Double.parseDouble(sList.get(i).getTrFee());
		}
		DecimalFormat formater = new DecimalFormat("#0.##");
		res.setSum_amt(Double.parseDouble(formater.format(sum_amt)));
		res.setFee_amt(Double.parseDouble(formater.format(fee_amt)));
		if((sum_amt-fee_amt)>Double.parseDouble(Ryt.div100(String.valueOf(acc.getBalance())))){
			res.setErr("账户余额不足，请充值！");
			return res;
		}
		// 判断是否超月限额
		String month = String.valueOf(DateUtil.today()).substring(0,6);
		long useredAmt = dao.queryMonthDaiFuSumAmt(acc.getAid(),month);	
		if((sumAmt+useredAmt) >= acc.getAccMonthAmt()){
			StringBuffer m = new StringBuffer("超过限额.");
			m.append("本月已支付:").append(Ryt.div100(useredAmt)).append("元，");
			m.append("本次总金额:").append(Ryt.div100(sumAmt)).append("元");
			m.append("月限额为:").append(Ryt.div100(acc.getAccMonthAmt())).append("元");
			res.setErr(m.toString());
			return res;
		}
//		sList_=sList;
		BatchB2B batch = new BatchB2B();
		batch.setBatchNumber(Ryt.crateBatchNumber());
		res.setBatch(batch);
		res.setSobjs(sList);
		res.setFobjs(fList);
		res.setSumAmt(sumAmt);
		res.setFlag(true);
		return res;
   }
   
   private int initData(AccInfos acc, List<DaiFu> sList, List<DaiFu> fList, String[] data,String mid, Map<String, FeeCalcMode> feeCalcModeMap) throws Exception {
		//	收款人户名,收款人账号，收款银行，开户所在省份，卡折标志，交易金额，用途
		String accNo = data[1].trim();
		String bkNo=data[2].trim();
		
		Integer toProvId=Integer.parseInt(data[3].trim());
		short cardFlag=(short)Integer.parseInt(data[4].trim());
		String amt = data[5].trim();
		DaiFu df = new DaiFu();
		df.setAccName(data[0].trim());
		df.setAccNo(accNo);
		df.setBkNo(bkNo);
		df.setToProvId(toProvId);
		df.setCardFlag(cardFlag);
		df.setTrAmt(amt);
		df.setUse(data[6].trim());
		df.setOpenBkNo(data[7].trim());
		if(Ryt.empty(BankNoUtil.getDaiFuGateMap().get(bkNo))){
			df.setErr("银行号错误");
			fList.add(df);
			return 0;
		}
		if(!"0".equals(cardFlag+"")&&!"1".equals(cardFlag+"")){
			df.setErr("卡折标识错误");
			fList.add(df);
			return 0;
		}
		if(RYFMapUtil.getProvMap().get(toProvId)==null){
			df.setErr("省份错误");
			fList.add(df);
			return 0;
		}
		if(!isAccNo(accNo)){
			df.setErr("账户格式错误");
			fList.add(df);
			return 0;
		}
		
		if(!isAmt(amt)){
			df.setErr("金额格式错误");
			fList.add(df);
			return 0;
		}
		
		FeeCalcMode feeCalcMode = null;
		if(feeCalcModeMap.containsKey(bkNo))
			feeCalcMode = feeCalcModeMap.get(bkNo);
		else {
			try{
				feeCalcMode = dao.getFeeModeByGate(mid, bkNo);
			} catch(Exception e){
				LogUtil.printErrorLog("DfB2CBatchService", "initData", "mid=" + mid + " gateId=" + bkNo + " errorMsg=" + e.getMessage());
				df.setErr(e.getMessage());
				fList.add(df);
				return 0;
			}
			feeCalcModeMap.put(bkNo, feeCalcMode);
		}
		String feeMode=feeCalcMode.getCalcMode();
		
		int dbAmt = (int) Ryt.mul100toInt(amt);
		//计算代发的手续费
		df.setTrFee(ChargeMode.reckon(feeMode, amt));
		df.setFlag(true);
		sList.add(df);
		return dbAmt;
		
	}
   
   public List<String> analysisExcel(Sheet sheet,int rows)throws Exception{
		List<String> lines=new ArrayList<String>();
			for(int i=1;i<rows ;i++){
//				收款人户名,收款人账号，收款银行，开户所在省份，卡折标志，交易金额，用途\r\n";
				String to_accName=sheet.getCell(0, i).getContents().trim();
				String to_accNo=sheet.getCell(1, i).getContents().trim();
				String to_bk_no=sheet.getCell(2, i).getContents().trim();
				String to_prov_id=sheet.getCell(3, i).getContents().trim();
				String to_cardFlag=sheet.getCell(4, i).getContents().trim();
				String to_payAmt=sheet.getCell(5, i).getContents().trim();
				String to_priv=sheet.getCell(6, i).getContents().trim();
				String to_openbk_no=sheet.getCell(7, i).getContents().trim();
				if(Ryt.empty(to_accName)&&Ryt.empty(to_accNo)&&Ryt.empty(to_bk_no)&&Ryt.empty(to_prov_id)
						&&Ryt.empty(to_cardFlag)&&Ryt.empty(to_payAmt)&&Ryt.empty(to_priv)&&Ryt.empty(to_openbk_no))
					continue;
				lines.add(to_accName+","+to_accNo+","+to_bk_no+","+to_prov_id+","+to_cardFlag+","+to_payAmt+","+to_priv+","+to_openbk_no);
			}
			return lines;
	}
   
   /**
	 * 批量对私代付
	 * @param aid
	 * @param payPwd
	 * @param gate
	 * @param data
	 * @param trans_flow
	 * @return 处理结果标识（0-处理成功；1-处理失败）|处理描述
	 * @throws Exception
	 */
	public String doActions(String batchNo, String payPwd,List<DaiFu> data) throws Exception{
		String mid = dao.getLoginUserMid();
		//校验 支付密码
		String oldPass = dao.getPass(mid); 
		if (!oldPass.equals(payPwd)) {
			dao.saveOperLog("批量付款到个人银行账户", "支付密码错误！");
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
			order.setType(Constant.TlogTransType.PAYMENT_FOR_PRIVATE);
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
				dao.saveOperLog("批量付款到个人银行账户", "该账户非正常状态!");
				return "1|该账户非正常状态!";
			}
			
			if(sumPayAmt > accInfos.getBalance()){
				dao.saveOperLog("批量付款到个人银行账户", "账户余额不足!");
				return "1|账户余额不足!";
			}
			
			try{
				dao.insertOrder(orderSqlList, sumPayAmt, mid);
			} catch(Exception e){
				dao.saveOperLog("批量付款到个人银行账户", "批次号 " + batchNo + " 支付失败");
				LogUtil.printErrorLog("DfB2CBatchService", "doActions", "批次号 " + batchNo + " 支付失败", e);
				return "1|操作失败！交易批次号：" + batchNo;
			}
		}
		
		dao.saveOperLog("批量付款到个人银行账户", "批次号 " + batchNo + " 受理成功");
		return "0|操作成功！交易批次号：" + batchNo;
	}
}
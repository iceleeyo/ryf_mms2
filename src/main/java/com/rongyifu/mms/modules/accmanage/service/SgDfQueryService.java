package com.rongyifu.mms.modules.accmanage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.accmanage.dao.SgdfQueryDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class SgDfQueryService {
	private SgdfQueryDao dao=new SgdfQueryDao();
	public CurrentPage<OrderInfo> queryTlogInfo(Integer pageNo,Integer pageSize,String mid,String tseq,String batchNo,String ptype,Integer mstate,Integer againPayStatus,Integer bdate,Integer edate){
		
		return dao.queryTlogInfo(pageNo, pageSize, mid, tseq, batchNo, ptype, mstate, againPayStatus, bdate, edate);
	}
	
	
	public FileTransfer downSGDFData(String mid,String tseq,String batchNo,String ptype,Integer mstate,Integer againPayStatus,Integer bdate,Integer edate){
		List<OrderInfo> tlogInfos=dao.queryDownData(mid, tseq, batchNo, ptype, mstate, againPayStatus, bdate, edate);
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		 RYFMapUtil obj = RYFMapUtil.getInstance();
		Map<Integer,String> merMap=obj.getMerMap();
		long totleTransAmt = 0;
		long totlePayAmt = 0;
		long totleTransFee=0;
		list.add("代付流水号,交易类型,批次号,商户号,商户简称,账户名称,商户订单,支付渠道,交易金额（元）,交易手续费（元）,付款金额（元）,收款银行,收款账户名,收款账号,交易状态".split(","));
		int i = 0;
		try {
			for (OrderInfo t : tlogInfos) {
				String gateRoute = "";
				if (t.getGid() != null && !String.valueOf(t.getGid()).equals("")) {
					gateRoute = gateRouteMap.get(t.getGid());
				}
				String[] str = {t.getTseq().toString(),(11==t.getType()?"对私代付":"对公代付")+"",t.getP8(), t.getMid(),merMap.get(t.getMid()),t.getName(),t.getOid(),gateRoute,
						Ryt.div100(t.getAmount())+"",Ryt.div100(t.getFeeAmt())+"",Ryt.div100(t.getPayAmt())+"",
						gates.get(t.getGate()),t.getP2(),t.getP1(),AppParam.tlog_tstat.get(t.getTstat().intValue())};
				totleTransAmt += t.getAmount();
				totlePayAmt += t.getPayAmt();
				totleTransFee+=t.getPayAmt();
				i += 1;
				list.add(str);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] str = { "总计:" + i + "条记录", "","","","","","","", Ryt.div100(totleTransAmt)+"", Ryt.div100(totleTransFee)+"" , Ryt.div100(totlePayAmt)+"","","","",""};
		list.add(str);
		String filename = "SGDFQUERY" + DateUtil.today() + ".xlsx";
		String name = "手工代付";
		try {
			return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		} catch (Exception e) {
			LogUtil.printErrorLog("SgDfQueryServices", "downSGDFData", "手工代付查询下载异常", e);
		}
		
		return null;
		
	}
	
	
}

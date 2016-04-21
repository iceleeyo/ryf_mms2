package com.rongyifu.mms.modules.myAccount.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import cn.com.infosec.util.Base64;

import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.sqlbean.extens.TlogBean;
import com.rongyifu.mms.ewp.ryf_df.CallRyfPayProcessor;
import com.rongyifu.mms.exception.B2EException;
import com.rongyifu.mms.modules.myAccount.dao.DfqrDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.service.PageService;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;
import org.apache.commons.lang.StringUtils;

/**
 * 代付确认
 *
 */
public class DfqrService extends MyAccountService{
	
	private DfqrDao dao = new DfqrDao();
	
	/**
	 * 代付确认 - 查询
	 * @param pageNo
	 * @param batchNo
	 * @param tseq
	 * @param type
	 * @param dateType
	 * @param state
	 * @param bdate
	 * @param edate
	 * @return
	 * @throws Exception
	 */
	public CurrentPage<TlogBean> queryDFQRinfo(Integer pageNo, String batchNo,
			Long tseq, Integer type, Integer dateType, Integer state,
			Integer bdate, Integer edate) throws Exception {
		
		if (!Ryt.isNumber(pageNo.toString())
				|| (type != -1 && !Ryt.isNumber(type.toString()))
				|| !Ryt.isNumber(dateType.toString())
				|| (state != -1 && !Ryt.isNumber(state.toString()))
				|| !Ryt.isNumber(bdate.toString())
				|| !Ryt.isNumber(edate.toString()))
			throw new Exception("数据类型错误");
		
		if ((type != -1 && type != 5 && type != 7)
				|| (dateType != 0 && dateType != 1 && dateType != 2)
				|| (state < -1 || state > 5))
			throw new Exception("数据类型错误");
		
		return dao.queryDFQRinfo(pageNo, 0, Ryt.sql(batchNo), tseq, type,
				dateType, state, bdate, edate);
	}
	
	/**
	 * 代付确认 - 下载
	 * @param pageNo
	 * @param batchNo
	 * @param tseq
	 * @param type
	 * @param dateType
	 * @param state
	 * @param bdate
	 * @param edate
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downqueryDaiFuQueRen(Integer pageNo,String batchNo,Long tseq,Integer type,Integer dateType,Integer state,Integer bdate,Integer edate) throws Exception{
		List<TlogBean> TrList = dao.downLoadInfo(Ryt.sql(batchNo),tseq,type,dateType,state,bdate,edate);
		String [] tstats={"申请代付","代付确认","代付成功","代付失败","请求银行失败","代付撤销"};
		Map<Integer, String> provMap=  new PageService().getProvMap();
		ArrayList<String[]> list = new ArrayList<String[]>();
		long totleTransAmt = 0;
		long totlePayAmt = 0;
		long totleTransFee=0;
		list.add("序号,商户号,电银流水号,账户号,商户订单号,批次号,交易金额（元）,系统手续费（元）,付款金额（元）,收款银行,收款账户名,收款账号,开户省份,代付状态,交易类型,代付日期,用途".split(","));
		int i = 0;
		try {
			for (TlogBean t : TrList) {
				// 开户省份
				String provName = Ryt.empty(t.getP10()) ? "" : provMap.get(t.getP10());
				// 用途
				String purpose = Ryt.empty(t.getP7()) ? "" : new String(Base64.decode(t.getP7().getBytes()));
				// 代付日期
				String dfDate = t.getBk_date() == null || t.getBk_date() == 0 ? "" : String.valueOf(t.getBk_date());
				String[] str = { (i + 1) + "", t.getMid(),
						String.valueOf(t.getTseq()), "结算账户", t.getOid(),
						t.getP8(), Ryt.div100(t.getAmount()) + "",
						Ryt.div100(t.getFee_amt()) + "",
						Ryt.div100(t.getPay_amt()) + "",
						RYFMapUtil.getRYTGateById(t.getGate()).getGateName(),
						t.getP2(), t.getP1PT(), provName,
						(tstats[t.getTstat().intValue()]),
						11 == t.getType() ? "对私代付" : "对公代付", dfDate, purpose };
				totleTransAmt += t.getAmount();
				totlePayAmt += t.getPay_amt();
				totleTransFee += t.getFee_amt();
				i++;
				list.add(str);

			}
		} catch (Exception e) {
			LogUtil.printErrorLog("DfqrService", "downqueryDaiFuQueRen", "下载异常：" + e.getMessage(), e);
		}
		String[] str = { "总计:" + i + "条记录", "","","","","", Ryt.div100(totleTransAmt)+"", Ryt.div100(totleTransFee)+"" , Ryt.div100(totlePayAmt)+"","","","","","","","","" };
		list.add(str);
		String filename = "DaiFUQueRen_" + DateUtil.today() + ".xlsx";
		String name = "代付确认表";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		
	}
	
	/**
	 * 代付确认
	 * @param orders
	 * @return
	 */
	public String DFconfirm(List<TlogBean> orders){
		if (orders == null || orders.size() == 0)
			return "请选择要确认的订单";
		
		List<TlogBean> dfList = null;
		try {
			dfList = handleOrders(orders, dao);
		} catch (B2EException e) {
			LogUtil.printErrorLog("DfqrService", "DFconfirm", "代付确认发生异常！", e);
			dao.saveOperLog("代付确认", "操作失败：" + e.getMessage());
			return e.getMessage();
		}
		
		// 发起代付
		String dfFailTseqs = "";
        String dfSucTseqs="";
		for(TlogBean item : dfList){
			
			String bkUrl=ParamCache.getStrParamByName("MMS_INTERNAL_URL")+"/mms/go?transCode=RYF_DF_ASYNC_RESP";
			String dfType=CallRyfPayProcessor.getDfType(item.getGate());
			String purpose="";
			try {
				purpose = new String(Base64.decode(item.getP7()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LogUtil.printErrorLog("DfqrService", "DFconfirm", "purpose base64.decode is error,tseq:"+item.getTseq(), e);
				purpose="";
			}
            String bkNo=item.getP3();
            //原中行渠道发起新代付特殊处理
            if(item.getGid()!=null &&item.getGid()==40001){
                bkNo=StringUtils.leftPad(bkNo, 12, "0");
            }
			String resp=CallRyfPayProcessor.ryfDfEntry(item.getMid(), item.getP2(), item.getP1(), item.getTseq_str(), Ryt.div100(item.getAmount()), bkUrl, item.getP6(), bkNo, purpose, item.getMer_priv(), dfType);
			if(!CallRyfPayProcessor.RecvSyncResult(resp))
				dfFailTseqs += item.getTseq() + ",";
            else
                dfSucTseqs+=item.getTseq()+",";

				
			/*if(!CallPayPocessor.ewpDfCommonEntry(item))
				dfFailTseqs += item.getTseq() + ",";*/
		}
		// 处理代付结果
		if (Ryt.empty(dfFailTseqs)) {
			dao.saveOperLog("代付确认", "操作完成|tseqs:"+dfSucTseqs);
			return "操作完成";
		} else {
			dfFailTseqs = dfFailTseqs.substring(0, dfFailTseqs.length() - 1);
			LogUtil.printInfoLog("DfqrService", "DFconfirm", "代付失败电银流水号：" + dfFailTseqs);
			
			String msg = "操作完成，其中" + dfFailTseqs.split(",").length + "笔代付发起失败";
			dao.saveOperLog("代付确认", msg);
			return msg + "\n电银流水号：" + dfFailTseqs;
		}
	}
	
	/**
	 * 处理订单
	 * @param orders
	 * @param dao
	 * @return 需要代付的订单
	 * @throws B2EException
	 */
	private List<TlogBean> handleOrders(List<TlogBean> orders, DfqrDao dao) throws B2EException{
		String tseqs = "";
		int i = 0;
		for(TlogBean item : orders){
			tseqs += item.getTseq_str();
			
			if(i != orders.size() - 1)
				tseqs += ",";
			i++;
		}
		
		List<TlogBean> dfOrderList = null;
		synchronized (lock) {
			// 剔除已经代付的订单，防止重复代付
			dfOrderList = dao.getOrders(tseqs, Constant.PayState.INIT);
			if(dfOrderList == null || dfOrderList.size() == 0 )
				throw new B2EException("没有需要处理的订单！");
			
			i = 0;
			String dfTseqs = "";
			for(TlogBean item : dfOrderList){
				dfTseqs += item.getTseq();
				if(i != dfOrderList.size() - 1)
					dfTseqs += ",";
				
				i++;
			}
			
			// 修改订单状态
			dao.batchUpdateOrder(dfTseqs);
		}
		
		return dfOrderList;
	}
	
	/**
	 * 代付撤销
	 * @param orders
	 * @return
	 */
	public Map<String, String> DfCancel(List<TlogBean> orders){
		Map<String, String> ret = new HashMap<String, String>();
		
		String tseqs = "";
		int i = 0;
		for(TlogBean item : orders){
			tseqs += item.getTseq_str();
			
			if(i != orders.size() - 1)
				tseqs += ",";
			i++;
		}
		
		List<TlogBean> dfOrderList = null;
		synchronized (lock) {
			// 剔除已经代付的订单
			dfOrderList = dao.getOrders(tseqs, Constant.PayState.INIT);
			if(dfOrderList == null || dfOrderList.size() == 0 ){
				ret.put("flag", "1");
				ret.put("msg", "没有需要处理的订单！");
				dao.saveOperLog("代付确认", "没有需要处理的订单！");
				return ret;
			}
		
			try {
				dao.DfCancel(dfOrderList);
				ret.put("flag", "0"); // 标识处理成功
				dao.saveOperLog("代付确认", "撤销成功|tseqs:"+tseqs);
			} catch (Exception e) {
				LogUtil.printErrorLog("DfqrService", "DfCancel", "tseqs=" + tseqs, e);
				
				ret.put("flag", "1");
				ret.put("msg", e.getMessage());
				
				dao.saveOperLog("代付确认", "撤销失败：" + e.getMessage());
			}
		}
		
		return ret;
	}
}

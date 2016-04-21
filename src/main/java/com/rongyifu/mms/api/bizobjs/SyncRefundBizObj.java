package com.rongyifu.mms.api.bizobjs;

import java.util.Map;

import com.rongyifu.mms.api.BizObj;
import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.RefundDao;
import com.rongyifu.mms.quartz.jobs.RefundSyncJob;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 同步退款信息给清算系统
 *
 */
public class SyncRefundBizObj implements BizObj{

	@Override
	public Object doBiz(Map<String, String> params) throws Exception {
		String mid = params.get("mid");
		String refundId = params.get("refundId");
		String type = params.get("type");
		
		if(Ryt.empty(mid))
			throw new Exception("商户号不能为空");
		if(Ryt.empty(refundId))
			throw new Exception("退款单号不能为空");
		if(Ryt.empty(type))
			throw new Exception("退款类型不能为空");
		else if("1,2,4".indexOf(type) == -1)
			throw new Exception("退款类型错误");
		
		// 校验退款信息是否存在
		RefundLog refundLog = new RefundDao().getRefundLogById(refundId);
		if(refundLog == null){
			LogUtil.printErrorLog(getClass().getCanonicalName(), "doBiz", "退款信息不存在", params);
			throw new Exception("退款信息不存在");
		}
		// 校验商户号
		if(!mid.equals(refundLog.getMid())){
			LogUtil.printErrorLog(getClass().getCanonicalName(), "doBiz", "商户号错误", params);
			throw new Exception("商户号错误");
		}
		
		if(refundLog.getStat() != 2 && refundLog.getStat() != 3){
			params.put("stat", String.valueOf(refundLog.getStat()));
			LogUtil.printErrorLog(getClass().getCanonicalName(), "doBiz", "只有退款成功的才能同步", params);
			throw new Exception("只有退款成功的才能同步");
		}
		
		// 添加到同步队列中
		RefundSyncJob.addJob(mid, refundId, type);
		
		return "同步请求受理成功";
	}
}
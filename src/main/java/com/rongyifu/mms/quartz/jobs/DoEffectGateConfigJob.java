package com.rongyifu.mms.quartz.jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class DoEffectGateConfigJob implements Job{

	private MerInfoDao merInfoDao = new MerInfoDao();

	public DoEffectGateConfigJob() {
	    
	}

    public void execute(JobExecutionContext context)
        throws JobExecutionException {
		//获取到记录 status=1 and effective_time=now()
		try {
			Set<String> mids = new HashSet<String>();
//			logger.info("Do Effect Gate Config Job.");
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			String date = sdf.format(now);
			List<FeeCalcMode> fcmList = merInfoDao.queryEffectableConfig(date);
			for (FeeCalcMode feeCalcMode : fcmList) {
				try {
					mids.add(feeCalcMode.getMid());
					merInfoDao.effectConfigChange(feeCalcMode);
					Map<String,String> params = new HashMap<String,String>();
					params.put("mid", feeCalcMode.getMid());
					params.put("applyId", feeCalcMode.getId()+"");
					params.put("gate", feeCalcMode.getGate()+"");
					params.put("calcMode",feeCalcMode.getCalcMode());
					params.put("toCalcMode",feeCalcMode.getCalcMode());
					params.put("gid",feeCalcMode.getGid()+"");
					params.put("toGid",feeCalcMode.getToGid()+"");
					LogUtil.printInfoLog("DoEffectGateConfigJob", "execute", "effect minfo changes.", params);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(CollectionUtils.isNotEmpty(mids)){
				//按商户刷新mms缓存
				for (String mid : mids) {
					try {
						RYFMapUtil util = RYFMapUtil.getInstance();
						util.refreshFeeCalcModel(mid);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//整体刷新ewp缓存	
				Map<String, Object> p = new HashMap<String, Object>();
		 		p.put("t", "fee_calc_mode");
		 		p.put("k", "");
		 		p.put("f", "");
		 		EWPService.refreshEwpETS(p);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package com.rongyifu.mms.quartz.jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.rongyifu.mms.bean.MinfoH;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.datasync.MerInfoAlterSync;
import com.rongyifu.mms.utils.LogUtil;

public class DoEffectMinfoChangeJob implements Job {

	private MerInfoDao merInfoDao = new MerInfoDao();

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			String date = sdf.format(now);
			List<MinfoH> infoList = merInfoDao.queryEffectableInfo(date);
			for (MinfoH minfo : infoList) {
				try {
					merInfoDao.effectiveChange(minfo);
					merInfoDao.updateMinfoChangeApplyStatus(minfo.getId());
					//商户信息同步至账户系统线程
					final String mid = minfo.getId();
					Thread trd=new Thread(new Runnable(){
						@Override
						public void run() {
							new MerInfoAlterSync().syncMinfo(mid);//商户信息同步
						}
					});
					trd.start();
					Map<String,String> params = new HashMap<String,String>();
					params.put("mid", minfo.getMid());
					params.put("applyId", minfo.getId());
					LogUtil.printInfoLog("DoEffectMinfoChangeJob", "execute", "effect minfo changes.", params);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

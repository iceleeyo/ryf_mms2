package com.rongyifu.mms.quartz;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;

public class MySchedulerListener implements SchedulerListener {
	private static Logger logger  =  Logger.getLogger(MySchedulerListener.class);

	@Override
	public void jobScheduled(Trigger trigger) {
		// TODO Auto-generated method stub
		System.out.println();
	}

	@Override
	public void jobUnscheduled(String triggerName, String triggerGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggerFinalized(Trigger trigger) {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggersPaused(String triggerName, String triggerGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggersResumed(String triggerName, String triggerGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobAdded(JobDetail jobDetail) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobDeleted(String jobName, String groupName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobsPaused(String jobName, String jobGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobsResumed(String jobName, String jobGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		// TODO Auto-generated method stub
		try {
			logger.error(msg);
			logger.error("Error Code:"+cause.getErrorCode());
			if (QuartzUtils.isSchedulerStarted()) {
				QuartzUtils.restart();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void schedulerInStandbyMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerStarted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerShutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerShuttingdown() {
		// TODO Auto-generated method stub

	}

}

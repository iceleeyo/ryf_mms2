package com.rongyifu.mms.quartz;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.impl.cookie.DateUtils;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rongyifu.mms.common.Ryt;

public class QuartzUtils {
	private static Scheduler scheduler;
	private static boolean isStarted = false;

    private static final Logger logger = LoggerFactory.getLogger(QuartzUtils.class);
	private static Properties props;
	private static final String propPath = "/props.properties";
	public static final Lock LOCK = new ReentrantLock();
	
	@SuppressWarnings("rawtypes")
	private static void init() throws IOException, ClassNotFoundException, ParseException{
        LOCK.lock();
		try {   
			scheduler = new StdSchedulerFactory().getScheduler();
//			new StdSchedulerFactory("myQuartz.properties").getScheduler("MyScheduler");//自定义scheduler
//			MySchedulerListener mySchedListener = new MySchedulerListener();
//			scheduler.addSchedulerListener(mySchedListener);
			loadProperties();
			Iterator itr = props.entrySet().iterator();
	        while (itr.hasNext()) {
	        	try {
					Entry e = (Entry) itr.next();
					String name = e.getKey().toString();
					String conExp = e.getValue().toString();
					JobDetail job = new JobDetail(name,Scheduler.DEFAULT_GROUP, Class.forName(name));
					CronTrigger trigger = new CronTrigger(name+"Trigger", Scheduler.DEFAULT_GROUP, name, Scheduler.DEFAULT_GROUP, conExp);
					scheduler.addJob(job, true);
					scheduler.scheduleJob(trigger);
					logger.info("job ["+ name + "] scheduled at ["+ conExp+"]");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				}
	        }
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}finally{
			LOCK.unlock();
		}
	}
	
	private static void loadProperties() throws IOException{
        InputStream in = null;
        try {
			in =QuartzUtils.class.getClassLoader().getResourceAsStream(propPath);  
			props = new Properties();
			props.load(in);
		}finally{
			in.close();
			in = null;
		}
	}
	
	public static void setProperty(String key,String value) throws IOException{
		if(null == props){
			loadProperties();
		}
		OutputStream fos = null;
        try {
        	fos = new FileOutputStream(propPath);
			props.setProperty(key, value);
			props.store(fos, DateUtils.formatDate(new Date(), "HH:mm:ss"));
		}finally{
			fos.close();
			fos = null;
		}
	}
	
	/**
	 * @return
	 * 启动定时器
	 */
	public static boolean start(){
		LOCK.lock();
		logger.info("starting QuartzUtils...");
		try {
			if(null == scheduler){
				init();
				scheduler.start();
				isStarted = scheduler.isStarted();
				logger.info("[QuartzUtils] Quartz is started.");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			shutDown();
		}finally{
			LOCK.unlock();
		}
		return isStarted;
	}
	//定时检查定时任务的线程
	private static ExecutorService exc =null;
	private static final Runnable r = new Runnable(){
		@Override
		public void run() {
			try {
					while(!Thread.interrupted()){
						try {
							Thread.sleep(1000*60*30);//sheep 30 分钟
							if(!isSchedulerStarted() && Ryt.isStartService("检查是否需要启用定时器.")){
								QuartzUtils.start();
							}
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
			} catch (Exception e) {
				logger.info("startChecker抛出异常");
				logger.info(e.getMessage(),e);
				logger.info("重新启动startChecker");
				exc.execute(r);
			}
		}
	};
	
	public static void startChecker(){
		LOCK.lock();
		try {
			logger.info("startChecker");
			if (null == exc) {
				exc = Executors.newSingleThreadExecutor();
				exc.execute(r);
			}
		}finally{
			LOCK.unlock();
		}
	}
	
	private static void closeChecker(){
		LOCK.lock();
		try {
			logger.info("closeChecker");
			if (null != exc) {
				exc.shutdownNow();
				exc = null;
			}
		} finally {
			LOCK.unlock();
		}
	}
	
	/**
	 * @return
	 * 关闭定时器
	 */
	public static boolean shutDown(){
		LOCK.lock();
		try {
			if(null != scheduler){
					closeChecker();//关闭检查
					scheduler.shutdown(true);
					isStarted = false;
					scheduler = null;
					props = null;
					logger.info("[QuartzUtils] Quartz is closed.");
			}
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}finally{
			LOCK.unlock();
		}
		return isStarted;
	}
	
	public static boolean isSchedulerStarted(){
		try {
			return scheduler==null?false:scheduler.isStarted();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	public static String showStatus(){
		StringBuilder sb = new StringBuilder();
		try {
			if(null != scheduler && scheduler.isStarted()){
				sb.append("<h1>------------------QuartzUtils status:RUNNING---------------------</h1>");
				String[] names = scheduler.getJobNames(Scheduler.DEFAULT_GROUP);
				if(names!=null){
					for(int i = 0; i<names.length;i++){
						JobDetail jd =scheduler.getJobDetail(names[i], Scheduler.DEFAULT_GROUP);
						sb.append("<p>").append(jd.toString()).append("</p>");
						Trigger t = scheduler.getTrigger(names[i]+"Trigger", Scheduler.DEFAULT_GROUP);
						if(t instanceof CronTrigger){
							sb.append("<p>").append("triggered at: ").append(((CronTrigger)t).getCronExpression()).append("</p>");
						}
					}
				}
			}else{
				sb.append("<h1>------------------QuartzUtils status:CLOSED---------------------</h1>");
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static boolean restart(){
		LOCK.lock();
		try {
			shutDown();
			start();
			startChecker();//重启的时候启动检查线程
		} finally {
			LOCK.unlock();
		}
		return isStarted;
	}
}

package com.xh.scheduler.manager;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.util.StringUtils;

import com.xh.scheduler.StartJob;
import com.xh.scheduler.vo.SchedulerVo;
import com.xh.util.SpringUtil;

import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Matcher;

public class QuartzManager  {
	
	private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory(); //创建一个SchedulerFactory工厂实例 
//	private static SchedulerFactoryBean gSchedulerFactory ;
	
	private static String JOB_GROUP_NAME = "TS_JOBGROUP_NAME"; //任务组 
	private static String TRIGGER_GROUP_NAME = "TS_TRIGGERGROUP_NAME"; //触发器组
	
	static {
		//配置Quartz属性
		//失火重复执行时间 默认过去的1分钟
		System.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
		//线程池大小
		System.setProperty("org.quartz.threadPool.threadCount", "50");
		//设置线程池为自定义的线程池
		System.setProperty("org.quartz.threadPool.class", "com.xh.scheduler.manager.TaskExecutorThreadPool");
	}
	
	public static SchedulerFactory getSchedulerFactory(){
		if(gSchedulerFactory == null) {
			gSchedulerFactory =  new StdSchedulerFactory();
		}
		return  gSchedulerFactory;
	}

	/**添加一个定时任务，使用默认的任务组名，触发器名，触发器组名  
	 * @param jobName 任务名
	 * @param cls 任务
	 * @param time 时间设置，参考quartz说明文档
	 * @throws ClassNotFoundException 
	 */
	public static void addJob(SchedulerVo vo) { 

		StartJob startJob= (StartJob) SpringUtil.getBean("StartJob");
		if(startJob != null ) {
			Class<? extends Job> dealClass = startJob.getJobClass(vo.getJobType());
			addJob(vo,dealClass);
		}
	
	}
	
	/**添加一个定时任务，使用默认的任务组名，触发器名，触发器组名  
	 * @param jobName 任务名
	 * @param cls 任务
	 * @param time 时间设置，参考quartz说明文档
	 * @throws ClassNotFoundException 
	 */
	public static void addJob(SchedulerVo vo,Class<? extends Job> dealClass) {  
		if(vo != null &&  dealClass != null) {
			if(StringUtils.isEmpty( vo.getJobTime() )) {
				//执行一次
				addRepeatJob(vo.getJobName(), vo.getJobGroupName(), vo.getTriggerName(), vo.getTriggerGroupName(), dealClass,vo.getParamsMap());
			}else {
				//按时间执行多次
				addJob(vo.getJobName(), vo.getJobGroupName(), vo.getTriggerName(), vo.getTriggerGroupName(), dealClass, vo.getJobTime(),vo.getParamsMap());
			}
		}
		
	}
	
	/**
	 * state的值代表该任务触发器的状态： NONE(不存在), NORMAL(正常), PAUSED(暂停), COMPLETE(完成), ERROR(错误), BLOCKED(阻塞)
	 * @return 
	 *
	 * @Title: getState
	 */
	public static TriggerState getState(String jobName ) {
		   try {  
			    Scheduler sched = getSchedulerFactory().getScheduler();  
		        TriggerKey triggerKey = TriggerKey.triggerKey(jobName,TRIGGER_GROUP_NAME);  //通过触发器名和组名获取TriggerKey
		        return sched.getTriggerState(triggerKey);
		   } catch (Exception e) {  
		        throw new RuntimeException(e);  
		    } 

		
	}
	
	/**
	 * state的值代表该任务触发器的状态： 未运行：NONE(不存在),COMPLETE(完成), ERROR(错误),  BLOCKED(阻塞)<br>
	 * 						       正在运行： NORMAL(正常), PAUSED(暂停)
	 * @return 
	 *
	 * @Title: getState
	 */
	public static Boolean isJobRuning (String jobName ) {
		   try {  
			    Scheduler sched = getSchedulerFactory().getScheduler();  
		        TriggerKey triggerKey = TriggerKey.triggerKey(jobName,TRIGGER_GROUP_NAME);  //通过触发器名和组名获取TriggerKey
		        TriggerState status = sched.getTriggerState(triggerKey);
		        
		       System.out.println(jobName+"状态："+status.name());
		        //不存在|完成|错误
				if( status == TriggerState.NONE ||  status == TriggerState.BLOCKED || status == TriggerState.COMPLETE ||status ==  TriggerState.ERROR ) {
					return false;
				}else {
					return true ;
				}
		   } catch (Exception e) {  
			   return false;  
		    } 

		
	}
	
	/**
	 * 添加一个5秒后执行一次的任务,不带参数，使用默认的任务组名，触发器名，触发器组名  
	 * @Title: addRepeatJob 
	 * @param jobName
	 * @param cls
	 */
	public static void addRepeatJob(String jobName, Class<? extends Job> cls) {  
		addRepeatJob(jobName, null, null, null, cls, null, null, null);
	}  
	
	/**
	 * 添加一个5秒后执行一次的任务,带参数，使用默认的任务组名，触发器名，触发器组名  
	 * @Title: addRepeatJob 
	 * @param jobName
	 * @param cls
	 */
	public static void addRepeatJob(String jobName, Class<? extends Job> cls,Map<String,Object> parameter) {  
		addRepeatJob(jobName, null, null, null, cls, parameter, null, null);
	}  
	
	/**
	 * 添加一个5秒后执行一次的任务，不带参数
	 * @Title: addRepeatJob 
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @param cls
	 */
	public static void addRepeatJob(String jobName, String jobGroupName,  
						      String triggerName, String triggerGroupName,
						      Class<? extends Job> cls) {  
		addRepeatJob(jobName, jobGroupName, triggerName, triggerGroupName, cls, null, null, null);
	}  
	
	/**
	 * 添加一个5秒后执行一次的任务，带参数
	 * @Title: addRepeatJob 
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @param cls
	 */
	public static void addRepeatJob(String jobName, String jobGroupName,  
						      String triggerName, String triggerGroupName,
						      Class<? extends Job> cls,Map<String,Object> parameter) {  
		addRepeatJob(jobName, jobGroupName, triggerName, triggerGroupName, cls, parameter, null, null);
	}  
	
	/**
	 * 添加一个5秒后重复执行repeatCount次的任务,带参数
	 * @Title: addNumberJob 
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @param cls
	 * @param parameter
	 * @param repeatCount ,重复次数，为空则执行一次
	 * @param seconds，间隔秒数，为空则5秒后执行一次
	 */
	public static void addRepeatJob(String jobName, String jobGroupName,  
						      String triggerName, String triggerGroupName,
						      Class<? extends Job> cls,Map<String,Object> parameter,Integer repeatCount,Integer seconds ) {  
	    try {  
	    	
	    	if(StringUtils.isEmpty(jobGroupName)) {
	    		jobGroupName = JOB_GROUP_NAME ;
	    	}
	    	if(StringUtils.isEmpty(triggerName)) {
	    		triggerName = jobName ;
	    	}
	    	if(StringUtils.isEmpty(triggerGroupName)) {
	    		triggerGroupName = TRIGGER_GROUP_NAME ;
	    	}
	    	
	        Scheduler sched = getSchedulerFactory().getScheduler();                                         //通过SchedulerFactory构建Scheduler对象
	        JobDetail jobDetail= JobBuilder.newJob(cls).withIdentity(jobName,jobGroupName).build();   //用于描叙Job实现类及其他的一些静态信息，构建一个作业实例
	        if(parameter!=null && parameter.size() >0) {  //传参数
	        	jobDetail.getJobDataMap().putAll(parameter);
	        }
	        
	        SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionNextWithRemainingCount();
	        if( repeatCount != null && seconds != null) {
	        	schedule.withIntervalInSeconds(seconds).withRepeatCount(repeatCount);
	        }
	        if(seconds != null && repeatCount == null) {
	        	schedule.withIntervalInSeconds(seconds).repeatForever() ;
	        }
	        
	        SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder
	        		.newTrigger()
	        		.withIdentity(triggerName, triggerGroupName)
	        		.startAt(DateBuilder.futureDate(5, IntervalUnit.SECOND)) 
	        		.withIdentity(jobName, triggerGroupName)
	        		.withSchedule(schedule)
	        	    .build();
	        sched.scheduleJob(jobDetail, trigger);  
	        if (!sched.isShutdown()) {  
	            sched.start();        // 启动  
	        }  
	    } catch (Exception e) {  
	        throw new RuntimeException(e);  
	    }  
	}  
	
	/**添加一个定时任务，不带参数，使用默认的任务组名，触发器名，触发器组名  
	 * @param jobName 任务名
	 * @param cls 任务
	 * @param time 时间设置，参考quartz说明文档
	 */
	public static void addJob(String jobName, Class<? extends Job> jobClass, String time) {  
		addJob(jobName, null, null, null, jobClass, time,null);
	}  

	/**添加一个定时任务，带参数，使用默认的任务组名，触发器名，触发器组名 
	 * @param jobName 任务名
	 * @param cls 任务
	 * @param time 时间设置，参考quartz说明文档
	 */
	public static void addJob(String jobName, Class<? extends Job> jobClass, String time, Map<String,Object> parameter) {  
		addJob(jobName, null, null, null, jobClass, time,parameter);
	}  

	/**添加一个定时任务 ,不带参数
	 * @param jobName   任务名 
	 * @param jobGroupName  任务组名 
	 * @param triggerName   触发器名 
	 * @param triggerGroupName  触发器组名 
	 * @param jobClass  任务 
	 * @param time  时间设置，参考quartz说明文档 
	 */
	public static void addJob(String jobName, String jobGroupName,  
	        String triggerName, String triggerGroupName, Class<? extends Job> jobClass,  
	        String time) {  
		addJob(jobName, jobGroupName, triggerName, triggerGroupName, jobClass, time, null); 
	}  

	/**添加一个定时任务  （带参数）
	 * @param jobName   任务名 
	 * @param jobGroupName  任务组名 
	 * @param triggerName   触发器名 
	 * @param triggerGroupName  触发器组名 
	 * @param jobClass  任务 
	 * @param time  时间设置，参考quartz说明文档 
	 * @param parameter  job参数 
	 */
	public static void addJob(String jobName, String jobGroupName,  
					          String triggerName, String triggerGroupName, 
					          Class<? extends Job> jobClass,  String time, Map<String,Object> parameter) {  
	    try {  
	    	if(StringUtils.isEmpty(jobGroupName)) {
	    		jobGroupName = JOB_GROUP_NAME ;
	    	}
	    	if(StringUtils.isEmpty(triggerName)) {
	    		triggerName = jobName ;
	    	}
	    	if(StringUtils.isEmpty(triggerGroupName)) {
	    		triggerGroupName = TRIGGER_GROUP_NAME ;
	    	}
	        Scheduler sched = getSchedulerFactory().getScheduler();  
	        JobDetail jobDetail= JobBuilder.newJob(jobClass).withIdentity(jobName,jobGroupName).build();// 任务名，任务组，任务执行类
	        if(parameter!=null && parameter.size() >0) {  //传参数
	        	jobDetail.getJobDataMap().putAll(parameter);
	        }              
	        CronTrigger trigger = (CronTrigger) TriggerBuilder   // 触发器  
	                .newTrigger()
	                .withIdentity(triggerName, triggerGroupName)
	                .withSchedule(CronScheduleBuilder.cronSchedule(time)
							 .withMisfireHandlingInstructionDoNothing())
	                .build();
	        sched.scheduleJob(jobDetail, trigger);
	        if (!sched.isShutdown()) {  
	            sched.start();        // 启动  
	        } 
	    } catch (Exception e) {  
	        throw new RuntimeException(e);  
	    }  
	} 

	/**
	 * 为job组增加job监听器
	 * @Title: addJobGroupListener 
	 * @param groupName
	 * @param jobListener
	 */
	public static void addJobGroupListener(String groupName, JobListener jobListener) {  
	    try {  
	        Scheduler sched = getSchedulerFactory().getScheduler();                       
	        GroupMatcher<JobKey> matcher = GroupMatcher.jobGroupEquals(groupName);
	        sched.getListenerManager().addJobListener(jobListener, matcher);
	    } catch (Exception e) {  
	        throw new RuntimeException(e);  
	    }  
	} 
	
	/**
	 * 为job增加监听器
	 * @Title: addJobListener 
	 * @param jobName
	 * @param jobListener
	 */
	public static void addJobListener(String jobName, JobListener jobListener) {  
	    try {  
	        Scheduler sched = getSchedulerFactory().getScheduler();                       
	        JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);        
	        Matcher<JobKey> matcher = KeyMatcher.keyEquals(jobKey);
	        sched.getListenerManager().addJobListener(jobListener, matcher);
	    } catch (Exception e) {  
	        throw new RuntimeException(e);  
	    }  
	} 
	
	/**
	 * 增加全局监听器
	 * @Title: addJobListener 
	 * @param jobName
	 * @param jobListener
	 */
	public static void addJobListener( JobListener jobListener) {  
	    try {  
	        Scheduler sched = getSchedulerFactory().getScheduler();                       
	        sched.getListenerManager().addJobListener(jobListener);
	    } catch (Exception e) {  
	        throw new RuntimeException(e);  
	    }  
	} 
	/**
	 * 移除监听器
	 * @Title: removeJobListener 
	 * @param listenerName
	 */
	public static void removeJobListener(String listenerName ) {  
	    try {  
	        Scheduler sched = getSchedulerFactory().getScheduler();                       
	        sched.getListenerManager().removeJobListener(listenerName) ;
	    } catch (Exception e) {  
	        throw new RuntimeException(e);  
	    }  
	} 
	
	
	/** 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名) 
	 * @param jobName   任务名 
	 * @param time  新的时间设置
	 */
	public static void modifyJobTime(String jobName, String time) {  
		modifyJobTime(jobName, null, time);
	}  

	/**修改一个任务的触发时间 
	 * @param triggerName   任务名称
	 * @param triggerGroupName  传过来的任务名称
	 * @param time  更新后的时间规则
	 */
	public static void modifyJobTime(String triggerName, String triggerGroupName, String time) {  
	    try {  
	    	
	    	if(StringUtils.isEmpty(triggerGroupName)) {
	    		triggerGroupName = TRIGGER_GROUP_NAME ;
	    	}
	    	
	        Scheduler sched = getSchedulerFactory().getScheduler();                             //通过SchedulerFactory构建Scheduler对象
	        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName,triggerGroupName);    //通过触发器名和组名获取TriggerKey
	        CronTrigger trigger = (CronTrigger)sched.getTrigger(triggerKey);                //通过TriggerKey获取CronTrigger
	        if (trigger == null)  return;  
	        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(trigger.getCronExpression());
	        String oldTime = trigger.getCronExpression();  
	        if (!oldTime.equalsIgnoreCase(time)) {  
	            trigger = (CronTrigger)trigger.getTriggerBuilder()      //重新构建trigger
	                    .withIdentity(triggerKey)
	                    .withSchedule(scheduleBuilder)
	                    .withSchedule(CronScheduleBuilder.cronSchedule(time)
								 .withMisfireHandlingInstructionDoNothing())
	                    .build();
	            sched.rescheduleJob(triggerKey, trigger);               //按新的trigger重新设置job执行
	        }
	    } catch (Exception e) {  
	        throw new RuntimeException(e);  
	    }  
	}  
	
	/**停止一个任务(使用默认的任务组名，触发器名，触发器组名) 
	 * @param jobName   任务名称
	 */
	public static void interruptJob(String jobName) {  
	    try {  
	        Scheduler sched = getSchedulerFactory().getScheduler();  
	        JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);                     //通过任务名和组名获取JobKey
	        sched.pauseJob(jobKey);
	        sched.interrupt(jobKey);
	    } catch (Exception e) {  
	        throw new RuntimeException(e);  
	    }  
	}  
	/**
	 * job是否已经加入定时器
	 * @Title: hasJob 
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @return
	 */
	public static boolean hasJob(String jobName) {  
		return hasJob(jobName, null, null, null);
	}
	
	/**
	 * job是否已经加入定时器
	 * @Title: hasJob 
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @return
	 */
	public static boolean hasJob(String jobName, String jobGroupName,String triggerName, String triggerGroupName) {  
	    try {  
	    	
	    	if(StringUtils.isEmpty(jobGroupName)) {
	    		jobGroupName = JOB_GROUP_NAME ;
	    	}
	    	if(StringUtils.isEmpty(triggerName)) {
	    		triggerName = jobName ;
	    	}
	    	if(StringUtils.isEmpty(triggerGroupName)) {
	    		triggerGroupName = TRIGGER_GROUP_NAME ;
	    	}
	    	
	        Scheduler sched = getSchedulerFactory().getScheduler();  
	        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName,triggerGroupName);    //通过触发器名和组名获取TriggerKey
	        Trigger trigger = sched.getTrigger(triggerKey);                //通过TriggerKey获取 Trigger
	        if (trigger == null) {
	        	return false;  
	        }
	       return true;
	    } catch (Exception e) {  
	        throw new RuntimeException(e);  
	    }  
	} 

	/**移除一个任务(使用默认的任务组名，触发器名，触发器组名) 
	 * @param jobName   任务名称
	 */
	public static void removeJob(String jobName) {  
		removeJob(jobName, null, null, null); 
	}  

	/**移除一个任务
	 * @param jobName   任务名
	 * @param jobGroupName  任务组名
	 * @param triggerName   触发器名
	 * @param triggerGroupName  触发器组名
	 */
	public static void removeJob(String jobName, String jobGroupName,String triggerName, String triggerGroupName) {  
	    try {  
	    	
	    	if(StringUtils.isEmpty(jobGroupName)) {
	    		jobGroupName = JOB_GROUP_NAME ;
	    	}
	    	if(StringUtils.isEmpty(triggerName)) {
	    		triggerName = jobName ;
	    	}
	    	if(StringUtils.isEmpty(triggerGroupName)) {
	    		triggerGroupName = TRIGGER_GROUP_NAME ;
	    	}
	    	
	        Scheduler sched = getSchedulerFactory().getScheduler();  
	        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName,triggerGroupName);    //通过触发器名和组名获取TriggerKey
	        Trigger trigger = sched.getTrigger(triggerKey);                //通过TriggerKey获取 Trigger
	        if (trigger == null) {
	        	return;  
	        }
	        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);                           //通过任务名和组名获取JobKey
	        sched.pauseTrigger(triggerKey); // 停止触发器  
	        sched.unscheduleJob(triggerKey);// 移除触发器  
	        sched.deleteJob(jobKey);        // 删除任务  
	    } catch (Exception e) {  
	        throw new RuntimeException(e);  
	    }  
	} 

	/**
	 * 启动所有定时任务 
	 */
	public static void startJobs() {  
	    try {  
	        Scheduler sched = getSchedulerFactory().getScheduler();  
	        sched.start();  
	    } catch (Exception e) {  
	        throw new RuntimeException(e);  
	    }  
	}  

	/**
	 * 关闭所有定时任务 
	 */
	public static void shutdownJobs() {  
	    try {  
	        Scheduler sched = getSchedulerFactory().getScheduler();  
	        if (!sched.isShutdown()) {  
	            sched.shutdown();  
	        }  
	    } catch (Exception e) {  
	        throw new RuntimeException(e);  
	    }  
	}  

}

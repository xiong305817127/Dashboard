package com.xh.scheduler.job;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.UnableToInterruptJobException;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.xh.scheduler.StartJob;
import com.xh.scheduler.manager.QuartzManager;
import com.xh.scheduler.service.IJobDetailService;
import com.xh.scheduler.service.IJobSchedulerService;
import com.xh.scheduler.vo.JobDetailVo;
import com.xh.scheduler.vo.SchedulerVo;
import com.xh.util.ConfigPropertyUtil;
import com.xh.util.JsonUtil;
import com.xh.util.SpringUtil;

/**
 * Job实现抽象类 ，
 * @param T 发起获取数据的参数对象，对应SchedulerVo的params数据的封装，getDate数据的参数
 * @author xh
 * @date 2017年11月2日 下午2:28:13
 */
//同一个Jobdetail不能同时执行
//@DisallowConcurrentExecution
public abstract class JobInterface<T> implements Job ,InterruptableJob {
	
	private Thread thisThread = null ;

	/**
	 * job实现的类型，用于区分不同的job
	 * @Title: getJobType 
	 * @return
	 */
	public abstract String getJobType() ;
	/**
	 * 通过 SchedulerVo的params参数传入的对象参数获取数据
	 * @Title: getDate 
	 * @param t  SchedulerVo的params参数传入的对象参数
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public abstract List  getDate(T t,SchedulerVo svo)  throws  Exception;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		thisThread  = Thread.currentThread();
		
		IJobDetailService jobDetailService = (IJobDetailService) SpringUtil.getBean("JobDetailService");
		IJobSchedulerService jobSchedulerService = (IJobSchedulerService) SpringUtil.getBean("JobSchedulerService");
		JobDetailVo detail =null;
		
		try {
			//运行实例ID
			String detailId = UUID.randomUUID().toString();
			//任务准备，数据还原
			JobDataMap map = arg0.getMergedJobDataMap();
			SchedulerVo svo = (SchedulerVo)map.get("source");
			//判断是否已经在运行了(重复触发和多实例)
			JobDetailVo oldDetailVo = jobDetailService.getLastRuningJobDetailByJobName(svo.getJobName());
			if(oldDetailVo != null) {
				//数据库任务已经在运行
				Date oldStartTime = oldDetailVo.getJobStartTime();
				long diff = new Date().getTime() - oldStartTime.getTime();
				long diffMins = diff / (1000 * 60 );
				Integer exceptionTimeout = svo.getExceptionTimeout();
				if(exceptionTimeout == null) {
					exceptionTimeout = Integer.valueOf(ConfigPropertyUtil.getProperty("job.exception.timeout", "60"));
				}
				if( (diffMins > exceptionTimeout)) {
					//运行超时，停止任务
					QuartzManager.interruptJob(svo.getJobName());
					oldDetailVo.setJobStatus(10);
					oldDetailVo.setJobEndTime(new Date());
					jobDetailService.recordJobDetail(oldDetailVo);
				}else {
					//正在正常运行
					return ;
				}
			}
			//多实例	抢夺 runkey
			String jobRunkey = jobSchedulerService.getJobRunKey(svo.getJobName());
			//子任务的jobRunkey为空，子任务也不会自动执行，不需要抢占
			if(!StringUtils.isEmpty(jobRunkey)) {
				//父任务多实例抢占,当获取的runkey不是同一个，判断详细表是否存在，不存在||存在但是正在运行 则是已经有实例在运行了
				JobDetailVo jobRunkeyDetail = jobDetailService.getJobDetailById(jobRunkey);
				if(!"0".equals(jobRunkey) && (jobRunkeyDetail == null || jobRunkeyDetail.getJobStatus() < 8)) {
					return ;
				}
				//父任务多实例抢占,当获取的runkey是同一个时，只有一个成功
				Integer res = jobSchedulerService.updateJobRunKey(svo.getJobName(), detailId, jobRunkey);
				if(res <= 0) {
					//抢占失败,同时有别的实例在运行了，直接结束
					return ;
				}
			}
			
			//增加任务运行详细实例
			detail = new JobDetailVo();
			detail.setId(detailId);
			svo.setDetailId(detailId);
			detail.setIsOnlyErrorRecord(svo.isOnlyErrorRecord());
			detail.setJobId(svo.getId());
			detail.setJobName(svo.getJobName());
			detail.setJobType(svo.getJobType());
			detail.setJobStartTime(arg0.getFireTime());
			detail.setParentId(svo.getParentJobid());
			detail.setJobRemark(InetAddress.getLocalHost().getHostAddress());
			detail.setReslutNum(0);
			detail.setJobStatus(0); //"正在启动"
			jobDetailService.recordJobDetail(detail);
			

			// 获取getData参数对象
			map.remove("source");
			T t = genetalParams(map);
			
			detail.setJobStatus(1);//"正在获取数据"
			jobDetailService.recordJobDetail(detail);
			//获取数据
			List data = getDate(t,svo);
			Object parentData = data; //默认子查询参数数据是 获取到的数据
			
			detail.setJobParams(JsonUtil.transObjectTojson(t));//保存任务运行数据
			detail.setJobStatus(2);//"已获取数据,准备保存数据"
			jobDetailService.recordJobDetail(detail);
			//保存数据
			Object num = saveData(data,t,svo);
			if(num != null) {
				if(num instanceof Integer || num.getClass()==int.class ){
					detail.setReslutNum((int)num );
					//更新父任务的结果数
					if(!StringUtils.isEmpty(svo.getParentDetailId())&& (int)num !=0) {
						jobDetailService.updateJobDetailResultNum(svo.getParentDetailId(), (int)num);
					}
				}else {
					parentData=num;//如果保存后返回的数据不为空也不为整型，则为子查询参数数据
				}
			}
			
			detail.setJobStatus(3);//已保存数据,准备启动子任务
			jobDetailService.recordJobDetail(detail);
			//执行子任务
			SchedulerVo childJob = svo.getChildJob();
			if(childJob != null) {
				childJob.setJobTime(null);
				childJob.setParentDetailId(detailId);
				StartJob startJob = (StartJob) SpringUtil.getBean("StartJob");
				
				//增加子任务监听器
				String listenerName = childJob.getJobName()+"Listener";
				String jobGroupName = childJob.getJobName()+"Group";
				childJob.setJobGroupName(jobGroupName);
				CountDownLatch countOrder = null ;
				
				if(svo.isLoopChildJob() && parentData != null && parentData instanceof List && ((List)parentData).size()>0 ) {
					
					countOrder = new CountDownLatch(((List)parentData).size());  
					ChildJobListener childListener =  new ChildJobListener(listenerName, countOrder);
					QuartzManager.addJobGroupListener(jobGroupName, childListener);
					
					String childJobName = childJob.getJobName();
					String childJobId = childJob.getId();
					for(int i=0;i<((List)parentData).size();i++) {
						Object d = ((List)parentData).get(i);
						SchedulerVo childJobClone = childJob.clone(null) ;
						childJobClone.setJobName(childJobName+i);
						childJobClone.setId(childJobId+i);
						childJobClone.setParentData(d);
						childJobClone.addParentInitData(svo.getInitData());
						QuartzManager.addJob(childJobClone,startJob.getJobClass(childJobClone.getJobType()));
					}
					detail.setChildrenNum(((List)parentData).size());
				}else if( !svo.isLoopChildJob()){
					
					countOrder = new CountDownLatch(1); 
					ChildJobListener childListener =  new ChildJobListener(listenerName, countOrder);
					QuartzManager.addJobGroupListener(jobGroupName, childListener);
					
					childJob.setParentData(parentData);
					childJob.addParentInitData(svo.getInitData());
					QuartzManager.addJob(childJob,startJob.getJobClass(childJob.getJobType()));
					detail.setChildrenNum(1);
				}
				
				if(countOrder!= null) {
					//等待计数器变为0
					countOrder.await();
					QuartzManager.removeJobListener(listenerName);
				}
			}
			
			detail.setJobEndTime(new Date());
			detail.setJobStatus(8);//"成功结束！"
			jobDetailService.recordJobDetail(detail);
			
		} catch (Exception e) {
			if(detail != null) {
				try {
					String error = e.getMessage();
					if(StringUtils.isEmpty(error) && e.getCause() != null) {
						error=e.getCause().getMessage();
					}
					if(!StringUtils.isEmpty(error)) {
						if(error.length() >1000) {
							error= error.substring(0 , 950) ;
						}
					}
					detail.setJobRemark(InetAddress.getLocalHost().getHostAddress()+" , "+error);
					detail.setJobStatus(9);//异常结束
					detail.setJobEndTime(new Date());
					jobDetailService.recordJobDetail(detail);
				} catch (Exception e1) {
				}
			}
			
			e.printStackTrace();
			throw new JobExecutionException(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Object saveData(List  s,T t,SchedulerVo sv) throws Exception {
		if(s ==null || s.size() ==0) {
			return 0;
		}
		Object result = invokeServiceMethod(sv.getSaveMethod(), new Object[] {s}, new Class[] {List.class});
		if(result == null) {
			result = invokeServiceMethod(sv.getSaveMethod(), new Object[] {s,t}, new Class[] {List.class,getTSClass(0)});
		}
		if(result == null) {
			result = invokeServiceMethod(sv.getSaveMethod(), new Object[] {s,t, sv}, new Class[] {List.class,getTSClass(0),SchedulerVo.class});
		}
		if(result == null) {
			return 0;
		}
		return result;
	}
	
	
	@SuppressWarnings("rawtypes")
	public Object invokeServiceMethod(String methodName,Object[] params, Class[] paramTypes) throws Exception {

		if(!StringUtils.isEmpty(methodName)) {
			StartJob startJob = (StartJob) SpringUtil.getBean("StartJob");
			JobService jobService=startJob.getJobServicelClass(getJobType());
			if(jobService != null) {
				if(params == null || params.length ==0) {
					Method method = ReflectionUtils.findMethod(jobService.getClass(), methodName);
					if(method != null ) {
						Object result = method.invoke(jobService);
						return result;
					}
				}else {
					Method method = ReflectionUtils.findMethod(jobService.getClass(), methodName,paramTypes);
					if(method != null ) {
						Object result = method.invoke(jobService,params);
						return result;
					}
				}
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private  T genetalParams(JobDataMap map) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		return  (T) JsonUtil.transObjectToObject( map , getTSClass(0));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Class<? extends Object> getTSClass(int index){
		Type type = getClass().getGenericSuperclass();
        // 判断 是否泛型
        if (type instanceof ParameterizedType) {
            // 返回表示此类型实际类型参数的Type对象的数组.
            // 当有多个泛型类时，数组的长度就不是1了
            Type[] ptype = ((ParameterizedType) type).getActualTypeArguments();
            return (Class) ptype[index];  //将第一个泛型T对应的类返回（这里只有一个）
        } else {
            return Object.class;//若没有给定泛型，则返回Object类
        }
	}
	
	public String replMap(String string,Map<String,Object> map) {
		if (string == null) {
			return null;
		}
		
		StringBuilder str = new StringBuilder(string);
		int idx = str.indexOf("${");
		while (idx >= 0) {
			// OK, so we found a marker, look for the next one...
			int to = str.indexOf("}", idx + 2);
			if (to >= 0) {
				// OK, we found the other marker also...
				String marker = str.substring(idx, to + 1);
				String var = str.substring(idx + 2, to);
				if (var != null && var.length() > 0) {
					// Get the environment variable
					Object newObj = null ;
					if(map !=null) {
						newObj = map.get(var);
					}
					if(newObj == null) {
						newObj = ConfigPropertyUtil.getProperty(var);
					}
					if (newObj != null) {
						String newval = newObj.toString();
						if(isChineseChar(newval)) {
							try {
								newval= URLEncoder.encode(newval, "UTF-8");
							} catch (UnsupportedEncodingException e) {
							}
						}
						// Replace the whole bunch
						str.replace(idx, to + 1, newval);
						// System.out.println("Replaced ["+marker+"] with
						// ["+newval+"]");
						// The last position has changed...
						to += newval.length() - marker.length();
					}
				}
			} else {
				// We found the start, but NOT the ending %% without closing %%
				to = idx;
			}
			// Look for the next variable to replace...
			idx = str.indexOf("${", to + 1);
		}
		return str.toString();
	}
	
	public static boolean isChineseChar(String str){ 
		boolean temp = false;
		Pattern p=Pattern.compile("[\u4e00-\u9fa5]"); 
		Matcher m=p.matcher(str); 
		if(m.find()){ 
			temp =  true; 
		}      
		return temp;  
	}
	
    // this method is called by the scheduler
    public void interrupt() throws UnableToInterruptJobException {
        if (thisThread != null) {
            thisThread.interrupt();
        }
    }

}

class ChildJobListener implements JobListener {
	
	private String childJobListenerName;
	private CountDownLatch countOrder;
	
	public ChildJobListener(String childJobListenerName,CountDownLatch countOrder) {
		super();
		this.childJobListenerName = childJobListenerName;
		this.countOrder = countOrder;
	}

	@Override
	public String getName() {
		return childJobListenerName;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		//执行前
		
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		//这个方法正常情况下不执行,但是如果当TriggerListener中的vetoJobExecution方法返回true时,那么执行这个方法.
		//需要注意的是 如果方法(2)执行 那么(1),(3)这个俩个方法不会执行,因为任务被终止了嘛.
		
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		// 任务执行完成后执行,jobException如果它不为空则说明任务在执行过程中出现了异常
//		String jobName = context.getJobDetail().getKey().getName();
		countOrder.countDown();  
	}
	
}

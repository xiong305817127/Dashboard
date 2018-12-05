package com.xh.scheduler;

import java.util.List;
import java.util.Optional;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.xh.scheduler.job.JobInterface;
import com.xh.scheduler.job.JobService;
import com.xh.scheduler.manager.QuartzManager;


@Component("StartJob")
public class StartJob implements ApplicationListener<ContextRefreshedEvent> {

	@SuppressWarnings("rawtypes")
	@Autowired
	List<JobInterface> jobInterfaces; 
	@Autowired
	List<JobService> jobServices ;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {

		if (arg0.getApplicationContext().getParent() == null)// root application context 
		{

//			SchedulerVo sv1 = new SchedulerVo("get_XXXX_parent");
//			sv1.setJobType("code");
//			sv1.setJobTime("0 */10 * * * ? *");
//			sv1.setSaveMethod("parentDataResponse");
//			sv1.setEnable(true);
//			//任务运行的初始化参数
//			sv1.addInitData("X1", "Y1");
//			sv1.addInitData("X2", "Y2");
//			//等效
////			sv1.setInitDataJson("{X1:Y1,X2:Y2}");
//			
//			//获取数据参数 见 com.xh.scheduler.job.code.CodeJobParam
//			sv1.addParam("beanName", "beanService");
//			sv1.addParam("methodName", "serverMethodName");
//			sv1.addParam("paramsClass", new String[] {"java.lang.String"});
//			sv1.addParam("paramsValue", new String[] {"${X1}"});
//			//等效
//			sv1.setJobParamsJson("{\"beanName\":\"beanService\",\"methodName\":\"serverMethodName\",\"paramsClass\":[\"java.lang.String\"],\"paramsValue\":[\"XXX\"]}");
//			
//			
//			SchedulerVo sv1_1 = new SchedulerVo("get_XXXX_child_");
//			sv1_1.setJobType("http");
//			sv1_1.setSaveMethod("saveDeviceResponse");
//			sv1_1.setEnable(true);
//			
//			//任务运行的初始化参数
//			sv1_1.addInitData("HTTP_SENZFLOW_USER", "Y1");
//			sv1_1.addInitData("HTTP_SENZFLOW_PASSWORD", "Y2");
//			sv1_1.addInitData("HTTP_SENZFLOW_URL", "https://api.senzflow.io/v1");
//			
//			//见 com.xh.scheduler.job.http.HttpJobParam
//			sv1_1.addParam("url", "${HTTP_SENZFLOW_URL}/devices/${id}/streams");
//			sv1_1.addParam("method", "GET");
//			sv1_1.addParam("httpParams", null);
//			sv1_1.addParam("responseClass", "com.xh.scheduler.job.http.vo.DeviceResponse");
//			sv1_1.addParam("httpHeaderClass", "getSenzflowHeader");
//			sv1_1.addParam("httpHeaderParams", "{\"userName\":\"${HTTP_SENZFLOW_USER}\",\"password\":\"${HTTP_SENZFLOW_PASSWORD}\",\"grantType\":\"password\",\"tokenUrl\":\"${HTTP_SENZFLOW_URL}/authentication/token\"}",);
//			
//			sv1.setChildJob(sv1_1);
//			sv1.setLoopChildJob(true);
//			
//			QuartzManager.addJob(sv1);

		}
	}

	@PreDestroy
	public void applicationEnd() {
		QuartzManager.shutdownJobs();
	}
	
	@SuppressWarnings("rawtypes")
	public  Class<? extends JobInterface> getJobClass(String type){
		if(jobInterfaces != null && jobInterfaces.size() >0) {
			Optional<JobInterface> opt = jobInterfaces.stream().filter(ji -> { return ji.getJobType().equalsIgnoreCase(type) ;}).findFirst();
			if(opt.isPresent()) {
				return opt.get().getClass() ;
			}
		}
		return null;
	}
	
	public  JobService getJobServicelClass(String type){
		if(jobServices != null && jobServices.size() >0) {
			Optional<JobService> opt = jobServices.stream().filter(js -> { return js.getJobType().equalsIgnoreCase(type) ;}).findFirst();
			if(opt.isPresent()) {
				return opt.get();
			}
		}
		return null;
	}
	
}

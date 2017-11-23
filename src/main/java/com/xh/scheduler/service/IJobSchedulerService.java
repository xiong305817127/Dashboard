package com.xh.scheduler.service;

import java.util.List;

import com.xh.common.exception.WebException;
import com.xh.scheduler.vo.SchedulerVo;


public interface  IJobSchedulerService {

	public void  addJobScheduler(SchedulerVo vo)  throws WebException;
	public void deleteJobScheduler(String[] ids)  throws WebException;
	public void updateJobScheduler(SchedulerVo vo) throws WebException;
	
	List<SchedulerVo> getJobSchedulerList() throws WebException;
	public SchedulerVo getJobSchedulerById(String idorName) throws WebException;
	List<SchedulerVo> getEnableJobSchedulerList( ) throws WebException;
	List<SchedulerVo> getNonChildJobSchedulerList() throws WebException;
	List<SchedulerVo> getParentScheduler(String childId) throws WebException;
	
	boolean startAllJobScheduler() ;
	boolean stopAllJobScheduler()  throws WebException;
	boolean startJobScheduler(String id)  throws WebException;
	boolean stopJobScheduler(String id)  throws WebException;
	
	String getJobRunKey(String jobId)throws WebException;
	Integer updateJobRunKey(String jobId ,String newKey ,String  oldKey)throws WebException;

}

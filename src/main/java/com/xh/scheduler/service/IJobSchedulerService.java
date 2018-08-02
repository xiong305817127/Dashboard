package com.xh.scheduler.service;

import java.util.List;

import com.xh.common.CommonException;
import com.xh.scheduler.vo.SchedulerVo;


public interface  IJobSchedulerService {

	public void  addJobScheduler(SchedulerVo vo)  throws CommonException;
	public void deleteJobScheduler(String[] ids)  throws CommonException;
	public void updateJobScheduler(SchedulerVo vo) throws CommonException;
	
	List<SchedulerVo> getJobSchedulerList() throws CommonException;
	public SchedulerVo getJobSchedulerById(String idorName) throws CommonException;
	List<SchedulerVo> getEnableJobSchedulerList( ) throws CommonException;
	List<SchedulerVo> getNonChildJobSchedulerList() throws CommonException;
	List<SchedulerVo> getParentScheduler(String childId) throws CommonException;
	
	boolean startAllJobScheduler() ;
	boolean stopAllJobScheduler()  throws CommonException;
	boolean startJobScheduler(String id)  throws CommonException;
	boolean stopJobScheduler(String id)  throws CommonException;
	
	String getJobRunKey(String jobId)throws CommonException;
	Integer updateJobRunKey(String jobId ,String newKey ,String  oldKey)throws CommonException;

}

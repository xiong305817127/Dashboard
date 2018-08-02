package com.xh.scheduler.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.xh.common.CommonException;

@Service("JobSchedulerService")
public class JobSchedulerServiceImpl implements IJobSchedulerService {

	@Override
	public void addJobScheduler(com.xh.scheduler.vo.SchedulerVo vo) throws CommonException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteJobScheduler(String[] ids) throws CommonException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateJobScheduler(com.xh.scheduler.vo.SchedulerVo vo) throws CommonException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<com.xh.scheduler.vo.SchedulerVo> getJobSchedulerList() throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public com.xh.scheduler.vo.SchedulerVo getJobSchedulerById(String idorName) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<com.xh.scheduler.vo.SchedulerVo> getEnableJobSchedulerList() throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<com.xh.scheduler.vo.SchedulerVo> getNonChildJobSchedulerList() throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<com.xh.scheduler.vo.SchedulerVo> getParentScheduler(String childId) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean startAllJobScheduler() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopAllJobScheduler() throws CommonException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean startJobScheduler(String id) throws CommonException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopJobScheduler(String id) throws CommonException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getJobRunKey(String jobId) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer updateJobRunKey(String jobId, String newKey, String oldKey) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	
}
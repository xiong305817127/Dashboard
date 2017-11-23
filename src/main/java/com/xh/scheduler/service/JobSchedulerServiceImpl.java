package com.xh.scheduler.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.xh.common.exception.WebException;

@Service("JobSchedulerService")
public class JobSchedulerServiceImpl implements IJobSchedulerService {

	@Override
	public void addJobScheduler(com.xh.scheduler.vo.SchedulerVo vo) throws WebException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteJobScheduler(String[] ids) throws WebException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateJobScheduler(com.xh.scheduler.vo.SchedulerVo vo) throws WebException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<com.xh.scheduler.vo.SchedulerVo> getJobSchedulerList() throws WebException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public com.xh.scheduler.vo.SchedulerVo getJobSchedulerById(String idorName) throws WebException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<com.xh.scheduler.vo.SchedulerVo> getEnableJobSchedulerList() throws WebException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<com.xh.scheduler.vo.SchedulerVo> getNonChildJobSchedulerList() throws WebException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<com.xh.scheduler.vo.SchedulerVo> getParentScheduler(String childId) throws WebException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean startAllJobScheduler() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopAllJobScheduler() throws WebException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean startJobScheduler(String id) throws WebException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopJobScheduler(String id) throws WebException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getJobRunKey(String jobId) throws WebException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer updateJobRunKey(String jobId, String newKey, String oldKey) throws WebException {
		// TODO Auto-generated method stub
		return null;
	}

	
}
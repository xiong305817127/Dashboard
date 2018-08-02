package com.xh.scheduler.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.xh.common.CommonException;

@Service("JobDetailService")
public class JobDetailServiceImpl implements IJobDetailService {

	@Override
	public List<com.xh.scheduler.vo.JobDetailVo> getJobDetailList() throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public com.xh.scheduler.vo.JobDetailVo getJobDetailById(String id) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateJobDetail(com.xh.scheduler.vo.JobDetailVo vo) throws CommonException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addJobDetail(com.xh.scheduler.vo.JobDetailVo vo) throws CommonException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteJobDetail(String[] ids) throws CommonException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<com.xh.scheduler.vo.JobDetailVo> getJobDetailListByJobId(String jobId) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public com.xh.scheduler.vo.JobDetailVo getLastRuningJobDetailByJobName(String jobName) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getJobdetailTimeInterval(String jobName) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateJobDetailResultNum(String id, Integer num) throws CommonException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void recordJobDetail(com.xh.scheduler.vo.JobDetailVo vo) throws CommonException {
		// TODO Auto-generated method stub
		
	}

	
	
}
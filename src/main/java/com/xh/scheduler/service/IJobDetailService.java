package com.xh.scheduler.service;

import java.util.List;
import java.util.Map;

import com.xh.common.CommonException;
import com.xh.scheduler.vo.JobDetailVo;

public interface  IJobDetailService {

	List<JobDetailVo> getJobDetailList() throws CommonException;
	JobDetailVo getJobDetailById(String id) throws CommonException ;
	void updateJobDetail(JobDetailVo vo) throws CommonException ;
	void addJobDetail(JobDetailVo vo) throws CommonException ;
	void deleteJobDetail(String[] ids) throws CommonException ;
	
	List<JobDetailVo> getJobDetailListByJobId(String jobId) throws CommonException;
	public JobDetailVo getLastRuningJobDetailByJobName(String jobName) throws CommonException;
	
	public  Map<String,String> getJobdetailTimeInterval(String jobName) throws CommonException;
	
	public void updateJobDetailResultNum(String id ,Integer num) throws CommonException;
	
	/**
	 * 根据 vo.isOnlyErrorRecord 判断是否保存到数据库
	 * @Title: recordJobDetail 
	 * @param vo
	 * @throws CommonException
	 */
	void recordJobDetail(JobDetailVo vo) throws CommonException ;

}

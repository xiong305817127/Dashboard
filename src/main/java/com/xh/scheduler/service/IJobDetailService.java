package com.xh.scheduler.service;

import java.util.List;
import java.util.Map;

import com.xh.common.exception.WebException;
import com.xh.scheduler.vo.JobDetailVo;

public interface  IJobDetailService {

	List<JobDetailVo> getJobDetailList() throws WebException;
	JobDetailVo getJobDetailById(String id) throws WebException ;
	void updateJobDetail(JobDetailVo vo) throws WebException ;
	void addJobDetail(JobDetailVo vo) throws WebException ;
	void deleteJobDetail(String[] ids) throws WebException ;
	
	List<JobDetailVo> getJobDetailListByJobId(String jobId) throws WebException;
	public JobDetailVo getLastRuningJobDetailByJobName(String jobName) throws WebException;
	
	public  Map<String,String> getJobdetailTimeInterval(String jobName) throws WebException;
	
	public void updateJobDetailResultNum(String id ,Integer num) throws WebException;
	
	/**
	 * 根据 vo.isOnlyErrorRecord 判断是否保存到数据库
	 * @Title: recordJobDetail 
	 * @param vo
	 * @throws WebException
	 */
	void recordJobDetail(JobDetailVo vo) throws WebException ;

}

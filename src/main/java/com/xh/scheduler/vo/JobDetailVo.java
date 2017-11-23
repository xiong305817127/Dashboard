package com.xh.scheduler.vo;

import java.util.Date;

public class JobDetailVo {
	
	private String id;
	private String jobId;
	private String jobName;
	private String jobType;
	private int childrenNum;
	private String parentId;
	private int reslutNum;
	private String jobParams;
	/**
	 * 0:正在启动 ,1:正在获取数据,2:已获取数据,准备保存数据,3:已保存数据,准备启动子任务，8:成功结束,9:异常结束,10:超时手动终止
	 */
	private Integer jobStatus;
	private String jobRemark;
	private Date jobStartTime;
	private Date jobEndTime;
	
	//扩展
	private Boolean isOnlyErrorRecord=false;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	public int getChildrenNum() {
		return childrenNum;
	}
	public void setChildrenNum(int childrenNum) {
		this.childrenNum = childrenNum;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public int getReslutNum() {
		return reslutNum;
	}
	public void setReslutNum(int reslutNum) {
		this.reslutNum = reslutNum;
	}
	public String getJobParams() {
		return jobParams;
	}
	public void setJobParams(String jobParams) {
		this.jobParams = jobParams;
	}
	
	public Integer getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(Integer jobStatus) {
		this.jobStatus = jobStatus;
	}
	public String getJobRemark() {
		return jobRemark;
	}
	public void setJobRemark(String jobRemark) {
		this.jobRemark = jobRemark;
	}
	public Date getJobStartTime() {
		return jobStartTime;
	}
	public void setJobStartTime(Date jobStartTime) {
		this.jobStartTime = jobStartTime;
	}
	public Date getJobEndTime() {
		return jobEndTime;
	}
	public void setJobEndTime(Date jobEndTime) {
		this.jobEndTime = jobEndTime;
	}
	public Boolean getIsOnlyErrorRecord() {
		return isOnlyErrorRecord;
	}
	public void setIsOnlyErrorRecord(Boolean isOnlyErrorRecord) {
		this.isOnlyErrorRecord = isOnlyErrorRecord;
	}
}

package com.xh.scheduler.vo;

import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.xh.util.JsonUtil;

public class SchedulerVo {

	private String id;
	private String jobName;
	private String jobDisplayName;
	private String jobType = "http"; // http、db
	private String jobParamsJson; // 根据type获取数据的参数
	private String saveMethod; // 继承了JobService的服务的方法名
	private String jobTime = "0 0 */1 * * ? *"; // 从小时执行一次,
	private Integer exceptionTimeout = 60; // 分钟 ，运行多少分钟就是任务超时，手动停止任务
	private Boolean  isOnlyErrorRecord =false ;//是否只有执行失败的记录才记入数据库
	private String childJobId;
	private Boolean isLoopChildJob = true; // 当主任务获取的数据是List时是否循环执行子任务
	private String initDataJson;// 任务的初始化json数据
	private Boolean isEnable = true;
	private String jobRemark;

	// 扩展
	//@JsonIgnoreProperties
	private Map<String,Object> initData;// 初始化json数据
	@JsonIgnore
	private Map<String, Object> paramsMap; // 根据type获取数据的参数
	private SchedulerVo childJob;
	private Object parentData;// 父任务的数据

	private String parentJobid;
	private String detailId;
	private String parentDetailId;
	private String jobGroupName = "TS_JOBGROUP_NAME";
	private String triggerName;
	private String triggerGroupName = "TS_TRIGGERGROUP_NAME";

	public SchedulerVo() {
		super();
		this.paramsMap = Maps.newHashMap();
		this.paramsMap.put("source", this);
	}
	
	public SchedulerVo(String jobName) {
		this();
		this.jobName = jobName;
		this.id = jobName;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobDisplayName() {
		return jobDisplayName;
	}

	public void setJobDisplayName(String jobDisplayName) {
		this.jobDisplayName = jobDisplayName;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getJobParamsJson() throws JsonProcessingException {
		this.paramsMap.remove("source");
		this.jobParamsJson =JsonUtil.transObjectTojson(paramsMap);
		this.paramsMap.put("source", this);
		return  this.jobParamsJson ;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setJobParamsJson(String jobParamsJson) {
		this.jobParamsJson = jobParamsJson;
		if (!StringUtils.isEmpty(jobParamsJson)) {
			try {
				if (jobParamsJson.startsWith("{")) {
					Map  jsonMap= JsonUtil.transJsonToObject( jobParamsJson, Map.class);
					if(jsonMap != null) {
						 this.paramsMap.putAll(jsonMap);
					}
				} 
			} catch (Exception e) {
			}
		}
		
	}

	public String getSaveMethod() {
		return saveMethod;
	}

	public void setSaveMethod(String saveMethod) {
		this.saveMethod = saveMethod;
	}

	public String getJobTime() {
		return jobTime;
	}

	public void setJobTime(String jobTime) {
		this.jobTime = jobTime;
	}

	public Integer getExceptionTimeout() {
		return exceptionTimeout;
	}

	public void setExceptionTimeout(Integer exceptionTimeout) {
		this.exceptionTimeout = exceptionTimeout;
	}

	public boolean isOnlyErrorRecord() {
		return isOnlyErrorRecord;
	}

	public void setOnlyErrorRecord(boolean isOnlyErrorRecord) {
		this.isOnlyErrorRecord = isOnlyErrorRecord;
	}

	public String getChildJobId() {
		return childJobId;
	}

	public void setChildJobId(String childJobId) {
		this.childJobId = childJobId;
	}

	public boolean isLoopChildJob() {
		return isLoopChildJob;
	}

	public void setLoopChildJob(boolean isLoopChildJob) {
		this.isLoopChildJob = isLoopChildJob;
	}

	public String getInitDataJson() throws JsonProcessingException {
		if(initData !=null) {
			this.initDataJson = JsonUtil.transObjectTojson(initData);
		}
		return initDataJson;
	}

	@SuppressWarnings("unchecked")
	public void setInitDataJson(String initDataJson) {
		this.initDataJson = initDataJson;
		if(!StringUtils.isEmpty(initDataJson)) {
			try {
				if (initDataJson.startsWith("{")) {
					this.initData = JsonUtil.transJsonToObject(initDataJson, Map.class);
				}else if (initDataJson.startsWith("[") && parentData == null) {
					this.parentData = JsonUtil.transJsontToList(initDataJson, String.class);
				}
			} catch (Exception e) { }
		}
	}

	public Map<String, Object> getInitData() {
		return initData;
	}

	public void setInitData(Map<String, Object> initData) {
		this.initData = initData;
		if(initData!= null) {
			try {
				this.initDataJson = JsonUtil.transObjectTojson(initData);
			} catch (Exception e) { }
		}
	}
	
	public void addParentInitData(Map<String, Object> parentInitData) {
		if(parentInitData == null ) {
			return ;
		}
		if(initData == null) {
			initData=parentInitData;
		}
		for(String key : parentInitData.keySet()) {
			if(!initData.containsKey(key)) {
				initData.put(key, parentInitData.get(key));
			}
		}
	}
	
	public String getInitValue(String key,String defaultVal) {
		if(initData == null) {
			return null;
		}
		String result = (String) initData.get(key);
		return StringUtils.isEmpty(result)?defaultVal:result;
	}

	public Map<String, Object> addInitData(String key, Object value) {
		if(initData==null) {
			initData = Maps.newHashMap() ;
		}
		initData.put(key, value);
		return initData;
	}
	
	
	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}

	public String getJobRemark() {
		return jobRemark;
	}

	public void setJobRemark(String jobRemark) {
		this.jobRemark = jobRemark;
	}

	public SchedulerVo getChildJob() {
		return childJob;
	}

	public void setChildJob(SchedulerVo childJob) {
		this.childJob = childJob;
	}

	public Object getParentData() {
		return parentData;
	}

	public void setParentData(Object parentData) {
		this.parentData = parentData;
		if (parentData != null) {
			try {
				if (parentData instanceof String) {
					if (((String) parentData).startsWith("{")) {
						this.parentData = JsonUtil.transJsonToObject((String) parentData, Map.class);
					} else if (((String) parentData).startsWith("[")) {
						this.parentData = JsonUtil.transJsontToList((String) parentData, Map.class);
					}
				}
			} catch (Exception e) {
			}
		}
	}

	public String getParentJobid() {
		return parentJobid;
	}

	public void setParentJobid(String parentJobid) {
		this.parentJobid = parentJobid;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getJobGroupName() {
		return jobGroupName;
	}

	public void setJobGroupName(String jobGroupName) {
		this.jobGroupName = jobGroupName;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTriggerGroupName() {
		return triggerGroupName;
	}

	public void setTriggerGroupName(String triggerGroupName) {
		this.triggerGroupName = triggerGroupName;
	}

	public Map<String, Object> getParamsMap() {
		return paramsMap;
	}

	public void setParamsMap(Map<String, Object> paramsMap) {
		this.paramsMap = paramsMap;
	}

	public Map<String, Object> addParam(String key, Object value) {
		paramsMap.put(key, value);
		return paramsMap;
	}
	
	public String getParentDetailId() {
		return parentDetailId;
	}

	public void setParentDetailId(String parentDetailId) {
		this.parentDetailId = parentDetailId;
	}

	public SchedulerVo clone(String newjobName) {
		if (StringUtils.isEmpty(newjobName)) {
			newjobName = jobName+"_clone";
		}
		SchedulerVo clone = new SchedulerVo();
		clone.setJobName(newjobName);
		clone.setChildJobId(childJobId);
		clone.setJobDisplayName(jobDisplayName);
		clone.setJobType(jobType);
		clone.setSaveMethod(saveMethod);
		clone.setJobTime(jobTime);
		clone.setOnlyErrorRecord(isOnlyErrorRecord);
		clone.setExceptionTimeout(exceptionTimeout);
		clone.setEnable(isEnable);
		clone.setJobRemark(jobRemark);
		
		clone.setInitData(initData);
		clone.setParentData(parentData);
		clone.setParentDetailId(parentDetailId);
		
		clone.setJobGroupName(jobGroupName);
		clone.setTriggerGroupName(triggerGroupName);
		clone.setTriggerName(triggerName);
		clone.setParentJobid(parentJobid);
		//复制子任务
		if (childJob != null) {
			clone.setChildJobId(childJobId);
			clone.setChildJob(childJob.clone(newjobName+"_"+childJob.getJobName()));
			clone.setLoopChildJob(isLoopChildJob);
		}
		//复制获取数据 请求参数paramsMap
		for (String key : paramsMap.keySet()) {
			if ("source".equalsIgnoreCase(key)) {
				continue;
			}
			clone.addParam(key, paramsMap.get(key));
		}
		return clone;
	}

}

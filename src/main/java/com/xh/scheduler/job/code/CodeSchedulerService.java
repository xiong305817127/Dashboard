package com.xh.scheduler.job.code;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.xh.common.exception.WebException;
import com.xh.scheduler.job.JobService;
import com.xh.scheduler.vo.SchedulerVo;

@Service("CodeSchedulerService")
public class CodeSchedulerService extends JobService{
	
//	@Autowired
//	@Qualifier("AppService")
//	IAppService appService;
	
	//get_XXX_parent
	//返回list 为 子任务的服务参数 AppServiceImpl.getAirMapByDeviceIdAndLimit(String deviceId, String unit, Integer isCheck,String limit)
	public List<List<Object>> parentDataResponse(List<String> devices,CodeJobParam t,SchedulerVo svo) throws WebException{
		if(devices!= null &&devices.size()>0) {
			
			String extendedUnit= svo.getInitValue("extended_Table_unit","air_minute");
			String extendedlimit=  svo.getInitValue("extended_select_limit","200");
			
			 List<List<Object>> result =  Lists.newArrayList() ;
				 for(String id : devices) {
					 List<Object> param = Lists.newArrayList();
					 param.add(id);
					 param.add(extendedUnit);
					 param.add(0);
					 param.add(extendedlimit);
					 result.add(param);
				 }
//			 }
			 return result;
		}
		return null;
	}
	
	//detecting_alarm_data_child
	//获取到了单个设备的待处理数据列表，进行处理
	public Integer childDataResponse(List<Map<String,Object>> airMapList,CodeJobParam t,SchedulerVo svo) throws WebException{
		
		String extendedUnit= svo.getInitValue("extended_Table_unit","air_minute");
		
		if(airMapList!= null &&airMapList.size()>0) {
			//TODO do something
		}
		return 0;
	}

	@Override
	public String getJobType() {
		return "code";
	}

}

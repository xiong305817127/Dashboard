package com.xh.scheduler.job.http;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.xh.common.CommonException;
import com.xh.scheduler.job.JobService;
import com.xh.scheduler.job.http.vo.DeviceResponse;
import com.xh.util.http.HttpUtils;

@Service("HttpSchedulerService")
public class HttpSchedulerService extends JobService{
	
//	@Resource(name = "AppService")
//	private IAppService appService;
	
	
	/**
	 * 同步设备信息，保存方法
	 * @Title: saveDeviceResponse 
	 * @param devices
	 * @return
	 * @throws CommonException
	 */
	public Integer saveDeviceResponse(List<DeviceResponse> devices) throws CommonException {
		if(devices!=null && devices.size()>0) {
			//TODO do something
		}
		return 0;
	}
	
	
	@Override
	public String getJobType() {
		return "http";
	}

	
	private Long TokenExpiresIn ;
	private String AccessToken ;
	/**
	 * 获取Senzflow系统的授权token方法，获取请求头方法
	 * @Title: getSenzflowHeader 
	 * @param headerParams
	 * @return
	 * @throws CommonException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public  Map<String,String> getSenzflowHeader(Map headerParams) throws CommonException, IOException {
		
		if(StringUtils.isEmpty(AccessToken) || TokenExpiresIn < new Date().getTime() ) {
			//更新token
			synchronized(HttpSchedulerService.class) {
				if(StringUtils.isEmpty(AccessToken) || TokenExpiresIn < new Date().getTime() ) {
					String url = (String) headerParams.get("tokenUrl") ;
					HashMap<String, String> params = Maps.newHashMap() ;
					params.put("grant_type", (String) headerParams.get("grantType") );
					params.put("username",(String) headerParams.get("userName") );
					params.put("password", (String) headerParams.get("password") );
					List<Map> tokenMap = HttpUtils.doGet(url,Map.class, params, null) ;
					if(tokenMap.size() > 0) {
						AccessToken = (String) tokenMap.get(0).get("access_token");
						int expiresIn = (int) tokenMap.get(0).get("expires_in");
						TokenExpiresIn = ( new Date().getTime() )+( expiresIn-10 ) ;
					}
				}
			}
		}
		
		if(StringUtils.isEmpty(AccessToken)) {
			throw new CommonException(" 401 ,token获取失败！");
		}
		
		HashMap<String, String> headers = Maps.newHashMap() ;
		headers.put("Authorization", "Bearer "+AccessToken) ;
		
		return headers;
	}
	
}

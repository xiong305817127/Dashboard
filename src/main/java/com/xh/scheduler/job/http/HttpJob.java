package com.xh.scheduler.job.http;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.xh.scheduler.job.JobInterface;
import com.xh.scheduler.vo.SchedulerVo;
import com.xh.util.JsonUtil;
import com.xh.util.http.HttpUtils;

@Component
public class HttpJob extends JobInterface<HttpJobParam> {
	
	@Override
	public String  getJobType() {
		return "http";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List getDate(HttpJobParam t,SchedulerVo svo) throws Exception {
		if(t == null) {
			return null;
		}
		
		//转换Url、获取http参数、header参数 中的占位符,优先initData
		Map parentMap = Maps.newHashMap();
		if(svo.getParentData() != null) {
				Object parentData = svo.getParentData() ;
				if(!(parentData instanceof Collection) && !(parentData.getClass().isArray())) {
					parentMap = JsonUtil.transObjectToObject(parentData, Map.class);
				}
		}
		if(svo.getInitData() != null) {
			parentMap.putAll(svo.getInitData());
		}
		t.setUrl( replMap(t.getUrl(),parentMap) );
		if( !StringUtils.isEmpty( t.getHttpParams())){
			t.setHttpParams( replMap(t.getHttpParams(),parentMap) );
		}
		if( !StringUtils.isEmpty( t.getHttpHeaderParams())){
			t.setHttpHeaderParams( replMap(t.getHttpHeaderParams(),parentMap) );
		}
		
		
		//获取真实的header
		Map<String, String> headers = null ;
		if(!StringUtils.isEmpty( t.getHttpHeaderParams())) {
			headers = JsonUtil.transJsonToObject( t.getHttpHeaderParams(), Map.class);
		}
		if(!StringUtils.isEmpty( t.getHttpHeaderClass() )) {
			if(headers == null) {
				headers = Maps.newHashMap();
			}
			headers = (Map<String, String>) invokeServiceMethod(t.getHttpHeaderClass(), new Object[] {headers}, new Class[] {Map.class});
		}
		
		//获取真实的http参数
		Object httpParam = t.getHttpParams() ;
		if(!StringUtils.isEmpty( t.getHttpParamsClass() )) {
			Map<Object, Object> paramMap;
			if(StringUtils.isEmpty( t.getHttpParams())) {
				paramMap = Maps.newHashMap();
			}else {
				paramMap = JsonUtil.transJsonToObject( t.getHttpParams(), Map.class);
			}
			httpParam = invokeServiceMethod(t.getHttpParamsClass(), new Object[] {paramMap}, new Class[] {Map.class});
			if(httpParam == null && svo.getParentData() != null) {
				httpParam = invokeServiceMethod(t.getHttpParamsClass(), new Object[] {paramMap,svo.getParentData()}, new Class[] {Map.class,svo.getParentData().getClass()});
			}
		}
		if( httpParam == null) {
			 httpParam = t.getHttpParams() ;
		}
		
		//发送请求
		if( "get".equalsIgnoreCase(t.getMethod())) {
			return HttpUtils.doGet(t.getUrl(),t.getResponseClassInstance(), httpParam, headers) ;
		}else if( "post".equalsIgnoreCase(t.getMethod())){
			return HttpUtils.doPost(t.getUrl(),httpParam,t.getResponseClassInstance(),  headers) ;
		}
		return null;
	}
	

	
}

class HttpJobParam {
	/**
	 * 访问httpUrl，使用${var-父查询的响应即SchedulerVo.parentData替换}占位-主要用于子任务
	 */
	private String url ;
	/**
	 * http 方式： get/post
	 */
	private String method ;
	/**
	 * http访问参数处理方法，相应service中的方法，参数为httpParams转换成的Map,和SchedulerVo.parentData对象，返回 Map对象为form提交,Object/json字符串为json提交,其他直接提交 ，可为空
	 */
	private String httpParamsClass ;
	/**
	 * http访问参数,json格式， 可使用${var-父查询的响应即SchedulerVo.parentData}占位(主要用于子任务)直接替换字符串，httpParamsClass不为空，则为httpParamsClass的Map入参
	 */
	private String httpParams ;
	/**
	 * http请求头处理方法，相应service中的方法，参数为httpHeaderParams转换成的Map,返回Map，可为空
	 */
	private String httpHeaderClass;
	/**
	 * http请求头处理方法的参数，json格式将会转换为Map
	 */
	private String httpHeaderParams;
	/**
	 * http成功返回对象，为空则为Map
	 */
	private String responseClass ;

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	@SuppressWarnings("rawtypes")
	public Class getResponseClassInstance() throws ClassNotFoundException {
		if(StringUtils.isEmpty(responseClass)) {
			return Map.class;
		}
		return Class.forName(responseClass);
	}
	
	public String getResponseClass() {
		return responseClass;
	}
	public void setResponseClass(String responseClass) {
		this.responseClass = responseClass;
	}
	
	public String getHttpParams() {
		return httpParams;
	}
	public void setHttpParams(String httpParams) {
		this.httpParams = httpParams;
	}

	public String getHttpParamsClass() {
		return httpParamsClass;
	}
	public void setHttpParamsClass(String httpParamsClass) {
		this.httpParamsClass = httpParamsClass;
	}
	public String getHttpHeaderClass() {
		return httpHeaderClass;
	}
	public void setHttpHeaderClass(String httpHeaderClass) {
		this.httpHeaderClass = httpHeaderClass;
	}
	public String getHttpHeaderParams() {
		return httpHeaderParams;
	}
	public void setHttpHeaderParams(String httpHeaderParams) {
		this.httpHeaderParams = httpHeaderParams;
	}

}

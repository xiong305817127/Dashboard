package com.xh.scheduler.job.code;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xh.scheduler.job.JobInterface;
import com.xh.scheduler.vo.SchedulerVo;
import com.xh.util.JsonUtil;
import com.xh.util.SpringUtil;

@Component
public class CodeJob extends JobInterface<CodeJobParam> {
	
	@Override
	public String  getJobType() {
		return "code";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List getDate(CodeJobParam t,SchedulerVo svo) throws Exception {
		if(t == null||StringUtils.isEmpty(t.getBeanName()) || StringUtils.isEmpty(t.getMethodName())) {
			return null;
		}
		//转换BeanName、获取http参数、header参数 中的占位符,优先initData
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
		t.setBeanName( replMap(t.getBeanName(),parentMap) );
		t.setMethodName( replMap(t.getMethodName(),parentMap) );
		if( t.getParamsClass() !=null &&  t.getParamsClass().length >0 ){
			for(int i=0;i< t.getParamsClass().length;i++) {
				 t.getParamsClass()[i]= replMap( t.getParamsClass()[i],parentMap) ;
			}
		}
		if( t.getParamsValue() !=null &&  t.getParamsValue().length >0 ){
			for(int i=0;i< t.getParamsValue().length;i++) {
				if(t.getParamsValue()[i] instanceof String) {
					 t.getParamsValue()[i]= replMap( (String)t.getParamsValue()[i],parentMap) ;
				}
			}
		}
		
		String beanName = t.getBeanName();
		Object serviceBean;
		if(beanName.contains(".")) {
			serviceBean = SpringUtil.getApplicationContext().getBean(Class.forName(beanName));
		}else {
			serviceBean = SpringUtil.getBean(beanName);
		}
		if(serviceBean == null) {
			serviceBean = Class.forName(beanName).newInstance();
		}
		if(serviceBean == null) {
			return null;
		}
		String[] params = t.getParamsClass();
		Class[] paramsClass = null;
		if(params != null  && params.length>0) {
			paramsClass = new Class[params.length];
			for(int i=0;i<params.length;i++){
				String paramStr = params[i]; 
				paramsClass[i]=Class.forName(paramStr);
			}
		}
		Method method = ReflectionUtils.findMethod(serviceBean.getClass(), t.getMethodName(),paramsClass);
		if(method != null) {
			Object result = null;
			if(paramsClass !=null && paramsClass.length >0 ){
				Object[] args ;
				if(t.getParamsValue() ==  null || t.getParamsValue().length == 0) {
					args =new Object[paramsClass.length];
					Object pobj = svo.getParentData();
					for(int i=0;i<paramsClass.length;i++){
						if(pobj != null) {
							Class argclass = paramsClass[i];
							args[i] = findArgFrom(i,pobj,argclass);
						}else {
							args[i] = null ;
						}
					}
				}else {
					args= t.getParamsValue() ;
				}
				result = method.invoke(serviceBean, args);
			}else {
				result = method.invoke(serviceBean);
			}
			if(result !=null && result instanceof List) {
				return (List)result ;
			}else {
				return Lists.newArrayList(result);
			}
		}
		return null;
	}
	
	/**
	 * @Title: findArgFrom 
	 * @param obj 主任务的初始化json数据或者父任务的返回值 String/List/Map
	 * @param argClass 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object findArgFrom(int index , Object obj ,Class argClass) {
		if(obj.getClass() == argClass || argClass.isAssignableFrom(obj.getClass())) {
			return obj ;
		}else if(obj instanceof List){
			Object o = ((List)obj).get(index);
//			if(o.getClass() == argClass || argClass.isAssignableFrom(o.getClass())) {
				return o;
//			}
		}else if(obj instanceof Map) {
			for(Object o : ((Map)obj).values()) {
				if(o.getClass() == argClass) {
					return o;
				}
			}
		}
		
		return null;
	}

}

class CodeJobParam {
	/**
	 * 处理类，springBean,可以是dao 或者 service 等
	 */
	private String beanName;
	/**
	 * 方法名，处理类的方法
	 */
	private String methodName; 
	/**
	 * 参数数组，处理类的方法的参数,当不为空，则SchedulerVo.parentData必须有值，参数实例从中获取，多个参数SchedulerVo.parentData使用List
	 */
	private String[] paramsClass;
	
	/**
	 * 参数实参数组，不为空时，直接使用调用服务，当为空时 使用SchedulerVo.parentData或者SchedulerVo.initData 为参数
	 */
	private Object[] paramsValue;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String[] getParamsClass() {
		return paramsClass;
	}

	public void setParamsClass(String[] paramsClass) {
		this.paramsClass = paramsClass;
	}

	public Object[] getParamsValue() {
		return paramsValue;
	}

	public void setParamsValue(Object[] paramsValue) {
		this.paramsValue = paramsValue;
	}

}

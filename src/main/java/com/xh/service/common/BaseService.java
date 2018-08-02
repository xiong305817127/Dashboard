package com.xh.service.common;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xh.util.ReflectUtils;
import com.xh.util.Utils;

public class BaseService {

	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * 将 obj中不为空的值设置到bean中并返回bean
	 * @param obj
	 * @param bean
	 * @return
	 */
	protected Object updateObjectToBean(Object obj,Object bean){
		
		List<String> beanFields = ReflectUtils.seekOsgiFieldList(bean, true).stream().map(field -> { return field.getName();}).collect(Collectors.toList());;
		ReflectUtils.seekOsgiFieldList(obj, true).stream().filter( field -> { return beanFields.contains(field.getName());}).forEach( field -> {
			Object fieldValue = ReflectUtils.getOsgiField(obj, field.getName(), true);
			if(!Utils.isNull(fieldValue)){
				ReflectUtils.setOsgiField(bean, field.getName(), fieldValue, true);
			}
		});
		
		return bean;
	}

}

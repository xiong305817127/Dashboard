package com.xh.common.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xh.util.ReflectUtils;
import com.xh.util.Utils;

public class BaseService {

	protected final Log logger = LogFactory.getLog(getClass());

	protected Object updateObjectToBean(Object obj,Object bean){
		
		List<String> beanFields = Arrays.asList(bean.getClass().getDeclaredFields()).stream().map(field -> { return field.getName();}).collect(Collectors.toList());
		Arrays.asList( obj.getClass().getDeclaredFields()).stream().filter( field -> { return beanFields.contains(field.getName());}).forEach( field -> {
			Object fieldValue = ReflectUtils.getOsgiField(obj, field.getName(), true);
			if(!Utils.isNull(fieldValue)){
				ReflectUtils.setOsgiField(bean, field.getName(), fieldValue, true);
			}
		});
		
		return bean;
	}

}

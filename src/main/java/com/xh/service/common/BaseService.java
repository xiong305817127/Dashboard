package com.xh.service.common;

import java.lang.reflect.Field;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xh.util.ReflectUtils;

public class BaseService {

	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * 将 obj中不为空的值设置到bean中并返回bean
	 * @param obj
	 * @param bean
	 * @return
	 */
	protected Object updateObjectToBean(Object source,Object bean){
		if (source == null || bean ==  null ) {
			return bean;
		}
		if(bean instanceof Class) {
			bean = ReflectUtils.newOsgiInstance(bean, null, null,null);
		}

		List<String> fields = ReflectUtils.getOsgiFieldNames(source);
		if(fields != null && fields.size() >0) {
			for(String fieldName : fields) {
				Object sourceFieldVlaue = ReflectUtils.getOsgiField(source, fieldName, true);
				if(sourceFieldVlaue != null ) {
					Field beanField = ReflectUtils.seekOsgiField(bean.getClass(), fieldName, true);
					if( beanField != null && ReflectUtils.isSameClass(beanField.getType(), sourceFieldVlaue.getClass()) ) {
						ReflectUtils.setOsgiField(bean, fieldName, sourceFieldVlaue, true);
					}
				}
			}
		}
		return bean;      
	}

}

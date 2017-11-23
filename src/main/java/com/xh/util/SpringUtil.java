package com.xh.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/** 
	 * 获取spring bean工具类  
	 * 要在applicationContext.xml中加上:  
	 * <bean id='SpringUtil ' class='com.ys.ydp.util.SpringUtil' singleton='true'/> 
	 * SpringUtil 实例时就自动设置applicationContext,以便后来可直接用applicationContext 
	 * </pre> 
	 *  
	 * @author tom 
	 *  
	 */  
	@Component
	public class SpringUtil implements ApplicationContextAware { 
		
		private static final Logger LOGGER = LoggerFactory.getLogger( SpringUtil.class );
	  
	    private static ApplicationContext applicationContext;  
	  
	    @Override  
	    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {  
	    	SpringUtil.applicationContext = applicationContext;  
	    }  
	  
	    public static ApplicationContext getApplicationContext() {  
	        return applicationContext;  
	    }  
	    /**
	     * 根据一个bean name获取一个bean
	     * @param name 在spring配置文件配置的bean name
	     * @return java bean
	     * @throws CommonException 
	     */
	    public static Object getBean(String name)  { 
	    	Object retObj=null;
	    	try{
	    		retObj=applicationContext.getBean(name);  
	    	}catch(BeansException e){
	    		LOGGER.error(e.getMessage());
	    	}
	    	return retObj;
	    }  
	  
	} 
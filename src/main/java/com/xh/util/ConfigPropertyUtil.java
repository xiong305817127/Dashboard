/**
 * GDBD iDatrix CloudETL System.
 */
package com.xh.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author XH
 * @since 2017年7月7日
 *
 */
public class ConfigPropertyUtil  {

	private static final Logger LOGGER = LoggerFactory.getLogger( ConfigPropertyUtil.class );
	private static Map<String, String> propertyMap= new HashMap<String, String>();;

	
	/**
	 * 获取key对应的value,如果value为空则返回defaultVal
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static String getProperty(String key , String defaultVal) {
		String val = getProperty(key);
		return  Utils.isEmpty(val) ? defaultVal : val;
	}

	/**
	 * 获取 key对应的value值
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		if( !isLoaded){
			getProperty();
		}
		return System.getProperty(key, propertyMap.get(key));
	}
	
	private static boolean isLoaded=false;
	private static void getProperty() {
		InputStream in = ConfigPropertyUtil.class.getClassLoader().getResourceAsStream("./config.properties");

		Properties p = new Properties();
		try {
			p.load(in);
			isLoaded=true;
			setPropertyToMap(p);
		} catch (Exception e) {
			LOGGER.info("loading local config.properties file failed : "+e.getMessage());
		}
	
	}
	
	private static void setPropertyToMap(Properties p ){
		
		for (Object key : p.keySet()) {
			String keyStr = key.toString();
			String value = p.getProperty(keyStr);
			propertyMap.put(keyStr, value);
			if(!System.getProperties().containsKey(keyStr)){
				System.setProperty(keyStr, value);
			}
		}
		
	}

}
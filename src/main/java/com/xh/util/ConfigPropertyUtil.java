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
			loadProperty();
		}
		return System.getProperty(key, propertyMap.get(key));
	}
	
	/**
	 * 获取key对应的value,如果value为空则返回defaultVal
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static Boolean getBooleanProperty(String key , Boolean defaultVal) {
		String val = getProperty(key);
		return  Utils.isEmpty(val) ? defaultVal : Boolean.valueOf(val); 
	}

	/**
	 * 获取 key对应的value值
	 * @param key
	 * @return
	 */
	public static Boolean getBooleanProperty(String key) {
		String val = getProperty(key);
		return  Boolean.valueOf(val) ;
	}
	
	/**
	 * 将获取到的值包含 的${XXXX}进行替换为并返回替换后的结果 ,eg.user为root的字符串: /path/${user}/sub${user}Path 替换为: /path/root/subrootPath
	 * @param key
	 * @return
	 */
	public static String getPropertyByFormatUser(String key) {
		String value = getProperty(key);
		while( !Utils.isEmpty(value) && value.contains("${")&& value.contains("}")){
			int startIndex = value.indexOf("${");
			int endIndex = value.indexOf("}",startIndex);
			if(startIndex >-1 && endIndex >-1 && startIndex <endIndex) {
				String key$ = value.substring(startIndex, endIndex+1);
				String interkey = key$.substring(2, key$.length()-1);
				String interValue = getProperty(interkey);
				if(Utils.isEmpty(interValue)) {
					return value;
				}
				value=value.replaceAll(key$, interValue);
			}
		}
		return value;
	}
	

	private static boolean isLoaded=false;
	private static void loadProperty() {
		InputStream in = ConfigPropertyUtil.class.getClassLoader().getResourceAsStream("file:./config.properties");
		if(in == null) {
			in = ConfigPropertyUtil.class.getClassLoader().getResourceAsStream("./config.properties");
		}
		if(in == null) {
			in = ConfigPropertyUtil.class.getClassLoader().getResourceAsStream("config.properties");
		}
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
package com.xh.service.config;

import com.xh.common.exception.WebException;
import com.xh.entry.Config;


public interface ConfigService {


	/**
	 * 增加配置
	 * @param key
	 * @param value
	 * @return Config
	 */
	public Config addConfig(String key, String value) throws WebException ;

	/**
	 * 删除配置
	 * @param key
	 * @return Integer
	 */
	public int deleteConfigByKey(String key)  throws WebException ;

	/**
	 * 更新配置
	 * @param key
	 * @param value
	 */
	public Config updateConfigByKey(String key, String value)  throws WebException ;

	/**
	 * @param key
	 * @return
	 */
	public String getStringByKey(String key) throws WebException ;
	
	/**
	 * @param key
	 * @return
	 */
	public int getIntKey(String key)  throws WebException ;
}

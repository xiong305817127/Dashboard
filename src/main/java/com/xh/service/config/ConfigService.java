package com.xh.service.config;

import com.xh.common.CommonException;
import com.xh.dto.ConfigDto;


public interface ConfigService {


	/**
	 * 增加配置
	 * @param key
	 * @param value
	 * @return ConfigDto
	 */
	public ConfigDto addConfig(String key, String value) throws CommonException ;

	/**
	 * 删除配置
	 * @param key
	 * @return Integer
	 */
	public int deleteConfigByKey(String key)  throws CommonException ;

	/**
	 * 更新配置
	 * @param key
	 * @param value
	 */
	public ConfigDto updateConfigByKey(String key, String value)  throws CommonException ;

	/**
	 * @param key
	 * @return
	 */
	public String getStringByKey(String key) throws CommonException ;
	
	/**
	 * @param key
	 * @return
	 */
	public int getIntKey(String key)  throws CommonException ;
}

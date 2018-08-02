package com.xh.service.config;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.xh.dao.datasource1.ConfigDao;
import com.xh.dto.ConfigDto;
import com.xh.service.common.BaseService;

/**
 * 网站配置
 * 
 * @author Zihan
 * 
 */
@Service
public class ConfigServiceImpl extends BaseService implements ConfigService{

	@Resource
	private ConfigDao configDao;
//	@Resource
//	private ConfigDao2 configDao2;

	/**
	 * 增加配置
	 * 
	 * @param key
	 * @param value
	 * @return ConfigDto
	 */
	public ConfigDto addConfig(String key, String value) {
		ConfigDto config = new ConfigDto();
		config.setKey(key);
		config.setValue(value);
		config.setCreateTime(new Date());
		configDao.addConfig(config);
		return config;
	}

	/**
	 * 删除配置
	 * 
	 * @param key
	 * @return Integer
	 */
	public int deleteConfigByKey(String key) {
		return configDao.deleteConfig(key);
	}

	/**
	 * 更新配置
	 * 
	 * @param key
	 * @param value
	 */
	public ConfigDto updateConfigByKey(String key, String value) {
		ConfigDto config = configDao.getConfigByKey(key);
		config.setValue(value);
		configDao.updateConfig(config);
		ConfigDto config2 = configDao.getConfigByKey(key);
		config2.setValue(value);
		configDao.updateConfig(config2);
		this.getStringByKey(key);
		return config;
	}

	/**
	 * @param key
	 * @return
	 */
	@Cacheable(value="commonCache")
	public String getStringByKey(String key) {
		ConfigDto config = configDao.getConfigByKey(key);
		if (config == null) {
			return "";
		} else {
			return config.getValue();
		}
	}

	/**
	 * @param key
	 * @return
	 */
	public int getIntKey(String key) {
		ConfigDto config = configDao.getConfigByKey(key);
		if (config == null) {
			return 0;
		} else {
			return Integer.parseInt(config.getValue());
		}
	}
}

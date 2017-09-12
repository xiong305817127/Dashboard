package com.xh.service.config;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.xh.common.service.BaseService;
import com.xh.dao.datasource1.ConfigDao;
import com.xh.dto.ConfigDto;
import com.xh.entry.Config;

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
	 * @return Config
	 */
	public ConfigDto addConfig(String key, String value) {
		Config config = new Config();
		config.setKey(key);
		config.setValue(value);
		config.setCreateTime(new Date());
		configDao.addConfig(config);
		return new ConfigDto(config);
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
		Config config = configDao.getConfigByKey(key);
		config.setValue(value);
		configDao.updateConfig(config);
		Config config2 = configDao.getConfigByKey(key);
		config2.setValue(value);
		configDao.updateConfig(config2);
		this.getStringByKey(key);
		return new ConfigDto(config);
	}

	/**
	 * @param key
	 * @return
	 */
	@Cacheable(value="commonCache")
	public String getStringByKey(String key) {
		Config config = configDao.getConfigByKey(key);
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
		Config config = configDao.getConfigByKey(key);
		if (config == null) {
			return 0;
		} else {
			return Integer.parseInt(config.getValue());
		}
	}
}

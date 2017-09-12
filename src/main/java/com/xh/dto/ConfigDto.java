package com.xh.dto;

import com.xh.common.dto.CommonDto;
import com.xh.entry.Config;

public class ConfigDto extends CommonDto {
	
	private String key;
	private String value;
	private String description;
	
	public ConfigDto(){
	}
	
	public ConfigDto(Config config){
		this(config.getKey(),config.getValue(),config.getDescription());
	}
	

	/**
	 * @param key
	 * @param value
	 * @param description
	 */
	public ConfigDto(String key, String value, String description) {
		super();
		this.key = key;
		this.value = value;
		this.description = description;
	}



	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param  设置 key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param  设置 value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param  设置 description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}

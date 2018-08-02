/*



 */

package com.xh.dao.datasource2;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xh.dto.ConfigDto;

/**
 * 网站配置
 * 
 * @author Zhangjiale
 * 
 */

//@Repository("configdao2")
@SuppressWarnings("unused")
public interface ConfigDao2 {

	// ///////////////////////////////
	// ///// 增加 ////////
	// ///////////////////////////////

	/**
	 * 增加配置
	 * 
	 * @return Integer
	 */
	public int addConfig(ConfigDto ConfigDto);

	// ///////////////////////////////
	// ///// 刪除 ////////
	// ///////////////////////////////

	/**
	 * 删除配置
	 * 
	 * return Integer
	 */
	public int deleteConfig(@Param("key") String key);

	// ///////////////////////////////
	// ///// 修改 ////////
	// ///////////////////////////////

	/**
	 * 更新配置
	 * 
	 * @return Integer
	 */
	public int updateConfig(ConfigDto ConfigDto);

	// ///////////////////////////////
	// ///// 查詢 ////////
	// ///////////////////////////////

	/**
	 * 查看配置
	 * 
	 * @return ConfigDto
	 */
	public ConfigDto getConfigByKey(@Param("key") String key);
}

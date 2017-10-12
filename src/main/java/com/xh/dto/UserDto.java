/*



 */

package com.xh.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xh.entry.User;

public class UserDto extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3791127404704120932L;
	
	private transient String dev = "production" ; //development

	/**
	 * @return the dev
	 */
	public String getDev() {
		return dev;
	}

	/**
	 * @param  设置 dev
	 */
	public void setDev(String dev) {
		this.dev = dev;
	}
	
	@JsonIgnore
	public boolean development(){
		return "development".equals(dev) ;
	}
	
}

/*



 */

package com.xh.entry;

import com.xh.common.dto.CommonDto;

public class Menu  extends CommonDto {

    private String id;
    private String bpid;
    private String mpid;
    private String name;
    private String icon;
    private String route;
    
    private boolean admin ;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param  设置 id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the bpid
	 */
	public String getBpid() {
		return bpid;
	}
	/**
	 * @param  设置 bpid
	 */
	public void setBpid(String bpid) {
		this.bpid = bpid;
	}
	
	/**
	 * @return the mpid
	 */
	public String getMpid() {
		return mpid;
	}
	/**
	 * @param  设置 mpid
	 */
	public void setMpid(String mpid) {
		this.mpid = mpid;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param  设置 name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}
	/**
	 * @param  设置 icon
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
	/**
	 * @return the route
	 */
	public String getRoute() {
		return route;
	}
	/**
	 * @param  设置 route
	 */
	public void setRoute(String route) {
		this.route = route;
	}
	/**
	 * @return the admin
	 */
	public boolean isAdmin() {
		return admin;
	}
	/**
	 * @param  设置 admin
	 */
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	
}

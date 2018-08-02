/*



 */

package com.xh.dto;

import com.xh.dto.common.CommonDto;

public class MenuDto  extends CommonDto {
	
	private static final long serialVersionUID = 6702897412889447527L;

    private String id;
    private String bpid;
    private String mpid;
    private String name;
    private String icon;
    private String route;
    
    private boolean admin ;
    
    //额外的扩展
	private transient Boolean createTemplate ;
    
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
	
	public Boolean isCreateTemplate() {
		return createTemplate;
	}
	/**
	 * @param  设置 createTemplate
	 */
	public void setCreateTemplate(Boolean createTemplate) {
		this.createTemplate = createTemplate;
	}
	
	
}

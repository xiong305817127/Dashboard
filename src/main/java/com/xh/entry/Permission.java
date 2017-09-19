/*



 */

package com.xh.entry;

import java.util.List;

import com.xh.common.dto.CommonDto;


public class Permission extends CommonDto {

    private String id;
    private List<String> visit;
    private String role;
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
	 * @return the visit
	 */
	public List<String> getVisit() {
		return visit;
	}
	/**
	 * @param  设置 visit
	 */
	public void setVisit(List<String> visit) {
		this.visit = visit;
	}
	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @param  设置 role
	 */
	public void setRole(String role) {
		this.role = role;
	}
   

	
}

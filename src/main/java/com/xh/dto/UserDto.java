/*



 */

package com.xh.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xh.dto.common.CommonDto;

public class UserDto extends CommonDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2039422486623951974L;
	
	private String id;
	private String  username;
	private String  password;
	private PermissionDto  permissions ;
	private String  permissionId ;

	private String   nickName;
	private String   phone;
	private Integer   age;
	private String  address;
	private boolean  isMale;
	private String   email;
	private String  createTime;
	private String  avatar ;
	
	 //额外的扩展
	private transient String dev = "production" ; //development
	
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param  设置 username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param  设置 password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the permissions
	 */
	public PermissionDto getPermissions() {
		return permissions;
	}
	/**
	 * @param  设置 permissions
	 */
	public void setPermissions(PermissionDto permissions) {
		this.permissions = permissions;
	}
	
	/**
	 * @return the permissionId
	 */
	public String getPermissionId() {
		return permissionId;
	}
	/**
	 * @param  设置 permissionId
	 */
	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}
	/**
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}
	/**
	 * @param  设置 nickName
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param  设置 phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the age
	 */
	public Integer getAge() {
		return age;
	}
	/**
	 * @param  设置 age
	 */
	public void setAge(Integer age) {
		this.age = age;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param  设置 address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	

	/**
	 * @return the isMale
	 */
	public boolean isMale() {
		return isMale;
	}
	/**
	 * @param  设置 isMale
	 */
	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param  设置 email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}
	/**
	 * @param  设置 createTime
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	/**
	 * @return the avatar
	 */
	public String getAvatar() {
		return avatar;
	}
	/**
	 * @param  设置 avatar
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
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
